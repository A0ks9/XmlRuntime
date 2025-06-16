package com.voyager.core.db

import androidx.collection.ArrayMap
import androidx.room.TypeConverter
import com.voyager.core.model.ConfigManager
import com.voyager.core.model.ViewNode
import com.voyager.core.utils.ErrorUtils
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
    private val TAG = ViewNodeConverters::class.java.simpleName
    private val error by lazy { ErrorUtils(TAG) }
    private val logger by lazy { LoggerFactory.getLogger(TAG) }
    private val isLoggingEnabled by lazy { ConfigManager.config.isLoggingEnabled }

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
    fun fromArrayMap(map: ArrayMap<String, String>): String = error.tryOrThrow({
        val result = json.encodeToString(
            MapSerializer(String.serializer(), String.serializer()), map
        )
        if (isLoggingEnabled) {
            logger.debug("fromArrayMap", "Successfully serialized map with ${map.size} entries")
        }
        result
    }, "fromArrayMap", "Failed to serialize map")

    /**
     * Converts a JSON string back to an ArrayMap.
     *
     * @param jsonStr The JSON string to convert
     * @return ArrayMap reconstructed from the JSON string
     * @throws Exception if deserialization fails
     */
    @TypeConverter
    fun toArrayMap(jsonStr: String): ArrayMap<String, String> = error.tryOrThrow({
        val map =
            json.decodeFromString(MapSerializer(String.serializer(), String.serializer()), jsonStr)
        ArrayMap<String, String>().apply { putAll(map) }.also {
            if (isLoggingEnabled) {
                logger.debug("toArrayMap", "Successfully deserialized map with ${it.size} entries")
            }
        }
    }, "toArrayMap", "Failed to deserialize map")

    /**
     * Converts a JSON string back to a MutableList of ViewNode objects.
     * This method is used by Room to reconstruct the list from its JSON representation
     * stored in the database.
     *
     * @param jsonStr The JSON string to convert.
     * @return MutableList of ViewNode objects reconstructed from the JSON string.
     * @throws Exception if deserialization fails. This can happen if the JSON string
     *                   is malformed or does not represent a valid list of ViewNode objects.
     */
    @TypeConverter
    fun toMutableList(jsonStr: String): MutableList<ViewNode> = error.tryOrThrow({
        val list = json.decodeFromString(ListSerializer(ViewNode.serializer()), jsonStr)
        MutableList(list.size) { list[it] }.also {
            if (isLoggingEnabled) {
                logger.debug(
                    "toMutableList",
                    "Successfully deserialized list with ${it.size} entries"
                )
            }
        }
    }, "toMutableList", "Failed to deserialize list")

    /**
     * Converts a MutableList of ViewNode to a JSON string for database storage.
     *
     * @param list The MutableList of ViewNode to convert.
     * @return JSON string representation of the list.
     * @throws Exception if serialization fails.
     */
    @TypeConverter
    fun fromMutableList(list: MutableList<ViewNode>): String = error.tryOrThrow({
        val serializedList = json.encodeToString(ListSerializer(ViewNode.serializer()), list)
        if (isLoggingEnabled) {
            logger.debug(
                "fromMutableList",
                "Successfully serialized list with ${list.size} entries"
            )
        }
        serializedList
    }, "fromMutableList", "Failed to serialize list")
} 