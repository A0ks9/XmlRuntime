package com.flipkart.android.proteus.value

import java.util.ArrayList

class Array() : Value() {

    /**
     * The mutable list storing the values of this array
     */
    var values: MutableList<Value> = mutableListOf()


    /**
     * Constructor to create an empty array with a specific capacity
     */
    constructor(capacity: Int) : this() {
        // Initialize with ArrayList of given capacity for internal mutable list
        // while still using emptyArray() for the primary constructor parameter
        this.values = ArrayList<Value>(capacity)
    }

    /**
     * Constructor to create an array by passing variable number of arguments of type Value
     */
    constructor(vararg values: Value) : this() {
        this.values = values.toMutableList()
    }

    /**
     * Creates a deep copy of this Array.
     */
    override fun copy(): Array = Array(*values.map { it.copy() }.toTypedArray())

    /**
     * Adds the specified boolean value to this array, creates a Null value in case the value is null.
     */
    fun add(bool: Boolean?) = values.add(bool?.let { Primitive(it) } ?: Null)

    /**
     * Adds the specified character value to this array, creates a Null value in case the value is null.
     */
    fun add(char: Char?) = values.add(char?.let { Primitive(it) } ?: Null)

    /**
     * Adds the specified number value to this array, creates a Null value in case the value is null.
     */
    fun add(number: Number?) = values.add(number?.let { Primitive(it) } ?: Null)

    /**
     * Adds the specified string value to this array, creates a Null value in case the value is null.
     */
    fun add(string: String?) = values.add(string?.let { Primitive(it) } ?: Null)

    /**
     * Adds the specified value to this array, creates a Null value in case the value is null.
     */
    fun add(value: Value?) = values.add(value ?: Null)

    /**
     * Adds the specified value to this array at a given position, creates a Null value in case the value is null.
     */
    fun add(position: Int, value: Value?) = values.add(position, value ?: Null)

    /**
     * Adds all the elements from the specified array to this array.
     */
    fun addAll(array: Array) = values.addAll(array.values)

    /**
     * Removes the first occurrence of the specified value from this array, if it is present.
     */
    fun remove(value: Value): Boolean = values.remove(value)

    /**
     * Removes the value at a specific index and returns the removed value.
     */
    internal fun removeAt(index: Int): Value = values.removeAt(index)

    /**
     * Checks if the array contains the specified value.
     */
    operator fun contains(value: Value): Boolean = values.contains(value)

    /**
     * Returns the number of elements in this array.
     */
    fun size(): Int = values.size

    /**
     * Returns an iterator to traverse this array.
     */
    operator fun iterator(): Iterator<Value> = values.iterator()

    /**
     * Returns the value at the specified index.
     */
    operator fun get(i: Int): Value = values[i]

    /**
     * Returns the value of element at specified index if it is valid, otherwise returns null.
     */
    fun getOrNull(index: Int): Value? = values.getOrNull(index)

    /**
     * Returns the last element or null if the list is empty.
     */
    fun lastOrNull(): Value? = values.lastOrNull()

    /**
     * Checks if the given object is equal to this object.
     */
    override fun equals(other: Any?): Boolean =
        this === other || (other is Array && values == other.values)

    /**
     * Returns the hash code of the object.
     */
    override fun hashCode(): Int = values.hashCode()

    /**
     * Performs the given action on each element of the Array
     */
    fun forEach(action: (Value) -> Unit) {
        values.forEach(action)
    }

    /**
     * Performs the given action on each element of the Array and also the index of the element.
     */
    fun forEachIndexed(action: (index: Int, value: Value) -> Unit) {
        values.forEachIndexed(action)
    }

    /**
     * Transforms the elements of this Array to a new Array using the given transformation.
     */
    inline fun <reified R : Value> map(transform: (Value) -> R): Array {
        val mappedList = values.map { transform(it) }
        return Array(*mappedList.toTypedArray())
    }

    /**
     * Transforms the elements of this Array to a new Array using the given transformation and the index of the element.
     */
    inline fun <reified R : Value> mapIndexed(transform: (index: Int, Value) -> R): Array {
        val mappedList = values.mapIndexed { index, value -> transform(index, value) }
        return Array(*mappedList.toTypedArray())
    }

    /**
     * Sets a value at a specific position.
     */
    operator fun set(index: Int, value: Value) {
        values[index] = value
    }

    /**
     * Checks if the collection is empty
     */
    val isEmpty: Boolean
        get() = values.isEmpty()
}