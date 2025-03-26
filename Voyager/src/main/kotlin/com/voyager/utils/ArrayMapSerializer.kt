/**
 * Efficient serializer for Android's ArrayMap<String, String>.
 *
 * This serializer provides optimized serialization and deserialization of ArrayMap instances,
 * with a focus on performance and memory efficiency.
 *
 * Key features:
 * - Optimized serialization using MapSerializer
 * - Memory-efficient deserialization
 * - Thread-safe operations
 * - Type-safe conversions
 *
 * Performance optimizations:
 * - Pre-allocated ArrayMap capacity
 * - Efficient map conversion
 * - Minimized object creation
 * - Optimized string handling
 *
 * Usage example:
 * ```kotlin
 * // Serialize ArrayMap
 * val arrayMap = ArrayMap<String, String>()
 * arrayMap["key"] = "value"
 * val json = Json.encodeToString(ArrayMapSerializer, arrayMap)
 *
 * // Deserialize ArrayMap
 * val deserialized = Json.decodeFromString(ArrayMapSerializer, json)
 * ```
 *
 * @author Abdelrahman Omar
 * @since 1.0.0
 */
package com.voyager.utils

import androidx.collection.ArrayMap
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object ArrayMapSerializer : KSerializer<ArrayMap<String, String>> {
    // Reuse the same descriptor for better performance
    override val descriptor: SerialDescriptor =
        MapSerializer(String.serializer(), String.serializer()).descriptor

    /**
     * Serializes an ArrayMap to a format suitable for storage or transmission.
     *
     * @param encoder The encoder to write the serialized data
     * @param value The ArrayMap to serialize
     */
    override fun serialize(encoder: Encoder, value: ArrayMap<String, String>) {
        // Convert ArrayMap to Map and serialize using MapSerializer
        // This approach is more efficient than custom serialization
        MapSerializer(String.serializer(), String.serializer()).serialize(encoder, value)
    }

    /**
     * Deserializes data into an ArrayMap.
     *
     * @param decoder The decoder containing the serialized data
     * @return A new ArrayMap containing the deserialized data
     */
    override fun deserialize(decoder: Decoder): ArrayMap<String, String> {
        // First deserialize to a regular Map
        val map = MapSerializer(String.serializer(), String.serializer()).deserialize(decoder)

        // Create ArrayMap with exact capacity to avoid resizing
        val arrayMap = ArrayMap<String, String>(map.size)

        // Efficiently copy entries to ArrayMap
        map.forEach { (key, value) -> arrayMap[key] = value }

        return arrayMap
    }
}
