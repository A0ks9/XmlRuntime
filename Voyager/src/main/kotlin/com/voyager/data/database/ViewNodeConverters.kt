package com.voyager.data.database

import androidx.collection.ArrayMap
import androidx.room.TypeConverter
import com.voyager.data.models.ViewNode
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
            MapSerializer(String.Companion.serializer(), String.Companion.serializer()), standardMap
        )
    }

    @TypeConverter
    fun toArrayMap(json: String): ArrayMap<String, String> {
        val standardMap: Map<String, String> = jsonFormat.decodeFromString(
            MapSerializer(String.Companion.serializer(), String.Companion.serializer()), json
        )
        return ArrayMap<String, String>().apply { putAll(standardMap) }
    }

    @TypeConverter
    fun fromViewNodeList(list: List<ViewNode>): String {
        return jsonFormat.encodeToString(
            ListSerializer(ViewNode.CREATOR.serializer()), list
        )
    }

    @TypeConverter
    fun toViewNodeList(json: String): List<ViewNode> {
        return jsonFormat.decodeFromString(
            ListSerializer(ViewNode.CREATOR.serializer()), json
        )
    }
}
