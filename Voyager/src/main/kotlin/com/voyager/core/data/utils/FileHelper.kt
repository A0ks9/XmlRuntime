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
import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import com.voyager.core.data.utils.FileHelper.parseXML
import com.voyager.core.utils.logging.LogLevel
import com.voyager.core.utils.logging.LoggerFactory
import kotlinx.coroutines.Dispatchers
import java.io.InputStream

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
internal object FileHelper {

    private val logger = LoggerFactory.getLogger(FileHelper::class.java.name)

    /**
     * Initializes the FileHelper by loading the native "xmlParser" library.
     * This library is used by the [parseXML] function for efficient XML processing.
     * If the library fails to load, an [UnsatisfiedLinkError] will be thrown at runtime
     * when [parseXML] is first called or during this init block if accessed early.
     */
    init {
        try {
            System.loadLibrary("xmlParser")
            logger.info(message = "Native library 'xmlParser' loaded successfully.")
        } catch (e: UnsatisfiedLinkError) {
            logger.log(
                LogLevel.ERROR,
                message = "Failed to load native library 'xmlParser'. XML parsing will not work., error: ${e.message}"
            )
        }
    }

    /**
     * External JNI (Java Native Interface) function to parse an XML [InputStream]
     * and stream tokens to the provided [XmlTokenStream].
     *
     * This function is implemented in the native library "xmlParser". It is designed for
     * performance-critical XML parsing tasks within the Voyager framework, specifically for
     * converting XML layouts into a stream of tokens that can be processed efficiently.
     *
     * Performance Considerations:
     * - Uses native code for efficient parsing
     * - Avoids intermediate string allocations
     * - Optimized for large XML files
     * - Calculates SHA256 hash in a single pass
     * - Streams tokens directly to Kotlin layer
     *
     * @param inputStream The [InputStream] containing the XML data to be parsed
     * @param tokenStream The [XmlTokenStream] to receive parsed tokens
     */
    external fun parseXML(
        @Suppress("UNUSED_PARAMETER") inputStream: InputStream,
        @Suppress("UNUSED_PARAMETER") tokenStream: XmlTokenStream,
    )

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
    fun getFileExtension(context: Context, uri: Uri): String {
        val extension = when (uri.scheme) {
            ContentResolver.SCHEME_CONTENT -> getMimeTypeExtension(context, uri) ?: ""
            ContentResolver.SCHEME_FILE -> uri.path?.substringAfterLast('.', "") ?: ""
            else -> "" // No standard way to get extension for other schemes
        }
        logger.debug(message = "Retrieved file extension for URI ($uri): $extension")
        return extension
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