package com.flipkart.android.proteus.value

import java.math.BigInteger

class Primitive(value: Any?) : Value() {

    private val value: Any? = when (value) {
        is Char -> value.toString()
        is Number, isPrimitiveOrString(value) -> value
        else -> throw IllegalArgumentException("Unsupported primitive type: ${value?.javaClass?.name}")
    }


    companion object {
        private val PRIMITIVE_TYPES = arrayOf<Class<*>>(
            Int::class.java,
            Long::class.java,
            Short::class.java,
            Float::class.java,
            Double::class.java,
            Byte::class.java,
            Boolean::class.java,
            Char::class.java,
            Integer::class.java,
            java.lang.Long::class.java,
            java.lang.Short::class.java,
            java.lang.Float::class.java,
            java.lang.Double::class.java,
            java.lang.Byte::class.java,
            java.lang.Boolean::class.java,
            Character::class.java
        )

        @JvmStatic
        fun isPrimitiveOrString(target: Any?): Boolean =
            target is String || target?.javaClass?.let { targetClass ->
                PRIMITIVE_TYPES.any { it.isAssignableFrom(targetClass) }
            } == true

        private fun isIntegral(primitive: Primitive): Boolean {
            return primitive.value is Number && (primitive.value is BigInteger || primitive.value is Long || primitive.value is Int || primitive.value is Short || primitive.value is Byte)
        }

    }

    override fun copy(): Primitive = this

    fun isBoolean(): Boolean = value is Boolean
    override fun getAsBoolean(): Boolean = when (value) {
        is Boolean -> value
        else -> value.toString().toBoolean()
    }

    fun isNumber(): Boolean = value is Number

    fun getAsNumber(): Number = if (value is String) LazilyParsedNumber(value) else value as Number

    fun isString(): Boolean = value is String
    override fun getAsString(): String = when (value) {
        is Number -> value.toString()
        is Boolean -> value.toString()
        else -> value.toString()
    }

    override fun getAsDouble(): Double =
        if (value is Number) getAsNumber().toDouble() else value.toString().toDouble()

    override fun getAsFloat(): Float =
        if (value is Number) getAsNumber().toFloat() else value.toString().toFloat()

    override fun getAsLong(): Long =
        if (value is Number) getAsNumber().toLong() else value.toString().toLong()

    override fun getAsInt(): Int =
        if (value is Number) getAsNumber().toInt() else value.toString().toInt()

    override fun getAsCharacter(): Char = getAsString()[0]

    override fun hashCode(): Int {
        return when (value) {
            null -> 31

            isIntegral(this) -> {
                val longValue = getAsNumber().toLong()
                (longValue xor (longValue ushr 32)).toInt()
            }

            is Number -> {
                val doubleValue = java.lang.Double.doubleToLongBits(getAsDouble())
                (doubleValue xor (doubleValue ushr 32)).toInt()
            }

            else -> value.hashCode()
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Primitive) return false
        return when {
            value == null && other.value == null -> true
            isIntegral(this) && isIntegral(other) -> getAsNumber().toLong() == other.getAsNumber()
                .toLong()

            value is Number && other.value is Number -> {
                val a = getAsNumber().toDouble()
                val b = other.getAsNumber().toDouble()
                a == b || (java.lang.Double.isNaN(a) && java.lang.Double.isNaN(b))
            }

            else -> value == other.value
        }
    }

    override fun toString(): String = getAsString()
    fun getAsSingleQuotedString(): String = "\'${getAsString()}\'"
    fun getAsDoubleQuotedString(): String = "\"${getAsString()}\""
}