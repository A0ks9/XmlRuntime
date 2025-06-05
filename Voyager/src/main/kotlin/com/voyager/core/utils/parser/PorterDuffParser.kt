package com.voyager.core.utils.parser

import android.graphics.PorterDuff
import com.voyager.core.utils.logging.LoggerFactory
import java.util.concurrent.ConcurrentHashMap

/**
 * Utility object for parsing Android PorterDuff.Mode values.
 * Provides efficient and thread-safe PorterDuff mode parsing operations.
 *
 * Key Features:
 * - PorterDuff mode parsing
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
 * 1. Use appropriate PorterDuff modes
 * 2. Handle null values appropriately
 * 3. Consider blend effects
 * 4. Use thread-safe operations
 * 5. Consider performance impact
 *
 * Example Usage:
 * ```kotlin
 * // Parse PorterDuff mode
 * val mode = PorterDuffParser.parsePorterDuff("src_in")
 *
 * // Check if mode is valid
 * val isValid = PorterDuffParser.isValidPorterDuff("multiply")
 * ```
 */
object PorterDuffParser {
    private val logger = LoggerFactory.getLogger(PorterDuffParser::class.java.simpleName)

    // Thread-safe map for PorterDuff modes
    private val porterDuffMap = ConcurrentHashMap<String, PorterDuff.Mode>().apply {
        this["add"] = PorterDuff.Mode.ADD
        this["multiply"] = PorterDuff.Mode.MULTIPLY
        this["screen"] = PorterDuff.Mode.SCREEN
        this["src_atop"] = PorterDuff.Mode.SRC_ATOP
        this["src_in"] = PorterDuff.Mode.SRC_IN
        this["src_over"] = PorterDuff.Mode.SRC_OVER
        this["src_out"] = PorterDuff.Mode.SRC_OUT
        this["dst_atop"] = PorterDuff.Mode.DST_ATOP
        this["dst_in"] = PorterDuff.Mode.DST_IN
        this["dst_out"] = PorterDuff.Mode.DST_OUT
        this["dst_over"] = PorterDuff.Mode.DST_OVER
        this["clear"] = PorterDuff.Mode.CLEAR
        this["darken"] = PorterDuff.Mode.DARKEN
        this["lighten"] = PorterDuff.Mode.LIGHTEN
        this["xor"] = PorterDuff.Mode.XOR
    }

    // Thread-safe cache for parsed PorterDuff modes
    private val porterDuffCache = ConcurrentHashMap<String, PorterDuff.Mode>()

    /**
     * Parses a PorterDuff mode string to a PorterDuff.Mode.
     * Thread-safe operation with caching.
     *
     * Performance Considerations:
     * - Uses ConcurrentHashMap for caching
     * - Efficient string operations
     * - Minimal object creation
     * - Safe null handling
     *
     * @param porterDuff The PorterDuff mode string (e.g., "src_in", "multiply")
     * @return The parsed PorterDuff.Mode or SRC_IN if parsing fails
     */
    fun parsePorterDuff(porterDuff: String?): PorterDuff.Mode {
        return try {
            if (porterDuff == null) {
                logger.warn("parsePorterDuff", "Null PorterDuff mode provided")
                return PorterDuff.Mode.SRC_IN
            }

            if (!isValidPorterDuff(porterDuff)) {
                logger.warn(
                    "parsePorterDuff",
                    "Invalid PorterDuff mode provided: '$porterDuff'. Using SRC_IN."
                )
                return PorterDuff.Mode.SRC_IN
            }

            porterDuffCache.getOrPut(porterDuff) {
                porterDuffMap[porterDuff.lowercase()] ?: run {
                    logger.warn(
                        "parsePorterDuff",
                        "Unknown PorterDuff mode: '$porterDuff'. Using SRC_IN."
                    )
                    return PorterDuff.Mode.SRC_IN
                }
            }
        } catch (e: Exception) {
            logger.error("parsePorterDuff", "Failed to parse PorterDuff mode: ${e.message}", e)
            return PorterDuff.Mode.SRC_IN
        }
    }

    /**
     * Checks if a string is a valid PorterDuff mode.
     * Thread-safe operation.
     *
     * @param value The string to check
     * @return true if the string is a valid PorterDuff mode, false otherwise
     */
    fun isValidPorterDuff(value: String?): Boolean = try {
        value?.let { porterDuffMap.containsKey(it.lowercase()) } == true
    } catch (e: Exception) {
        logger.error("isValidPorterDuff", "Failed to check PorterDuff mode: ${e.message}", e)
        false
    }

    /**
     * Clears the PorterDuff mode cache.
     * Useful for testing or when configuration changes.
     * Thread-safe operation.
     */
    fun clearCache() {
        porterDuffCache.clear()
        logger.debug("clearCache", "PorterDuff mode cache cleared")
    }
} 