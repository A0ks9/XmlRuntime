package com.dynamic.utils

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
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
import java.io.FileOutputStream
import java.util.UUID


object FileHelper {

    init {
        System.loadLibrary("xmlParser")
    }

    external fun parseXML(xmlPath: String): String?

    private const val FALLBACK_FOLDER = "upload_part"

    /**
     * Attempts to resolve the file system path for the given URI.
     * If the URI doesn't directly map to a file, it falls back
     * to copying the file into internal storage and returns that path.
     */
    @JvmStatic
    fun getPath(context: Context, uri: Uri): String? {
        return when {
            DocumentsContract.isDocumentUri(context, uri) -> when {
                isExternalStorage(uri) -> {
                    val docId = DocumentsContract.getDocumentId(uri)
                    val split = docId.split(":")
                    val fullPath = if (split[0].equals(
                            "primary",
                            ignoreCase = true
                        )
                    ) "${Environment.getExternalStorageDirectory()}${File.separator}${split[1]}"
                    else "${System.getenv("EXTERNAL_STORAGE")}${File.separator}${split[1]}"
                    if (File(fullPath).exists()) fullPath else copyToInternal(context, uri)
                }

                isDownloads(uri) -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        query(context, uri, MediaStore.MediaColumns.DISPLAY_NAME)?.let { name ->
                            val path = "${Environment.getExternalStorageDirectory()}/Download/$name"
                            if (path.isNotEmpty()) path else null
                        } ?: run {
                            val id = DocumentsContract.getDocumentId(uri)
                            if (id.startsWith("raw:")) id.removePrefix("raw:")
                            else getDataColumn(
                                context,
                                Uri.parse("content://downloads/public_downloads"),
                                "_id=?",
                                arrayOf(id)
                            )
                        }
                    } else {
                        val id = DocumentsContract.getDocumentId(uri)
                        if (id.startsWith("raw:")) id.removePrefix("raw:")
                        else {
                            val contentUri = ContentUris.withAppendedId(
                                Uri.parse("content://downloads/public_downloads"),
                                id.toLongOrNull() ?: 0
                            )
                            getDataColumn(context, contentUri, null, null)
                        }
                    }
                }

                isMedia(uri) -> {
                    val docId = DocumentsContract.getDocumentId(uri)
                    val split = docId.split(":")
                    val contentUri = when (split.firstOrNull()) {
                        "image" -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                        "video" -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                        "audio" -> MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                        else -> null
                    }
                    contentUri?.let { getDataColumn(context, it, "_id=?", arrayOf(split[1])) }
                }

                isGoogleDrive(uri) -> getDrivePath(context, uri)
                else -> copyToInternal(context, uri)
            }

            uri.scheme.equals("content", ignoreCase = true) -> when {
                isGooglePhotos(uri) -> uri.lastPathSegment
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> copyToInternal(context, uri)
                else -> getDataColumn(context, uri, null, null)
            }

            uri.scheme.equals("file", ignoreCase = true) -> uri.path
            else -> copyToInternal(context, uri)
        }
    }

    // Extension function: safely gets a column's string value.
    private fun Cursor.getSafeString(columnName: String): String? {
        val index = getColumnIndex(columnName)
        return if (index >= 0) getString(index) else null
    }

    // Helper: Query for a single column value.
    private fun query(context: Context, uri: Uri, column: String): String? =
        context.contentResolver.query(uri, arrayOf(column), null, null, null)
            ?.use { if (it.moveToFirst()) it.getSafeString(column) else null }

    // Helper: Get data column value ("_data") from the given URI.
    private fun getDataColumn(
        context: Context, uri: Uri, selection: String?, selectionArgs: Array<String>?
    ): String? =
        context.contentResolver.query(uri, arrayOf("_data"), selection, selectionArgs, null)
            ?.use { if (it.moveToFirst()) it.getSafeString("_data") else null }

    // Helper: Get file path for Google Drive URIs by copying them to cache.
    private fun getDrivePath(context: Context, uri: Uri): String? =
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val name = cursor.getSafeString(OpenableColumns.DISPLAY_NAME)
            name?.let {
                val file = File(context.cacheDir, it)
                context.contentResolver.openInputStream(uri)?.use { input ->
                    FileOutputStream(file).use { output -> input.copyTo(output) }
                }
                file.path
            }
        }

    // Fallback: Copy the file to internal storage and return the new path.
    private fun copyToInternal(context: Context, uri: Uri): String? =
        context.contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)
            ?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val name = cursor.getSafeString(OpenableColumns.DISPLAY_NAME)
                    name?.let {
                        val file = File(
                            context.filesDir,
                            "$FALLBACK_FOLDER/${UUID.randomUUID()}/$it"
                        ).apply { parentFile?.mkdirs() }
                        context.contentResolver.openInputStream(uri)?.use { input ->
                            FileOutputStream(file).use { output -> input.copyTo(output) }
                        }
                        file.path
                    }
                } else null
            }

    // URI type checkers.
    private fun isExternalStorage(uri: Uri): Boolean =
        "com.android.externalstorage.documents" == uri.authority

    private fun isDownloads(uri: Uri): Boolean =
        "com.android.providers.downloads.documents" == uri.authority

    private fun isMedia(uri: Uri): Boolean =
        "com.android.providers.media.documents" == uri.authority

    private fun isGooglePhotos(uri: Uri): Boolean =
        "com.google.android.apps.photos.content" == uri.authority

    private fun isGoogleDrive(uri: Uri): Boolean = uri.authority in listOf(
        "com.google.android.apps.docs.storage",
        "com.google.android.apps.docs.storage.legacy"
    )

    @JvmStatic
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
            callback(extension)
        }
    }

    private fun queryFileName(contentResolver: ContentResolver, uri: Uri): String {
        contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)
            ?.use { cursor ->
                return if (cursor.moveToFirst()) cursor.getString(0) else "unknown_file"
            }
        return "unknown_file"
    }

    private fun getMimeTypeExtension(context: Context, uri: Uri): String? {
        return context.contentResolver.getType(uri)
            ?.let { MimeTypeMap.getSingleton().getExtensionFromMimeType(it) }
    }
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