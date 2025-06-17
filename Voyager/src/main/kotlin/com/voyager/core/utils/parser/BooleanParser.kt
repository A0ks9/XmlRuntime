package com.voyager.core.utils.parser

import com.voyager.core.utils.ErrorUtils
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap

/**
 * Utility object for parsing string values into booleans.
 * Provides efficient and thread-safe boolean parsing operations.
 *
 * Key Features:
 * - Case-insensitive parsing
 * - Multiple boolean representations
 * - Thread-safe operations
 * - Performance optimized
 * - Comprehensive error handling
 *
 * Performance Optimizations:
 * - ConcurrentHashMap for thread safety
 * - Cached boolean values
 * - Efficient string operations
 * - Minimal object creation
 *
 * Best Practices:
 * 1. Use isBoolean() to check before parsing
 * 2. Handle null values appropriately
 * 3. Consider case sensitivity
 * 4. Use thread-safe operations
 * 5. Consider performance impact
 *
 * Example Usage:
 * ```kotlin
 * // Check if string is boolean
 * val isBool = "true".isBoolean()
 *
 * // Parse boolean value
 * val value = BooleanParser.parseBoolean("yes")
 * ```
 */
object BooleanParser {
    private val errorUtils by lazy { ErrorUtils("BooleanParser") }

    // Thread-safe map for boolean values
    private val booleanValuesMap by lazy {
        ConcurrentHashMap<String, Boolean>().apply {
            // True values
            this["true"] = true
            this["1"] = true
            this["yes"] = true
            this["t"] = true
            this["on"] = true
            this["y"] = true

            // False values
            this["false"] = false
            this["0"] = false
            this["no"] = false
            this["f"] = false
            this["off"] = false
            this["n"] = false
        }
    }

    /**
     * Checks if this string represents a boolean value (case-insensitive).
     * Thread-safe operation.
     *
     * Performance Considerations:
     * - Uses ConcurrentHashMap for thread safety
     * - Efficient string operations
     * - Minimal object creation
     * - Safe null handling
     *
     * @receiver The string to check.
     * @return `true` if the string is recognized as a boolean value, `false` otherwise.
     */
    val String.isBoolean
        get() = errorUtils.tryOrDefault({
            return@tryOrDefault booleanValuesMap.containsKey(
                this.lowercase(
                    Locale.ROOT
                )
            )
        }, "isBoolean", { "Failed to check boolean value: ${it.message}" }, { false })

    /**
     * Parses a string to a boolean value.
     * Thread-safe operation with error handling.
     *
     * Performance Considerations:
     * - Uses ConcurrentHashMap for thread safety
     * - Efficient string operations
     * - Minimal object creation
     * - Safe null handling
     *
     * @return The parsed boolean value, or false if parsing fails
     */
    val String.toBoolean
        get() = errorUtils.tryOrDefault({
            return@tryOrDefault (this.lowercase(Locale.ROOT).let { booleanValuesMap[it] }
                ?: this.toBooleanStrictOrNull()) == true
        }, "parseBoolean", { "Failed to parse boolean value: ${it.message}" }, { false })

    /**
     * Parses a string to a boolean value with strict validation.
     * Thread-safe operation with error handling.
     *
     * Performance Considerations:
     * - Uses ConcurrentHashMap for thread safety
     * - Efficient string operations
     * - Minimal object creation
     * - Safe null handling
     *
     * @return The parsed boolean value, or null if parsing fails
     */
    val String.toBooleanStrict
        get() = errorUtils.tryOrDefault({
            return@tryOrDefault this.lowercase(Locale.ROOT).let { booleanValuesMap[it] }
                ?: this.toBooleanStrictOrNull()
        }, "parseBooleanStrict", { "Failed to parse boolean value: ${it.message}" }, { null })
} 