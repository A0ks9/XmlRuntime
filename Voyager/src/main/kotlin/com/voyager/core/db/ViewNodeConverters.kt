package com.voyager.core.db

import androidx.collection.ArrayMap
import androidx.room.TypeConverter
import com.voyager.core.model.ViewNode
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json

object ViewNodeConverters {
    private val json = Json { ignoreUnknownKeys = true }
    @TypeConverter
    fun fromArrayMap(map: ArrayMap<String, String>): String =
        json.encodeToString(MapSerializer(String.serializer(), String.serializer()), map)
    @TypeConverter
    fun toArrayMap(jsonStr: String): ArrayMap<String, String> =
        ArrayMap<String, String>().apply {
            putAll(json.decodeFromString(MapSerializer(String.serializer(), String.serializer()), jsonStr))
        }
    @TypeConverter
    fun fromViewNodeList(list: List<ViewNode>): String =
        json.encodeToString(ListSerializer(ViewNode.serializer()), list)
    @TypeConverter
    fun toViewNodeList(jsonStr: String): List<ViewNode> =
        json.decodeFromString(ListSerializer(ViewNode.serializer()), jsonStr)
} 