package com.voyager.core.utils.parser

import android.os.Build
import android.view.View
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import com.voyager.core.utils.logging.LoggerFactory
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap

/**
 * Utility object for parsing general Android View and ViewGroup properties from strings.
 * Provides efficient and thread-safe view property parsing operations.
 *
 * Key Features:
 * - Visibility parsing
 * - Drawing cache quality parsing
 * - Over scroll mode parsing
 * - Accessibility importance parsing
 * - Scroll indicators parsing
 * - Divider mode parsing
 * - Thread-safe operations
 * - Performance optimized
 * - Comprehensive error handling
 *
 * Performance Optimizations:
 * - ConcurrentHashMap for caching
 * - Efficient string operations
 * - Minimal object creation
 * - Safe view property handling
 *
 * Best Practices:
 * 1. Use appropriate view properties
 * 2. Handle null values appropriately
 * 3. Consider view property availability
 * 4. Use thread-safe operations
 * 5. Consider performance impact
 *
 * Example Usage:
 * ```kotlin
 * // Parse visibility
 * val visibility = ViewPropertyParser.parseVisibility("visible")
 *
 * // Parse over scroll mode
 * val overScrollMode = ViewPropertyParser.parseOverScrollMode("never")
 *
 * // Parse scroll indicators
 * val scrollIndicators = ViewPropertyParser.parseScrollIndicators("start|end")
 * ```
 */
object ViewPropertyParser {
    private val logger = LoggerFactory.getLogger(ViewPropertyParser::class.java.simpleName)

    // Thread-safe maps for view properties
    private val sVisibilityMap = ConcurrentHashMap<Int, Int>().apply {
        this[View.VISIBLE] = View.VISIBLE
        this[View.INVISIBLE] = View.INVISIBLE
        this[View.GONE] = View.GONE
    }

    private val sVisibilityMode = ConcurrentHashMap<String, Int>().apply {
        this["visible"] = View.VISIBLE
        this["invisible"] = View.INVISIBLE
        this["gone"] = View.GONE
    }

    @Suppress("DEPRECATION")
    private val sDrawingCacheQuality = ConcurrentHashMap<String, Int>().apply {
        this["auto"] = View.DRAWING_CACHE_QUALITY_AUTO
        this["high"] = View.DRAWING_CACHE_QUALITY_HIGH
        this["low"] = View.DRAWING_CACHE_QUALITY_LOW
    }

    private val sOverScrollModes = ConcurrentHashMap<String, Int>().apply {
        this["always"] = View.OVER_SCROLL_ALWAYS
        this["ifcontentscrolls"] = View.OVER_SCROLL_IF_CONTENT_SCROLLS
        this["never"] = View.OVER_SCROLL_NEVER
    }

    private val sImportantAccessibility = ConcurrentHashMap<String, Int>().apply {
        this["auto"] = View.IMPORTANT_FOR_ACCESSIBILITY_AUTO
        this["yes"] = View.IMPORTANT_FOR_ACCESSIBILITY_YES
        this["no"] = View.IMPORTANT_FOR_ACCESSIBILITY_NO
        this["nohideDescendants"] = View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS
    }

    private val sDividerMode = ConcurrentHashMap<String, Int>().apply {
        this["end"] = LinearLayout.SHOW_DIVIDER_END
        this["middle"] = LinearLayout.SHOW_DIVIDER_MIDDLE
        this["beginning"] = LinearLayout.SHOW_DIVIDER_BEGINNING
    }

    /**
     * Parses a visibility string or integer to an integer visibility value.
     * Thread-safe operation with caching.
     *
     * Performance Considerations:
     * - Uses ConcurrentHashMap for caching
     * - Efficient string operations
     * - Minimal object creation
     * - Safe view property handling
     *
     * @param value The visibility string or integer
     * @return The parsed visibility value
     */
    fun parseVisibility(value: String?): Int = try {
        sVisibilityMode[value] ?: (value?.toIntOrNull()?.let { sVisibilityMap[it] } ?: run {
            logger.warn("parseVisibility", "Invalid visibility: '$value', using VISIBLE")
            View.VISIBLE
        })
    } catch (e: Exception) {
        logger.error("parseVisibility", "Failed to parse visibility: ${e.message}")
        View.VISIBLE
    }

    /**
     * Gets the integer visibility value for a string representation.
     * Thread-safe operation with caching.
     *
     * Performance Considerations:
     * - Uses ConcurrentHashMap for caching
     * - Efficient integer operations
     * - Minimal object creation
     * - Safe view property handling
     *
     * @param visibility The visibility integer
     * @return The corresponding visibility value
     */
    fun getVisibility(visibility: Int): Int = try {
        sVisibilityMap[visibility] ?: run {
            logger.warn("getVisibility", "Invalid visibility integer: $visibility, using GONE")
            View.GONE
        }
    } catch (e: Exception) {
        logger.error("getVisibility", "Failed to get visibility: ${e.message}")
        View.GONE
    }

    /**
     * Parses a drawing cache quality string to an integer value.
     * Thread-safe operation with caching.
     *
     * Performance Considerations:
     * - Uses ConcurrentHashMap for caching
     * - Efficient string operations
     * - Minimal object creation
     * - Safe view property handling
     *
     * @param attributeValue The drawing cache quality string
     * @return The parsed drawing cache quality value
     */
    @Suppress("DEPRECATION")
    fun parseDrawingCacheQuality(attributeValue: String?): Int = try {
        sDrawingCacheQuality[attributeValue?.lowercase(Locale.ROOT)] ?: run {
            logger.warn("parseDrawingCacheQuality", "Invalid drawing cache quality: '$attributeValue', using AUTO")
            View.DRAWING_CACHE_QUALITY_AUTO
        }
    } catch (e: Exception) {
        logger.error("parseDrawingCacheQuality", "Failed to parse drawing cache quality: ${e.message}")
        View.DRAWING_CACHE_QUALITY_AUTO
    }

    /**
     * Parses an over scroll mode string to an integer value.
     * Thread-safe operation with caching.
     *
     * Performance Considerations:
     * - Uses ConcurrentHashMap for caching
     * - Efficient string operations
     * - Minimal object creation
     * - Safe view property handling
     *
     * @param attributeValue The over scroll mode string
     * @return The parsed over scroll mode value
     */
    fun parseOverScrollMode(attributeValue: String): Int = try {
        sOverScrollModes[attributeValue.lowercase(Locale.ROOT)] ?: run {
            logger.warn("parseOverScrollMode", "Invalid over scroll mode: '$attributeValue', using ALWAYS")
            View.OVER_SCROLL_ALWAYS
        }
    } catch (e: Exception) {
        logger.error("parseOverScrollMode", "Failed to parse over scroll mode: ${e.message}")
        View.OVER_SCROLL_ALWAYS
    }

    /**
     * Parses an important for accessibility string to an integer value.
     * Thread-safe operation with caching.
     *
     * Performance Considerations:
     * - Uses ConcurrentHashMap for caching
     * - Efficient string operations
     * - Minimal object creation
     * - Safe view property handling
     *
     * @param attributeValue The important for accessibility string
     * @return The parsed important for accessibility value
     */
    fun parseImportantForAccessibility(attributeValue: String): Int = try {
        sImportantAccessibility[attributeValue] ?: run {
            logger.warn("parseImportantForAccessibility", "Invalid accessibility importance: '$attributeValue', using AUTO")
            View.IMPORTANT_FOR_ACCESSIBILITY_AUTO
        }
    } catch (e: Exception) {
        logger.error("parseImportantForAccessibility", "Failed to parse accessibility importance: ${e.message}")
        View.IMPORTANT_FOR_ACCESSIBILITY_AUTO
    }

    /**
     * Parses a scroll indicators string to an integer value.
     * Thread-safe operation.
     *
     * Performance Considerations:
     * - Efficient string operations
     * - Minimal object creation
     * - Safe view property handling
     *
     * @param attributeValue The scroll indicators string (e.g., "start|end")
     * @return The parsed scroll indicators value
     */
    @RequiresApi(Build.VERSION_CODES.M)
    fun parseScrollIndicators(attributeValue: String): Int = try {
        var flags = 0
        attributeValue.split("|").forEach { indicator ->
            flags = when (indicator.trim().lowercase(Locale.ROOT)) {
                "start" -> flags or View.SCROLL_INDICATOR_START
                "end" -> flags or View.SCROLL_INDICATOR_END
                "top" -> flags or View.SCROLL_INDICATOR_TOP
                "bottom" -> flags or View.SCROLL_INDICATOR_BOTTOM
                "left" -> flags or View.SCROLL_INDICATOR_LEFT
                "right" -> flags or View.SCROLL_INDICATOR_RIGHT
                "none" -> 0
                else -> {
                    logger.warn("parseScrollIndicators", "Invalid scroll indicator: '$indicator'")
                    flags
                }
            }
        }
        flags
    } catch (e: Exception) {
        logger.error("parseScrollIndicators", "Failed to parse scroll indicators: ${e.message}")
        0
    }

    /**
     * Parses a divider mode string to an integer value for LinearLayout.
     * Thread-safe operation with caching.
     *
     * Performance Considerations:
     * - Uses ConcurrentHashMap for caching
     * - Efficient string operations
     * - Minimal object creation
     * - Safe view property handling
     *
     * @param attributeValue The divider mode string
     * @return The parsed divider mode value
     */
    fun parseDividerMode(attributeValue: String?): Int = try {
        sDividerMode[attributeValue?.lowercase(Locale.ROOT)] ?: run {
            logger.warn("parseDividerMode", "Invalid divider mode: '$attributeValue', using NONE")
            LinearLayout.SHOW_DIVIDER_NONE
        }
    } catch (e: Exception) {
        logger.error("parseDividerMode", "Failed to parse divider mode: ${e.message}")
        LinearLayout.SHOW_DIVIDER_NONE
    }

    /**
     * Clears all view property caches.
     * Useful for testing or when view properties change.
     * Thread-safe operation.
     */
    fun clearCache() {
        sVisibilityMap.clear()
        sVisibilityMode.clear()
        sDrawingCacheQuality.clear()
        sOverScrollModes.clear()
        sImportantAccessibility.clear()
        sDividerMode.clear()
        logger.debug("clearCache", "View property caches cleared")
    }
} 