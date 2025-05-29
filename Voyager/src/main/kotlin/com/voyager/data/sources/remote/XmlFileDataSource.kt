package com.voyager.data.sources.remote

import android.content.ContentResolver
import android.net.Uri
import com.voyager.utils.FileHelper
// Removed specific import for parseXML as it's called on FileHelper directly or via class instance.
// If FileHelper.parseXML is a static/object method, FileHelper.parseXML is fine.
// If parseXML is a member of a FileHelper class instance, then an instance would be needed.
// Assuming FileHelper.parseXML and FileHelper.getFileName are accessible as shown.
import java.io.InputStream

/**
 * Data source responsible for operations related to XML files.
 *
 * This class handles tasks such as parsing XML content from an [InputStream] (typically converting it to JSON)
 * and retrieving file metadata, like display names from content URIs.
 * It primarily delegates these responsibilities to the [FileHelper] utility class.
 */
class XmlFileDataSource {

    /**
     * Converts XML data from an [InputStream] into a JSON string.
     *
     * This method delegates the parsing and conversion logic to [FileHelper.parseXML].
     *
     * @param inputStream The [InputStream] containing the XML data to be converted.
     * @return A [String] containing the JSON representation of the XML if parsing is successful,
     *         or `null` if parsing fails or the input stream is invalid.
     */
    fun convertXml(inputStream: InputStream): String? = FileHelper.parseXML(inputStream)

    /**
     * Retrieves the display name of a file from a given content [Uri] using a [ContentResolver].
     *
     * This method delegates the file name retrieval logic to [FileHelper.getFileName].
     * The result (the file name) is provided asynchronously via a callback.
     *
     * @param contentResolver The [ContentResolver] instance used to query the content URI.
     * @param uri The content [Uri] from which to retrieve the file name.
     * @param callback A lambda function `(String) -> Unit` that will be invoked with the
     *                 retrieved file name as a [String]. If the file name cannot be determined,
     *                 the behavior of the callback (e.g., whether it's called with null or not called)
     *                 depends on the implementation within [FileHelper.getFileName].
     */
    fun getFileNameFromUri(contentResolver: ContentResolver, uri: Uri, callback: (String) -> Unit) {
        FileHelper.getFileName(contentResolver, uri, callback)
    }
}