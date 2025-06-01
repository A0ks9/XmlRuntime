package com.voyager.core.utils.parser

import android.os.Build
import android.view.View
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap

/**
 * Utility object for parsing general Android View and ViewGroup properties from strings.
 */
object ViewPropertyParser {

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
        this["nohidedescendants"] = View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS
    }

    private val sDividerMode = ConcurrentHashMap<String, Int>().apply {
        this["end"] = LinearLayout.SHOW_DIVIDER_END
        this["middle"] = LinearLayout.SHOW_DIVIDER_MIDDLE
        this["beginning"] = LinearLayout.SHOW_DIVIDER_BEGINNING
    }

    /**
     * Parses a visibility string (e.g., "visible", "invisible", "gone") or integer to an integer visibility value.
     *
     * @param value The visibility string or integer
     * @return The parsed visibility value
     */
    fun parseVisibility(value: String?): Int =
        sVisibilityMode[value] ?: (value?.toIntOrNull()?.let { sVisibilityMap[it] } ?: View.VISIBLE)

    /**
     * Gets the integer visibility value for a string representation.
     *
     * @param visibility The visibility integer
     * @return The corresponding visibility value
     */
    fun getVisibility(visibility: Int): Int = sVisibilityMap[visibility] ?: View.GONE

    /**
     * Parses a drawing cache quality string to an integer value.
     *
     * @param attributeValue The drawing cache quality string
     * @return The parsed drawing cache quality value
     */
    @Suppress("DEPRECATION")
    fun parseDrawingCacheQuality(attributeValue: String?): Int =
        sDrawingCacheQuality[attributeValue?.lowercase(Locale.ROOT)] ?: View.DRAWING_CACHE_QUALITY_AUTO

    /**
     * Parses an over scroll mode string to an integer value.
     *
     * @param attributeValue The over scroll mode string
     * @return The parsed over scroll mode value
     */
    fun parseOverScrollMode(attributeValue: String): Int =
        sOverScrollModes[attributeValue.lowercase(Locale.ROOT)] ?: View.OVER_SCROLL_ALWAYS

    /**
     * Parses an important for accessibility string to an integer value.
     *
     * @param attributeValue The important for accessibility string
     * @return The parsed important for accessibility value
     */
    fun parseImportantForAccessibility(attributeValue: String): Int =
        sImportantAccessibility[attributeValue] ?: View.IMPORTANT_FOR_ACCESSIBILITY_AUTO

    /**
     * Parses a scroll indicators string to an integer value.
     *
     * @param attributeValue The scroll indicators string (e.g., "start|end")
     * @return The parsed scroll indicators value
     */
    @RequiresApi(Build.VERSION_CODES.M)
    fun parseScrollIndicators(attributeValue: String): Int {
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
                else -> flags
            }
        }
        return flags
    }

    /**
     * Parses a divider mode string to an integer value for LinearLayout.
     *
     * @param attributeValue The divider mode string
     * @return The parsed divider mode value
     */
    fun parseDividerMode(attributeValue: String?): Int =
        sDividerMode[attributeValue?.lowercase(Locale.ROOT)] ?: LinearLayout.SHOW_DIVIDER_NONE
} 