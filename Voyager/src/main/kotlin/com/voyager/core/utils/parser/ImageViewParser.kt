package com.voyager.core.utils.parser

import android.widget.ImageView
import com.voyager.core.utils.logging.LoggerFactory
import java.util.concurrent.ConcurrentHashMap

/**
 * Utility object for parsing ImageView-specific properties from strings.
 * Provides efficient and thread-safe ImageView property parsing operations.
 *
 * Key Features:
 * - Scale type parsing
 * - Thread-safe operations
 * - Performance optimized
 * - Comprehensive error handling
 *
 * Performance Optimizations:
 * - ConcurrentHashMap for caching
 * - Efficient string operations
 * - Minimal object creation
 * - Safe null handling
 *
 * Best Practices:
 * 1. Use appropriate scale types
 * 2. Handle null values appropriately
 * 3. Consider image dimensions
 * 4. Use thread-safe operations
 * 5. Consider performance impact
 *
 * Example Usage:
 * ```kotlin
 * // Parse scale type
 * val scaleType = ImageViewParser.parseScaleType("centerCrop")
 *
 * // Check if scale type is valid
 * val isValid = ImageViewParser.isValidScaleType("fitXY")
 * ```
 */
object ImageViewParser {
    private val logger = LoggerFactory.getLogger(ImageViewParser::class.java.simpleName)

    // Thread-safe map for scale types
    private val scaleTypeMap = ConcurrentHashMap<String, ImageView.ScaleType>().apply {
        this["center"] = ImageView.ScaleType.CENTER
        this["centerCrop"] = ImageView.ScaleType.CENTER_CROP
        this["centerInside"] = ImageView.ScaleType.CENTER_INSIDE
        this["fitCenter"] = ImageView.ScaleType.FIT_CENTER
        this["fitXY"] = ImageView.ScaleType.FIT_XY
        this["fitEnd"] = ImageView.ScaleType.FIT_END
        this["fitStart"] = ImageView.ScaleType.FIT_START
        this["matrix"] = ImageView.ScaleType.MATRIX
    }

    // Thread-safe cache for parsed scale types
    private val scaleTypeCache = ConcurrentHashMap<String, ImageView.ScaleType>()

    /**
     * Parses a scale type string to an ImageView.ScaleType.
     * Thread-safe operation with caching.
     *
     * Performance Considerations:
     * - Uses ConcurrentHashMap for caching
     * - Efficient string operations
     * - Minimal object creation
     * - Safe null handling
     *
     * @param attributeValue The scale type string (e.g., "centerCrop", "fitXY")
     * @return The parsed ScaleType or null if not found
     */
    fun parseScaleType(attributeValue: String?): ImageView.ScaleType? {
        return try {
            if (attributeValue == null) {
                logger.warn("parseScaleType", "Null scale type value provided")
                return null
            }

            if (!isValidScaleType(attributeValue)) {
                logger.warn(
                    "parseScaleType", "Invalid scale type value provided: '$attributeValue'"
                )
                return null
            }

            scaleTypeCache.getOrPut(attributeValue) {
                scaleTypeMap[attributeValue.lowercase()] ?: run {
                    logger.warn("parseScaleType", "Unknown scale type: '$attributeValue'")
                    return null
                }
            }
        } catch (e: Exception) {
            logger.error(
                "parseScaleType", "Failed to parse scale type: ${e.message}", e
            )
            return null
        }
    }

    /**
     * Checks if a string is a valid scale type.
     * Thread-safe operation.
     *
     * @param value The string to check
     * @return true if the string is a valid scale type, false otherwise
     */
    fun isValidScaleType(value: String?): Boolean = try {
        value?.let { scaleTypeMap.containsKey(it.lowercase()) } == true
    } catch (e: Exception) {
        logger.error("isValidScaleType", "Failed to check scale type: ${e.message}")
        false
    }

    /**
     * Gets the default scale type.
     * Thread-safe operation.
     *
     * @return The default scale type (FIT_CENTER)
     */
    fun getDefaultScaleType(): ImageView.ScaleType = ImageView.ScaleType.FIT_CENTER

    /**
     * Clears the scale type cache.
     * Useful for testing or when configuration changes.
     * Thread-safe operation.
     */
    fun clearCache() {
        scaleTypeCache.clear()
        logger.debug("clearCache", "Scale type cache cleared")
    }
} 