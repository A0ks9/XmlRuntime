package com.flipkart.android.proteus.value

import java.util.ArrayList
import kotlin.collections.List


class Array(values: List<Value> = ArrayList()) : Value() {

    private val values: MutableList<Value> = values.toMutableList()

    constructor(capacity: Int) : this(ArrayList(capacity))
    constructor(vararg values: Value) : this(values.toList())

    override fun copy(): Array = Array(values.map { it.copy() }.toMutableList())

    fun add(bool: Boolean?) = values.add(bool?.let { Primitive(it) } ?: Null)
    fun add(char: Char?) = values.add(char?.let { Primitive(it) } ?: Null)
    fun add(number: Number?) = values.add(number?.let { Primitive(it) } ?: Null)
    fun add(string: String?) = values.add(string?.let { Primitive(it) } ?: Null)
    fun add(value: Value?) = values.add(value ?: Null)
    fun add(position: Int, value: Value?) = values.add(position, value ?: Null)
    fun addAll(array: Array) = values.addAll(array.values)
    fun remove(value: Value): Boolean = values.remove(value)
    fun remove(index: Int): Value = values.removeAt(index)
    fun contains(value: Value): Boolean = values.contains(value)
    fun size(): Int = values.size
    operator fun iterator(): Iterator<Value> = values.iterator()
    operator fun get(i: Int): Value = values[i]
    fun getOrNull(index: Int): Value? = if (index >= 0 && index < size()) this[index] else null
    fun lastOrNull(): Value? = values.lastOrNull()

    override fun equals(other: Any?): Boolean =
        this === other || (other is Array && values == other.values)


    override fun hashCode(): Int = values.hashCode()

    // Extension functions for forEach and forEachIndexed
    fun forEach(action: (Value) -> Unit) {
        values.forEach(action)
    }


    fun forEachIndexed(action: (index: Int, value: Value) -> Unit) {
        values.forEachIndexed(action)
    }

    // Extension function for map
    fun <R : Value> map(transform: (Value) -> R): Array {
        val mappedList = values.map { transform(it) }
        return Array(mappedList)
    }

    // Extension function for mapIndexed
    fun <R : Value> mapIndexed(transform: (index: Int, Value) -> R): Array {
        val mappedList = values.mapIndexed { index, value -> transform(index, value) }
        return Array(mappedList)
    }

    operator fun set(index: Int, value: Value) {
        values[index] = value
    }
}