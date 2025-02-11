package com.runtimexml.utils

import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.ViewGroup

/**
 * Converts a dimension string (e.g., "16dp", "10px", "50%") to pixels.
 * Supports percentage-based values relative to the parent ViewGroup.
 *
 * @param metrics DisplayMetrics for unit conversion.
 * @param parent Optional parent view for percentage calculations.
 * @param horizontal If true, percentage is based on width; otherwise, height.
 * @param asInt If true, returns an Int; otherwise, returns a Float.
 */
fun String.toPixels(
    metrics: DisplayMetrics,
    parent: ViewGroup? = null,
    horizontal: Boolean = false,
    asInt: Boolean = false
): Number {
    if (endsWith("%")) { // Handle percentage values
        val percentage = dropLast(1).toFloatOrNull() ?: return if (asInt) 0 else 0f
        val size = if (horizontal) parent?.measuredWidth ?: 0 else parent?.measuredHeight ?: 0
        return if (asInt) (percentage / 100 * size).toInt() else percentage / 100 * size
    }

    val match =
        Regex("""^\s*(\d+(?:\.\d+)?)\s*([a-zA-Z]+)\s*$""").find(this) ?: return if (asInt) 0 else 0f
    val (value, unit) = match.destructured

    val unitType = mapOf(
        "px" to TypedValue.COMPLEX_UNIT_PX,
        "dp" to TypedValue.COMPLEX_UNIT_DIP,
        "dip" to TypedValue.COMPLEX_UNIT_DIP,
        "sp" to TypedValue.COMPLEX_UNIT_SP,
        "pt" to TypedValue.COMPLEX_UNIT_PT,
        "in" to TypedValue.COMPLEX_UNIT_IN,
        "mm" to TypedValue.COMPLEX_UNIT_MM
    )[unit.lowercase()] ?: return if (asInt) 0 else 0f

    val pixels = TypedValue.applyDimension(unitType, value.toFloat(), metrics)
    return if (asInt) (pixels + 0.5f).toInt() else pixels
}
