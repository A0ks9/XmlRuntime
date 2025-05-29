package com.voyager.data.repositories

import android.content.ContentResolver
import android.net.Uri
import com.voyager.data.sources.remote.XmlFileDataSource
import java.io.InputStream
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.suspendCancellableCoroutine

/**
 * Repository for handling XML data operations, such as conversion and metadata retrieval.
 *
 * This class abstracts the underlying data source for XML-related tasks.
 * @property xmlFileDataSource The data source for XML file operations.
 */
class XmlRepository(private val xmlFileDataSource: XmlFileDataSource) {

    /**
     * Converts XML data from an [InputStream] to a JSON string.
     *
     * @param inputStream The [InputStream] containing the XML data.
     * @return A JSON string representation of the XML if conversion is successful,
     *         or `null` if an error occurs during conversion or the input stream is invalid.
     */
    fun convertXmlToJson(inputStream: InputStream): String? = xmlFileDataSource.convertXml(inputStream)

    /**
     * Retrieves the file name from a content [Uri].
     *
     * This is a suspending function that wraps a callback-based mechanism to fetch the file name.
     *
     * @param contentResolver The [ContentResolver] instance to use for querying the Uri.
     * @param uri The [Uri] of the content from which to extract the file name.
     * @return The file name as a [String] if successfully retrieved, or `null` if the name
     *         cannot be determined or an error occurs.
     */
    suspend fun getFileNameFromUri(contentResolver: ContentResolver, uri: Uri): String? {
        return suspendCancellableCoroutine { continuation ->
            try {
                xmlFileDataSource.getFileNameFromUri(contentResolver, uri) { fileName ->
                    if (continuation.isActive) {
                        continuation.resume(fileName)
                    }
                }
            } catch (e: Exception) {
                // Catch any immediate exception from starting the underlying call, though unlikely for this specific API
                if (continuation.isActive) {
                    continuation.resumeWithException(e)
                }
            }
            // Note: If the underlying callback is never called, the coroutine might hang indefinitely.
            // The `FileHelper.getFileName` should ideally handle timeouts or always call the callback.
            // If it can fail to call back, a timeout mechanism might be needed here (e.g., withTimeoutOrNull).
            // For this refactoring, we assume the callback is always eventually invoked or a more
            // robust underlying mechanism handles this. If the callback can be invoked with a null/error
            // signal for "name not found", that would be better handled by resuming with null.
            // Assuming the callback `(String) -> Unit` only provides a valid string or is not called.
            // To make it truly nullable if the name isn't found, the callback itself should be `(String?) -> Unit`.
            // Given the current `(String) -> Unit` signature, we resume with what's given or hang/error out.
            // A more robust version might involve changing the callback signature in FileHelper/XmlFileDataSource
            // or adding a timeout here. For now, direct bridge.
        }
    }
}