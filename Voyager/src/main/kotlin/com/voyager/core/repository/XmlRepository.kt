package com.voyager.core.repository

import android.content.ContentResolver
import android.net.Uri
import com.voyager.core.exceptions.VoyagerParsingException
import com.voyager.core.exceptions.VoyagerResourceException
import java.io.InputStream

/**
 * Repository interface for managing XML data.
 * Provides operations for XML to JSON conversion and file name resolution.
 *
 * Features:
 * - XML to JSON conversion
 * - File name resolution
 * - Error handling
 * - Thread safety
 * - Performance optimization
 * - Memory efficiency
 *
 * Example Usage:
 * ```kotlin
 * class XmlRepositoryImpl(
 *     private val dataSource: XmlFileDataSource
 * ) : XmlRepository {
 *     override fun convertXmlToJson(inputStream: InputStream): String? =
 *         dataSource.convertXmlToJson(inputStream)
 *
 *     override fun getFileNameFromUri(
 *         contentResolver: ContentResolver,
 *         uri: Uri,
 *         callback: (String) -> Unit
 *     ) = dataSource.getFileNameFromUri(contentResolver, uri, callback)
 * }
 * ```
 *
 * @throws VoyagerParsingException.XmlParsingException if XML parsing fails
 * @throws VoyagerParsingException.JsonConversionException if JSON conversion fails
 * @throws VoyagerResourceException.ResourceNotFoundException if file not found
 * @throws VoyagerResourceException.ResourceLoadException if file loading fails
 */
interface XmlRepository {
    /**
     * Converts an XML input stream to a JSON string.
     * Handles XML parsing and JSON conversion with proper error handling.
     *
     * @param inputStream The XML input stream to convert
     * @return The converted JSON string, or null if conversion fails
     * @throws VoyagerParsingException.XmlParsingException if XML parsing fails
     * @throws VoyagerParsingException.JsonConversionException if JSON conversion fails
     */
    fun convertXmlToJson(inputStream: InputStream): String?

    /**
     * Resolves a file name from a content URI.
     * Handles different URI schemes and content providers.
     *
     * @param contentResolver The content resolver to use
     * @param uri The content URI to resolve
     * @param callback The callback to receive the file name
     * @throws VoyagerResourceException.ResourceNotFoundException if file not found
     * @throws VoyagerResourceException.ResourceLoadException if file loading fails
     */
    fun getFileNameFromUri(
        contentResolver: ContentResolver,
        uri: Uri,
        callback: (String) -> Unit,
    )
}