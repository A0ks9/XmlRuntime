package com.voyager.utils

import androidx.collection.ArrayMap
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object ArrayMapSerializer : KSerializer<ArrayMap<String, String>> {
    override val descriptor: SerialDescriptor =
        MapSerializer(String.serializer(), String.serializer()).descriptor

    override fun serialize(encoder: Encoder, value: ArrayMap<String, String>) {
        // Convert ArrayMap to Map and then serialize
        val map: Map<String, String> = value
        MapSerializer(String.serializer(), String.serializer()).serialize(encoder, map)
    }

    override fun deserialize(decoder: Decoder): ArrayMap<String, String> {
        val map = MapSerializer(String.serializer(), String.serializer()).deserialize(decoder)
        val arrayMap = ArrayMap<String, String>(map.size)
        for ((key, value) in map) {
            arrayMap[key] = value
        }
        return arrayMap
    }
}
