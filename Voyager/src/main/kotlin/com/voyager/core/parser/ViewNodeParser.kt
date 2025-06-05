package com.voyager.core.parser

import com.voyager.core.exceptions.VoyagerParsingException
import com.voyager.core.model.ViewNode
import com.voyager.core.utils.logging.LoggerFactory
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

/**
 * Parses JSON strings into [ViewNode] hierarchies.
 * Provides efficient and robust JSON parsing with validation and error handling.
 *
 * Features:
 * - JSON to ViewNode conversion
 * - Input validation
 * - Error handling
 * - Performance optimization
 * - Detailed logging
 * - Default value handling
 *
 * Example Usage:
 * ```kotlin
 * // Parse a JSON string into a ViewNode
 * val jsonString = """
 * {
 *     "type": "LinearLayout",
 *     "activityName": "MainActivity",
 *     "attributes": {
 *         "orientation": "vertical"
 *     },
 *     "children": []
 * }
 * """.trimIndent()
 *
 * val viewNode = ViewNodeParser.fromJson(jsonString)
 * ```
 *
 * @throws VoyagerParsingException.JsonConversionException if JSON parsing fails
 * @throws VoyagerParsingException.InvalidXmlException if the parsed ViewNode is invalid
 */
object ViewNodeParser {
    private val logger = LoggerFactory.getLogger(ViewNodeParser::class.java.simpleName)

    /**
     * Configured JSON parser with lenient settings for better compatibility.
     * - Ignores unknown keys to handle future schema changes
     * - Uses lenient parsing to handle minor JSON formatting issues
     * - Coerces input values to handle type mismatches
     */
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
        encodeDefaults = true
        allowStructuredMapKeys = true
        prettyPrint = false
    }

    /**
     * Parses a JSON string into a [ViewNode] hierarchy.
     * Handles null/blank input, validates the parsed node, and ensures required fields.
     *
     * @param jsonString The JSON string to parse
     * @return The parsed ViewNode, or null if the input is null/blank
     * @throws VoyagerParsingException.JsonConversionException if JSON parsing fails
     * @throws VoyagerParsingException.InvalidXmlException if the parsed ViewNode is invalid
     */
    fun fromJson(jsonString: String?): ViewNode? {
        if (jsonString.isNullOrBlank()) {
            logger.debug("fromJson", "Input JSON string is null or blank. Returning null ViewNode.")
            return null
        }

        return try {
            logger.debug("fromJson", "Parsing JSON string into ViewNode")
            val node = json.decodeFromString<ViewNode>(jsonString)
            validateAndNormalizeNode(node)
        } catch (e: SerializationException) {
            val error = "Failed to deserialize ViewNode: ${e.localizedMessage}"
            logger.error("fromJson", error)
            throw VoyagerParsingException.JsonConversionException(error, e)
        } catch (e: Exception) {
            val error = "Unexpected error parsing ViewNode: ${e.localizedMessage}"
            logger.error("fromJson", error)
            throw VoyagerParsingException.JsonConversionException(error, e)
        }
    }

    /**
     * Validates and normalizes a parsed ViewNode.
     * Ensures required fields are present and have valid values.
     *
     * @param node The ViewNode to validate and normalize
     * @return The normalized ViewNode
     * @throws VoyagerParsingException.InvalidXmlException if the ViewNode is invalid
     */
    private fun validateAndNormalizeNode(node: ViewNode): ViewNode {
        // Validate type
        if (node.type.isBlank()) {
            val error = "ViewNode type cannot be blank"
            logger.error("validateAndNormalizeNode", error)
            throw VoyagerParsingException.InvalidXmlException(error)
        }

        // Normalize activityName
        val normalizedNode = if (node.activityName.isBlank()) {
            logger.warn(
                "validateAndNormalizeNode",
                "ViewNode activityName is blank. Setting default value."
            )
            node.copy(activityName = "no_activity")
        } else {
            node
        }

        // Validate attributes
        if (normalizedNode.attributes.isEmpty()) {
            logger.warn("validateAndNormalizeNode", "ViewNode has no attributes")
        }

        // Validate children recursively
        normalizedNode.children.forEach { child ->
            try {
                validateAndNormalizeNode(child)
            } catch (e: VoyagerParsingException.InvalidXmlException) {
                val error = "Invalid child node: ${e.message}"
                logger.error("validateAndNormalizeNode", error)
                throw VoyagerParsingException.InvalidXmlException(error, e)
            }
        }

        return normalizedNode
    }

    /**
     * Converts a ViewNode to a JSON string.
     * Useful for debugging and serialization.
     *
     * @param node The ViewNode to convert
     * @return The JSON string representation
     * @throws VoyagerParsingException.JsonConversionException if JSON conversion fails
     */
    fun toJson(node: ViewNode): String {
        return try {
            logger.debug("toJson", "Converting ViewNode to JSON string")
            json.encodeToString(ViewNode.serializer(), node)
        } catch (e: Exception) {
            val error = "Failed to serialize ViewNode: ${e.localizedMessage}"
            logger.error("toJson", error)
            throw VoyagerParsingException.JsonConversionException(error, e)
        }
    }
} 