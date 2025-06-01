package com.voyager.core.utils.parser

import android.view.Gravity
import java.util.concurrent.ConcurrentHashMap

/**
 * Utility object for parsing gravity values.
 */
object GravityParser {

    private val sGravityMap = ConcurrentHashMap<String, Int>().apply {
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

    /**
     * Parses a gravity string (e.g., "center|bottom") to an integer gravity value.
     *
     * @param value The gravity string to parse
     * @return The parsed gravity value or NO_GRAVITY if parsing fails
     */
    fun parseGravity(value: String?): Int =
        value?.split("\\|")?.sumOf { sGravityMap[it] ?: 0 } ?: Gravity.NO_GRAVITY

    /**
     * Gets the gravity value for a string.
     *
     * @param value The gravity string
     * @return The corresponding gravity value
     */
    fun getGravity(value: String): Int = parseGravity(value)
} 