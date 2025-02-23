package com.dynamic.utils

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

object FileHelper {

    /**
     * Converts an XML file from the given URI to a Proteus-compatible JSON string.
     * This runs on the UI thread but delegates parsing to the IO dispatcher.
     */
    @JvmStatic
    fun convertXmlToJson(contentResolver: ContentResolver, uri: Uri): String? = runBlocking {
        // Convert the XML to a JSON object and pretty-print it with an indent of 4 spaces.
        parseXmlToJson(contentResolver, uri)?.toString(4)
    }

    /**
     * Converts the XML file to a JSON object.
     */
    fun convertXml(contentResolver: ContentResolver, uri: Uri): JSONObject? = runBlocking {
        parseXmlToJson(contentResolver, uri)
    }

    /**
     * Opens an input stream from the URI and parses the XML to JSON on the IO dispatcher.
     * Exceptions are handled using runCatching for concise error handling.
     */
    private suspend fun parseXmlToJson(
        contentResolver: ContentResolver, uri: Uri
    ): JSONObject? = withContext(Dispatchers.IO) {
        runCatching {
            // Open the input stream and parse the XML.
            contentResolver.openInputStream(uri)?.use { parseXml(it) }
        }.getOrElse { e ->
            Log.e("UtilsKt", "Error converting XML to JSON", e)
            null
        }
    }

    /**
     * Parses the XML from the provided InputStream using a SAX parser.
     * Returns the resulting JSON object that matches the Proteus UI structure.
     * Uses runCatching to handle exceptions concisely.
     */
    private fun parseXml(inputStream: InputStream): JSONObject? = runCatching {
        // Create a new SAX parser instance.
        val parser = SAXParserFactory.newInstance().newSAXParser()
        // Create our custom handler that builds the Proteus-compatible JSON.
        val handler = ProteusHandler()
        // Parse the XML using the handler.
        parser.parse(InputSource(inputStream), handler)
        // Return the root JSON object produced by the handler.
        handler.rootObject
    }.getOrElse { e ->
        Log.e("UtilsKt", "Error parsing XML", e)
        null
    }

    /**
     * SAX event handler that builds a Proteus-compatible JSON structure.
     * Each XML element becomes a JSON object with:
     * - "type": the tag name
     * - Attributes: added directly as key/value pairs (with "android:" removed)
     * - "children": an array of nested JSON objects (if any)
     * - "content": any text content inside the element
     */
    private class ProteusHandler : DefaultHandler() {
        // Root JSON object for the parsed XML.
        var rootObject = JSONObject()

        // Stack to keep track of nested elements.
        private val stack = mutableListOf<JSONObject>()

        // Buffer to accumulate text content within an element.
        private val textBuffer = StringBuilder()

        /**
         * Called when an XML start tag is encountered.
         */
        override fun startElement(
            uri: String?, localName: String?, qName: String?, attrs: Attributes?
        ) {
            // Use localName if available; otherwise, use qName.
            val name = localName ?: qName ?: ""
            // Create a new JSON object for this element and set its "type".
            val json = JSONObject().apply {
                put("type", name)
                // Iterate over all attributes, remove "android:" prefix, and add them directly.
                attrs?.let {
                    put("attributes", JSONObject())
                    for (i in 0 until it.length) {
                        getJSONObject("attributes").put(
                            it.getLocalName(i), it.getValue(i)
                        )
                    }
                }
            }
            // If a parent element exists, add this element to its "children" array.
            if (stack.isNotEmpty()) {
                val parent = stack.last()
                if (!parent.has("children")) parent.put("children", JSONArray())
                parent.getJSONArray("children").put(json)
            }
            // Push this element onto the stack.
            stack.add(json)
            // Clear the text buffer for the new element.
            textBuffer.setLength(0)
        }

        /**
         * Called to accumulate character data within an element.
         */
        override fun characters(ch: CharArray?, start: Int, length: Int) {
            if (ch != null) textBuffer.append(String(ch, start, length))
        }

        /**
         * Called when an XML end tag is encountered.
         */
        override fun endElement(uri: String?, localName: String?, qName: String?) {
            // Pop the current element from the stack.
            val json = stack.removeAt(stack.lastIndex)
            // Trim the accumulated text and add it as "content" if it's not empty.
            val text = textBuffer.toString().trim()
            if (text.isNotEmpty()) json.put("content", text)
            // If the stack is empty, this element is the root element.
            if (stack.isEmpty()) rootObject = json
        }
    }

    /**
     * Retrieves the file name from a given URI.
     * @param uri The URI of the file.
     * @param contentResolver The ContentResolver instance to query file metadata.
     * @return The file name as a String, or null if not found.
     */
    @JvmStatic
    fun getFileNameFromUri(uri: Uri, contentResolver: ContentResolver): String? = runCatching {
        contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)
            ?.use { cursor -> if (cursor.moveToFirst()) cursor.getString(0) else null }
    }.getOrElse {
        Log.e("FileUtils", "Error getting file name", it)
        null // Return null if an error occurs
    }

    /**
     * Extracts the file extension from a given URI.
     * @param context The Context instance to access ContentResolver.
     * @param uri The URI of the file.
     * @return The file extension as a String, or null if not found.
     */
    @JvmStatic
    fun getFileExtension(context: Context, uri: Uri): String? = when (uri.scheme) {
        ContentResolver.SCHEME_CONTENT -> MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(context.contentResolver.getType(uri)) // Get extension from MIME type
        ContentResolver.SCHEME_FILE -> MimeTypeMap.getFileExtensionFromUrl(uri.toString()) // Get extension from file URL
        else -> null
    }
}