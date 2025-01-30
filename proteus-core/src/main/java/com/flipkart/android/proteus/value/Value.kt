package com.flipkart.android.proteus.value

/**
 * Abstract base class for all values used in the Proteus framework.
 * This class provides common functionality and type checking for different value types.
 */
abstract class Value {

    /**
     * Returns a deep copy of this value. Immutable elements
     * like primitives and nulls are not copied.
     */
    abstract fun copy(): Value

    // region Type Checking Properties

    /**
     * Inline property to check if this value is an array.
     */
    val isArray get() = this is Array

    /**
     * Inline property to check if this value is an object.
     */
    val isObject get() = this is ObjectValue

    /**
     * Inline property to check if this value is primitive.
     */
    val isPrimitive get() = this is Primitive

    /**
     * Inline property to check if this value is null.
     */
    val isNull get() = this is Null

    /**
     * Inline property to check if this value is Layout.
     */
    val isLayout get() = this is Layout

    /**
     * Inline property to check if this value is Dimension.
     */
    val isDimension get() = this is Dimension

    /**
     * Inline property to check if this value is StyleResource.
     */
    val isStyleResource get() = this is StyleResource

    /**
     * Inline property to check if this value is Color.
     */
    val isColor get() = this is Color

    /**
     * Inline property to check if this value is AttributeResource.
     */
    val isAttributeResource get() = this is AttributeResource

    /**
     * Inline property to check if this value is Resource.
     */
    val isResource get() = this is Resource

    /**
     * Inline property to check if this value is Binding.
     */
    val isBinding get() = this is Binding

    /**
     * Inline property to check if this value is DrawableValue.
     */
    val isDrawable get() = this is DrawableValue

    // endregion

    // region Type Casting Functions

    /**
     * Convenience method to get this value as an ObjectValue.
     * @throws IllegalStateException if the value is not of the correct type.
     */
    fun asObject(): ObjectValue = castOrThrow(isObject, "ObjectValue")

    /**
     * Convenience method to get this value as an Array.
     * @throws IllegalStateException if the value is not of the correct type.
     */
    fun asArray(): Array = castOrThrow(isArray, "Array")

    /**
     * Convenience method to get this value as a Primitive.
     * @throws IllegalStateException if the value is not of the correct type.
     */
    fun asPrimitive(): Primitive = castOrThrow(isPrimitive, "Primitive")

    /**
     * Convenience method to get this value as a Null.
     * @throws IllegalStateException if the value is not of the correct type.
     */
    fun asNull(): Null = castOrThrow(isNull, "Null")

    /**
     * Convenience method to get this value as a Layout.
     * @throws IllegalStateException if the value is not of the correct type.
     */
    fun asLayout(): Layout = castOrThrow(isLayout, "Layout")

    /**
     * Convenience method to get this value as a Dimension.
     * @throws IllegalStateException if the value is not of the correct type.
     */
    fun asDimension(): Dimension = castOrThrow(isDimension, "Dimension")

    /**
     * Convenience method to get this value as a StyleResource.
     * @throws IllegalStateException if the value is not of the correct type.
     */
    fun asStyleResource(): StyleResource = castOrThrow(isStyleResource, "StyleResource")

    /**
     * Convenience method to get this value as an AttributeResource.
     * @throws IllegalStateException if the value is not of the correct type.
     */
    fun asAttributeResource(): AttributeResource =
        castOrThrow(isAttributeResource, "AttributeResource")

    /**
     * Convenience method to get this value as a Color.
     * @throws IllegalStateException if the value is not of the correct type.
     */
    fun asColor(): Color = castOrThrow(isColor, "Color")

    /**
     * Convenience method to get this value as a Resource.
     * @throws IllegalStateException if the value is not of the correct type.
     */
    fun asResource(): Resource = castOrThrow(isResource, "Resource")

    /**
     * Convenience method to get this value as a Binding.
     * @throws IllegalStateException if the value is not of the correct type.
     */
    fun asBinding(): Binding = castOrThrow(isBinding, "Binding")

    /**
     * Convenience method to get this value as a DrawableValue.
     * @throws IllegalStateException if the value is not of the correct type.
     */
    fun asDrawable(): DrawableValue = castOrThrow(isDrawable, "Drawable")

    // endregion

    /**
     * Helper function to perform type casting with error handling.
     */
    private inline fun <reified T : Value> castOrThrow(condition: Boolean, typeName: String): T {
        if (condition) return this as T
        throw IllegalStateException("Not a $typeName: $this")
    }

    // region Abstract getAs... Methods

    /**
     * Returns this value as a Boolean, throws exception if not a Boolean
     */
    open fun getAsBoolean(): Boolean = throw UnsupportedOperationException(this::class.simpleName)

    /**
     * Returns this value as a String, throws exception if not a String
     */
    open fun getAsString(): String = throw UnsupportedOperationException(this::class.simpleName)

    /**
     * Returns this value as a Double, throws exception if not a Double
     */
    open fun getAsDouble(): Double = throw UnsupportedOperationException(this::class.simpleName)

    /**
     * Returns this value as a Float, throws exception if not a Float
     */
    open fun getAsFloat(): Float = throw UnsupportedOperationException(this::class.simpleName)

    /**
     * Returns this value as a Long, throws exception if not a Long
     */
    open fun getAsLong(): Long = throw UnsupportedOperationException(this::class.simpleName)

    /**
     * Returns this value as a Integer, throws exception if not a Integer
     */
    open fun getAsInt(): Int = throw UnsupportedOperationException(this::class.simpleName)

    /**
     * Returns this value as a Character, throws exception if not a Character
     */
    open fun getAsCharacter(): Char = throw UnsupportedOperationException(this::class.simpleName)

    // endregion
}