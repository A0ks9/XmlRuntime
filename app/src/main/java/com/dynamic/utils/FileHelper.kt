package com.dynamic.utils

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap


object FileHelper {

    init {
        System.loadLibrary("xmlParser")
    }

    external fun parseXML(inputStream: InputStream): String?

    // Cache to store resolved file paths for URIs (avoids re-copying or re-querying)
    private val pathCache = ConcurrentHashMap<Uri, String>()

    /**
     * Attempts to resolve the file system path for the given URI.
     * Uses a fallback copy if the file cannot be directly resolved.
     */
    @JvmStatic
    fun getPath(context: Context, uri: Uri): String? {
        // Return from cache if available
        pathCache[uri]?.let { return it }

        val resolvedPath = when {
            DocumentsContract.isDocumentUri(context, uri) -> handleDocumentUri(context, uri)
            "content".equals(uri.scheme, ignoreCase = true) -> handleContentUri(context, uri)
            "file".equals(uri.scheme, ignoreCase = true) -> uri.path
            else -> copyToInternal(context, uri)
        }
        resolvedPath?.also { pathCache[uri] = it }
        return resolvedPath
    }

    // --- Document URI Handlers ---

    private fun handleDocumentUri(context: Context, uri: Uri): String? = when {
        isExternalStorage(uri) -> handleExternalStorage(context, uri)
        isDownloads(uri) -> handleDownloads(context, uri)
        isMedia(uri) -> handleMedia(context, uri)
        isGoogleDrive(uri) -> copyToInternal(context, uri)
        else -> copyToInternal(context, uri)
    }

    private fun handleExternalStorage(context: Context, uri: Uri): String? {
        // Direct access is not allowed for Android 10+ so use fallback copy
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) return copyToInternal(context, uri)

        val docId = DocumentsContract.getDocumentId(uri)
        val split = docId.split(":")
        val type = split.getOrNull(0) ?: return null
        val relativePath = split.getOrNull(1) ?: return null

        val fullPath = if ("primary".equals(type, ignoreCase = true)) {
            "${Environment.getExternalStorageDirectory().absolutePath}${File.separator}$relativePath"
        } else {
            "${System.getenv("EXTERNAL_STORAGE")}${File.separator}$relativePath"
        }
        return if (File(fullPath).exists()) fullPath else copyToInternal(context, uri)
    }

    private fun handleDownloads(context: Context, uri: Uri): String? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) return copyToInternal(context, uri)

        val id = DocumentsContract.getDocumentId(uri)
        return if (id.startsWith("raw:")) {
            val rawPath = id.removePrefix("raw:")
            if (File(rawPath).exists()) rawPath else copyToInternal(context, uri)
        } else {
            val contentUri = ContentUris.withAppendedId(
                Uri.parse("content://downloads/public_downloads"), id.toLongOrNull() ?: 0
            )
            getDataColumn(context, contentUri, null, null) ?: copyToInternal(context, uri)
        }
    }

    private fun handleMedia(context: Context, uri: Uri): String? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) return copyToInternal(context, uri)
        val docId = DocumentsContract.getDocumentId(uri)
        val split = docId.split(":")
        val mediaType = split.getOrNull(0) ?: return null
        val id = split.getOrNull(1) ?: return null
        val contentUri: Uri? = when (mediaType) {
            "image" -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            "video" -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            "audio" -> MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            else -> null
        }
        return contentUri?.let { getDataColumn(context, it, "_id=?", arrayOf(id)) }
            ?: copyToInternal(context, uri)
    }

    private fun handleContentUri(context: Context, uri: Uri): String? {
        return when {
            isGooglePhotos(uri) -> uri.lastPathSegment
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> copyToInternal(context, uri)
            else -> getDataColumn(context, uri, null, null) ?: copyToInternal(context, uri)
        }
    }

    // --- Low-Level Helpers ---

    private fun getDataColumn(
        context: Context, uri: Uri, selection: String?, selectionArgs: Array<String>?,
    ): String? =
        context.contentResolver.query(uri, arrayOf("_data"), selection, selectionArgs, null)
            ?.use { cursor ->
                if (cursor.moveToFirst()) cursor.getString(0) else null
            }

    /**
     * Fallback: Copies the file pointed to by the URI into internal storage using FileChannels.
     * This approach minimizes memory overhead by transferring bytes directly between channels.
     */
    private fun copyToInternal(context: Context, uri: Uri): String? {
        val displayName = context.contentResolver.query(
            uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null
        )?.use { cursor ->
            if (cursor.moveToFirst()) cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
            else null
        } ?: return null

        val destFile = File(
            context.filesDir, "$FALLBACK_FOLDER/${UUID.randomUUID()}/$displayName"
        ).apply { parentFile?.mkdirs() }

        // Try opening a file descriptor for efficient copying using channels.
        context.contentResolver.openFileDescriptor(uri, "r")?.use { pfd ->
            FileInputStream(pfd.fileDescriptor).use { input ->
                FileOutputStream(destFile).use { output ->
                    input.channel.transferTo(0, input.channel.size(), output.channel)
                }
            }
        } ?: run {
            // Fallback to stream copying if openFileDescriptor fails.
            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(destFile).use { output ->
                    input.copyTo(output)
                }
            }
        }
        return destFile.absolutePath
    }

    // --- URI Type Checkers ---

    private fun isExternalStorage(uri: Uri): Boolean =
        "com.android.externalstorage.documents" == uri.authority

    private fun isDownloads(uri: Uri): Boolean =
        "com.android.providers.downloads.documents" == uri.authority

    private fun isMedia(uri: Uri): Boolean =
        "com.android.providers.media.documents" == uri.authority

    private fun isGooglePhotos(uri: Uri): Boolean =
        "com.google.android.apps.photos.content" == uri.authority

    private fun isGoogleDrive(uri: Uri): Boolean = uri.authority in listOf(
        "com.google.android.apps.docs.storage", "com.google.android.apps.docs.storage.legacy"
    )

    fun getFileName(contentResolver: ContentResolver, uri: Uri, callback: (String) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val fileName = when (uri.scheme) {
                ContentResolver.SCHEME_CONTENT -> queryFileName(contentResolver, uri)
                ContentResolver.SCHEME_FILE -> uri.lastPathSegment ?: "unknown_file"
                else -> uri.path?.substringAfterLast('/') ?: "unknown_file"
            }
            withContext(Dispatchers.Main) { callback(fileName) }
        }
    }

    fun getFileExtension(context: Context, uri: Uri, callback: (String) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val extension = when (uri.scheme) {
                ContentResolver.SCHEME_CONTENT -> getMimeTypeExtension(context, uri) ?: ""
                ContentResolver.SCHEME_FILE -> uri.path?.substringAfterLast('.', "") ?: ""
                else -> ""
            }
            withContext(Dispatchers.Main) { callback(extension) }
        }
    }

    private fun queryFileName(contentResolver: ContentResolver, uri: Uri): String {
        return contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)
            ?.use { cursor ->
                if (cursor.moveToFirst()) cursor.getString(0) else "unknown_file"
            } ?: "unknown_file"
    }

    private fun getMimeTypeExtension(context: Context, uri: Uri): String? {
        return context.contentResolver.getType(uri)
            ?.let { MimeTypeMap.getSingleton().getExtensionFromMimeType(it) }
    }

    private const val FALLBACK_FOLDER = "fallback_files"
}

//    suspend fun parseXML(inputStream: InputStream?, context: Context): ViewNode? =
//        withContext(Dispatchers.IO) {
//            val factory = SAXParserFactory.newInstance()
//            val parser = factory.newSAXParser()
//            val handler = XMLHandler(context.getActivityName())
//
//            parser.parse(BufferedInputStream(inputStream), handler)
//            return@withContext handler.getRoot()
//        }
//

//
//    fun convertToJson(viewNode: ViewNode?): String = Gson().toJson(viewNode)
//}
//
//class XMLHandler(private val activityName: String) : DefaultHandler2() {
//
//    private val rootStack = ArrayDeque<ViewNode>()
//    private var rootNode: ViewNode? = null
//
//    override fun startElement(
//        uri: String?, localName: String?, qName: String?, attributes: Attributes?
//    ) {
//        val node = ViewNode("UnKnown", type = qName ?: localName ?: "", activityName)
//
//        for (i in 0 until attributes!!.length) {
//            val attributeName = attributes.getQName(i)
//            val attributeValue = attributes.getValue(i)
//            if (attributeName == attributesInit.Common.ID) node.id = attributeValue
//            node.attributes[attributeName] = attributeValue
//        }
//
//        rootStack.lastOrNull()?.children?.add(node)
//        rootStack.addLast(node)
//    }
//
//    override fun endElement(uri: String?, localName: String?, qName: String?) {
//        val node = rootStack.removeLast()
//        if (rootStack.isEmpty()) rootNode = node
//    }
//
//    fun getRoot(): ViewNode? = rootNode
//}