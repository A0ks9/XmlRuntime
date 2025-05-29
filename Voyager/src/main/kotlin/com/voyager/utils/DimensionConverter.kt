/**
 * This file provides utility functions for converting dimension strings (e.g., "16dp", "50%", "12sp")
 * into pixel values for use in Android UI development. It supports various dimension units
 * and percentage-based calculations relative to a parent view.
 *
 * The primary entry point is the [String.toPixels] extension function.
 *
 * Key Features:
 * - **Unit Support:** Handles common Android dimension units: "px", "dp", "dip", "sp", "pt", "in", "mm", "pc".
 * - **Percentage Values:** Converts percentage strings (e.g., "75%") into pixel values based on the
 *   dimensions of an optional parent [ViewGroup].
 * - **Flexible Output:** Can return results as [Float] or rounded [Int] pixels.
 * - **Error Handling:** Gracefully handles invalid dimension strings by logging a warning and
 *   returning a default value (0 or 0f).
 * - **Performance:** Uses a cached [Regex] for parsing dimension strings and an immutable map
 *   for unit-to-TypedValue mapping to optimize repeated conversions.
 *
 * @author Abdelrahman Omar
 * @since 1.0.0
 */
package com.voyager.utils

import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.ViewGroup
import java.util.Locale // For lowercase in dimensionUnitToComplexUnitMap key access

// --- Private Constants ---

/** Symbol indicating a percentage value (e.g., "50%"). */
private const val PERCENTAGE_SYMBOL = "%"
/** Default integer value to return in case of parsing errors or invalid input. */
private const val DEFAULT_VALUE = 0
/** Default float value to return in case of parsing errors or invalid input. */
private const val DEFAULT_FLOAT_VALUE = 0f
/** Factor used for rounding float pixel values to the nearest integer when `asInt` is true. */
private const val ROUNDING_FACTOR = 0.5f

/**
 * Cached [Regex] for parsing dimension strings.
 * It expects a number (integer or float) followed by a unit string (e.g., "16dp", "12.5sp").
 * Groups:
 *  1. The numeric value (e.g., "16", "12.5").
 *  2. The unit string (e.g., "dp", "sp").
 */
private val dimensionRegex = Regex("""^\s*(\d+(?:\.\d+)?)\s*([a-zA-Z]+)\s*$""")

/**
 * Immutable map that translates common dimension unit strings (lowercase) to their corresponding
 * Android [TypedValue] complex unit integers.
 * This map is used for efficient lookup during unit conversion.
 */
private val dimensionUnitToComplexUnitMap = mapOf(
    "px" to TypedValue.COMPLEX_UNIT_PX,
    "dp" to TypedValue.COMPLEX_UNIT_DIP,
    "dip" to TypedValue.COMPLEX_UNIT_DIP, // "dip" is an alias for "dp"
    "sp" to TypedValue.COMPLEX_UNIT_SP,
    "pt" to TypedValue.COMPLEX_UNIT_PT,
    "in" to TypedValue.COMPLEX_UNIT_IN,
    "mm" to TypedValue.COMPLEX_UNIT_MM,
    "pc" to TypedValue.COMPLEX_UNIT_PT * 12 // 1 pica = 12 points (standard typographical unit)
)

// --- Public Extension Function ---

/**
 * Converts this dimension string (e.g., "16dp", "50%", "12sp") into a pixel value.
 *
 * The function handles:
 * - **Percentage values:** If the string ends with "%", it's treated as a percentage.
 *   The calculation requires a non-null [parent] [ViewGroup] and the [horizontal] flag
 *   to determine if the percentage is relative to the parent's width or height.
 *   If [parent] is null or its relevant dimension is zero, the percentage calculation defaults to 0.
 * - **Unit-based values:** Strings like "16dp", "12sp", etc., are parsed using [dimensionRegex].
 *   The numeric part is converted to a float, and [TypedValue.applyDimension] is used
 *   with the appropriate unit type from [dimensionUnitToComplexUnitMap] and the provided [metrics].
 * - **Error handling:** If the string format is invalid, the number or unit is unrecognized,
 *   or a percentage calculation cannot be performed, a warning is logged via [logWarning],
 *   and a default value ([DEFAULT_VALUE] or [DEFAULT_FLOAT_VALUE]) is returned.
 *
 * @receiver The dimension string to convert.
 * @param metrics The [DisplayMetrics] required for converting density-independent units (dp, sp) to pixels.
 * @param parent An optional parent [ViewGroup]. Required if the dimension string is a percentage value,
 *               to calculate the percentage against the parent's width or height. Defaults to `null`.
 * @param horizontal If `true` and the value is a percentage, the percentage is calculated based on the
 *                   [parent]'s measured width. If `false`, it's based on the parent's measured height.
 *                   Defaults to `false`. This parameter is ignored if the value is not a percentage.
 * @param asInt If `true`, the resulting pixel value is rounded to the nearest [Int].
 *              If `false`, the result is a [Float]. Defaults to `false`.
 * @return The converted dimension in pixels as a [Number] ([Int] or [Float] based on `asInt`).
 *         Returns default values (0 or 0f) in case of errors.
 */
fun String.toPixels(
    metrics: DisplayMetrics,
    parent: ViewGroup? = null,
    horizontal: Boolean = false,
    asInt: Boolean = false,
): Number {
    val trimmedString = this.trim()

    // Handle percentage values first
    if (trimmedString.endsWith(PERCENTAGE_SYMBOL)) {
        return handlePercentageValue(trimmedString, parent, horizontal, asInt)
    }

    // Handle regular dimension values with units
    return handleDimensionValue(trimmedString, metrics, asInt)
}

// --- Private Helper Functions ---

/**
 * Handles the conversion of percentage-based dimension strings to pixels.
 *
 * @param value The percentage string (e.g., "50%"). It's assumed `endsWith(PERCENTAGE_SYMBOL)` is true.
 * @param parent The parent [ViewGroup] for calculating the percentage against its dimensions.
 * @param horizontal `true` if percentage is relative to parent's width, `false` for height.
 * @param asInt `true` to return an [Int], `false` for a [Float].
 * @return The calculated pixel value as [Number] ([Int] or [Float]). Returns default values on error.
 */
private fun handlePercentageValue(
    value: String,
    parent: ViewGroup?,
    horizontal: Boolean,
    asInt: Boolean,
): Number {
    val percentageString = value.dropLast(1).trim() // Remove '%' and trim
    val percentage = percentageString.toFloatOrNull()

    if (percentage == null) {
        logWarning("Invalid percentage numeric value: '$percentageString' in full string: '$value'. Defaulting to 0.")
        return if (asInt) DEFAULT_VALUE else DEFAULT_FLOAT_VALUE
    }

    if (parent == null) {
        logWarning("Parent ViewGroup is null for percentage value: '$value'. Cannot calculate percentage. Defaulting to 0.")
        return if (asInt) DEFAULT_VALUE else DEFAULT_FLOAT_VALUE
    }

    // Use measuredWidth/Height as they are available after the measure pass.
    // If used before measure pass, these might be 0.
    val parentSize = if (horizontal) parent.measuredWidth else parent.measuredHeight
    if (parentSize == 0) {
        logWarning(
            "Parent ViewGroup's relevant dimension (width/height) is 0 for percentage value: '$value'. " +
                    "Ensure the parent view has been measured. Defaulting to 0."
        )
    }

    val result = (percentage / 100f) * parentSize // Ensure float division

    return if (asInt) (result + ROUNDING_FACTOR).toInt() else result
}

/**
 * Handles the conversion of regular dimension strings (e.g., "16dp", "12sp") to pixels.
 *
 * @param value The dimension string (assumed not to be a percentage).
 * @param metrics [DisplayMetrics] for unit conversion.
 * @param asInt `true` to return an [Int], `false` for a [Float].
 * @return The converted pixel value as [Number] ([Int] or [Float]). Returns default values on error.
 */
private fun handleDimensionValue(
    value: String,
    metrics: DisplayMetrics,
    asInt: Boolean,
): Number {
    val match = dimensionRegex.find(value)
    // Use destructuring declaration with a fallback if match is null or groups are not as expected.
    val (valueStr, unitStr) = match?.destructured ?: run {
        logWarning("Invalid dimension string format: '$value'. Expected format like '16dp' or '12.5sp'. Defaulting to 0.")
        return if (asInt) DEFAULT_VALUE else DEFAULT_FLOAT_VALUE
    }

    val floatValue = valueStr.toFloatOrNull()
    if (floatValue == null) {
        logWarning("Invalid numeric value in dimension string: '$valueStr' from full string: '$value'. Defaulting to 0.")
        return if (asInt) DEFAULT_VALUE else DEFAULT_FLOAT_VALUE
    }

    // Use Locale.ROOT for lowercase to ensure consistent behavior across locales for unit keys.
    val unitType = dimensionUnitToComplexUnitMap[unitStr.lowercase(Locale.ROOT)]
    if (unitType == null) {
        logWarning("Unsupported dimension unit: '$unitStr' in dimension string: '$value'. Supported units: ${dimensionUnitToComplexUnitMap.keys}. Defaulting to 0.")
        return if (asInt) DEFAULT_VALUE else DEFAULT_FLOAT_VALUE
    }

    val pixels = TypedValue.applyDimension(unitType, floatValue, metrics)
    // Add ROUNDING_FACTOR before converting to Int for proper rounding.
    return if (asInt) (pixels + ROUNDING_FACTOR).toInt() else pixels
}

/**
 * Logs a warning message using Android's Logcat.
 * Prepends a standard tag "VoyagerDimensionConverter" to the message.
 *
 * @param message The warning message to log.
 */
private fun logWarning(message: String) {
    // Using a more specific tag for easier filtering in Logcat.
    Log.w("VoyagerDimConverter", message)
}
