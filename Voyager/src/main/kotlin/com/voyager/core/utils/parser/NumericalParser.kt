package com.voyager.core.utils.parser

import com.voyager.core.model.ConfigManager
import com.voyager.core.utils.ErrorUtils
import com.voyager.core.utils.logging.LoggerFactory
import java.util.concurrent.ConcurrentHashMap

/**
 * Utility object for parsing numerical string values into various numerical types.
 * Provides efficient and thread-safe numerical parsing operations.
 *
 * Key Features:
 * - Integer parsing
 * - Float parsing
 * - Double parsing
 * - Thread-safe operations
 * - Performance optimized
 * - Comprehensive error handling
 *
 * Performance Optimizations:
 * - ConcurrentHashMap for caching
 * - Efficient string operations
 * - Minimal object creation
 * - Safe null handling
 *
 * Best Practices:
 * 1. Use appropriate numerical types
 * 2. Handle null values appropriately
 * 3. Consider number ranges
 * 4. Use thread-safe operations
 * 5. Consider performance impact
 *
 * Example Usage:
 * ```kotlin
 * // Parse integer
 * val intValue = NumericalParser.parseInt("42")
 *
 * // Parse float
 * val floatValue = NumericalParser.parseFloat("3.14")
 *
 * // Parse double
 * val doubleValue = NumericalParser.parseDouble("3.14159")
 * ```
 */
object NumericalParser {

    private val logger by lazy { LoggerFactory.getLogger("NumericalParser") }
    private val isLoggingEnabled by lazy { ConfigManager.config.isLoggingEnabled }
    private val errorUtils by lazy { ErrorUtils("NumericalParser") }

    // Thread-safe cache for parsed integers
    private val intCache by lazy { ConcurrentHashMap<String, Int>() }

    // Thread-safe cache for parsed floats
    private val floatCache by lazy { ConcurrentHashMap<String, Float>() }

    // Thread-safe cache for parsed doubles
    private val doubleCache by lazy { ConcurrentHashMap<String, Double>() }

    /**
     * Parses a string to an integer, returning 0 if parsing fails.
     * Thread-safe operation with caching.
     *
     * Performance Considerations:
     * - Uses ConcurrentHashMap for caching
     * - Efficient string operations
     * - Minimal object creation
     * - Safe null handling
     *
     * @param attributeValue The string value to parse
     * @return The parsed integer value, or 0 if the string is null or cannot be parsed as an integer
     */
    fun parseInt(attributeValue: String?): Int = errorUtils.tryOrDefault({
        if (attributeValue == null) {
            if (isLoggingEnabled) {
                logger.warn("parseInt", "Null Integer value provided")
            }
            return@tryOrDefault 0
        }

        intCache.getOrPut(attributeValue) {
            attributeValue.toIntOrNull() ?: run {
                if (isLoggingEnabled) {
                    logger.error(
                        "parseInt", "Failed to parse '$attributeValue' as Int. Returning 0."
                    )
                }
                return@tryOrDefault 0
            }
        }
    }, "parseInt", { "Failed to parse integer: ${it.message}" }, { 0 })

    /**
     * Parses a string to an integer, providing a result object indicating success or failure.
     * Thread-safe operation with caching.
     *
     * Performance Considerations:
     * - Uses ConcurrentHashMap for caching
     * - Efficient string operations
     * - Minimal object creation
     * - Safe null handling
     *
     * @param s The string value to parse
     * @return An [IntResult] object containing either the parsed integer or an error message
     */
    fun parseIntUnsafe(s: String?): IntResult = errorUtils.tryOrDefault(
        {
        if (s == null) {
            return@tryOrDefault IntResult("Input string is null")
        }

        var num = 0
        for (char in s) {
            val digit = char - '0'
            if (digit !in 0..9) {
                return@tryOrDefault IntResult("Malformed integer string: '$s'")
            }
            num = num * 10 + digit
        }
        return@tryOrDefault IntResult(null, num)
    },
        "parseIntUnsafe",
        { "Failed to parse integer: ${it.message}" },
        { IntResult("Failed to parse integer: ${it.message}") })

    /**
     * Parses a string to a float, returning 0f if parsing fails.
     * Thread-safe operation with caching.
     *
     * Performance Considerations:
     * - Uses ConcurrentHashMap for caching
     * - Efficient string operations
     * - Minimal object creation
     * - Safe null handling
     *
     * @param value The string value to parse
     * @return The parsed float value, or 0f if the string is null or cannot be parsed as a float
     */
    fun parseFloat(value: String?): Float = errorUtils.tryOrDefault({
        if (value == null) {
            if (isLoggingEnabled) {
                logger.warn("parseFloat", "Null float value provided")
            }
            return@tryOrDefault 0f
        }

        floatCache.getOrPut(value) {
            value.toFloatOrNull() ?: run {
                if (isLoggingEnabled) {
                    logger.error(
                        "parseFloat", "Failed to parse '$value' as Float. Returning 0f."
                    )
                }
                return@tryOrDefault 0f
            }
        }
    }, "parseFloat", { "Failed to parse float: ${it.message}" }, { 0f })

    /**
     * Parses a string to a double, returning 0.0 if parsing fails.
     * Thread-safe operation with caching.
     *
     * Performance Considerations:
     * - Uses ConcurrentHashMap for caching
     * - Efficient string operations
     * - Minimal object creation
     * - Safe null handling
     *
     * @param attributeValue The string value to parse
     * @return The parsed double value, or 0.0 if the string is null or cannot be parsed as a double
     */
    fun parseDouble(attributeValue: String?): Double = errorUtils.tryOrDefault({
        if (attributeValue == null) {
            if (isLoggingEnabled) {
                logger.warn("parseDouble", "Null double value provided")
            }
            return@tryOrDefault 0.0
        }

        doubleCache.getOrPut(attributeValue) {
            attributeValue.toDoubleOrNull() ?: run {
                if (isLoggingEnabled) {
                    logger.error(
                        "parseDouble", "Failed to parse '$attributeValue' as Double. Returning 0.0."
                    )
                }
                return@tryOrDefault 0.0
            }
        }
    }, "parseDouble", { "Failed to parse double: ${it.message}" }, { 0.0 })

    /**
     * Data class to hold the result of an integer parsing operation.
     *
     * @property error A string containing an error message if parsing failed, or `null` if successful
     * @property result The parsed integer value, defaults to -1
     */
    data class IntResult(val error: String?, val result: Int = -1)

    /**
     * Clears all numerical caches.
     * Useful for testing or when configuration changes.
     * Thread-safe operation.
     */
    fun clearCache() {
        intCache.clear()
        floatCache.clear()
        doubleCache.clear()
        if (isLoggingEnabled) {
            logger.debug("clearCache", "Numerical caches cleared")
        }
    }
}