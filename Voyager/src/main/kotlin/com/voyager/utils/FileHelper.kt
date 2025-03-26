/**
 * Efficient file handling utility for Android.
 *
 * This utility provides optimized file operations, URI handling, and file system access,
 * with a focus on performance and memory efficiency.
 *
 * Key features:
 * - Efficient file path resolution
 * - Memory-optimized file operations
 * - Support for various URI schemes
 * - Thread-safe operations
 * - Comprehensive file handling
 *
 * Performance optimizations:
 * - Path caching
 * - Efficient file copying
 * - Minimized object creation
 * - Optimized URI handling
 * - Reduced memory allocations
 *
 * Usage example:
 * ```kotlin
 * // Get file path from URI
 * val path = FileHelper.getPath(context, uri)
 *
 * // Get file extension
 * FileHelper.getFileExtension(context, uri) { extension ->
 *     // Handle extension
 * }
 * ```
 *
 * @author Abdelrahman Omar
 * @since 1.0.0
 */
package com.voyager.utils

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
import androidx.core.net.toUri
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

// Constants for frequently used values
private const val FALLBACK_FOLDER = "fallback_files"
private const val SCHEME_CONTENT = "content"
private const val SCHEME_FILE = "file"
private const val UNKNOWN_FILE = "unknown_file"
private const val PRIMARY_STORAGE = "primary"
private const val RAW_PREFIX = "raw:"
private const val DOWNLOADS_PUBLIC = "content://downloads/public_downloads"

object FileHelper {
    init {
        System.loadLibrary("xmlParser")
    }

    external fun parseXML(@Suppress("UNUSED_PARAMETER") inputStream: InputStream): String?

    // Thread-safe cache for resolved file paths
    private val pathCache = ConcurrentHashMap<Uri, String>()

    /**
     * Resolves the file system path for a given URI.
     *
     * @param context The application context
     * @param uri The URI to resolve
     * @return The resolved file path or null if resolution fails
     */
    @JvmStatic
    fun getPath(context: Context, uri: Uri): String? {
        // Return cached path if available
        pathCache[uri]?.let { return it }

        val resolvedPath = when {
            DocumentsContract.isDocumentUri(context, uri) -> handleDocumentUri(context, uri)
            SCHEME_CONTENT.equals(uri.scheme, ignoreCase = true) -> handleContentUri(context, uri)
            SCHEME_FILE.equals(uri.scheme, ignoreCase = true) -> uri.path
            else -> copyToInternal(context, uri)
        }

        resolvedPath?.also { pathCache[uri] = it }
        return resolvedPath
    }

    /**
     * Handles document URI resolution.
     */
    private fun handleDocumentUri(context: Context, uri: Uri): String? = when {
        isExternalStorage(uri) -> handleExternalStorage(context, uri)
        isDownloads(uri) -> handleDownloads(context, uri)
        isMedia(uri) -> handleMedia(context, uri)
        isGoogleDrive(uri) -> copyToInternal(context, uri)
        else -> copyToInternal(context, uri)
    }

    /**
     * Handles external storage URI resolution.
     */
    private fun handleExternalStorage(context: Context, uri: Uri): String? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) return copyToInternal(context, uri)

        val docId = DocumentsContract.getDocumentId(uri)
        val (type, relativePath) = docId.split(":").let { it.getOrNull(0) to it.getOrNull(1) }

        val fullPath = if (PRIMARY_STORAGE.equals(type, ignoreCase = true)) {
            "${Environment.getExternalStorageDirectory().absolutePath}${File.separator}$relativePath"
        } else {
            "${System.getenv("EXTERNAL_STORAGE")}${File.separator}$relativePath"
        }

        return if (File(fullPath).exists()) fullPath else copyToInternal(context, uri)
    }

    /**
     * Handles downloads URI resolution.
     */
    private fun handleDownloads(context: Context, uri: Uri): String? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) return copyToInternal(context, uri)

        val id = DocumentsContract.getDocumentId(uri)
        return if (id.startsWith(RAW_PREFIX)) {
            val rawPath = id.removePrefix(RAW_PREFIX)
            if (File(rawPath).exists()) rawPath else copyToInternal(context, uri)
        } else {
            val contentUri = ContentUris.withAppendedId(
                DOWNLOADS_PUBLIC.toUri(), id.toLongOrNull() ?: 0
            )
            getDataColumn(context, contentUri, null, null) ?: copyToInternal(context, uri)
        }
    }

    /**
     * Handles media URI resolution.
     */
    private fun handleMedia(context: Context, uri: Uri): String? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) return copyToInternal(context, uri)

        val (mediaType, id) = DocumentsContract.getDocumentId(uri).split(":").let {
            it.getOrNull(0) to it.getOrNull(1)
        }

        val contentUri = when (mediaType) {
            "image" -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            "video" -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            "audio" -> MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            else -> null
        }

        return contentUri?.let { getDataColumn(context, it, "_id=?", arrayOf(id)) }
            ?: copyToInternal(context, uri)
    }

    /**
     * Handles content URI resolution.
     */
    private fun handleContentUri(context: Context, uri: Uri): String? {
        return when {
            isGooglePhotos(uri) -> uri.lastPathSegment
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> copyToInternal(context, uri)
            else -> getDataColumn(context, uri, null, null) ?: copyToInternal(context, uri)
        }
    }

    /**
     * Queries the data column for a given URI.
     */
    private fun getDataColumn(
        context: Context,
        uri: Uri,
        selection: String?,
        selectionArgs: Array<String?>?,
    ): String? =
        context.contentResolver.query(uri, arrayOf("_data"), selection, selectionArgs, null)
            ?.use { cursor ->
                if (cursor.moveToFirst()) cursor.getString(0) else null
            }

    /**
     * Copies a file to internal storage using efficient file channels.
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

        // Try efficient file channel transfer first
        context.contentResolver.openFileDescriptor(uri, "r")?.use { pfd ->
            FileInputStream(pfd.fileDescriptor).use { input ->
                FileOutputStream(destFile).use { output ->
                    input.channel.transferTo(0, input.channel.size(), output.channel)
                }
            }
        } ?: run {
            // Fallback to stream copying
            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(destFile).use { output ->
                    input.copyTo(output)
                }
            }
        }

        return destFile.absolutePath
    }

    // URI type checkers
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

    /**
     * Gets the file name from a URI asynchronously.
     */
    fun getFileName(contentResolver: ContentResolver, uri: Uri, callback: (String) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val fileName = when (uri.scheme) {
                ContentResolver.SCHEME_CONTENT -> queryFileName(contentResolver, uri)
                ContentResolver.SCHEME_FILE -> uri.lastPathSegment ?: UNKNOWN_FILE
                else -> uri.path?.substringAfterLast('/') ?: UNKNOWN_FILE
            }
            withContext(Dispatchers.Main) { callback(fileName) }
        }
    }

    /**
     * Gets the file extension from a URI asynchronously.
     */
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

    /**
     * Queries the file name from a content URI.
     */
    private fun queryFileName(contentResolver: ContentResolver, uri: Uri): String =
        contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)
            ?.use { cursor ->
                if (cursor.moveToFirst()) cursor.getString(0) else UNKNOWN_FILE
            } ?: UNKNOWN_FILE

    /**
     * Gets the file extension from a MIME type.
     */
    private fun getMimeTypeExtension(context: Context, uri: Uri): String? =
        context.contentResolver.getType(uri)
            ?.let { MimeTypeMap.getSingleton().getExtensionFromMimeType(it) }
}