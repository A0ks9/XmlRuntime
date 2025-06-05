package com.voyager.core.db

import androidx.collection.ArrayMap
import androidx.room.TypeConverter
import com.voyager.core.model.ViewNode
import com.voyager.core.model.ConfigManager
import com.voyager.core.utils.logging.LoggerFactory
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json

/**
 * Type converters for Room database to handle complex data types.
 * This object provides conversion methods between database types and Kotlin types
 * using kotlinx.serialization.
 *
 * Features:
 * - ArrayMap serialization
 * - ViewNode list serialization
 * - Error handling
 * - Logging support
 * - Thread-safe operations
 *
 * @author Abdelrahman Omar
 * @since 1.0.0
 */
object ViewNodeConverters {
    private val logger = LoggerFactory.getLogger(ViewNodeConverters::class.java.simpleName)
    private val config = ConfigManager.config

    /** JSON instance with configuration for serialization */
    private val json = Json { 
        ignoreUnknownKeys = true 
        isLenient = true
        encodeDefaults = true
    }

    /**
     * Converts an ArrayMap to a JSON string for database storage.
     *
     * @param map The ArrayMap to convert
     * @return JSON string representation of the map
     * @throws Exception if serialization fails
     */
    @TypeConverter
    fun fromArrayMap(map: ArrayMap<String, String>): String {
        return try {
            val result = json.encodeToString(
                MapSerializer(String.serializer(), String.serializer()),
                map
            )
            if (config.isLoggingEnabled) {
                logger.debug("fromArrayMap", "Successfully serialized map with ${map.size} entries")
            }
            result
        } catch (e: Exception) {
            if (config.isLoggingEnabled) {
                logger.error("fromArrayMap", "Failed to serialize map", e)
            }
            throw e
        }
    }

    /**
     * Converts a JSON string back to an ArrayMap.
     *
     * @param jsonStr The JSON string to convert
     * @return ArrayMap reconstructed from the JSON string
     * @throws Exception if deserialization fails
     */
    @TypeConverter
    fun toArrayMap(jsonStr: String): ArrayMap<String, String> {
        return try {
            val map = json.decodeFromString(
                MapSerializer(String.serializer(), String.serializer()),
                jsonStr
            )
            ArrayMap<String, String>().apply {
                putAll(map)
            }.also {
                if (config.isLoggingEnabled) {
                    logger.debug("toArrayMap", "Successfully deserialized map with ${it.size} entries")
                }
            }
        } catch (e: Exception) {
            if (config.isLoggingEnabled) {
                logger.error("toArrayMap", "Failed to deserialize map", e)
            }
            throw e
        }
    }

    /**
     * Converts a list of ViewNodes to a JSON string for database storage.
     *
     * @param list The list of ViewNodes to convert
     * @return JSON string representation of the list
     * @throws Exception if serialization fails
     */
    @TypeConverter
    fun fromViewNodeList(list: List<ViewNode>): String {
        return try {
            val result = json.encodeToString(
                ListSerializer(ViewNode.serializer()),
                list
            )
            if (config.isLoggingEnabled) {
                logger.debug("fromViewNodeList", "Successfully serialized list with ${list.size} nodes")
            }
            result
        } catch (e: Exception) {
            if (config.isLoggingEnabled) {
                logger.error("fromViewNodeList", "Failed to serialize list", e)
            }
            throw e
        }
    }

    /**
     * Converts a JSON string back to a list of ViewNodes.
     *
     * @param jsonStr The JSON string to convert
     * @return List of ViewNodes reconstructed from the JSON string
     * @throws Exception if deserialization fails
     */
    @TypeConverter
    fun toViewNodeList(jsonStr: String): List<ViewNode> {
        return try {
            json.decodeFromString(
                ListSerializer(ViewNode.serializer()),
                jsonStr
            ).also {
                if (config.isLoggingEnabled) {
                    logger.debug("toViewNodeList", "Successfully deserialized list with ${it.size} nodes")
                }
            }
        } catch (e: Exception) {
            if (config.isLoggingEnabled) {
                logger.error("toViewNodeList", "Failed to deserialize list", e)
            }
            throw e
        }
    }
} 