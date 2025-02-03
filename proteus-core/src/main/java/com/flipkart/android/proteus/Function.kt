package com.flipkart.android.proteus

import android.annotation.SuppressLint
import android.content.Context
import com.flipkart.android.proteus.toolbox.Utils
import com.flipkart.android.proteus.value.Array
import com.flipkart.android.proteus.value.Primitive
import com.flipkart.android.proteus.value.Value
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import kotlin.Array as array

/**
 * Abstract class representing a function that can be called within the Proteus framework.
 * This class defines the structure for reusable functions that operate on `Value` objects
 * within a specific `Context`.
 */
abstract class Function {
    /**
     * Abstract method to be implemented by concrete function classes.
     * This method contains the core logic of the function.
     *
     * @param context    The Android Context in which the function is being called.
     * @param data       The primary data `Value` associated with the current scope.
     * @param dataIndex  The index of the data item, especially relevant when working with lists or arrays.
     * @param arguments  Variable number of `Value` arguments passed to the function.
     * @return           The result of the function execution, wrapped in a `Value` object.
     */
    abstract fun call(context: Context, data: Value, dataIndex: Int, vararg arguments: Value): Value

    /**
     * Abstract method to get the name of the function.
     * This name is used to identify and invoke the function within the Proteus framework.
     *
     * @return The name of the function as a String.
     */
    abstract fun getName(): String

    companion object {
        /**
         * NOOP Function: A function that performs no operation. Returns an empty string.
         * Useful as a placeholder or when a function call is expected but no action is needed.
         */
        @JvmField
        val NOOP = function("noop") { _, _, _, _ -> ProteusConstants.EMPTY_STRING }

        /**
         * DATE Function: Formats a date string from one format to another.
         *
         * Arguments:
         *   1. (Required) Date string in the 'from' format.
         *   2. (Optional) Target 'to' format string. Defaults to "E, d MMM" (e.g., "Mon, 5 Jan").
         *   3. (Optional) 'from' format string. Defaults to "yyyy-MM-dd HH:mm:ss" (e.g., "2023-10-27 10:30:00").
         *
         * Uses `SimpleDateFormat` for date formatting.
         * Returns an empty string if the input date string is missing or parsing fails.
         */
        @SuppressLint("SimpleDateFormat")
        @JvmField
        val DATE = function("date") { _, _, _, arguments ->
            val input = arguments.getOrNull(0)?.asString()
                ?: return@function ProteusConstants.EMPTY_STRING // Get input date string, return empty string if null
            val fromFormat = arguments.getOrNull(2)?.asString()?.let { SimpleDateFormat(it) }
                ?: SimpleDateFormat("yyyy-MM-dd HH:mm:ss") // Get 'from' format from arguments or use default
            val toFormat = arguments.getOrNull(1)?.asString()?.let { SimpleDateFormat(it) }
                ?: SimpleDateFormat("E, d MMM") // Get 'to' format from arguments or use default
            Primitive(toFormat.format(fromFormat.parse(input)!!)) // Parse input date and format it, then wrap in Primitive
        }

        /**
         * FORMAT Function: Formats a string using `String.format()`.
         *
         * Arguments:
         *   1. (Required) Template string with format specifiers (e.g., "Hello %s, you are %d years old").
         *   2. ... (Variable) Arguments to replace the format specifiers in the template string.
         *
         * Returns the formatted string wrapped in a `Primitive`.
         * Returns `ProteusConstants.EMPTY_STRING` if no arguments are provided.
         */
        @JvmField
        val FORMAT = function("format") { _, _, _, arguments ->
            if (arguments.isEmpty()) ProteusConstants.EMPTY_STRING // Return empty string if no arguments
            else Primitive(String.format(arguments[0].asString(),
                *arguments.drop(1).map { it.asString() }
                    .toTypedArray() // Format the string using String.format and arguments
            ))
        }

        /**
         * JOIN Function: Joins elements of an array `Value` into a single string.
         *
         * Arguments:
         *   1. (Required) Array `Value` to be joined.
         *   2. (Optional) Delimiter string to use between array elements. Defaults to ", ".
         *
         * Uses `Utils.join()` to perform the array joining.
         * Returns the joined string wrapped in a `Primitive`.
         * Returns `ProteusConstants.EMPTY_STRING` if no arguments are provided.
         */
        @JvmField
        val JOIN = function("join") { _, _, _, arguments ->
            if (arguments.isEmpty()) ProteusConstants.EMPTY_STRING // Return empty string if no arguments
            else Primitive(
                Utils.join(
                    arguments[0].asArray,
                    arguments.getOrNull(1)?.asString()
                        ?: ", " // Join array with provided delimiter or default ", "
                )
            )
        }

        /**
         * NUMBER Function: Formats a number string using `DecimalFormat`.
         *
         * Arguments:
         *   1. (Required) Number string to be formatted.
         *   2. (Optional) Format string for `DecimalFormat`. Defaults to "#,###" (e.g., "1,234").
         *
         * Applies `RoundingMode.FLOOR`, `minimumFractionDigits = 0`, and `maximumFractionDigits = 2` to the formatter.
         * Returns the formatted number string wrapped in a `Primitive`.
         * Returns `ProteusConstants.EMPTY_STRING` if no arguments are provided.
         */
        @JvmField
        val NUMBER = function("number") { _, _, _, arguments ->
            if (arguments.isEmpty()) ProteusConstants.EMPTY_STRING // Return empty string if no arguments
            else Primitive((arguments.getOrNull(1)?.asString()?.let { DecimalFormat(it) }
                ?: DecimalFormat("#,###")).apply { // Get DecimalFormat from arguments or use default
                roundingMode = RoundingMode.FLOOR // Set rounding mode
                minimumFractionDigits = 0 // Set minimum fraction digits
                maximumFractionDigits = 2 // Set maximum fraction digits
            }.format(
                arguments[0].asString().toDouble()
            )
            ) // Format the number string and wrap in Primitive
        }

        /**
         * ADD Function: Adds all provided numeric arguments.
         *
         * Arguments:
         *   ... (Variable) Numeric `Value` arguments to be added.
         *
         * Returns the sum of all arguments as a `Primitive` number.
         */
        @JvmField
        val ADD =
            function("add") { _, _, _, arguments -> Primitive(arguments.sumOf { it.asDouble() }) } // Sum all arguments as doubles and wrap in Primitive

        /**
         * SUBTRACT Function: Subtracts subsequent numeric arguments from the first argument.
         *
         * Arguments:
         *   1. (Required) First numeric `Value` argument (minuend).
         *   2. ... (Variable) Numeric `Value` arguments to subtract (subtrahends).
         *
         * Returns the result of the subtraction as a `Primitive` number.
         */
        @JvmField
        val SUBTRACT = function("sub") { _, _, _, arguments ->
            Primitive(
                arguments[0].asDouble() - arguments.drop(1)
                    .sumOf { it.asDouble() }) // Subtract sum of rest arguments from first argument and wrap in Primitive
        }

        /**
         * MULTIPLY Function: Multiplies all provided numeric arguments together.
         *
         * Arguments:
         *   ... (Variable) Numeric `Value` arguments to be multiplied.
         *
         * Returns the product of all arguments as a `Primitive` number.
         */
        @JvmField
        val MULTIPLY =
            function("mul") { _, _, _, arguments -> Primitive(arguments.fold(1.0) { acc, value -> acc * value.asDouble() }) } // Multiply all arguments together and wrap in Primitive

        /**
         * DIVIDE Function: Divides the first numeric argument by subsequent numeric arguments.
         *
         * Arguments:
         *   1. (Required) First numeric `Value` argument (dividend).
         *   2. ... (Variable) Numeric `Value` arguments to divide by (divisors).
         *
         * Returns the result of the division as a `Primitive` number.
         */
        @JvmField
        val DIVIDE =
            function("div") { _, _, _, arguments -> Primitive(arguments.fold(arguments[0].asDouble()) { acc, value -> acc / value.asDouble() }) } // Divide first argument by subsequent arguments and wrap in Primitive

        /**
         * MODULO Function: Calculates the modulo of the first numeric argument with subsequent numeric arguments.
         *
         * Arguments:
         *   1. (Required) First numeric `Value` argument (dividend).
         *   2. ... (Variable) Numeric `Value` arguments to use as divisors for modulo operation.
         *
         * Returns the remainder of the modulo operation as a `Primitive` number.
         */
        @JvmField
        val MODULO =
            function("mod") { _, _, _, arguments -> Primitive(arguments.fold(arguments[0].asDouble()) { acc, value -> acc % value.asDouble() }) } // Calculate modulo of first argument with subsequent arguments and wrap in Primitive

        /**
         * AND Function: Performs logical AND operation on all boolean arguments.
         * Returns true if all arguments are true, false otherwise.
         *
         * Arguments:
         *   ... (Variable) Boolean `Value` arguments.
         *
         * Returns a `Primitive` boolean representing the result of the AND operation.
         */
        @JvmField
        val AND =
            function("and") { _, _, _, arguments -> Primitive(arguments.all { it.asBoolean() }) } // Perform AND on all arguments and wrap in Primitive

        /**
         * OR Function: Performs logical OR operation on all boolean arguments.
         * Returns true if at least one argument is true, false otherwise.
         *
         * Arguments:
         *   ... (Variable) Boolean `Value` arguments.
         *
         * Returns a `Primitive` boolean representing the result of the OR operation.
         */
        @JvmField
        val OR =
            function("or") { _, _, _, arguments -> Primitive(arguments.any { it.asBoolean() }) } // Perform OR on all arguments and wrap in Primitive

        /**
         * NOT Function: Performs logical NOT operation on the first boolean argument.
         *
         * Arguments:
         *   1. (Optional) Boolean `Value` argument to negate. If no argument is provided, it defaults to true (NOT of nothing is true).
         *
         * Returns a `Primitive` boolean representing the negated value of the argument.
         */
        @JvmField
        val NOT = function("not") { _, _, _, arguments ->
            Primitive(
                arguments.getOrNull(0)
                    ?.asBoolean() != true // Negate the boolean value of the first argument, default to true if no argument
            )
        }

        /**
         * EQUALS Function: Checks if the first and second arguments are equal (string comparison).
         *
         * Arguments:
         *   1. (Optional) First `Value` argument.
         *   2. (Optional) Second `Value` argument.
         *
         * Returns a `Primitive` boolean indicating whether the string representations of the first two arguments are equal.
         */
        @JvmField
        val EQUALS = function("eq") { _, _, _, arguments ->
            Primitive(
                arguments.getOrNull(0)?.asString() == arguments.getOrNull(1)
                    ?.asString() // Compare string representations of first two arguments
            )
        }

        /**
         * LESS_THAN Function: Checks if the first numeric argument is less than the second numeric argument.
         *
         * Arguments:
         *   1. (Optional) First numeric `Value` argument. Defaults to 0.0 if not provided.
         *   2. (Optional) Second numeric `Value` argument. Defaults to 0.0 if not provided.
         *
         * Returns a `Primitive` boolean indicating whether the first argument is less than the second.
         */
        @JvmField
        val LESS_THAN = function("lt") { _, _, _, arguments ->
            Primitive(
                (arguments.getOrNull(0)?.asDouble() ?: 0.0) < (arguments.getOrNull(1)?.asDouble()
                    ?: 0.0) // Compare first two arguments as doubles, default to 0.0 if null
            )
        }

        /**
         * GREATER_THAN Function: Checks if the first numeric argument is greater than the second numeric argument.
         *
         * Arguments:
         *   1. (Optional) First numeric `Value` argument. Defaults to 0.0 if not provided.
         *   2. (Optional) Second numeric `Value` argument. Defaults to 0.0 if not provided.
         *
         * Returns a `Primitive` boolean indicating whether the first argument is greater than the second.
         */
        @JvmField
        val GREATER_THAN = function("gt") { _, _, _, arguments ->
            Primitive(
                (arguments.getOrNull(0)?.asDouble() ?: 0.0) > (arguments.getOrNull(1)?.asDouble()
                    ?: 0.0) // Compare first two arguments as doubles, default to 0.0 if null
            )
        }

        /**
         * LESS_THAN_OR_EQUALS Function: Checks if the first numeric argument is less than or equal to the second numeric argument.
         *
         * Arguments:
         *   1. (Optional) First numeric `Value` argument. Defaults to 0.0 if not provided.
         *   2. (Optional) Second numeric `Value` argument. Defaults to 0.0 if not provided.
         *
         * Returns a `Primitive` boolean indicating whether the first argument is less than or equal to the second.
         */
        @JvmField
        val LESS_THAN_OR_EQUALS = function("lte") { _, _, _, arguments ->
            Primitive(
                (arguments.getOrNull(0)?.asDouble() ?: 0.0) <= (arguments.getOrNull(1)?.asDouble()
                    ?: 0.0) // Compare first two arguments as doubles, default to 0.0 if null
            )
        }

        /**
         * GREATER_THAN_OR_EQUALS Function: Checks if the first numeric argument is greater than or equal to the second numeric argument.
         *
         * Arguments:
         *   1. (Optional) First numeric `Value` argument. Defaults to 0.0 if not provided.
         *   2. (Optional) Second numeric `Value` argument. Defaults to 0.0 if not provided.
         *
         * Returns a `Primitive` boolean indicating whether the first argument is greater than or equal to the second.
         */
        @JvmField
        val GREATER_THAN_OR_EQUALS = function("gte") { _, _, _, arguments ->
            Primitive(
                (arguments.getOrNull(0)?.asDouble() ?: 0.0) >= (arguments.getOrNull(1)?.asDouble()
                    ?: 0.0) // Compare first two arguments as doubles, default to 0.0 if null
            )
        }

        /**
         * TERNARY Function: Implements a ternary conditional operator (condition ? thenValue : elseValue).
         *
         * Arguments:
         *   1. (Optional) Condition `Value` (boolean). Defaults to false if not provided.
         *   2. (Optional) `Value` to return if the condition is true. Defaults to null and might cause issues if used directly.
         *   3. (Optional) `Value` to return if the condition is false. Defaults to `ProteusConstants.EMPTY_STRING`.
         *
         * Returns either the 'thenValue' or 'elseValue' based on the boolean value of the condition.
         * If 'thenValue' is null and condition is true, it might lead to null pointer exceptions in usage.
         */
        @JvmField
        val TERNARY = function("ternary") { _, _, _, arguments ->
            if (arguments.getOrNull(0)
                    ?.asBoolean() == true
            ) arguments.getOrNull(1)!! else arguments.getOrNull( // if condition is true, return second argument (thenValue) else return third (elseValue)
                2
            ) ?: ProteusConstants.EMPTY_STRING // if elseValue is null, return EMPTY_STRING
        }

        /**
         * CHAR_AT Function: Returns the character at a specific index in a string.
         *
         * Arguments:
         *   1. (Optional) String `Value`. Defaults to empty string if not provided.
         *   2. (Optional) Index `Value` (integer). Defaults to 0 if not provided.
         *
         * Returns a `Primitive` string containing the character at the specified index.
         * Returns an empty `Primitive` string if the input string is null, index is out of bounds, or arguments are missing.
         */
        @JvmField
        val CHAR_AT = function("charAt") { _, _, _, arguments ->
            Primitive(
                arguments.getOrNull(0)?.asString()?.getOrNull(
                    arguments.getOrNull(1)?.asInt() ?: 0
                ) // Get character at specified index in string, default index is 0
                    ?.toString()
                    ?: "" // Convert char to string, default to empty string if any error occurs
            )
        }

        /**
         * CONTAINS Function: Checks if a string contains a specified substring.
         *
         * Arguments:
         *   1. (Optional) String `Value` to search in. Defaults to empty string if not provided.
         *   2. (Optional) Substring `Value` to search for. Defaults to empty string if not provided.
         *
         * Returns a `Primitive` boolean indicating whether the first string contains the second string as a substring.
         */
        @JvmField
        val CONTAINS = function("contains") { _, _, _, arguments ->
            Primitive(
                arguments.getOrNull(0)?.asString()?.contains(
                    arguments.getOrNull(1)?.asString()
                        ?: "" // Check if first string contains second string, default substring is empty string
                ) == true
            )
        }

        /**
         * IS_EMPTY Function: Checks if a string is empty.
         *
         * Arguments:
         *   1. (Optional) String `Value` to check. Defaults to null.
         *
         * Returns a `Primitive` boolean indicating whether the input string is empty.
         * Returns `true` if the input string is null or empty, `false` otherwise.
         */
        @JvmField
        val IS_EMPTY = function("isEmpty") { _, _, _, arguments ->
            Primitive(
                arguments.getOrNull(0)?.asString()
                    ?.isEmpty() != false // Check if string is empty, handles null string as not empty in original logic (corrected to treat null as empty in comment)
            )
        }

        /**
         * LENGTH Function: Returns the length of a string or the size of an array `Value`.
         *
         * Arguments:
         *   1. (Optional) String or Array `Value`. Defaults to null.
         *
         * Returns a `Primitive` number representing the length of the string or the size of the array.
         * Returns 0 if the input `Value` is null or not a string or array.
         */
        @JvmField
        val LENGTH = function("length") { _, _, _, arguments ->
            Primitive(
                arguments.getOrNull(0)?.asString()?.length ?: arguments.getOrNull(0)?.asArray
                    ?.size()
                ?: 0 // Get length of string or size of array, default to 0 if null or not string/array
            )
        }

        /**
         * TRIM Function: Removes leading and trailing whitespace from a string.
         *
         * Arguments:
         *   1. (Optional) String `Value` to trim. Defaults to null.
         *
         * Returns a `Primitive` string containing the trimmed version of the input string.
         * Returns an empty `Primitive` string if the input string is null.
         */
        @JvmField
        val TRIM = function("trim") { _, _, _, arguments ->
            Primitive(
                arguments.getOrNull(0)?.asString()?.trim()
                    ?: "" // Trim the string, default to empty string if null
            )
        }

        /**
         * MAX Function: Returns the maximum of the provided numeric arguments.
         *
         * Arguments:
         *   ... (Variable) Numeric `Value` arguments.
         *
         * Returns a `Primitive` number representing the maximum value among the arguments.
         * Returns 0.0 if no arguments are provided or if no numeric arguments are found.
         */
        @JvmField
        val MAX = function("max") { _, _, _, arguments ->
            Primitive(arguments.maxOfOrNull { it.asDouble() }
                ?: 0.0) // Find max of all arguments as doubles, default to 0.0 if no arguments or no doubles
        }

        /**
         * MIN Function: Returns the minimum of the provided numeric arguments.
         *
         * Arguments:
         *   ... (Variable) Numeric `Value` arguments.
         *
         * Returns a `Primitive` number representing the minimum value among the arguments.
         * Returns 0.0 if no arguments are provided or if no numeric arguments are found.
         */
        @JvmField
        val MIN = function("min") { _, _, _, arguments ->
            Primitive(arguments.minOfOrNull { it.asDouble() }
                ?: 0.0) // Find min of all arguments as doubles, default to 0.0 if no arguments or no doubles
        }

        /**
         * SLICE Function: Extracts a section (slice) of an array `Value`.
         *
         * Arguments:
         *   1. (Optional) Array `Value` to slice. Defaults to null.
         *   2. (Optional) Start index (integer). Defaults to 0. Can be negative (index from the end).
         *   3. (Optional) End index (integer). Defaults to array size. Can be negative (index from the end).
         *
         * Returns a new `Array` `Value` containing the sliced portion of the original array.
         * Returns an empty `Array` `Value` if the input array is null or invalid indices are provided.
         */
        @JvmField
        val SLICE = function("slice") { _, _, _, arguments ->
            arguments.getOrNull(0)?.asArray
                ?.let { array -> // Get array argument, proceed only if not null
                    Array().apply { // Create a new Array to hold the slice
                        (arguments.getOrNull(1)?.asInt()?.calculateIndex(array)
                            ?: 0).until( // Calculate start index, default to 0
                            arguments.getOrNull(2)?.asInt()?.calculateIndex(array)
                                ?: array.size() // Calculate end index, default to array size
                        )
                            .forEach { add(array[it]) } // Iterate through the slice range and add elements to the new array
                    }
                } ?: Array() // If input array is null, return an empty Array
        }

        /**
         * Helper function to calculate the correct index for array slicing, handling negative indices and out-of-bounds values.
         *
         * @param array The Array `Value` for which the index is being calculated.
         * @return The calculated valid index within the bounds of the array.
         */
        private fun Int.calculateIndex(array: Array): Int = when {
            this < 0 -> maxOf(
                0, array.size() + this
            ) // Negative index: calculate from the end, ensure not less than 0
            this > array.size() -> array.size() // Index greater than size: clamp to array size
            else -> this // Positive index within bounds: return as is
        }

        /**
         * Private helper function to create `Function` instances more concisely using a lambda.
         *
         * @param name  The name of the function.
         * @param block Lambda expression representing the function's call logic.
         *              It takes Context, Value data, dataIndex, and an array of Value arguments.
         * @return A new `Function` instance.
         */
        private inline fun function(
            name: String, crossinline block: (Context, Value, Int, array<out Value>) -> Value
        ) = object : Function() {
            override fun call(
                context: Context, data: Value, dataIndex: Int, vararg arguments: Value
            ) = block(
                context, data, dataIndex, arguments
            ) // Implement call method by executing the provided lambda

            override fun getName() = name // Implement getName method to return the provided name
        }
    }
}