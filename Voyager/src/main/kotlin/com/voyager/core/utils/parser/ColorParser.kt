package com.voyager.core.utils.parser

import android.content.Context
import android.graphics.Color
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import com.voyager.core.model.ConfigManager
import com.voyager.core.utils.logging.LoggerFactory
import java.util.concurrent.ConcurrentHashMap

/**
 * Utility object for parsing color strings and resources.
 * Provides efficient and thread-safe color parsing operations.
 *
 * Key Features:
 * - Hex color parsing
 * - Resource color parsing
 * - Thread-safe operations
 * - Performance optimized
 * - Comprehensive error handling
 *
 * Performance Optimizations:
 * - ConcurrentHashMap for caching
 * - Efficient string operations
 * - Minimal object creation
 * - Safe resource handling
 *
 * Best Practices:
 * 1. Use isColor() to check before parsing
 * 2. Handle null values appropriately
 * 3. Consider resource availability
 * 4. Use thread-safe operations
 * 5. Consider performance impact
 *
 * Example Usage:
 * ```kotlin
 * // Check if string is color
 * val isColor = "#FF0000".isColor()
 *
 * // Parse color value
 * val color = ColorParser.getColor("#FF0000", context)
 * ```
 */
object ColorParser {
    private val logger = LoggerFactory.getLogger(ColorParser::class.java.simpleName)
    private val config = ConfigManager.config

    // Thread-safe cache for color resources
    private val colorResourceCache = ConcurrentHashMap<String, Int>()

    /**
     * Checks if this string likely represents a color value.
     * Thread-safe operation.
     *
     * Performance Considerations:
     * - Efficient string operations
     * - Minimal object creation
     * - Safe null handling
     *
     * @receiver The string to check.
     * @return `true` if the string format suggests a color value, `false` otherwise.
     */
    fun String.isColor(): Boolean = try {
        this.startsWith("#") || this.startsWith("@color/")
    } catch (e: Exception) {
        logger.error("isColor", "Failed to check color value: ${e.message}")
        false
    }

    /**
     * Gets a color from a string or resource.
     * Thread-safe operation with error handling.
     *
     * Performance Considerations:
     * - Uses ConcurrentHashMap for caching
     * - Efficient string operations
     * - Minimal object creation
     * - Safe resource handling
     *
     * @param colorValue The color string or resource name
     * @param context The application context
     * @param defaultColor The default color to return if parsing fails
     * @return The parsed color value as an integer
     */
    fun getColor(colorValue: String?, context: Context, defaultColor: Int = Color.BLACK): Int = try {
        colorValue?.removePrefix("@color/")?.let { c ->
            if (c.startsWith("#")) {
                parseHexColor(c)
            } else {
                parseResourceColor(c, context, defaultColor)
            }
        } ?: defaultColor
    } catch (e: Exception) {
        logger.error("getColor", "Failed to parse color: ${e.message}")
        defaultColor
    }

    /**
     * Parses a hex color string.
     * Thread-safe operation with error handling.
     *
     * @param hexColor The hex color string
     * @return The parsed color value
     */
    private fun parseHexColor(hexColor: String): Int = try {
        when (hexColor.length) {
            4 -> "#${hexColor[1]}${hexColor[1]}${hexColor[2]}${hexColor[2]}${hexColor[3]}${hexColor[3]}".toColorInt()
            5 -> "#${hexColor[1]}${hexColor[1]}${hexColor[2]}${hexColor[2]}${hexColor[3]}${hexColor[3]}${hexColor[4]}${hexColor[4]}".toColorInt()
            else -> hexColor.toColorInt()
        }
    } catch (e: Exception) {
        logger.error("parseHexColor", "Failed to parse hex color: ${e.message}")
        Color.BLACK
    }

    /**
     * Parses a color resource.
     * Thread-safe operation with caching.
     *
     * @param resourceName The color resource name
     * @param context The application context
     * @param defaultColor The default color to return if parsing fails
     * @return The parsed color value
     */
    private fun parseResourceColor(resourceName: String, context: Context, defaultColor: Int): Int = try {
        colorResourceCache.getOrPut(resourceName) {
            config.provider.getResId("color", resourceName).takeIf { it != 0 }
                ?.let { ContextCompat.getColor(context, it) } ?: defaultColor
        }
    } catch (e: Exception) {
        logger.error("parseResourceColor", "Failed to parse resource color: ${e.message}")
        defaultColor
    }

    /**
     * Clears the color resource cache.
     * Useful for testing or when resource configuration changes.
     * Thread-safe operation.
     */
    fun clearCache() {
        colorResourceCache.clear()
        logger.debug("clearCache", "Color resource cache cleared")
    }
}