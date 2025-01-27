package com.runtimexml.utils

import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.ViewGroup
import java.util.regex.Pattern

/**
 * Object that provides utility functions to convert dimension strings (e.g., "16dp", "20px", "50%")
 * into their corresponding float or integer pixel values.
 */
object DimensionConvertor {

    // Lookup map of dimension units to their corresponding TypedValue constants
    private val dimensionConstantLookup = mapOf(
        "px" to TypedValue.COMPLEX_UNIT_PX,
        "dip" to TypedValue.COMPLEX_UNIT_DIP,
        "dp" to TypedValue.COMPLEX_UNIT_DIP,
        "sp" to TypedValue.COMPLEX_UNIT_SP,
        "pt" to TypedValue.COMPLEX_UNIT_PT,
        "in" to TypedValue.COMPLEX_UNIT_IN,
        "mm" to TypedValue.COMPLEX_UNIT_MM
    )

    // Regex pattern for parsing dimension strings.
    // It matches strings that consist of an optional whitespace followed by a number (with optional decimal part)
    // followed by optional whitespaces, followed by the dimension unit string (e.g. "16dp", " 10.5 px").
    private val DIMENSION_PATTERN = Pattern.compile("^\\s*(\\d+(\\.\\d+)*)\\s*([a-zA-Z]+)\\s*$")

    // Cache to store previously parsed dimension values for performance optimization.
    private val cached = mutableMapOf<String, Float>()

    // A small value that used for floating point comparisons.
    private const val EPSILON = 0.00001f


    /**
     * Converts a dimension string to an integer pixel size.
     *
     * @param dimension The dimension string to convert (e.g., "16dp", "50%", "10px").
     * @param metrics   The display metrics used for conversion.
     * @param parent    The parent ViewGroup to use for percentage calculations
     * @param horizontal Whether the dimension is horizontal.
     * @return The integer pixel size corresponding to the dimension, or 0 for invalid dimension.
     */
    fun stringToDimensionPixelSize(
        dimension: String,
        metrics: DisplayMetrics,
        parent: ViewGroup? = null,
        horizontal: Boolean = false
    ): Int = if (dimension.endsWith("%")) {
        // If dimension is a percentage, calculates the corresponding pixel size based on the parent view dimensions.
        val pct = dimension.substring(0, dimension.length - 1).toFloatOrNull() ?: 0f
        (pct / 100f * (if (horizontal) parent?.measuredWidth ?: 0 else parent?.measuredHeight
            ?: 0)).toInt()

    } else {
        // If dimension is not a percentage, then calls the other stringToDimensionPixelSize method.
        stringToDimensionPixelSize(dimension, metrics)
    }

    /**
     * Converts a dimension string to an integer pixel size.
     *
     * @param dimension The dimension string to convert (e.g., "16dp", "10px").
     * @param metrics   The display metrics used for conversion.
     * @return The integer pixel size corresponding to the dimension, or 0 for invalid dimension.
     */
    fun stringToDimensionPixelSize(dimension: String, metrics: DisplayMetrics): Int =
        cached.getOrPut(dimension) {
            // Try to get the cached value, or create a new value if doesn't exist
            stringToInternalDimension(dimension).let { (value, unit) ->
                //Convert to pixel
                TypedValue.applyDimension(unit, value, metrics)
            }
        }.let { f -> // Check if the float value is zero or positive based on an epsilon
            when {
                f > EPSILON -> (f + 0.5f).toInt() // If positive, return a round integer value
                else -> 0 // otherwise return 0
            }
        }

    /**
     * Converts a dimension string to a float pixel value
     *
     * @param dimension The dimension string to convert (e.g., "16dp", "10px").
     * @param metrics   The display metrics used for conversion.
     * @return The float value of pixel corresponding to the dimension, or 0 if the dimension string is invalid.
     */
    fun stringToDimension(dimension: String, metrics: DisplayMetrics): Float =
        cached.getOrPut(dimension) {
            // If the value is not cached, then, get value by calling `stringToInternalDimension`
            stringToInternalDimension(dimension).let { (value, unit) ->
                //Then use `applyDimension` function to convert the value to pixel.
                TypedValue.applyDimension(unit, value, metrics)
            }
        }

    /**
     * Parses a dimension string into an InternalDimension object.
     *
     * @param dimension The dimension string to parse (e.g., "16dp", "10.5px").
     * @return An InternalDimension object that contains the dimension's value and unit.
     * @throws NumberFormatException If the dimension string is invalid or not following the pattern.
     */
    private fun stringToInternalDimension(dimension: String): InternalDimension {
        val matcher = DIMENSION_PATTERN.matcher(dimension)
        if (matcher.matches()) {
            // Extract the value of dimension from matcher group.
            val value = matcher.group(1)?.toFloatOrNull()
                ?: throw NumberFormatException("Invalid number format: $dimension")
            // Extract the unit from matcher group.
            val unit = matcher.group(3)?.lowercase()
                ?: throw NumberFormatException("Invalid number format: $dimension")

            return dimensionConstantLookup[unit]?.let {
                // Creates and returns `InternalDimension` if unit can be found in dimension lookup.
                InternalDimension(value, it)
            } ?: throw NumberFormatException("Invalid dimension unit: $dimension")
        } else {
            // if the string does not follow dimension pattern, then it will throw an exception.
            Log.e("DimensionConverter", "Invalid number format: $dimension")
            throw NumberFormatException("Invalid number format: $dimension")
        }
    }

    /**
     * Data class that holds the parsed value and unit of a dimension.
     *
     * @param value The numerical value of the dimension.
     * @param unit The dimension unit (e.g. `TypedValue.COMPLEX_UNIT_DIP`).
     */
    private data class InternalDimension(val value: Float, val unit: Int)
}