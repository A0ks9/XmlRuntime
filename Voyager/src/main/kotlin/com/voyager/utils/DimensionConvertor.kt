/**
 * Efficient dimension converter for Android views.
 *
 * This utility provides optimized conversion of dimension strings to pixels,
 * with support for various units and percentage-based values.
 *
 * Key features:
 * - Efficient string parsing
 * - Memory-optimized conversions
 * - Support for percentage values
 * - Thread-safe operations
 * - Comprehensive unit support
 *
 * Performance optimizations:
 * - Cached regex patterns
 * - Optimized string operations
 * - Minimized object creation
 * - Efficient unit conversion
 * - Reduced memory allocations
 *
 * Usage example:
 * ```kotlin
 * // Convert dp to pixels
 * val pixels = "16dp".toPixels(displayMetrics)
 *
 * // Convert percentage to pixels
 * val percentagePixels = "50%".toPixels(displayMetrics, parentView, horizontal = true)
 *
 * // Convert with specific unit
 * val spPixels = "12sp".toPixels(displayMetrics, asInt = true)
 * ```
 *
 * @author Abdelrahman Omar
 * @since 1.0.0
 */
package com.voyager.utils

import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.ViewGroup

// Constants for frequently used values
private const val PERCENTAGE_SYMBOL = "%"
private const val DEFAULT_VALUE = 0
private const val DEFAULT_FLOAT_VALUE = 0f
private const val ROUNDING_FACTOR = 0.5f

// Cached regex pattern for better performance
private val dimensionRegex = Regex("""^\s*(\d+(?:\.\d+)?)\s*([a-zA-Z]+)\s*$""")

// Immutable map for unit conversion
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

/**
 * Converts a dimension string to pixels.
 *
 * @param metrics DisplayMetrics for unit conversion
 * @param parent Optional parent view for percentage calculations
 * @param horizontal If true, percentage is based on width; otherwise, height
 * @param asInt If true, returns an Int; otherwise, returns a Float
 * @return The converted dimension in pixels
 */
fun String.toPixels(
    metrics: DisplayMetrics,
    parent: ViewGroup? = null,
    horizontal: Boolean = false,
    asInt: Boolean = false,
): Number {
    val trimmedString = trim()

    // Handle percentage values
    if (trimmedString.endsWith(PERCENTAGE_SYMBOL)) {
        return handlePercentageValue(trimmedString, parent, horizontal, asInt)
    }

    // Handle regular dimension values
    return handleDimensionValue(trimmedString, metrics, asInt)
}

/**
 * Handles percentage-based dimension values.
 *
 * @param value The percentage string value
 * @param parent The parent view for percentage calculation
 * @param horizontal Whether to use width or height
 * @param asInt Whether to return an integer value
 * @return The calculated pixel value
 */
private fun handlePercentageValue(
    value: String,
    parent: ViewGroup?,
    horizontal: Boolean,
    asInt: Boolean,
): Number {
    val percentageString = value.dropLast(1).trim()
    val percentage = percentageString.toFloatOrNull()

    if (percentage == null) {
        logWarning("Invalid percentage value: '$percentageString', defaulting to 0")
        return if (asInt) DEFAULT_VALUE else DEFAULT_FLOAT_VALUE
    }

    val size = if (horizontal) parent?.measuredWidth ?: DEFAULT_VALUE else parent?.measuredHeight
        ?: DEFAULT_VALUE
    val result = percentage / 100 * size

    return if (asInt) result.toInt() else result
}

/**
 * Handles regular dimension values.
 *
 * @param value The dimension string value
 * @param metrics DisplayMetrics for unit conversion
 * @param asInt Whether to return an integer value
 * @return The converted pixel value
 */
private fun handleDimensionValue(
    value: String,
    metrics: DisplayMetrics,
    asInt: Boolean,
): Number {
    val match = dimensionRegex.find(value)
    val (valueStr, unitStr) = match?.destructured ?: run {
        logWarning("Invalid dimension string format: '$value', defaulting to 0")
        return if (asInt) DEFAULT_VALUE else DEFAULT_FLOAT_VALUE
    }

    val floatValue = valueStr.toFloatOrNull()
    if (floatValue == null) {
        logWarning("Invalid dimension value: '$valueStr', defaulting to 0")
        return if (asInt) DEFAULT_VALUE else DEFAULT_FLOAT_VALUE
    }

    val unitType = dimensionUnitToComplexUnitMap[unitStr.lowercase()]
    if (unitType == null) {
        logWarning("Unsupported dimension unit: '$unitStr' in string: '$value'")
        return if (asInt) DEFAULT_VALUE else DEFAULT_FLOAT_VALUE
    }

    val pixels = TypedValue.applyDimension(unitType, floatValue, metrics)
    return if (asInt) (pixels + ROUNDING_FACTOR).toInt() else pixels
}

/**
 * Logs a warning message.
 *
 * @param message The warning message to log
 */
private fun logWarning(message: String) {
    Log.w("DimensionConverter", message)
}