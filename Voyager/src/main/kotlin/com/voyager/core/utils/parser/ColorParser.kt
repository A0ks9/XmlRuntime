package com.voyager.core.utils.parser

import android.content.Context
import android.graphics.Color
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import com.voyager.core.model.ConfigManager
import com.voyager.core.utils.ErrorUtils
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
    private val errorUtils by lazy { ErrorUtils("ColorParser") }
    private val logger by lazy { LoggerFactory.getLogger("ColorParser") }
    private val config by lazy { ConfigManager.config }

    // Thread-safe cache for color resources
    private val colorResourceCache by lazy { ConcurrentHashMap<String, Int>() }

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
    val String.isColor
        get() = errorUtils.tryOrDefault({
            return@tryOrDefault startsWith("#") || startsWith("@color/")
        }, "isColor", { "Failed to check color value: ${it.message}" }, { false })

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
     * @param context The application context
     * @param defaultColor The default color to return if parsing fails
     * @return The parsed color value as an integer
     */
    fun String.toColor(context: Context, defaultColor: Int = Color.BLACK) =
        errorUtils.tryOrDefault({
            return@tryOrDefault removePrefix("@color/").let { c ->
                if (c.startsWith("#")) {
                    parseHexColor
                } else {
                    parseResourceColor(c, context, defaultColor)
                }
            }
        }, "getColor", { "Failed to parse color: ${it.message}" }, { defaultColor })

    /**
     * Parses a hex color string.
     * Thread-safe operation with error handling.
     *
     * @return The parsed color value
     */
    private val String.parseHexColor
        get() = errorUtils.tryOrDefault({
            return@tryOrDefault when (length) {
                4 -> "#${this[1]}${this[1]}${this[2]}${this[2]}${this[3]}${this[3]}".toColorInt()
                5 -> "#${this[1]}${this[1]}${this[2]}${this[2]}${this[3]}${this[3]}${this[4]}${this[4]}".toColorInt()
                else -> toColorInt()
            }
        }, "parseHexColor", { "Failed to parse hex color: ${it.message}" }, { Color.BLACK })

    /**
     * Parses a color resource.
     * Thread-safe operation with caching.
     *
     * @param resourceName The color resource name
     * @param context The application context
     * @param defaultColor The default color to return if parsing fails
     * @return The parsed color value
     */
    private fun parseResourceColor(resourceName: String, context: Context, defaultColor: Int): Int =
        errorUtils.tryOrDefault(
            {
            return@tryOrDefault colorResourceCache.getOrPut(resourceName) {
                config.provider.getResId("color", resourceName).takeIf { it != 0 }
                    ?.let { ContextCompat.getColor(context, it) } ?: defaultColor
            }
        },
            "parseResourceColor",
            { "Failed to parse resource color: ${it.message}" },
            { defaultColor })

    /**
     * Clears the color resource cache.
     * Useful for testing or when resource configuration changes.
     * Thread-safe operation.
     */
    fun clearCache() {
        colorResourceCache.clear()
        if (config.isLoggingEnabled) {
            logger.debug("clearCache", "Color resource cache cleared")
        }
    }
}