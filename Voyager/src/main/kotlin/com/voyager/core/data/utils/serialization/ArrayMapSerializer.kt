package com.voyager.core.data.utils.serialization

import androidx.collection.ArrayMap
import com.voyager.core.model.ConfigManager
import com.voyager.core.utils.logging.LoggerFactory
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * Custom [KSerializer] for `androidx.collection.ArrayMap<String, String>` in the Voyager framework.
 * This serializer provides efficient serialization and deserialization of ArrayMap instances,
 * optimizing memory usage and performance for the ViewNode attributes.
 *
 * Key Features:
 * - Memory-efficient serialization
 * - Optimized ArrayMap creation
 * - Thread-safe operations
 * - Detailed logging
 * - Integration with kotlinx.serialization
 *
 * Example Usage:
 * ```kotlin
 * @Serializable(with = ArrayMapSerializer::class)
 * data class ViewNode(
 *     val attributes: ArrayMap<String, String>
 * )
 * ```
 *
 * @author Abdelrahman Omar
 * @since 1.0.0
 */
internal object ArrayMapSerializer : KSerializer<ArrayMap<String, String>> {
    private val logger = LoggerFactory.getLogger(ArrayMapSerializer::class.java.simpleName)
    private val config = ConfigManager.config

    /** Reuse the same descriptor for better performance */
    override val descriptor: SerialDescriptor =
        MapSerializer(String.Companion.serializer(), String.serializer()).descriptor

    /**
     * Serializes an ArrayMap to a format suitable for storage or transmission.
     * This method delegates to the standard MapSerializer for efficient serialization.
     *
     * @param encoder The encoder to write the serialized data
     * @param value The ArrayMap to serialize
     */
    override fun serialize(encoder: Encoder, value: ArrayMap<String, String>) {
        if (config.isLoggingEnabled) {
            logger.debug("serialize", "Serializing ArrayMap with ${value.size} entries")
        }
        // Convert ArrayMap to Map and serialize using MapSerializer
        // This approach is more efficient than custom serialization
        MapSerializer(String.serializer(), String.serializer()).serialize(encoder, value)
    }

    /**
     * Deserializes data into an ArrayMap.
     * This method optimizes the creation of the ArrayMap by:
     * 1. First deserializing to a regular Map
     * 2. Creating an ArrayMap with exact capacity
     * 3. Efficiently copying entries
     *
     * @param decoder The decoder containing the serialized data
     * @return A new ArrayMap containing the deserialized data
     * @throws kotlinx.serialization.SerializationException if deserialization fails
     */
    override fun deserialize(decoder: Decoder): ArrayMap<String, String> {
        if (config.isLoggingEnabled) {
            logger.debug("deserialize", "Starting ArrayMap deserialization")
        }

        try {
            // First deserialize to a regular Map
            val map = MapSerializer(String.serializer(), String.serializer()).deserialize(decoder)

            // Create ArrayMap with exact capacity to avoid resizing
            val arrayMap = ArrayMap<String, String>(map.size)

            // Efficiently copy entries to ArrayMap
            map.forEach { (key, value) -> arrayMap[key] = value }

            if (config.isLoggingEnabled) {
                logger.debug(
                    "deserialize",
                    "Successfully deserialized ArrayMap with ${arrayMap.size} entries"
                )
            }

            return arrayMap
        } catch (e: Exception) {
            if (config.isLoggingEnabled) {
                logger.error("deserialize", "Error deserializing ArrayMap", e)
            }
            throw e
        }
    }
}