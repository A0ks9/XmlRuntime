package com.voyager.core.utils.parser

import java.util.Locale

// A set of lowercase string values that are considered boolean representations.
private val booleanValuesSet =
    hashSetOf("true", "false", "0", "1", "yes", "no", "t", "f", "on", "off")

/**
 * Utility object for parsing string values into booleans.
 */
object BooleanParser {

    /**
     * Checks if this string represents a boolean value (case-insensitive).
     * It compares the lowercase string against a predefined set of common boolean representations
     * (e.g., "true", "false", "0", "1", "yes", "no").
     *
     * @receiver The string to check.
     * @return `true` if the string is recognized as a boolean value, `false` otherwise.
     */
    fun String.isBoolean(): Boolean = booleanValuesSet.contains(this.lowercase(Locale.ROOT))

    /**
     * Parses a string to a boolean value.
     *
     * @param value The string value to parse
     * @return The parsed boolean value
     */
    fun parseBoolean(value: String?): Boolean =
        (value?.lowercase(Locale.ROOT)?.let { booleanValuesSet.contains(it) } ?: value?.toBooleanStrictOrNull()) == true
} 