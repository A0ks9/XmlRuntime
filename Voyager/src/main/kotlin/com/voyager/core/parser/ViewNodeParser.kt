package com.voyager.core.parser

import com.voyager.core.model.ViewNode
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.util.logging.Logger

/**
 * Parses JSON strings into [ViewNode] hierarchies.
 */
object ViewNodeParser {
    private val logger by lazy { Logger.getLogger(ViewNodeParser::class.java.name) }
    private val json = Json { ignoreUnknownKeys = true; isLenient = true }
    fun fromJson(jsonString: String?): ViewNode? =
        if (jsonString.isNullOrBlank()) {
            logger.config("Input JSON string is null or blank. Returning null ViewNode.")
            null
        } else try {
            json.decodeFromString<ViewNode>(jsonString)
        } catch (e: SerializationException) {
            logger.severe("Failed to deserialize ViewNode: ${e.localizedMessage}")
            null
        } catch (e: Exception) {
            logger.severe("Unexpected error parsing ViewNode: ${e.localizedMessage}")
            null
        }
} 