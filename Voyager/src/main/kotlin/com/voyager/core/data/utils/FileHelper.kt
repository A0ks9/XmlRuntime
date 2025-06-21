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
import com.voyager.core.data.utils.FileHelper.isLoggingEnabled
import com.voyager.core.model.ConfigManager
import com.voyager.core.utils.logging.LoggerFactory

/**
 * `FileHelper` provides utility functions for file operations, focusing on robustly
 * resolving various Android [Uri] schemes to file system paths.
 *
 * It handles complexities arising from different Android versions, URI providers (documents, media, downloads),
 * and Scoped Storage restrictions. When direct path resolution is not possible, it falls back to
 * copying the file content to a temporary location in the app's internal storage.
 *
 * This utility addresses common file handling challenges in Android development:
 * - **URI Scheme Diversity**: Supports `content://`, `file://`, and other URI schemes.
 * - **Android Version Compatibility**: Adapts to file access changes across different Android API levels.
 * - **Content Provider Nuances**: Correctly resolves paths from various content providers like
 *   `DownloadsProvider`, `MediaProvider`, and `ExternalStorageProvider`.
 * - **Scoped Storage Handling**: Provides mechanisms to work with files under Scoped Storage,
 *   often involving copying to app-specific directories when direct path access is restricted.
 * - **Error Handling and Fallbacks**: Implements fallback strategies (e.g., copying files) when
 *   primary methods of path resolution fail.
 *
 * Key Features:
 * - **Robust URI to Path Resolution**: Converts various URI types to absolute file paths,
 *   handling numerous edge cases.
 * - **File Name and Extension Retrieval**: Extracts file names and extensions from URIs.
 * - **MIME Type Handling**: Utilizes MIME types for better file identification.
 * - **Asynchronous Operations**: Offers asynchronous methods for non-blocking file operations,
 *   typically using callbacks or coroutines.
 * - **Temporary File Management**: Manages temporary files created during operations like
 *   copying from `content://` URIs.
 * - **Logging**: Integrates with a logging framework for easier debugging of file operations.
 *
 * Usage Considerations and Best Practices:
 * 1.  **Null Checks**: Always check for `null` return values from functions like `getPath()`,
 *     as path resolution can fail for various reasons (e.g., invalid URI, missing file,
 *     permission issues).
 * 2.  **Error Handling**: Implement proper error handling (e.g., try-catch blocks) around
 *     file operations that might throw `IOException` or other exceptions.
 */
internal object FileHelper {

    private val isLoggingEnabled by lazy { ConfigManager.config.isLoggingEnabled }
    private val logger by lazy { LoggerFactory.getLogger("FileHelper") }

    /**
     * Retrieves the file extension for a given [Uri].
     *
     * This function operates as follows:
     * - For `content://` URIs, it attempts to determine the MIME type using [ContentResolver.getType]
     *   and then maps this MIME type to a file extension using [MimeTypeMap.getExtensionFromMimeType].
     * - For `file://` URIs, it directly extracts the extension from the `uri.path` string by taking
     *   the substring after the last period ('.').
     * - For any other URI schemes, it returns an empty string, as there's no standard method
     *   to determine the file extension.
     *
     * If logging is enabled (via [isLoggingEnabled]), a debug message
     * containing the URI and the retrieved extension will be logged.
     *
     * @param context The application [Context], required to access the [ContentResolver]
     *                for `content://` URIs.
     * @param uri The [Uri] from which to extract the file extension.
     * @return The file extension as a [String] (e.g., "jpg", "pdf"). Returns an empty string
     *         if the extension cannot be determined or if the URI scheme is not supported.
     *         For `file://` URIs without an extension in the path, it also returns an empty string.
     */
    fun getFileExtension(context: Context, uri: Uri): String {
        var extension = when (uri.scheme) {
            ContentResolver.SCHEME_CONTENT -> getMimeTypeExtension(context, uri) ?: ""
            ContentResolver.SCHEME_FILE -> uri.path?.substringAfterLast('.', "") ?: ""
            else -> "" // No standard way to get extension for other schemes
        }

        if (isLoggingEnabled) {
            logger.debug(message = "Retrieved file extension for URI ($uri): $extension")
        }

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