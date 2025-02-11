package com.flipkart.android.proteus.toolbox

import androidx.annotation.IntDef
import com.flipkart.android.proteus.value.Array
import com.flipkart.android.proteus.value.ObjectValue
import com.flipkart.android.proteus.value.Value
import kotlin.Array as array

/**
 * Kotlin object containing utility functions and constants for Proteus library.
 */
object Utils { // Converted class to object as it contains only static members

    /**
     * Library name.
     */
    const val LIB_NAME = "proteus" // Converted to const val

    /**
     * Library version.
     */
    const val VERSION = "5.0.0-SNAPSHOT" // Converted to const val

    /**
     * Style constant for no quotes.
     */
    const val STYLE_NONE = 0 // Converted to const val

    /**
     * Style constant for single quotes.
     */
    const val STYLE_SINGLE = 1 // Converted to const val

    /**
     * Style constant for double quotes.
     */
    const val STYLE_DOUBLE = 2 // Converted to const val

    /**
     * Adds all entries from [source] ObjectValue to [destination] ObjectValue,
     * only if the key does not already exist in the [destination].
     *
     * @param destination The ObjectValue to which entries will be added.
     * @param source      The ObjectValue from which entries will be taken.
     * @return The modified [destination] ObjectValue.
     */
    fun addAllEntries(
        destination: ObjectValue, source: ObjectValue
    ): ObjectValue { // Converted to Kotlin function
        source.forEach { key, value -> // Using forEach for cleaner iteration over Map.Entry
            if (destination[key] == null) { // Using map-like access for ObjectValue and null check
                destination[key] = value // Add entry if key is not in destination
            }
        }
        return destination // Return the modified destination
    }

    /**
     * Joins an array of strings with the given delimiter.
     *
     * @param array     The array of strings to join.
     * @param delimiter The delimiter to use.
     * @return The joined string.
     */
    fun join(array: array<String>, delimiter: String): String { // Converted to Kotlin function
        return array.joinToString(delimiter) // Using Kotlin's built-in joinToString for simplicity and efficiency
    }

    /**
     * Joins a Proteus Array with the given delimiter and quoting style.
     *
     * @param array     The Proteus Array to join.
     * @param delimiter The delimiter to use.
     * @param style     The quoting style to apply to primitive values. Use [STYLE_NONE], [STYLE_SINGLE], or [STYLE_DOUBLE].
     * @return The joined string.
     */
    fun join(
        array: Array, delimiter: String, @QuoteStyle style: Int
    ): String { // Converted to Kotlin function, kept annotation
        val sb = StringBuilder()
        for (i in 0 until array.size()) { // Using until for index iteration, more Kotlin-like
            val value = array[i] // Using array-like access for Proteus Array
            if (value.isPrimitive) {
                val primitive = value.asPrimitive // Using 'as' cast, Kotlin style
                val string = when (style) { // Using when expression for cleaner switch-case
                    STYLE_NONE -> primitive.asString()
                    STYLE_SINGLE -> primitive.getAsSingleQuotedString()
                    STYLE_DOUBLE -> primitive.getAsDoubleQuotedString()
                    else -> primitive.asString() // Default case, same as Java
                }
                sb.append(string)
            } else {
                sb.append(value.toString())
            }
            if (i < array.size() - 1) {
                sb.append(delimiter)
            }
        }
        return sb.toString()
    }

    /**
     * Overload of [join] with default [STYLE_NONE] quoting style.
     *
     * @param array     The Proteus Array to join.
     * @param delimiter The delimiter to use.
     * @return The joined string.
     */
    fun join(array: Array, delimiter: String): String { // Overload function
        return join(array, delimiter, STYLE_NONE) // Call the main join function with default style
    }

    /**
     * Overload of [join] that takes a [Value] array and converts it to a Proteus [Array] before joining.
     *
     * @param array     The array of Proteus Values to join.
     * @param delimiter The delimiter to use.
     * @param style     The quoting style to apply.
     * @return The joined string.
     */
    fun join(
        array: array<Value>, delimiter: String, @QuoteStyle style: Int
    ): String { // Overload function
        return join(
            Array(*array), delimiter, style
        ) // Create Proteus Array and call main join function
    }

    /**
     * Overload of [join] with default [STYLE_NONE] quoting style, taking a [Value] array.
     *
     * @param array     The array of Proteus Values to join.
     * @param delimiter The delimiter to use.
     * @return The joined string.
     */
    fun join(array: array<Value>, delimiter: String): String { // Overload function
        return join(
            Array(*array), delimiter
        ) // Create Proteus Array and call main join function with default style
    }

    /**
     * Returns the library version string in the format "LIB_NAME:VERSION".
     *
     * @return The version string.
     */
    fun getVersion(): String { // Converted to Kotlin function
        return "$LIB_NAME:$VERSION" // Using string interpolation for cleaner string formatting
    }

    /**
     * Defines the possible quoting styles for the [join] function.
     *
     * These styles determine how primitive values in the array are quoted in the joined string.
     */
    @IntDef(value = [STYLE_NONE, STYLE_SINGLE, STYLE_DOUBLE]) // Kept annotation, using value = [...] for clarity
    @Retention(AnnotationRetention.SOURCE)
    annotation class QuoteStyle // Kept annotation class
}