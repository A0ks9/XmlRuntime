package com.voyager.core.data.utils.serialization

import androidx.collection.ArrayMap
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * `ArrayMapSerializer` is a custom [KSerializer] for `androidx.collection.ArrayMap<String, String>`.
 * It leverages `kotlinx.serialization`'s [MapSerializer] for the actual serialization and
 * deserialization process, ensuring compatibility with the JSON format, while providing
 * optimized creation of [ArrayMap] instances during deserialization.
 *
 * This serializer is used by [com.voyager.data.models.ViewNode] to efficiently handle its
 * `attributes` field when serializing to/from JSON.
 *
 * Key Optimizations:
 * - **Serialization:** Delegates to the standard [MapSerializer], which is efficient.
 * - **Deserialization:**
 *   1. Deserializes the JSON into a standard [Map<String, String>].
 *   2. Creates an [ArrayMap] with the exact capacity of the deserialized map,
 *      avoiding potential reallocations and improving memory efficiency for `ArrayMap`.
 *   3. Populates the [ArrayMap] from the standard [Map].
 *
 * This approach balances standard, robust serialization logic with `ArrayMap`-specific optimizations.
 *
 * @author Abdelrahman Omar
 * @since 1.0.0
 */
object ArrayMapSerializer : KSerializer<ArrayMap<String, String>> {
    // Reuse the same descriptor for better performance
    override val descriptor: SerialDescriptor =
        MapSerializer(String.Companion.serializer(), String.serializer()).descriptor

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