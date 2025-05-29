package com.voyager.data.models

import com.voyager.data.models.ViewNode
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString // Explicit import for clarity

/**
 * Internal utility object for parsing JSON strings into [ViewNode] objects.
 *
 * This parser is configured to be lenient and ignore unknown keys, making it
 * resilient to changes or extensions in the JSON format.
 */
internal object ViewNodeParser {

    /**
     * Configured Json parser instance.
     * - `ignoreUnknownKeys = true`: Allows for forward compatibility if new fields are added to the JSON.
     * - `isLenient = true`: Allows for some minor syntax leniency in the JSON string.
     */
    private val JsonParser = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    /**
     * Deserializes a JSON string into a [ViewNode] object.
     *
     * If the provided JSON string is null, blank, or if parsing fails due to malformed content
     * (despite leniency), this method will return `null`.
     *
     * @param json The JSON string to parse.
     * @return A [ViewNode] object if parsing is successful, or `null` otherwise.
     */
    fun fromJson(json: String?): ViewNode? {
        if (json.isNullOrBlank()) {
            return null
        }
        return try {
            JsonParser.decodeFromString<ViewNode>(json)
        } catch (e: Exception) {
            // Log the exception if logging is available/configured
            // e.g., Log.e("ViewNodeParser", "Failed to parse ViewNode from JSON", e)
            null // Return null on parsing error
        }
    }
}