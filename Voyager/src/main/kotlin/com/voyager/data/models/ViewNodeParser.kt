package com.voyager.data.models

import com.voyager.data.models.ViewNode // Explicit import for clarity
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.util.logging.Level
import java.util.logging.Logger

/**
 * `ViewNodeParser` is an internal singleton object responsible for parsing JSON strings
 * into [ViewNode] object hierarchies.
 *
 * It utilizes `kotlinx.serialization.json.Json` for deserialization, configured for leniency
 * and to ignore unknown keys, enhancing robustness against minor variations or future additions
 * to the JSON schema.
 *
 * This parser is a key component in Voyager's dynamic layout inflation process, particularly when
 * layouts are defined or transmitted as JSON.
 *
 * @see ViewNode
 * @see DynamicLayoutInflation
 */
internal object ViewNodeParser {

    private val logger by lazy { Logger.getLogger(ViewNodeParser::class.java.name) }

    /**
     * The configured [Json] instance used for deserializing [ViewNode] objects.
     *
     * Configuration:
     *  - `ignoreUnknownKeys = true`: Allows the parser to ignore JSON properties that are not
     *    defined in the [ViewNode] data class. This improves forward compatibility if new
     *    properties are added to the JSON schema that older code doesn't recognize.
     *  - `isLenient = true`: Allows for more flexible JSON syntax, such as unquoted strings
     *    or trailing commas (though relying on this is generally discouraged for stricter formats).
     *    This can help in parsing JSON from various sources that might not be perfectly strict.
     */
    private val JsonParser = Json {
        ignoreUnknownKeys = true // Robustness: Ignores properties in JSON not present in ViewNode
        isLenient = true         // Flexibility: Allows for slightly less strict JSON syntax
    }

    /**
     * Deserializes a JSON string into a [ViewNode] object.
     *
     * If the input [jsonString] is null, blank, or results in a [SerializationException]
     * (e.g., malformed JSON, incompatible schema despite lenient settings), this function
     * will return `null` and log an error.
     *
     * @param jsonString The JSON string representation of a [ViewNode] (and potentially its children).
     *                   Can be `null` or blank.
     * @return A [ViewNode] instance if parsing is successful; `null` otherwise.
     */
    fun fromJson(jsonString: String?): ViewNode? {
        if (jsonString.isNullOrBlank()) {
            logger.config("Input JSON string is null or blank. Returning null ViewNode.")
            return null
        }
        return try {
            JsonParser.decodeFromString<ViewNode>(jsonString)
        } catch (e: SerializationException) {
            logger.log(Level.SEVERE, "Failed to deserialize ViewNode from JSON string.", e)
            null
        } catch (e: Exception) { // Catch any other unexpected errors during parsing
            logger.log(Level.SEVERE, "An unexpected error occurred while parsing ViewNode from JSON.", e)
            null
        }
    }
}