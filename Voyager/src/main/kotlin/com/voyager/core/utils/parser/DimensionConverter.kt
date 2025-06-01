 package com.voyager.core.utils.parser

import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.ViewGroup
import java.util.Locale

// Constants
private const val PERCENTAGE_SYMBOL = "%"
private const val DEFAULT_VALUE = 0
private const val DEFAULT_FLOAT_VALUE = 0f
private const val ROUNDING_FACTOR = 0.5f

// Cached Regex for parsing dimension strings
private val dimensionRegex = Regex("""^\s*(\d+(?:\.\d+)?)\s*([a-zA-Z]+)\s*$""")

// Immutable map for dimension unit translation
private val dimensionUnitToComplexUnitMap = mapOf(
    "px" to TypedValue.COMPLEX_UNIT_PX,
    "dp" to TypedValue.COMPLEX_UNIT_DIP,
    "dip" to TypedValue.COMPLEX_UNIT_DIP,
    "sp" to TypedValue.COMPLEX_UNIT_SP,
    "pt" to TypedValue.COMPLEX_UNIT_PT,
    "in" to TypedValue.COMPLEX_UNIT_IN,
    "mm" to TypedValue.COMPLEX_UNIT_MM,
    "pc" to TypedValue.COMPLEX_UNIT_PT * 12
)

/**
 * Converts a dimension string (e.g., "16dp", "50%", "12sp") into a pixel value.
 *
 * @receiver The dimension string.
 * @param metrics DisplayMetrics for density conversion.
 * @param parent Optional parent ViewGroup for percentage calculation.
 * @param horizontal True for parent width, false for parent height (if percentage).
 * @param asInt True to return Int, false for Float.
 * @return The converted dimension in pixels.
 */
fun String.toPixels(
    metrics: DisplayMetrics,
    parent: ViewGroup? = null,
    horizontal: Boolean = false,
    asInt: Boolean = false,
): Number {
    val trimmedString = this.trim()

    if (trimmedString.endsWith(PERCENTAGE_SYMBOL)) {
        return handlePercentageValue(trimmedString, parent, horizontal, asInt)
    }

    return handleDimensionValue(trimmedString, metrics, asInt)
}

private fun handlePercentageValue(
    value: String,
    parent: ViewGroup?,
    horizontal: Boolean,
    asInt: Boolean,
): Number {
    val percentageString = value.dropLast(1).trim()
    val percentage = percentageString.toFloatOrNull()

    if (percentage == null) {
        logWarning("Invalid percentage numeric value: '$percentageString'")
        return if (asInt) DEFAULT_VALUE else DEFAULT_FLOAT_VALUE
    }

    if (parent == null) {
        logWarning("Parent ViewGroup is null for percentage value: '$value'")
        return if (asInt) DEFAULT_VALUE else DEFAULT_FLOAT_VALUE
    }

    val parentSize = if (horizontal) parent.measuredWidth else parent.measuredHeight
    if (parentSize == 0) {
        logWarning("Parent ViewGroup's relevant dimension is 0 for '$value'")
    }

    val result = (percentage / 100f) * parentSize

    return if (asInt) (result + ROUNDING_FACTOR).toInt() else result
}

private fun handleDimensionValue(
    value: String,
    metrics: DisplayMetrics,
    asInt: Boolean,
): Number {
    val match = dimensionRegex.find(value)
    val (valueStr, unitStr) = match?.destructured ?: run {
        logWarning("Invalid dimension string format: '$value'")
        return if (asInt) DEFAULT_VALUE else DEFAULT_FLOAT_VALUE
    }

    val floatValue = valueStr.toFloatOrNull()
    if (floatValue == null) {
        logWarning("Invalid numeric value in dimension string: '$valueStr'")
        return if (asInt) DEFAULT_VALUE else DEFAULT_FLOAT_VALUE
    }

    val unitType = dimensionUnitToComplexUnitMap[unitStr.lowercase(Locale.ROOT)]
    if (unitType == null) {
        logWarning("Unsupported dimension unit: '$unitStr'")
        return if (asInt) DEFAULT_VALUE else DEFAULT_FLOAT_VALUE
    }

    val pixels = TypedValue.applyDimension(unitType, floatValue, metrics)
    return if (asInt) (pixels + ROUNDING_FACTOR).toInt() else pixels
}

private fun logWarning(message: String) {
    Log.w("VoyagerDimConverter", message)
}
