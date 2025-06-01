package com.voyager.core.utils.parser

import android.util.Log
import com.voyager.core.utils.logging.LoggerFactory

/**
 * Utility object for parsing numerical string values into various numerical types.
 */
object NumericalParser {

    private val logger = LoggerFactory.getLogger(NumericalParser::class.java.simpleName)

    private const val TAG = "NumericalParser"

    /**
     * Parses a string to an integer, returning 0 if parsing fails.
     * A warning is logged if the input string cannot be parsed as an integer.
     *
     * @param attributeValue The string value to parse.
     * @return The parsed integer value, or 0 if the string is null or cannot be parsed as an integer.
     */
    fun parseInt(attributeValue: String?): Int = attributeValue?.toIntOrNull() ?: run {
        logger.e(TAG, "Failed to parse '$attributeValue' as Int. Returning 0.")
        0
    }

    /**
     * Parses a string to an integer, providing a result object indicating success or failure.
     * This function performs manual parsing and returns an [IntResult] which contains
     * either the parsed integer or an error message.
     * Note: In most cases, [parseInt] using `toIntOrNull` might be more idiomatic and concise.
     *
     * @param s The string value to parse.
     * @return An [IntResult] object. If successful, [IntResult.error] is null and [IntResult.result]
     *         contains the parsed integer. If failed, [IntResult.error] contains a description
     *         of the parsing error.
     */
    fun parseIntUnsafe(s: String?): IntResult {
        s ?: return IntResult("Input string is null")
        var num = 0
        for (char in s) {
            val digit = char - '0'
            if (digit !in 0..9) return IntResult("Malformed integer string: '$s'")
            num = num * 10 + digit
        }
        return IntResult(null, num)
    }

    /**
     * Parses a string to a float, returning 0f if parsing fails.
     *
     * @param value The string value to parse.
     * @return The parsed float value, or 0f if the string is null or cannot be parsed as a float.
     */
    fun parseFloat(value: String?): Float = value?.toFloatOrNull() ?: 0f

    /**
     * Parses a string to a double, returning 0.0 if parsing fails.
     * A warning is logged if the input string cannot be parsed as a double.
     *
     * @param attributeValue The string value to parse.
     * @return The parsed double value, or 0.0 if the string is null or cannot be parsed as a double.
     */
    fun parseDouble(attributeValue: String?): Double = attributeValue?.toDoubleOrNull() ?: run {
        logger.e(TAG, "Failed to parse '$attributeValue' as Double. Returning 0.0.")
        0.0
    }

    /**
     * Data class to hold the result of an integer parsing operation, including potential error information.
     *
     * @property error A string containing an error message if parsing failed, or `null` if successful.
     * @property result The parsed integer value. Defaults to -1, which might indicate an error if [error] is not null.
     */
    data class IntResult(val error: String?, val result: Int = -1)
}