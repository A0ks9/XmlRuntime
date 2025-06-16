package com.voyager.core.data.utils

import androidx.collection.ArrayMap

/**
 * Represents a token in the XML parsing process.
 * This allows streaming parsed XML data from native code to Kotlin without
 * building a complete JSON string.
 */
sealed class XmlToken {
    /**
     * Start of an XML element
     * @param type The element type (e.g., "LinearLayout", "TextView")
     * @param attributes Map of attribute names to values
     */
    data class StartElement(
        val type: String,
        val attributes: ArrayMap<String, String>
    ) : XmlToken()

    /**
     * End of an XML element
     * @param type The element type being closed
     */
    data class EndElement(
        val type: String
    ) : XmlToken()

    /**
     * Text content within an element
     * @param text The text content
     */
    data class Text(
        val text: String
    ) : XmlToken()

    /**
     * End of document marker
     */
    object EndDocument : XmlToken()
} 