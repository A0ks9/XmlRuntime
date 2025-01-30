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
import java.util.Date
import kotlin.Array as array

/**
 * Kotlin abstract class representing a function in Proteus data binding.
 *
 * This class serves as a base for defining functions that can be used within
 * Proteus data binding expressions to manipulate data and produce values.
 */
abstract class Function { // Converted to Kotlin abstract class

    /**
     * Abstract method to execute the function logic.
     *
     * Subclasses must implement this method to define the specific operation
     * the function performs.
     *
     * @param context   Android Context.
     * @param data      Current data context Value.
     * @param dataIndex Index of the data item (if in an array).
     * @param arguments Array of arguments passed to the function.
     * @return The Value resulting from the function call.
     * @throws Exception if any error occurs during function execution.
     */
    abstract fun call(context: Context, data: Value, dataIndex: Int, vararg arguments: Value): Value

    /**
     * Abstract method to get the name of the function.
     *
     * Subclasses must implement this method to return the unique name
     * that identifies the function in Proteus expressions.
     *
     * @return The name of the function as a String.
     */
    abstract fun getName(): String

    companion object { // Kotlin Companion Object for static members (like static final functions in Java)

        /**
         * A NO-OP Function that returns an empty string.
         */
        @JvmField // To keep it accessible as a static field from Java
        val NOOP: Function =
            object : Function() { // Kotlin anonymous object for Function implementation
                override fun call(
                    context: Context,
                    data: Value,
                    dataIndex: Int,
                    vararg arguments: Value
                ): Value {
                    return ProteusConstants.EMPTY_STRING // Returns empty string as Value
                }

                override fun getName(): String {
                    return "noop" // Function name is "noop"
                }
            }

        /**
         * DATE Function: Formats a date string.
         *
         * Supports parsing a date string from one format and formatting it into another.
         * Default input format: "yyyy-MM-dd HH:mm:ss", default output format: "E, d MMM".
         * Formats can be overridden by passing format strings as arguments.
         */
        @SuppressLint("SimpleDateFormat") // Suppress lint warning for SimpleDateFormat in static context
        @JvmField
        val DATE: Function = object : Function() {

            private val defaultFromFormat =
                SimpleDateFormat("yyyy-MM-dd HH:mm:ss") // Default input format
            private val defaultToFormat = SimpleDateFormat("E, d MMM")     // Default output format

            override fun call(
                context: Context,
                data: Value,
                dataIndex: Int,
                vararg arguments: Value
            ): Value {
                val dateString = arguments.getOrNull(0)?.getAsString()
                    ?: "" // Get date string argument, default to empty string if missing
                val inputFormat =
                    arguments.getFromFormat(defaultFromFormat) // Determine input format from arguments or use default
                val outputFormat =
                    arguments.getToFormat(defaultToFormat)   // Determine output format from arguments or use default

                val date = inputFormat.parse(dateString)
                    ?: Date() // Parse the date string, default to current date if parsing fails
                val formattedDate = outputFormat.format(date)      // Format the date

                return Primitive(formattedDate) // Return formatted date as Primitive Value
            }

            private fun array<out Value>.getFromFormat(defaultFormat: SimpleDateFormat): SimpleDateFormat { // Extension function to get input format from arguments
                return if (size > 2) SimpleDateFormat(get(2).getAsString()) else defaultFormat // Use format from 3rd argument if provided, else default
            }

            private fun array<out Value>.getToFormat(defaultFormat: SimpleDateFormat): SimpleDateFormat { // Extension function to get output format from arguments
                return if (size > 1) SimpleDateFormat(get(1).getAsString()) else defaultFormat // Use format from 2nd argument if provided, else default
            }

            override fun getName(): String {
                return "date" // Function name is "date"
            }
        }

        /**
         * FORMAT Function: Formats a string using String.format.
         *
         * First argument is the template string, subsequent arguments are values to be formatted into the template.
         */
        @JvmField
        val FORMAT: Function = object : Function() {
            override fun call(
                context: Context,
                data: Value,
                dataIndex: Int,
                vararg arguments: Value
            ): Value {
                val template = arguments.getOrNull(0)?.getAsString()
                    ?: "" // Get template string, default to empty string if missing
                val formatValues = arguments.drop(1).map { it.getAsString() }
                    .toTypedArray() // Extract formatting values from arguments
                val formattedString = String.format(template, *formatValues) // Format the string

                return Primitive(formattedString) // Return formatted string as Primitive Value
            }

            override fun getName(): String {
                return "format" // Function name is "format"
            }
        }

        /**
         * JOIN Function: Joins elements of an array into a string.
         *
         * First argument is the array to join.
         * Second optional argument is the delimiter, default is ", ".
         */
        @JvmField
        val JOIN: Function = object : Function() {

            private val DEFAULT_DELIMITER = ", " // Default delimiter for join

            override fun call(
                context: Context,
                data: Value,
                dataIndex: Int,
                vararg arguments: Value
            ): Value {
                val arrayToJoin = arguments.getOrNull(0)?.asArray()
                    ?: Array() // Get array argument, default to empty array if missing
                val delimiter =
                    arguments.getDelimiter()                       // Determine delimiter from arguments or use default

                val joinedString =
                    Utils.join(arrayToJoin, delimiter)       // Join the array elements
                return Primitive(joinedString)                            // Return joined string as Primitive Value
            }

            private fun array<out Value>.getDelimiter(): String { // Extension function to get delimiter from arguments
                return if (size > 1) get(1).getAsString() else DEFAULT_DELIMITER // Use delimiter from 2nd argument if provided, else default
            }

            override fun getName(): String {
                return "join" // Function name is "join"
            }
        }

        /**
         * NUMBER Function: Formats a number with thousands separators and decimal places.
         *
         * First argument is the number to format.
         * Second optional argument is the format string, default is "#,###".
         */
        @JvmField
        val NUMBER: Function = object : Function() {

            private val defaultFormatter = DecimalFormat("#,###") // Default number formatter

            override fun call(
                context: Context,
                data: Value,
                dataIndex: Int,
                vararg arguments: Value
            ): Value {
                val numberString = arguments.getOrNull(0)?.getAsString()
                    ?: "0" // Get number string, default to "0" if missing
                val number = numberString.toDoubleOrNull()
                    ?: 0.0             // Parse number string to double, default to 0.0 if parsing fails
                val formatter =
                    arguments.getFormatter(defaultFormatter)    // Determine formatter from arguments or use default

                formatter.roundingMode = RoundingMode.FLOOR               // Set rounding mode
                formatter.minimumFractionDigits =
                    0                     // Set minimum fraction digits
                formatter.maximumFractionDigits =
                    2                     // Set maximum fraction digits

                val formattedNumber = formatter.format(number)             // Format the number
                return Primitive(formattedNumber)                         // Return formatted number as Primitive Value
            }

            private fun array<out Value>.getFormatter(defaultFormatter: DecimalFormat): DecimalFormat { // Extension function to get formatter from arguments
                return if (size > 1) DecimalFormat(get(1).getAsString()) else defaultFormatter // Use format from 2nd argument if provided, else default
            }

            override fun getName(): String {
                return "number" // Function name is "number"
            }
        }

        // Mathematical Functions
        /** ADD Function: Adds all numeric arguments. */
        @JvmField
        val ADD: Function = object : Function() {
            override fun call(
                context: Context,
                data: Value,
                dataIndex: Int,
                vararg arguments: Value
            ): Value {
                var sum = 0.0
                arguments.forEach { sum += it.getAsDouble() } // Sum up all arguments
                return Primitive(sum) // Return sum as Primitive Value
            }

            override fun getName() = "add" // Function name is "add"
        }

        /** SUBTRACT Function: Subtracts subsequent arguments from the first argument. */
        @JvmField
        val SUBTRACT: Function = object : Function() {
            override fun call(
                context: Context,
                data: Value,
                dataIndex: Int,
                vararg arguments: Value
            ): Value {
                if (arguments.isEmpty()) return ProteusConstants.EMPTY_STRING // Handle no arguments case

                var sum = arguments[0].getAsDouble() // Initialize sum with first argument
                for (i in 1 until arguments.size) sum -= arguments[i].getAsDouble() // Subtract subsequent arguments
                return Primitive(sum) // Return result as Primitive Value
            }

            override fun getName() = "sub" // Function name is "sub"
        }

        /** MULTIPLY Function: Multiplies all numeric arguments. */
        @JvmField
        val MULTIPLY: Function = object : Function() {
            override fun call(
                context: Context,
                data: Value,
                dataIndex: Int,
                vararg arguments: Value
            ): Value {
                var product = 1.0
                arguments.forEach { product *= it.getAsDouble() } // Multiply all arguments
                return Primitive(product) // Return product as Primitive Value
            }

            override fun getName() = "mul" // Function name is "mul"
        }

        /** DIVIDE Function: Divides the first argument by subsequent arguments. */
        @JvmField
        val DIVIDE: Function = object : Function() {
            override fun call(
                context: Context,
                data: Value,
                dataIndex: Int,
                vararg arguments: Value
            ): Value {
                if (arguments.isEmpty()) return ProteusConstants.EMPTY_STRING // Handle no arguments case
                var quotient = arguments[0].getAsDouble() // Initialize quotient with first argument
                for (i in 1 until arguments.size) quotient /= arguments[i].getAsDouble() // Divide by subsequent arguments
                return Primitive(quotient) // Return quotient as Primitive Value
            }

            override fun getName() = "div" // Function name is "div"
        }

        /** MODULO Function: Calculates the modulo (remainder) of arguments. */
        @JvmField
        val MODULO: Function = object : Function() {
            override fun call(
                context: Context,
                data: Value,
                dataIndex: Int,
                vararg arguments: Value
            ): Value {
                if (arguments.isEmpty()) return ProteusConstants.EMPTY_STRING // Handle no arguments case
                var remainder =
                    arguments[0].getAsDouble() // Initialize remainder with first argument
                for (i in 1 until arguments.size) remainder %= arguments[i].getAsDouble() // Calculate modulo with subsequent arguments
                return Primitive(remainder) // Return remainder as Primitive Value
            }

            override fun getName() = "mod" // Function name is "mod"
        }


        // Logical Functions
        /** AND Function: Logical AND of boolean arguments. */
        @JvmField
        val AND: Function = object : Function() {
            override fun call(
                context: Context,
                data: Value,
                dataIndex: Int,
                vararg arguments: Value
            ): Value {
                if (arguments.isEmpty()) return ProteusConstants.FALSE // Default to FALSE if no arguments
                var bool = true
                for (argument in arguments) {
                    bool = parseBoolean(argument) // Parse each argument to boolean
                    if (!bool) break            // Short-circuit AND if any argument is false
                }
                return if (bool) ProteusConstants.TRUE else ProteusConstants.FALSE // Return TRUE or FALSE as Primitive Value
            }

            override fun getName() = "and" // Function name is "and"
        }

        /** OR Function: Logical OR of boolean arguments. */
        @JvmField
        val OR: Function = object : Function() {
            override fun call(
                context: Context,
                data: Value,
                dataIndex: Int,
                vararg arguments: Value
            ): Value {
                if (arguments.isEmpty()) return ProteusConstants.FALSE // Default to FALSE if no arguments
                var bool = false
                for (argument in arguments) {
                    bool = parseBoolean(argument) // Parse each argument to boolean
                    if (bool) break             // Short-circuit OR if any argument is true
                }
                return if (bool) ProteusConstants.TRUE else ProteusConstants.FALSE // Return TRUE or FALSE as Primitive Value
            }

            override fun getName() = "or" // Function name is "or"
        }


        // Unary Function
        /** NOT Function: Logical NOT of a boolean argument. */
        @JvmField
        val NOT: Function = object : Function() {
            override fun call(
                context: Context,
                data: Value,
                dataIndex: Int,
                vararg arguments: Value
            ): Value {
                return if (arguments.isEmpty()) ProteusConstants.TRUE // Default to TRUE if no arguments
                else if (parseBoolean(arguments[0])) ProteusConstants.FALSE else ProteusConstants.TRUE // Return NOT of the first argument as Primitive Value
            }

            override fun getName() = "not" // Function name is "not"
        }


        // Comparison Functions
        /** EQUALS Function: Checks if two arguments are equal (primitive comparison). */
        @JvmField
        val EQUALS: Function = object : Function() {
            override fun call(
                context: Context,
                data: Value,
                dataIndex: Int,
                vararg arguments: Value
            ): Value {
                if (arguments.size < 2) return ProteusConstants.FALSE // Default to FALSE if less than 2 arguments
                val x = arguments[0]
                val y = arguments[1]
                val bool =
                    x.isPrimitive && y.isPrimitive && x.asPrimitive() == y.asPrimitive() // Compare primitives
                return if (bool) ProteusConstants.TRUE else ProteusConstants.FALSE // Return TRUE or FALSE as Primitive Value
            }

            override fun getName() = "eq" // Function name is "eq"
        }

        /** LESS_THAN Function: Checks if the first argument is less than the second (numeric comparison). */
        @JvmField
        val LESS_THAN: Function = object : Function() {
            override fun call(
                context: Context,
                data: Value,
                dataIndex: Int,
                vararg arguments: Value
            ): Value {
                if (arguments.size < 2) return ProteusConstants.FALSE // Default to FALSE if less than 2 arguments
                val x = arguments[0]
                val y = arguments[1]
                val bool = x.isPrimitive && y.isPrimitive && x.asPrimitive()
                    .getAsDouble() < y.asPrimitive().getAsDouble() // Numeric comparison
                return if (bool) ProteusConstants.TRUE else ProteusConstants.FALSE // Return TRUE or FALSE as Primitive Value
            }

            override fun getName() = "lt" // Function name is "lt"
        }

        /** GREATER_THAN Function: Checks if the first argument is greater than the second (numeric comparison). */
        @JvmField
        val GREATER_THAN: Function = object : Function() {
            override fun call(
                context: Context,
                data: Value,
                dataIndex: Int,
                vararg arguments: Value
            ): Value {
                if (arguments.size < 2) return ProteusConstants.FALSE // Default to FALSE if less than 2 arguments
                val x = arguments[0]
                val y = arguments[1]
                val bool = x.isPrimitive && y.isPrimitive && x.asPrimitive()
                    .getAsDouble() > y.asPrimitive().getAsDouble() // Numeric comparison
                return if (bool) ProteusConstants.TRUE else ProteusConstants.FALSE // Return TRUE or FALSE as Primitive Value
            }

            override fun getName() = "gt" // Function name is "gt"
        }

        /** LESS_THAN_OR_EQUALS Function: Checks if the first argument is less than or equal to the second (numeric comparison). */
        @JvmField
        val LESS_THAN_OR_EQUALS: Function = object : Function() {
            override fun call(
                context: Context,
                data: Value,
                dataIndex: Int,
                vararg arguments: Value
            ): Value {
                if (arguments.size < 2) return ProteusConstants.FALSE // Default to FALSE if less than 2 arguments
                val x = arguments[0]
                val y = arguments[1]
                val bool = x.isPrimitive && y.isPrimitive && x.asPrimitive()
                    .getAsDouble() <= y.asPrimitive().getAsDouble() // Numeric comparison
                return if (bool) ProteusConstants.TRUE else ProteusConstants.FALSE // Return TRUE or FALSE as Primitive Value
            }

            override fun getName() = "lte" // Function name is "lte"
        }

        /** GREATER_THAN_OR_EQUALS Function: Checks if the first argument is greater than or equal to the second (numeric comparison). */
        @JvmField
        val GREATER_THAN_OR_EQUALS: Function = object : Function() {
            override fun call(
                context: Context,
                data: Value,
                dataIndex: Int,
                vararg arguments: Value
            ): Value {
                if (arguments.size < 2) return ProteusConstants.FALSE // Default to FALSE if less than 2 arguments
                val x = arguments[0]
                val y = arguments[1]
                val bool = x.isPrimitive && y.isPrimitive && x.asPrimitive()
                    .getAsDouble() >= y.asPrimitive().getAsDouble() // Numeric comparison
                return if (bool) ProteusConstants.TRUE else ProteusConstants.FALSE // Return TRUE or FALSE as Primitive Value
            }

            override fun getName() = "gte" // Function name is "gte"
        }


        // Conditional Function
        /** TERNARY Function: Ternary conditional operator (if-then-else). */
        @JvmField
        val TERNARY: Function = object : Function() {
            override fun call(
                context: Context,
                data: Value,
                dataIndex: Int,
                vararg arguments: Value
            ): Value {
                val condition = arguments.getOrNull(0)
                    ?: ProteusConstants.FALSE // Get condition, default to FALSE if missing
                val thenValue = arguments.getOrNull(1)
                    ?: ProteusConstants.EMPTY_STRING // Get then-value, default to empty string if missing
                val elseValue = arguments.getOrNull(2)
                    ?: ProteusConstants.EMPTY_STRING // Get else-value, default to empty string if missing

                return if (parseBoolean(condition)) thenValue else elseValue // Return thenValue if condition is true, else elseValue
            }

            override fun getName() = "ternary" // Function name is "ternary"
        }


        // String Functions
        /** CHAR_AT Function: Returns the character at a specified index in a string. */
        @JvmField
        val CHAR_AT: Function = object : Function() {
            override fun call(
                context: Context,
                data: Value,
                dataIndex: Int,
                vararg arguments: Value
            ): Value {
                val string = arguments.getOrNull(0)?.getAsString()
                    ?: ""      // Get string argument, default to empty string if missing
                val index = arguments.getOrNull(1)?.getAsInt()
                    ?: 0            // Get index argument, default to 0 if missing
                val charAtIndex = string.getOrNull(index)
                    ?: '\u0000'  // Get char at index, default to null char if index invalid

                return Primitive(charAtIndex.toString()) // Return char as Primitive Value
            }

            override fun getName() = "charAt" // Function name is "charAt"
        }

        private fun String.getOrNull(index: Int): Char? = // Extension function to get char or null
            if (index in 0 until length) get(index) else null


        /** CONTAINS Function: Checks if a string contains a substring. */
        @JvmField
        val CONTAINS: Function = object : Function() {
            override fun call(
                context: Context,
                data: Value,
                dataIndex: Int,
                vararg arguments: Value
            ): Value {
                val string = arguments.getOrNull(0)?.getAsString()
                    ?: ""         // Get string argument, default to empty string if missing
                val substring = arguments.getOrNull(1)?.getAsString()
                    ?: ""      // Get substring argument, default to empty string if missing
                val bool =
                    string.contains(substring)                         // Check if string contains substring

                return Primitive(bool) // Return boolean result as Primitive Value
            }

            override fun getName() = "contains" // Function name is "contains"
        }


        /** IS_EMPTY Function: Checks if a string is empty. */
        @JvmField
        val IS_EMPTY: Function = object : Function() {
            override fun call(
                context: Context,
                data: Value,
                dataIndex: Int,
                vararg arguments: Value
            ): Value {
                val string = arguments.getOrNull(0)?.getAsString()
                    ?: ""      // Get string argument, default to empty string if missing
                return Primitive(string.isEmpty())                          // Check if string is empty and return as Primitive Value
            }

            override fun getName() = "isEmpty" // Function name is "isEmpty"
        }


        /** LENGTH Function: Returns the length of a string or an array. */
        @JvmField
        val LENGTH: Function = object : Function() {
            override fun call(
                context: Context,
                data: Value,
                dataIndex: Int,
                vararg arguments: Value
            ): Value {
                val value = arguments.getOrNull(0)
                    ?: ProteusConstants.EMPTY_STRING // Get argument, default to empty string if missing
                val length = when {       // Determine length based on value type
                    value.isPrimitive -> value.getAsString().length
                    value.isArray -> value.asArray().size()
                    else -> 0            // Default length is 0 for other types
                }
                return Primitive(length) // Return length as Primitive Value
            }

            override fun getName() = "length" // Function name is "length"
        }


        /** TRIM Function: Removes leading and trailing whitespace from a string. */
        @JvmField
        val TRIM: Function = object : Function() {
            override fun call(
                context: Context,
                data: Value,
                dataIndex: Int,
                vararg arguments: Value
            ): Value {
                val string = arguments.getOrNull(0)?.getAsString()
                    ?: "" // Get string argument, default to empty string if missing
                return Primitive(string.trim())                         // Trim the string and return as Primitive Value
            }

            override fun getName() = "trim" // Function name is "trim"
        }


        // Math Functions (Math.max and Math.min already defined as MAX and MIN above)
        /** MAX Function: Returns the maximum of numeric arguments (already defined above) */

        /** MIN Function: Returns the minimum of numeric arguments (already defined above) */


        // Array Function
        /** SLICE Function: Returns a slice of an array. */
        @JvmField
        val SLICE: Function = object : Function() {
            override fun call(
                context: Context,
                data: Value,
                dataIndex: Int,
                vararg arguments: Value
            ): Value {
                val inputArr = arguments.getOrNull(0)?.asArray()
                    ?: Array() // Get input array, default to empty array if missing
                val start =
                    arguments.getStart(inputArr, arguments)                // Determine start index from arguments
                val end =
                    arguments.getEnd(inputArr, arguments)                  // Determine end index from arguments

                val outputArr = Array()                                // Create new output array
                for (i in start until end) outputArr.add(inputArr[i]) // Copy elements from slice to output array
                return outputArr                                     // Return sliced array as Value
            }

            private fun array<out Value>.getStart(a: Array, arguments: array<out Value>): Int { // Extension function to get start index from arguments
                if (arguments.size <= 1) return 0                               // Default start index is 0
                var index =
                    arguments[1].getAsInt()                           // Get start index from 2nd argument
                return when {
                    index < 0 -> maxOf(
                        0,
                        a.size() - index
                    )              // Handle negative start index (from end)
                    index > a.size() -> a.size()                             // Limit start index to array size
                    else -> index                                         // Use given start index
                }
            }

            private fun array<out Value>.getEnd(a: Array, arguments: array<out Value>): Int { // Extension function to get end index from arguments
                if (arguments.size <= 2) return a.size()                              // Default end index is array size
                var index =
                    arguments[2].getAsInt()                             // Get end index from 3rd argument
                return when {
                    index < 0 -> maxOf(
                        0,
                        a.size() - index
                    )                // Handle negative end index (from end)
                    index > a.size() -> a.size()                               // Limit end index to array size
                    else -> index                                           // Use given end index
                }
            }

            override fun getName() = "slice" // Function name is "slice"
        }
    }
}