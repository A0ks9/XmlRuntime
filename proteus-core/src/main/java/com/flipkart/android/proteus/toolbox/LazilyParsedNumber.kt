package com.flipkart.android.proteus.toolbox

import java.math.BigDecimal

/**
 * Kotlin class representing a number parsed lazily from a string.
 *
 * This class extends `Number` and stores the numeric value as a string initially.
 * Parsing to `Int`, `Long`, `Float`, `Double`, `Short`, or `Byte` is deferred until the
 * corresponding value method is called.
 * This can be useful when you have a number represented as a string but only need
 * its numeric value in specific cases, potentially improving performance by delaying parsing.
 *
 * @property value The string representation of the number. Must not be null.
 * @throws IllegalArgumentException if the [value] is null.
 */
class LazilyParsedNumber(private val value: String) : Number() {

    init {
        requireNotNull(value) { "Value must not be null" }
    }

    /**
     * Returns the value of this number as an [Int].
     *
     * Attempts to parse the string value as an Integer, then as a Long if Integer parsing fails,
     * and finally as a BigDecimal to handle potentially very large or decimal numbers, returning the integer part.
     *
     * @return The integer representation of the number.
     */
    override fun toInt(): Int {
        return value.toIntOrNull() ?: value.toLongOrNull()?.toInt() ?: BigDecimal(value).toInt()
    }

    /**
     * Returns the value of this number as a [Long].
     *
     * Attempts to parse the string value as a Long, and if that fails, parses it as a BigDecimal
     * to handle potentially very large or decimal numbers, returning the long part.
     *
     * @return The long representation of the number.
     */
    override fun toLong(): Long {
        return value.toLongOrNull() ?: BigDecimal(value).toLong()
    }

    /**
     * Returns the value of this number as a [Float].
     *
     * Directly parses the string value as a Float. If parsing fails, it will throw [NumberFormatException].
     *
     * @return The float representation of the number.
     * @throws NumberFormatException if the string value cannot be parsed as a Float.
     */
    override fun toFloat(): Float {
        return value.toFloat()
    }

    /**
     * Returns the value of this number as a [Double].
     *
     * Directly parses the string value as a Double. If parsing fails, it will throw [NumberFormatException].
     *
     * @return The double representation of the number.
     * @throws NumberFormatException if the string value cannot be parsed as a Double.
     */
    override fun toDouble(): Double {
        return value.toDouble()
    }

    /**
     * Returns the value of this number as a [Short].
     *
     * Attempts to parse the string value as a Short, then as an Integer if Short parsing fails,
     * and finally as a BigDecimal, returning the short part.
     *
     * @return The short representation of the number.
     */
    override fun toShort(): Short {
        return value.toShortOrNull() ?: // Try to parse as Short, return null if fails
        value.toIntOrNull()?.toShort()
        ?: // If Short fails, try Int, convert to Short if successful, null if fails
        BigDecimal(value).toShort() // If Int also fails, parse as BigDecimal and get shortValue
    }

    /**
     * Returns the value of this number as a [Byte].
     *
     * Attempts to parse the string value as a Byte, then as an Integer if Byte parsing fails,
     * and finally as a BigDecimal, returning the byte part.
     *
     * @return The byte representation of the number.
     */
    override fun toByte(): Byte {
        return value.toByteOrNull() ?: // Try to parse as Byte, return null if fails
        value.toIntOrNull()?.toByte()
        ?: // If Byte fails, try Int, convert to Byte if successful, null if fails
        BigDecimal(value).toByte() // If Int also fails, parse as BigDecimal and get byteValue
    }


    /**
     * Returns the string representation of this number, which is the original string value passed to the constructor.
     *
     * @return The string value of this LazilyParsedNumber.
     */
    override fun toString(): String {
        return value
    }

    /**
     * Returns the hash code for this LazilyParsedNumber, which is based on the hash code of its string value.
     *
     * @return The hash code of the string value.
     */
    override fun hashCode(): Int {
        return value.hashCode()
    }

    /**
     * Indicates whether some other object is "equal to" this LazilyParsedNumber.
     *
     * Two LazilyParsedNumber objects are considered equal if they are the same instance
     * or if their string values are equal.
     *
     * @param other The reference object with which to compare.
     * @return `true` if this object is the same as the [other] object or if their string values are equal; `false` otherwise.
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is LazilyParsedNumber) return false

        return value == other.value
    }
}