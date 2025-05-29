package com.voyager.data.database

import androidx.collection.ArrayMap
import androidx.room.TypeConverter
import com.voyager.data.models.ViewNode
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer // For String.serializer()
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer // For ViewNode.serializer() if not automatically resolved by plugin

/**
 * Room [TypeConverter] implementations for custom data types used within `ViewNode` entities.
 *
 * This object provides methods to convert complex types, such as [ArrayMap] and lists of [ViewNode] objects,
 * into formats (typically JSON strings) that can be stored in a Room database. It uses
 * Kotlinx Serialization for the conversion process.
 */
object ViewNodeConverters {
    /**
     * Configured JSON format for serialization and deserialization.
     * `ignoreUnknownKeys = true` allows for more resilient parsing if the data schema evolves.
     */
    private val jsonFormat = Json { ignoreUnknownKeys = true }

    /**
     * Converts an [ArrayMap] of String key-value pairs to its JSON String representation.
     *
     * @param map The [ArrayMap] to convert.
     * @return A JSON String representing the map.
     */
    @TypeConverter
    fun fromArrayMap(map: ArrayMap<String, String>): String {
        // Convert ArrayMap to a standard Map for serialization, as ArrayMap might not be directly serializable
        // by default with kotlinx.serialization without a custom serializer.
        val standardMap: Map<String, String> = map
        return jsonFormat.encodeToString(
            MapSerializer(String.serializer(), String.serializer()), standardMap
        )
    }

    /**
     * Converts a JSON String back to an [ArrayMap] of String key-value pairs.
     *
     * @param json The JSON String to convert.
     * @return An [ArrayMap] representing the deserialized JSON object.
     */
    @TypeConverter
    fun toArrayMap(json: String): ArrayMap<String, String> {
        val standardMap: Map<String, String> = jsonFormat.decodeFromString(
            MapSerializer(String.serializer(), String.serializer()), json
        )
        // Convert standard Map back to ArrayMap
        return ArrayMap<String, String>().apply { putAll(standardMap) }
    }

    /**
     * Converts a list of [ViewNode] objects to its JSON String representation.
     *
     * @param list The list of [ViewNode] objects to convert.
     * @return A JSON String representing the list.
     */
    @TypeConverter
    fun fromViewNodeList(list: List<ViewNode>): String {
        return jsonFormat.encodeToString(
            ListSerializer(ViewNode.serializer()), list // Use ViewNode.serializer()
        )
    }

    /**
     * Converts a JSON String back to a list of [ViewNode] objects.
     *
     * @param json The JSON String to convert.
     * @return A list of [ViewNode] objects.
     */
    @TypeConverter
    fun toViewNodeList(json: String): List<ViewNode> {
        return jsonFormat.decodeFromString(
            ListSerializer(ViewNode.serializer()), json // Use ViewNode.serializer()
        )
    }
}
