package com.flipkart.android.proteus.value

import android.content.Context

class NestedBinding private constructor(val value: Value) : Binding() {

    companion object {
        const val NESTED_BINDING_KEY = "@"

        @JvmStatic
        fun valueOf(value: Value): NestedBinding = NestedBinding(value)
    }

    fun getValue(): Value = value

    override fun evaluate(context: Context, data: Value, index: Int): Value =
        evaluate(context, value, data, index)

    override fun toString(): String = "${javaClass.name}@${hashCode().toString(16)}"


    private fun evaluate(context: Context, binding: Binding, data: Value, index: Int): Value =
        binding.evaluate(context, data, index)

    private fun evaluate(
        context: Context, objectValue: ObjectValue, data: Value, index: Int
    ): Value {
        val evaluated = ObjectValue()
        objectValue.forEach { key, value ->
            evaluated[key] = evaluate(context, value, data, index)
        }
        return evaluated
    }

    private fun evaluate(context: Context, array: Array, data: Value, index: Int): Value {
        return array.map { evaluate(context, it, data, index) }
    }

    private fun evaluate(context: Context, value: Value, data: Value, index: Int): Value = when {
        value.isBinding -> evaluate(context, value.asBinding(), data, index)
        value.isObject -> evaluate(context, value.asObject, data, index)
        value.isArray -> evaluate(context, value.asArray, data, index)
        else -> value
    }

    override fun copy(): Binding = this
}