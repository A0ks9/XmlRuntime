package com.flipkart.android.proteus.toolbox

import androidx.annotation.IntDef
import com.flipkart.android.proteus.value.Null
import com.flipkart.android.proteus.value.Value

/**
 * Kotlin class representing the result of a data path lookup in Proteus.
 *
 * This class encapsulates the outcome of attempting to retrieve a [Value] at a specified data path.
 * It includes a [RESULT_CODE] indicating the status of the lookup and the [value] found (if successful).
 */
class Result(
    /**
     * Indicates the return status of the method for a given data path.
     * The return value will be [RESULT_SUCCESS] if and only if the data path exists and contains
     * a valid [Value].
     */
    @field:ResultCode // Annotations for the constructor parameter and field
    val RESULT_CODE: Int,

    /**
     * The value at the specified data path.
     * [value] will be [Null] if [RESULT_CODE] is not [RESULT_SUCCESS].
     */
    // Annotations for the constructor parameter and field
    val value: Value
) {

    /**
     * Checks if the result indicates success ([RESULT_CODE] == [RESULT_SUCCESS]).
     *
     * @return `true` if the result is a success, `false` otherwise.
     */
    fun isSuccess(): Boolean {
        return RESULT_CODE == RESULT_SUCCESS
    }

    companion object { // Companion object to hold static constants and factory methods

        /**
         * Indicates that a valid [Value] was found at the specified data path.
         */
        const val RESULT_SUCCESS = 0

        /**
         * Indicates that the object does not have the specified data path.
         */
        const val RESULT_NO_SUCH_DATA_PATH_EXCEPTION = -1

        /**
         * Indicates that the data path specified is invalid.
         * Example: looking for a property inside a [com.flipkart.android.proteus.value.Primitive] or [com.flipkart.android.proteus.value.Array].
         */
        const val RESULT_INVALID_DATA_PATH_EXCEPTION = -2

        /**
         * Indicates that the data path prematurely led to a [com.flipkart.android.proteus.value.Null] value.
         */
        const val RESULT_NULL_EXCEPTION = -3

        /**
         * Singleton instance for [RESULT_NO_SUCH_DATA_PATH_EXCEPTION].
         */
        @JvmField // Added to access this static field from Java code directly if needed
        val NO_SUCH_DATA_PATH_EXCEPTION = Result(RESULT_NO_SUCH_DATA_PATH_EXCEPTION, Null)

        /**
         * Singleton instance for [RESULT_INVALID_DATA_PATH_EXCEPTION].
         */
        @JvmField // Added to access this static field from Java code directly if needed
        val INVALID_DATA_PATH_EXCEPTION = Result(RESULT_INVALID_DATA_PATH_EXCEPTION, Null)

        /**
         * Singleton instance for [RESULT_NULL_EXCEPTION].
         */
        @JvmField // Added to access this static field from Java code directly if needed
        val NULL_EXCEPTION = Result(RESULT_NULL_EXCEPTION, Null)

        /**
         * Creates a [Result] object representing a successful lookup.
         *
         * This factory method returns a [Result] with [RESULT_CODE] == [RESULT_SUCCESS]
         * and the provided [value] as the result value.
         *
         * @param value The [Value] to be wrapped in a success [Result].
         * @return A [Result] object indicating success.
         */
        @JvmStatic // Added to call this static method from Java code directly if needed
        fun success(value: Value): Result {
            return Result(RESULT_SUCCESS, value)
        }
    }

    /**
     * Defines the possible result codes for a data path lookup.
     *
     * These codes are used in [RESULT_CODE] to indicate the status of a [Result].
     */
    @IntDef(
        RESULT_INVALID_DATA_PATH_EXCEPTION,
        RESULT_NO_SUCH_DATA_PATH_EXCEPTION,
        RESULT_SUCCESS,
        RESULT_NULL_EXCEPTION
    )
    @Retention(AnnotationRetention.SOURCE)
    annotation class ResultCode
}