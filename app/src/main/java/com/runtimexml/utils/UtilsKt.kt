package com.runtimexml.utils

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import android.webkit.MimeTypeMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import org.xml.sax.Attributes
import org.xml.sax.InputSource
import org.xml.sax.helpers.DefaultHandler
import java.io.InputStream
import javax.xml.parsers.SAXParserFactory

object UtilsKt {

    /**
     * Converts an XML file from a given URI to a JSON string.
     * @param contentResolver The content resolver to access the XML file.
     * @param uri The URI of the XML file.
     * @return The JSON string representation of the XML, or null on failure.
     */
    @JvmStatic
    fun convertXmlToJson(contentResolver: ContentResolver, uri: Uri): String? = runBlocking {
        convertXmlLayoutFromUriToJson(contentResolver, uri)?.toString(4)
    }

    fun convertXml(contentResolver: ContentResolver, uri: Uri): JSONObject? = runBlocking {
        convertXmlLayoutFromUriToJson(contentResolver, uri)
    }

    /**
     * Opens the input stream from the given URI and parses the XML to JSON.
     * It executes on the IO dispatcher to avoid blocking UI thread.
     * @param contentResolver The content resolver to access the XML file.
     * @param uri The URI of the XML file.
     * @return The JSON object of the parsed XML data, or null if an error occurs.
     */
    private suspend fun convertXmlLayoutFromUriToJson(
        contentResolver: ContentResolver, uri: Uri
    ): JSONObject? = withContext(Dispatchers.IO) {
        try {
            contentResolver.openInputStream(uri)?.use { inputStream ->
                parseXmlToJson(inputStream)
            }
        } catch (e: Exception) {
            Log.e("UtilsKt", "Error converting XML to JSON", e)
            null // Return null if an exception occurred during conversion
        }
    }

    /**
     * Parses the XML data from the input stream to a JSON object.
     * @param inputStream The input stream of the XML data.
     * @return The JSON object of the parsed XML data, or null if parsing fails.
     */
    private fun parseXmlToJson(inputStream: InputStream): JSONObject? {
        return try {
            val saxParser = SAXParserFactory.newInstance().newSAXParser()
            val handler = SaxHandler()
            saxParser.parse(InputSource(inputStream), handler)
            handler.jsonObject // Access the result directly using public property
        } catch (e: Exception) {
            Log.e("UtilsKt", "Error parsing XML", e)
            null // Return null if an exception occurred during parsing
        }
    }

    /**
     * SAX event handler to parse XML into a JSON object.
     */
    private class SaxHandler : DefaultHandler() {
        var jsonObject = JSONObject()  // public property for accessing the result
        private var elementStack =
            mutableListOf<JSONObject>() // Tracks the current nesting of JSON objects
        private var currentText = StringBuilder()  // Captures characters of the element

        /**
         * Called when a new element (start tag) is encountered.
         *
         * @param uri The Namespace URI, or the empty string if the element has no Namespace URI or if Namespace processing is not being performed.
         * @param localName The local name (without prefix), or the empty string if Namespace processing is not being performed.
         * @param qName The qualified name (with prefix), or the empty string if qualified names are not available.
         * @param attributes The attributes attached to the element.
         */
        override fun startElement(
            uri: String?, localName: String?, qName: String?, attributes: Attributes?
        ) {
            val currentElementName = localName ?: qName ?: ""
            val elementObject = JSONObject().apply {
                put("type", currentElementName) // Add the type of the view.

                attributes?.let { // Add attributes to current json object
                    put("attributes", JSONObject())
                    for (i in 0 until it.length) {
                        getJSONObject("attributes").put(it.getQName(i), it.getValue(i))
                    }
                }
                if (elementStack.isEmpty() && !uri.isNullOrEmpty()) {
                    put("xmlns", uri) //add xmlns for root element.
                }
            }
            elementStack.add(elementObject)
            currentText.clear()
        }

        /**
         * Called when character data is encountered.
         * @param ch The characters from the XML document.
         * @param start The start position in the array.
         * @param length The number of characters to read from the array.
         */
        override fun characters(ch: CharArray?, start: Int, length: Int) {
            ch?.let { currentText.append(String(it, start, length)) }
        }


        override fun endElement(uri: String?, localName: String?, qName: String?) {
            val currentJsonObject = elementStack.removeAt(elementStack.lastIndex)
            val textContent = currentText.toString().trim()
            if (textContent.isNotEmpty()) {
                currentJsonObject.put("content", textContent)  // add content to current json object
            }

            if (elementStack.isNotEmpty()) {
                val parentObject = elementStack.last()
                if (!parentObject.has("children")) {
                    parentObject.put(
                        "children", JSONArray()
                    ) // If children doesn't exist, initialize it.
                }
                parentObject.getJSONArray("children").put(currentJsonObject)
            } else {
                jsonObject = currentJsonObject // Set json object for root element
            }
        }
    }

    /**
     * Get the name of the file from the given URI.
     * @param uri The uri of the file.
     * @param contentResolver The content resolver to perform query operation.
     * @return The file name.
     */
    @JvmStatic
    fun getFileNameFromUri(uri: Uri, contentResolver: ContentResolver): String? = try {
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            cursor.takeIf { it.moveToFirst() }
                ?.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
        }
    } catch (e: Exception) {
        Log.e("UtilsKt", "Error getting file name", e)
        null // Return null if an exception occurred getting file name
    }

    @JvmStatic
    fun getFileExtension(context: Context, uri: Uri): String? {
        return when (uri.scheme) {
            ContentResolver.SCHEME_CONTENT -> {
                val mimeType = context.contentResolver.getType(uri)
                MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)
            }

            ContentResolver.SCHEME_FILE -> {
                MimeTypeMap.getFileExtensionFromUrl(uri.toString())
            }

            else -> null
        }
    }

}