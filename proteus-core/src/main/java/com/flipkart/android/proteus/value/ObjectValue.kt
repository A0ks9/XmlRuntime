package com.flipkart.android.proteus.value

class ObjectValue : Value() {

    private val members = mutableMapOf<String, Value>()

    /**
     * Creates a deep copy of this ObjectValue.
     */
    override fun copy(): ObjectValue = ObjectValue().apply {
        members.forEach { (key, value) -> set(key, value.copy()) }
    }

    /**
     * Puts a value with a given property key. If the value is null, then Null is put
     */
    operator fun set(property: String, value: Value?) {
        members[property] = value ?: Null
    }

    /**
     * Puts a string value with a given property key.
     */
    fun putProperty(property: String, value: String?) = set(property, value?.let { Primitive(it) })

    /**
     * Puts a number value with a given property key.
     */
    fun putProperty(property: String, value: Number?) = set(property, value?.let { Primitive(it) })

    /**
     * Puts a boolean value with a given property key.
     */
    fun putProperty(property: String, value: Boolean?) = set(property, value?.let { Primitive(it) })

    /**
     * Puts a char value with a given property key.
     */
    fun putProperty(property: String, value: Char?) = set(property, value?.let { Primitive(it) })

    /**
     * Removes a value from this object, returns the removed value, otherwise null
     */
    fun remove(property: String): Value? = members.remove(property)


    /**
     * Returns the size of the members.
     */
    fun size(): Int = members.size

    /**
     * Checks if the members contains the given key.
     */
    operator fun contains(memberName: String): Boolean = members.containsKey(memberName)

    /**
     * Checks if member is a primitive.
     */
    fun isPrimitive(memberName: String): Boolean =
        contains(memberName) && this[memberName] is Primitive

    /**
     * Checks if member is a boolean primitive.
     */
    fun isBoolean(memberName: String): Boolean =
        contains(memberName) && this[memberName] is Primitive && getAsPrimitive(memberName)?.isBoolean() == true

    /**
     * Checks if member is a number primitive.
     */
    fun isNumber(memberName: String): Boolean =
        contains(memberName) && this[memberName] is Primitive && getAsPrimitive(memberName)?.isNumber() == true

    /**
     * Checks if member is an ObjectValue.
     */
    fun isObject(memberName: String): Boolean =
        contains(memberName) && this[memberName] is ObjectValue

    /**
     * Checks if member is an Array.
     */
    fun isArray(memberName: String): Boolean = contains(memberName) && this[memberName] is Array

    /**
     * Checks if member is a Null.
     */
    fun isNull(memberName: String): Boolean = contains(memberName) && this[memberName] is Null

    /**
     * Checks if member is a Layout.
     */
    fun isLayout(memberName: String): Boolean = contains(memberName) && this[memberName] is Layout

    /**
     * Checks if member is a Binding.
     */
    fun isBinding(memberName: String): Boolean = contains(memberName) && this[memberName] is Binding

    /**
     * Returns the value with a given property key.
     */
    operator fun get(memberName: String): Value? = members[memberName]

    /**
     * Returns the value with a given property key as a primitive.
     */
    fun getAsPrimitive(memberName: String): Primitive? = this[memberName] as? Primitive

    /**
     * Returns the value with a given property key as a boolean.
     */
    fun getAsBoolean(memberName: String): Boolean? = getAsPrimitive(memberName)?.getAsBoolean()

    /**
     * Returns the value with a given property key as a boolean, returns the default value if the property is not set.
     */
    fun getAsBoolean(memberName: String, defaultValue: Boolean): Boolean =
        getAsBoolean(memberName) ?: defaultValue

    /**
     * Returns the value with a given property key as a integer.
     */
    fun getAsInteger(memberName: String): Int? = getAsPrimitive(memberName)?.getAsInt()

    /**
     * Returns the value with a given property key as a integer, returns the default value if the property is not set.
     */
    fun getAsInteger(memberName: String, defaultValue: Int): Int =
        getAsInteger(memberName) ?: defaultValue

    /**
     * Returns the value with a given property key as a float.
     */
    fun getAsFloat(memberName: String): Float? = getAsPrimitive(memberName)?.getAsFloat()

    /**
     * Returns the value with a given property key as a float, returns the default value if the property is not set.
     */
    fun getAsFloat(memberName: String, defaultValue: Float): Float =
        getAsFloat(memberName) ?: defaultValue

    /**
     * Returns the value with a given property key as a double.
     */
    fun getAsDouble(memberName: String): Double? = getAsPrimitive(memberName)?.getAsDouble()

    /**
     * Returns the value with a given property key as a double, returns the default value if the property is not set.
     */
    fun getAsDouble(memberName: String, defaultValue: Double): Double =
        getAsDouble(memberName) ?: defaultValue

    /**
     * Returns the value with a given property key as a long.
     */
    fun getAsLong(memberName: String): Long? = getAsPrimitive(memberName)?.getAsLong()

    /**
     * Returns the value with a given property key as a long, returns the default value if the property is not set.
     */
    fun getAsLong(memberName: String, defaultValue: Long): Long =
        getAsLong(memberName) ?: defaultValue

    /**
     * Returns the value with a given property key as a String.
     */
    fun getAsString(memberName: String): String? = getAsPrimitive(memberName)?.getAsString()

    /**
     * Returns the value with a given property key as an Array.
     */
    fun getAsArray(memberName: String): Array? = this[memberName] as? Array

    /**
     * Returns the value with a given property key as an ObjectValue.
     */
    fun getAsObject(memberName: String): ObjectValue? = this[memberName] as? ObjectValue

    /**
     * Returns the value with a given property key as a Layout.
     */
    fun getAsLayout(memberName: String): Layout? = this[memberName] as? Layout

    /**
     * Returns the value with a given property key as a Binding.
     */
    fun getAsBinding(memberName: String): Binding? = this[memberName] as? Binding

    /**
     * Returns the value of the property with a given key, or null if the property is not found
     */
    fun getOrNull(key: String): Value? = this[key]

    /**
     * Performs the given action for each key-value pair.
     */
    fun forEach(action: (key: String, value: Value) -> Unit) =
        members.forEach { (key, value) -> action(key, value) }

    /**
     * Returns the entry set of this map.
     */
    fun entrySet(): Set<MutableMap.MutableEntry<String, Value>> = members.entries

    /**
     * Compares two objects for equality
     */
    override fun equals(other: Any?): Boolean =
        this === other || (other is ObjectValue && members == other.members)

    /**
     * Returns the hash code for this object.
     */
    override fun hashCode(): Int = members.hashCode()
    val isEmpty: Boolean
        get() = members.isEmpty()
}
