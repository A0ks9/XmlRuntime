package com.voyager.utils

import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.ViewGroup

/**
 * Converts a dimension string (e.g., "16dp", "10px", "50%") to pixels.
 * Supports percentage-based values relative to the parent ViewGroup.
 *
 * @param metrics DisplayMetrics for unit conversion.
 * @param parent Optional parent view for percentage calculations. If null for percentage values, defaults to 0 size.
 * @param horizontal If true, percentage is based on width; otherwise, height.
 * @param asInt If true, returns an Int; otherwise, returns a Float.
 * @throws IllegalArgumentException if the dimension string is invalid or unit is unsupported, unless percentage with null parent.
 */
fun String.toPixels(
    metrics: DisplayMetrics,
    parent: ViewGroup? = null,
    horizontal: Boolean = false,
    asInt: Boolean = false,
): Number {
    val trimmedString = trim() // Trim whitespace right at the beginning

    if (trimmedString.endsWith("%")) { // Handle percentage values
        val percentageString = trimmedString.dropLast(1).trim() // Trim percentage value too
        val percentage = percentageString.toFloatOrNull()
        if (percentage == null) {
            Log.w(
                "DimensionConverter",
                "Invalid percentage value: '$percentageString', defaulting to 0"
            )
            return if (asInt) 0 else 0f
        }
        val size = if (horizontal) parent?.measuredWidth ?: 0 else parent?.measuredHeight ?: 0
        return if (asInt) (percentage / 100 * size).toInt() else percentage / 100 * size
    }

    val match = dimensionRegex.find(trimmedString)
    val (valueStr, unitStr) = match?.destructured ?: run {
        Log.w(
            "DimensionConverter",
            "Invalid dimension string format: '$trimmedString', defaulting to 0"
        )
        return if (asInt) 0 else 0f
    }

    val value = valueStr.toFloatOrNull()
    val unit = unitStr.lowercase()

    if (value == null) {
        Log.w("DimensionConverter", "Invalid dimension value: '$valueStr', defaulting to 0")
        return if (asInt) 0 else 0f
    }


    val unitType = dimensionUnitToComplexUnitMap[unit]
    if (unitType == null) {
        val errorMessage = "Unsupported dimension unit: '$unit' in string: '$trimmedString'"
        Log.w("DimensionConverter", errorMessage)
        return if (asInt) 0 else 0f // Or throw IllegalArgumentException for stricter error handling
        // throw IllegalArgumentException(errorMessage) // Uncomment to throw exception instead of defaulting
    }

    val pixels = TypedValue.applyDimension(unitType, value, metrics)
    return if (asInt) (pixels + 0.5f).toInt() else pixels
}


private val dimensionRegex = Regex("""^\s*(\d+(?:\.\d+)?)\s*([a-zA-Z]+)\s*$""")

private val dimensionUnitToComplexUnitMap = mapOf(
    "px" to TypedValue.COMPLEX_UNIT_PX,
    "dp" to TypedValue.COMPLEX_UNIT_DIP,
    "dip" to TypedValue.COMPLEX_UNIT_DIP,
    "sp" to TypedValue.COMPLEX_UNIT_SP,
    "pt" to TypedValue.COMPLEX_UNIT_PT,
    "in" to TypedValue.COMPLEX_UNIT_IN,
    "mm" to TypedValue.COMPLEX_UNIT_MM,
    "pc" to TypedValue.COMPLEX_UNIT_PT * 12 // 1 pica = 12 points
)