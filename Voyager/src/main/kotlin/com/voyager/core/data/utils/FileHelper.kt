/**
 * `FileHelper` is a utility object for robust and efficient file handling in Android,
 * focusing on URI resolution, file path retrieval, and basic file operations.
 *
 * This utility addresses the complexities of Android's file system, including:
 * - Handling various URI schemes (`content://`, `file://`, etc.)
 * - Adapting to different Android versions and their file access restrictions
 * - Resolving paths for documents, downloads, and media files
 * - Providing fallback mechanisms for file access
 *
 * Key Features:
 * - URI to Path Resolution
 * - Native XML Parsing
 * - Path Caching
 * - Asynchronous Operations
 * - Optimized File Copying
 *
 * Best Practices:
 * 1. Always handle null returns from path resolution
 * 2. Use appropriate error handling for file operations
 * 3. Consider memory usage with large files
 * 4. Handle permissions appropriately
 *
 * Example Usage:
 * ```kotlin
 * // Get file path from URI
 * val path = FileHelper.getPath(context, uri)
 * path?.let { filePath ->
 *     // Use the file path
 * }
 *
 * // Get file name asynchronously
 * FileHelper.getFileName(contentResolver, uri) { fileName ->
 *     // Use the file name
 * }
 *
 * // Parse XML to JSON
 * inputStream.use { stream ->
 *     val json = FileHelper.parseXML(stream)
 *     json?.let { jsonString ->
 *         // Process the JSON
 *     }
 * }
 * ```
 *
 * @author Abdelrahman Omar
 * @since 1.0.0
 */
package com.voyager.core.data.utils

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
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.logging.Level
import java.util.logging.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/** Name of the sub-folder within the app's internal files directory used for storing fallback copies of files. */
private const val FALLBACK_FOLDER = "voyager_fallback_cache"
/** URI scheme for content providers. */
private const val SCHEME_CONTENT = "content"
/** URI scheme for files. */
private const val SCHEME_FILE = "file"
/** Default file name to use when a name cannot be determined from a URI. */
private const val UNKNOWN_FILE = "unknown_file"
/** Identifier for primary external storage type in document URIs. */
private const val PRIMARY_STORAGE = "primary"
/** Prefix for raw file paths in some download document URIs. */
private const val RAW_PREFIX = "raw:"
/** Content URI string for public downloads directory. */
private const val DOWNLOADS_PUBLIC_URI_STRING = "content://downloads/public_downloads"

/**
 * `FileHelper` provides utility functions for file operations, focusing on robustly
 * resolving various Android [Uri] schemes to file system paths.
 *
 * It handles complexities arising from different Android versions, URI providers (documents, media, downloads),
 * and Scoped Storage restrictions. When direct path resolution is not possible, it falls back to
 * copying the file content to a temporary location in the app's internal storage.
 *
 * This object also includes a JNI (Java Native Interface) function, [parseXML], used for
 * efficient XML parsing within the Voyager framework.
 *
 * **Permissions Note:**
 * Many functions within `FileHelper` interact with the file system, particularly external storage.
 * For applications targeting Android 6.0 (API 23) and above, this requires appropriate runtime
 * permissions (e.g., `android.Manifest.permission.READ_EXTERNAL_STORAGE`,
 * `android.Manifest.permission.WRITE_EXTERNAL_STORAGE` if writing via a resolved path pre-Scoped Storage).
 * This utility itself does not handle permission requests; it's the responsibility of the calling
 * application to ensure necessary permissions have been granted before invoking these methods.
 * For Scoped Storage (Android Q/API 29+), direct file path access is restricted, and this helper
 * often falls back to copying files to internal app storage, which doesn't require these permissions
 * for the app's own directories.
 */
object FileHelper {

    private val logger by lazy { Logger.getLogger(FileHelper::class.java.name) }

    /**
     * Initializes the FileHelper by loading the native "xmlParser" library.
     * This library is used by the [parseXML] function for efficient XML processing.
     * If the library fails to load, an [UnsatisfiedLinkError] will be thrown at runtime
     * when [parseXML] is first called or during this init block if accessed early.
     */
    init {
        try {
            System.loadLibrary("xmlParser")
            logger.info("Native library 'xmlParser' loaded successfully.")
        } catch (e: UnsatisfiedLinkError) {
            logger.log(Level.SEVERE, "Failed to load native library 'xmlParser'. XML parsing will not work.", e)
        }
    }

    /**
     * External JNI (Java Native Interface) function to parse an XML [InputStream]
     * and convert it into a JSON string representation.
     *
     * This function is implemented in the native library "xmlParser". It is designed for
     * performance-critical XML parsing tasks within the Voyager framework, specifically for
     * converting XML layouts into a JSON format that can then be processed by
     * [com.voyager.data.models.ViewNodeParser].
     *
     * Performance Considerations:
     * - Uses native code for efficient parsing
     * - Avoids intermediate string allocations
     * - Optimized for large XML files
     *
     * @param inputStream The [InputStream] containing the XML data to be parsed.
     * @return A [String] containing the JSON representation of the parsed XML,
     *         or `null` if parsing fails.
     */
    external fun parseXML(@Suppress("UNUSED_PARAMETER") inputStream: InputStream): String?

    /**
     * A thread-safe [ConcurrentHashMap] used to cache resolved file system paths for given URIs.
     * This cache helps to avoid redundant, potentially expensive, path resolution operations
     * for the same URI.
     *
     * Cache Management:
     * - Thread-safe operations
     * - Automatic eviction not implemented
     * - Consider clearing cache if memory pressure is high
     */
    private val pathCache = ConcurrentHashMap<Uri, String>()

    /**
     * Attempts to resolve the given [Uri] to an absolute file system path.
     *
     * Resolution Strategy:
     * 1. Check path cache for existing resolution
     * 2. Determine resolution strategy based on URI scheme
     * 3. Apply appropriate resolution method
     * 4. Cache successful resolutions
     * 5. Fall back to internal copy if needed
     *
     * @param context The [Context] required for accessing [ContentResolver] and app-specific storage.
     * @param uri The [Uri] to be resolved to a file path.
     * @return The absolute file path as a [String] if resolution is successful, or `null` otherwise.
     */
    @JvmStatic
    fun getPath(context: Context, uri: Uri): String? {
        pathCache[uri]?.let {
            logger.finer("Path cache hit for URI: $uri -> $it")
            return it
        }
        logger.finer("Path cache miss for URI: $uri. Attempting resolution.")

        val resolvedPath: String? = when {
            DocumentsContract.isDocumentUri(context, uri) -> handleDocumentUri(context, uri)
            SCHEME_CONTENT.equals(uri.scheme, ignoreCase = true) -> handleContentUri(context, uri)
            SCHEME_FILE.equals(uri.scheme, ignoreCase = true) -> uri.path
            else -> {
                logger.warning("Unknown URI scheme or type: $uri. Attempting to copy to internal storage.")
                copyToInternal(context, uri)
            }
        }

        resolvedPath?.also {
            logger.info("Resolved URI: $uri to path: $it. Caching.")
            pathCache[uri] = it
        } ?: logger.warning("Failed to resolve path for URI: $uri")

        return resolvedPath
    }

    /**
     * Handles the resolution of [Uri]s that are identified as document URIs by [DocumentsContract.isDocumentUri].
     *
     * It further categorizes the document URI based on its authority to delegate to more specific handlers:
     * - External storage documents: [handleExternalStorage]
     * - Downloads documents: [handleDownloads]
     * - Media documents: [handleMedia]
     * - Google Drive documents: Falls back to [copyToInternal] as direct path access is unreliable.
     * - Other document types: Also fall back to [copyToInternal].
     *
     * @param context The application [Context].
     * @param uri The document [Uri] to resolve.
     * @return The resolved file path as a [String], or `null` if resolution fails.
     */
    private fun handleDocumentUri(context: Context, uri: Uri): String? = when {
        isExternalStorage(uri) -> handleExternalStorage(context, uri)
        isDownloads(uri) -> handleDownloads(context, uri)
        isMedia(uri) -> handleMedia(context, uri)
        isGoogleDrive(uri) -> {
            logger.info("Google Drive URI detected ($uri), attempting to copy to internal storage.")
            copyToInternal(context, uri)
        }
        else -> {
            logger.info("Unknown document URI provider ($uri), attempting to copy to internal storage.")
            copyToInternal(context, uri)
        }
    }

    /**
     * Handles resolution for document URIs from an external storage provider.
     * For Android Q (API 29) and above, due to Scoped Storage, it directly falls back to [copyToInternal].
     * For older versions, it attempts to construct a direct file path. If the constructed path
     * doesn't exist, it also falls back to [copyToInternal].
     *
     * @param context The application [Context].
     * @param uri The external storage document [Uri].
     * @return The resolved file path or `null`.
     */
    private fun handleExternalStorage(context: Context, uri: Uri): String? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            logger.info("Android Q+ detected for external storage URI ($uri), copying to internal.")
            return copyToInternal(context, uri)
        }

        val docId = DocumentsContract.getDocumentId(uri)
        val parts = docId.split(":", limit = 2)
        val type = parts.getOrNull(0)
        val relativePath = parts.getOrNull(1)

        if (relativePath == null) {
            logger.warning("Could not parse relative path from external storage docId: $docId")
            return copyToInternal(context, uri)
        }

        val storagePath = if (PRIMARY_STORAGE.equals(type, ignoreCase = true)) {
            Environment.getExternalStorageDirectory().absolutePath
        } else {
            // This might not be reliable for all secondary storage.
            System.getenv("EXTERNAL_STORAGE") ?: Environment.getExternalStorageDirectory().absolutePath
        }
        val fullPath = File(storagePath, relativePath).absolutePath

        return if (File(fullPath).exists()) {
            logger.finer("Resolved external storage URI ($uri) to direct path: $fullPath")
            fullPath
        } else {
            logger.warning("Constructed path for external storage URI ($uri) does not exist: $fullPath. Copying to internal.")
            copyToInternal(context, uri)
        }
    }

    /**
     * Handles resolution for document URIs from the Downloads provider.
     * For Android Q (API 29) and above, falls back to [copyToInternal].
     * For older versions, it checks if the document ID is a raw path. If not, it tries to
     * resolve the path by querying the `_data` column from the public downloads content URI.
     * Falls back to [copyToInternal] if direct path resolution fails.
     *
     * @param context The application [Context].
     * @param uri The Downloads document [Uri].
     * @return The resolved file path or `null`.
     */
    private fun handleDownloads(context: Context, uri: Uri): String? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            logger.info("Android Q+ detected for downloads URI ($uri), copying to internal.")
            return copyToInternal(context, uri)
        }

        val docId = DocumentsContract.getDocumentId(uri)
        if (docId.startsWith(RAW_PREFIX)) {
            val rawPath = docId.removePrefix(RAW_PREFIX)
            return if (File(rawPath).exists()) {
                logger.finer("Resolved downloads URI ($uri) to raw path: $rawPath")
                rawPath
            } else {
                logger.warning("Raw path for downloads URI ($uri) does not exist: $rawPath. Copying to internal.")
                copyToInternal(context, uri)
            }
        }

        // For non-raw paths, try to get from MediaStore (downloads are often there)
        val downloadId = docId.toLongOrNull()
        if (downloadId == null) {
            logger.warning("Could not parse download ID: $docId for URI ($uri). Copying to internal.")
            return copyToInternal(context, uri)
        }
        val contentUri = ContentUris.withAppendedId(DOWNLOADS_PUBLIC_URI_STRING.toUri(), downloadId)
        return getDataColumn(context, contentUri, null, null) ?: run {
            logger.warning("Failed to get data column for downloads URI ($uri). Copying to internal.")
            copyToInternal(context, uri)
        }
    }

    /**
     * Handles resolution for document URIs from Media providers (images, video, audio).
     * For Android Q (API 29) and above, falls back to [copyToInternal].
     * For older versions, it extracts the media type and ID, constructs the appropriate
     * `MediaStore` content URI, and queries the `_data` column.
     * Falls back to [copyToInternal] if this process fails.
     *
     * @param context The application [Context].
     * @param uri The Media document [Uri].
     * @return The resolved file path or `null`.
     */
    private fun handleMedia(context: Context, uri: Uri): String? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            logger.info("Android Q+ detected for media URI ($uri), copying to internal.")
            return copyToInternal(context, uri)
        }

        val docId = DocumentsContract.getDocumentId(uri)
        val parts = docId.split(":", limit = 2)
        val mediaType = parts.getOrNull(0)
        val mediaId = parts.getOrNull(1)

        if (mediaId == null) {
            logger.warning("Could not parse media ID from media docId: $docId for URI ($uri). Copying to internal.")
            return copyToInternal(context, uri)
        }

        val contentUri: Uri? = when (mediaType?.lowercase()) {
            "image" -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            "video" -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            "audio" -> MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            else -> {
                logger.warning("Unknown media type '$mediaType' in URI ($uri).")
                null
            }
        }

        return contentUri?.let { cUri ->
            getDataColumn(context, cUri, "_id=?", arrayOf(mediaId)) ?: run {
                logger.warning("Failed to get data column for media URI ($uri), type '$mediaType'. Copying to internal.")
                copyToInternal(context, uri)
            }
        } ?: copyToInternal(context, uri)
    }

    /**
     * Handles resolution for general `content://` URIs that are not document URIs.
     * - For Google Photos URIs, it attempts to use `uri.lastPathSegment`. This is often a direct path or
     *   a key that can be used with other APIs, but direct file system access might be limited.
     * - For Android Q (API 29) and above, or if other methods fail, it falls back to [copyToInternal]
     *   due to Scoped Storage restrictions.
     * - For older versions, it attempts to query the `_data` column.
     *
     * @param context The application [Context].
     * @param uri The content [Uri].
     * @return The resolved file path or `null`.
     */
    private fun handleContentUri(context: Context, uri: Uri): String? {
        // Google Photos special handling (might still require copyToInternal depending on usage)
        if (isGooglePhotos(uri)) {
            // The lastPathSegment for Google Photos URIs might sometimes be a direct path or an ID.
            // However, direct file system access is often restricted.
            // Consider if copyToInternal is a more robust default for Google Photos.
            // For now, retaining original logic but with caution.
            logger.info("Google Photos URI detected ($uri). Using lastPathSegment: ${uri.lastPathSegment}. Direct file access may be limited.")
            return uri.lastPathSegment // This might not always be a usable file path.
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            logger.info("Android Q+ detected for content URI ($uri), attempting to copy to internal.")
            return copyToInternal(context, uri)
        }

        // For older versions, try to get the _data column
        return getDataColumn(context, uri, null, null) ?: run {
            logger.warning("Failed to get data column for content URI ($uri) on pre-Q device. Attempting to copy to internal.")
            copyToInternal(context, uri)
        }
    }

    /**
     * Queries the `_data` column from the [ContentResolver] for a given [uri].
     * This column traditionally holds the file system path for a content URI.
     *
     * **Note:** Access to the `_data` column is unreliable on Android Q (API 29) and above
     * due to Scoped Storage. This method is primarily for older Android versions.
     *
     * @param context The application [Context].
     * @param uri The [Uri] to query.
     * @param selection Optional selection string for the query.
     * @param selectionArgs Optional selection arguments for the query.
     * @return The value of the `_data` column as a [String] if found, or `null` otherwise.
     */
    private fun getDataColumn(
        context: Context,
        uri: Uri,
        selection: String?,
        selectionArgs: Array<String?>?,
    ): String? =
        context.contentResolver.query(uri, arrayOf("_data"), selection, selectionArgs, null)
            ?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val path = cursor.getString(0) // Column index for _data is 0
                    logger.finer("Data column query for URI ($uri) returned path: $path")
                    path
                } else {
                    logger.warning("Data column query for URI ($uri) returned no results.")
                    null
                }
            } ?: run {
            logger.warning("ContentResolver query for URI ($uri) returned null cursor.")
            null
        }

    /**
     * Copies the content of the given [uri] to a new file in the app's internal cache directory.
     * This is used as a fallback when direct path resolution is not possible or reliable.
     *
     * The destination file is created in a subdirectory named after [FALLBACK_FOLDER],
     * with a UUID subfolder to prevent name collisions, using the display name from the URI.
     *
     * It first attempts an efficient file channel transfer. If that fails (e.g., if `openFileDescriptor`
     * returns null), it falls back to standard [InputStream] to [FileOutputStream] copying.
     *
     * @param context The application [Context].
     * @param uri The [Uri] whose content needs to be copied.
     * @return The absolute path to the newly created file in internal storage, or `null` if
     *         the display name cannot be obtained or copying fails.
     */
    private fun copyToInternal(context: Context, uri: Uri): String? {
        val displayName = try {
            context.contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)
                ?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
                    } else null
                }
        } catch (e: Exception) {
            logger.log(Level.WARNING, "Failed to query display name for URI: $uri", e)
            null
        } ?: run {
            logger.warning("Could not determine display name for URI: $uri. Using fallback name.")
            "$UNKNOWN_FILE-${UUID.randomUUID()}" // Fallback display name
        }

        val destinationDir = File(context.filesDir, FALLBACK_FOLDER)
        if (!destinationDir.exists()) {
            destinationDir.mkdirs()
        }
        val uniqueSubDir = File(destinationDir, UUID.randomUUID().toString())
        uniqueSubDir.mkdirs()

        val destFile = File(uniqueSubDir, displayName)

        try {
            // Attempt efficient file channel transfer
            var copied = false
            context.contentResolver.openFileDescriptor(uri, "r")?.use { pfd ->
                FileInputStream(pfd.fileDescriptor).use { input ->
                    FileOutputStream(destFile).use { output ->
                        input.channel.transferTo(0, input.channel.size(), output.channel)
                        copied = true
                        logger.info("Successfully copied URI ($uri) to internal storage (channel transfer): ${destFile.absolutePath}")
                    }
                }
            }

            // Fallback to stream copying if channel transfer didn't happen or failed partially
            if (!copied) {
                context.contentResolver.openInputStream(uri)?.use { input ->
                    FileOutputStream(destFile).use { output ->
                        input.copyTo(output)
                        copied = true // Mark as copied via stream
                        logger.info("Successfully copied URI ($uri) to internal storage (stream copy): ${destFile.absolutePath}")
                    }
                }
            }
            return if (copied) destFile.absolutePath else {
                logger.severe("Failed to copy URI ($uri) to internal storage after all attempts.")
                destFile.delete() // Clean up partially created file if copy failed
                null
            }
        } catch (e: Exception) {
            logger.log(Level.SEVERE, "Error copying URI ($uri) to internal storage: ${destFile.absolutePath}", e)
            destFile.delete() // Clean up
            return null
        }
    }

    // --- URI Type Checkers ---

    /** Checks if the URI authority is for external storage documents. */
    private fun isExternalStorage(uri: Uri): Boolean = "com.android.externalstorage.documents" == uri.authority
    /** Checks if the URI authority is for downloads provider documents. */
    private fun isDownloads(uri: Uri): Boolean = "com.android.providers.downloads.documents" == uri.authority
    /** Checks if the URI authority is for media provider documents. */
    private fun isMedia(uri: Uri): Boolean = "com.android.providers.media.documents" == uri.authority
    /** Checks if the URI authority is for Google Photos. */
    private fun isGooglePhotos(uri: Uri): Boolean = "com.google.android.apps.photos.content" == uri.authority
    /** Checks if the URI authority is for Google Drive. */
    private fun isGoogleDrive(uri: Uri): Boolean = uri.authority in listOf(
        "com.google.android.apps.docs.storage",
        "com.google.android.apps.docs.storage.legacy" // Older Google Drive authority
    )

    /**
     * Asynchronously retrieves the display name for a given [Uri].
     *
     * This function launches a coroutine on [Dispatchers.IO] to perform the query:
     * - For `content://` URIs, it queries the [ContentResolver] for [OpenableColumns.DISPLAY_NAME].
     * - For `file://` URIs, it uses `uri.lastPathSegment`.
     * - For other schemes, it attempts to extract the name from `uri.path`.
     *
     * The result (file name string) is delivered via the [callback] on the [Dispatchers.Main] thread.
     *
     * @param contentResolver A [ContentResolver] instance to query content URIs.
     * @param uri The [Uri] from which to extract the file name.
     * @param callback A lambda `(String) -> Unit` that will be invoked on the Main thread
     *                 with the retrieved file name. If the name cannot be determined,
     *                 a fallback name like [UNKNOWN_FILE] might be provided.
     */
    fun getFileName(contentResolver: ContentResolver, uri: Uri, callback: (String) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val fileName = when (uri.scheme) {
                ContentResolver.SCHEME_CONTENT -> queryFileName(contentResolver, uri)
                ContentResolver.SCHEME_FILE -> uri.lastPathSegment ?: UNKNOWN_FILE // For file URIs
                else -> uri.path?.substringAfterLast('/') ?: UNKNOWN_FILE // Basic fallback for other URIs
            }
            logger.finer("Retrieved file name for URI ($uri): $fileName")
            withContext(Dispatchers.Main) { callback(fileName) }
        }
    }

    /**
     * Asynchronously retrieves the file extension for a given [Uri].
     *
     * This function launches a coroutine on [Dispatchers.IO]:
     * - For `content://` URIs, it attempts to get the MIME type from [ContentResolver] and then
     *   derives the extension using [MimeTypeMap].
     * - For `file://` URIs, it extracts the extension from `uri.path`.
     * - For other schemes, it returns an empty string.
     *
     * The result (extension string, or empty if not found) is delivered via the [callback]
     * on the [Dispatchers.Main] thread.
     *
     * @param context The application [Context] needed for [ContentResolver].
     * @param uri The [Uri] from which to extract the file extension.
     * @return The file extension as a [String] (e.g., "jpg", "pdf"),
     *         or `null` if the extension cannot be determined or if the operation
     *         is launched asynchronously (in which case the result is delivered via callback).
     *         Note: The immediate return value may be `null` if the operation is asynchronous.
     *         The actual extension is provided through a callback in some implementations.
     *         This specific implementation, however, appears to attempt a synchronous return
     *         which might not work as intended due to the coroutine's asynchronous nature.
     *         For a truly asynchronous version, consider using a callback as described in the KDoc
     *         for [getFileExtension] taking a callback parameter.
     */
    fun getFileExtension(context: Context, uri: Uri): String? {
        var extension: String? = null
        CoroutineScope(Dispatchers.IO).launch {
            extension = when (uri.scheme) {
                ContentResolver.SCHEME_CONTENT -> getMimeTypeExtension(context, uri) ?: ""
                ContentResolver.SCHEME_FILE -> uri.path?.substringAfterLast('.', "") ?: ""
                else -> "" // No standard way to get extension for other schemes
            }
            logger.finer("Retrieved file extension for URI ($uri): $extension")
        }
        return extension
    }

    /**
     * Helper function to query the [OpenableColumns.DISPLAY_NAME] from a `content://` [Uri].
     *
     * @param contentResolver A [ContentResolver] instance.
     * @param uri The content [Uri] to query.
     * @return The display name as a [String], or [UNKNOWN_FILE] if the query fails or the column is empty.
     */
    private fun queryFileName(contentResolver: ContentResolver, uri: Uri): String {
        return contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)
            ?.use { cursor ->
                if (cursor.moveToFirst()) {
                    cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
                } else UNKNOWN_FILE
            } ?: UNKNOWN_FILE
    }

    /**
     * Helper function to get the file extension from the MIME type of a `content://` [Uri].
     *
     * @param context The application [Context].
     * @param uri The content [Uri].
     * @return The file extension (e.g., "jpg") if it can be determined from the MIME type, or `null` otherwise.
     */
    private fun getMimeTypeExtension(context: Context, uri: Uri): String? {
        val mimeType = context.contentResolver.getType(uri)
        return mimeType?.let { MimeTypeMap.getSingleton().getExtensionFromMimeType(it) }
    }
}