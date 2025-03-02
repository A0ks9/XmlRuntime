package com.dynamic.data.database

import androidx.collection.ArrayMap
import androidx.room.TypeConverter
import com.dynamic.data.models.ViewNode
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json

object ViewNodeConverters {
    // Configure Json; you can add other settings if needed.
    private val jsonFormat = Json { ignoreUnknownKeys = true }

    @TypeConverter
    fun fromArrayMap(map: ArrayMap<String, String>): String {
        // Convert ArrayMap to a standard Map, then serialize.
        val standardMap: Map<String, String> = map
        return jsonFormat.encodeToString(
            MapSerializer(String.serializer(), String.serializer()), standardMap
        )
    }

    @TypeConverter
    fun toArrayMap(json: String): ArrayMap<String, String> {
        val standardMap: Map<String, String> = jsonFormat.decodeFromString(
            MapSerializer(String.serializer(), String.serializer()), json
        )
        return ArrayMap<String, String>().apply { putAll(standardMap) }
    }

    @TypeConverter
    fun fromViewNodeList(list: List<ViewNode>): String {
        return jsonFormat.encodeToString(
            ListSerializer(ViewNode.serializer()), list
        )
    }

    @TypeConverter
    fun toViewNodeList(json: String): List<ViewNode> {
        return jsonFormat.decodeFromString(
            ListSerializer(ViewNode.serializer()), json
        )
    }
}
