package com.voyager.core.utils.parser

import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.ViewGroup
import com.voyager.core.utils.logging.LoggerFactory
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap

/**
 * Utility object for converting dimension strings to pixel values.
 * Provides efficient and thread-safe dimension conversion operations.
 *
 * Key Features:
 * - Dimension string parsing
 * - Percentage calculations
 * - Thread-safe operations
 * - Performance optimized
 * - Comprehensive error handling
 *
 * Performance Optimizations:
 * - ConcurrentHashMap for caching
 * - Efficient string operations
 * - Minimal object creation
 * - Safe resource handling
 *
 * Best Practices:
 * 1. Use appropriate dimension units
 * 2. Handle null values appropriately
 * 3. Consider screen density
 * 4. Use thread-safe operations
 * 5. Consider performance impact
 *
 * Example Usage:
 * ```kotlin
 * // Convert dimension to pixels
 * val pixels = "16dp".toPixels(metrics)
 *
 * // Convert percentage to pixels
 * val percentagePixels = "50%".toPixels(metrics, parent)
 * ```
 */
object DimensionConverter {
    private val logger = LoggerFactory.getLogger(DimensionConverter::class.java.simpleName)

    // Constants
    private const val PERCENTAGE_SYMBOL = "%"
    private const val DEFAULT_VALUE = 0
    private const val DEFAULT_FLOAT_VALUE = 0f
    private const val ROUNDING_FACTOR = 0.5f

    // Thread-safe cache for dimension conversions
    private val dimensionCache = ConcurrentHashMap<String, Float>()

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
        "pc" to TypedValue.COMPLEX_UNIT_PT.times(12)
    )

    /**
     * Converts a dimension string to pixel value.
     * Thread-safe operation with caching.
     *
     * Performance Considerations:
     * - Uses ConcurrentHashMap for caching
     * - Efficient string operations
     * - Minimal object creation
     * - Safe resource handling
     *
     * @receiver The dimension string
     * @param metrics DisplayMetrics for density conversion
     * @param parent Optional parent ViewGroup for percentage calculation
     * @param horizontal True for parent width, false for parent height
     * @param asInt True to return Int, false for Float
     * @return The converted dimension in pixels
     */
    fun String.toPixels(
        metrics: DisplayMetrics,
        parent: ViewGroup? = null,
        horizontal: Boolean = false,
        asInt: Boolean = false,
    ): Number = try {
        val trimmedString = this.trim()
        val cacheKey = "$trimmedString:${parent?.id}:$horizontal:$asInt"

        dimensionCache.getOrPut(cacheKey) {
            if (trimmedString.endsWith(PERCENTAGE_SYMBOL)) {
                handlePercentageValue(trimmedString, parent, horizontal, asInt).toFloat()
            } else {
                handleDimensionValue(trimmedString, metrics, asInt).toFloat()
            }
        }.let { if (asInt) it.toInt() else it }
    } catch (e: Exception) {
        logger.error("toPixels", "Failed to convert dimension: ${e.message}")
        if (asInt) DEFAULT_VALUE else DEFAULT_FLOAT_VALUE
    }

    /**
     * Handles percentage value conversion.
     * Thread-safe operation.
     *
     * @param value The percentage string
     * @param parent The parent ViewGroup
     * @param horizontal True for parent width, false for parent height
     * @param asInt True to return Int, false for Float
     * @return The converted percentage value
     */
    private fun handlePercentageValue(
        value: String,
        parent: ViewGroup?,
        horizontal: Boolean,
        asInt: Boolean,
    ): Number {
        try {
            val percentageString = value.dropLast(1).trim()
            val percentage = percentageString.toFloatOrNull()

            if (percentage == null) {
                logger.warn(
                    "handlePercentageValue", "Invalid percentage numeric value: '$percentageString'"
                )
                return if (asInt) DEFAULT_VALUE else DEFAULT_FLOAT_VALUE
            }

            if (parent == null) {
                logger.warn(
                    "handlePercentageValue",
                    "Parent ViewGroup is null for percentage value: '$value'"
                )
                return if (asInt) DEFAULT_VALUE else DEFAULT_FLOAT_VALUE
            }

            val parentSize = if (horizontal) parent.measuredWidth else parent.measuredHeight
            if (parentSize == 0) {
                logger.warn(
                    "handlePercentageValue",
                    "Parent ViewGroup's relevant dimension is 0 for '$value'"
                )
            }

            val result = (percentage.div(100f)).times(parentSize)
            return if (asInt) (result.plus(ROUNDING_FACTOR)).toInt() else result
        } catch (e: Exception) {
            logger.error(
                "handlePercentageValue",
                "Failed to handle percentage value: ${e.message}",
                throwable = e
            )
            return if (asInt) DEFAULT_VALUE else DEFAULT_FLOAT_VALUE
        }
    }

    /**
     * Handles dimension value conversion.
     * Thread-safe operation.
     *
     * @param value The dimension string
     * @param metrics DisplayMetrics for density conversion
     * @param asInt True to return Int, false for Float
     * @return The converted dimension value
     */
    private fun handleDimensionValue(
        value: String,
        metrics: DisplayMetrics,
        asInt: Boolean,
    ): Number {
        try {
            val match = dimensionRegex.find(value)
            val (valueStr, unitStr) = match?.destructured ?: run {
                logger.warn("handleDimensionValue", "Invalid dimension string format: '$value'")
                return if (asInt) DEFAULT_VALUE else DEFAULT_FLOAT_VALUE
            }

            val floatValue = valueStr.toFloatOrNull()
            if (floatValue == null) {
                logger.warn(
                    "handleDimensionValue", "Invalid numeric value in dimension string: '$valueStr'"
                )
                return if (asInt) DEFAULT_VALUE else DEFAULT_FLOAT_VALUE
            }

            val unitType = dimensionUnitToComplexUnitMap[unitStr.lowercase(Locale.ROOT)]
            if (unitType == null) {
                logger.warn("handleDimensionValue", "Unsupported dimension unit: '$unitStr'")
                return if (asInt) DEFAULT_VALUE else DEFAULT_FLOAT_VALUE
            }

            val pixels = TypedValue.applyDimension(unitType, floatValue, metrics)
            return if (asInt) (pixels.plus(ROUNDING_FACTOR)).toInt() else pixels
        } catch (e: Exception) {
            logger.error(
                "handleDimensionValue",
                "Failed to handle dimension value: ${e.message}",
                throwable = e
            )
            return if (asInt) DEFAULT_VALUE else DEFAULT_FLOAT_VALUE
        }
    }

    /**
     * Clears the dimension cache.
     * Useful for testing or when configuration changes.
     * Thread-safe operation.
     */
    fun clearCache() {
        dimensionCache.clear()
        logger.debug("clearCache", "Dimension cache cleared")
    }
}
