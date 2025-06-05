package com.voyager.core.utils.parser

import android.view.Gravity
import com.voyager.core.utils.logging.LoggerFactory
import java.util.concurrent.ConcurrentHashMap

/**
 * Utility object for parsing gravity values.
 * Provides efficient and thread-safe gravity parsing operations.
 *
 * Key Features:
 * - Gravity string parsing
 * - Multiple gravity combinations
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
 * 1. Use appropriate gravity values
 * 2. Handle null values appropriately
 * 3. Consider gravity combinations
 * 4. Use thread-safe operations
 * 5. Consider performance impact
 *
 * Example Usage:
 * ```kotlin
 * // Parse single gravity
 * val gravity = GravityParser.parseGravity("center")
 *
 * // Parse combined gravity
 * val combinedGravity = GravityParser.parseGravity("center|bottom")
 * ```
 */
object GravityParser {
    private val logger = LoggerFactory.getLogger(GravityParser::class.java.simpleName)

    // Thread-safe map for gravity values
    private val gravityMap = ConcurrentHashMap<String, Int>().apply {
        // Basic gravity values
        this["center"] = Gravity.CENTER
        this["center_horizontal"] = Gravity.CENTER_HORIZONTAL
        this["center_vertical"] = Gravity.CENTER_VERTICAL
        this["left"] = Gravity.LEFT
        this["right"] = Gravity.RIGHT
        this["top"] = Gravity.TOP
        this["bottom"] = Gravity.BOTTOM
        this["start"] = Gravity.START
        this["end"] = Gravity.END
        this["fill"] = Gravity.FILL
        this["fill_vertical"] = Gravity.FILL_VERTICAL
        this["fill_horizontal"] = Gravity.FILL_HORIZONTAL
        this["clip_vertical"] = Gravity.CLIP_VERTICAL
        this["clip_horizontal"] = Gravity.CLIP_HORIZONTAL
    }

    // Thread-safe cache for parsed gravity values
    private val gravityCache = ConcurrentHashMap<String, Int>()

    /**
     * Parses a gravity string (e.g., "center|bottom") to an integer gravity value.
     * Thread-safe operation with caching.
     *
     * Performance Considerations:
     * - Uses ConcurrentHashMap for caching
     * - Efficient string operations
     * - Minimal object creation
     * - Safe null handling
     *
     * @param value The gravity string to parse
     * @return The parsed gravity value or NO_GRAVITY if parsing fails
     */
    fun parseGravity(value: String?): Int {
        return try {
            if (!isValidGravity(value)) {
                logger.warn("parseGravity", "Invalid gravity value provided: '$value'")
                return Gravity.NO_GRAVITY
            }

            if (value == null) {
                logger.warn("parseGravity", "Null gravity value provided")
                return Gravity.NO_GRAVITY
            }

            gravityCache.getOrPut(value) {
                value.split("|").map { it.trim() }.sumOf {
                    gravityMap[it.lowercase()] ?: run {
                        logger.warn("parseGravity", "Unknown gravity value: '$it'")
                        0
                    }
                }
            }
        } catch (e: Exception) {
            logger.error("parseGravity", "Failed to parse gravity: ${e.message}")
            Gravity.NO_GRAVITY
        }
    }

    /**
     * Gets the gravity value for a string.
     * Thread-safe operation with caching.
     *
     * Performance Considerations:
     * - Uses ConcurrentHashMap for caching
     * - Efficient string operations
     * - Minimal object creation
     * - Safe null handling
     *
     * @param value The gravity string
     * @return The corresponding gravity value
     */
    fun getGravity(value: String): Int = parseGravity(value)

    /**
     * Checks if a string is a valid gravity value.
     * Thread-safe operation.
     *
     * @param value The string to check
     * @return true if the string is a valid gravity value, false otherwise
     */
    fun isValidGravity(value: String?): Boolean = try {
        value?.split("|")?.mapNotNull { it.trim() }
            ?.all { gravityMap.containsKey(it.lowercase()) } == true
    } catch (e: Exception) {
        logger.error("isValidGravity", "Failed to check gravity value: ${e.message}")
        false
    }

    /**
     * Clears the gravity cache.
     * Useful for testing or when configuration changes.
     * Thread-safe operation.
     */
    fun clearCache() {
        gravityCache.clear()
        logger.debug("clearCache", "Gravity cache cleared")
    }
} 