package com.flipkart.android.proteus.value

abstract class Value {
    abstract fun copy(): Value

    // Inline properties for type checks
    val isArray get() = this is Array
    val isObject get() = this is ObjectValue
    val isPrimitive get() = this is Primitive
    val isNull get() = this is Null
    val isLayout get() = this is Layout
    val isDimension get() = this is Dimension
    val isStyleResource get() = this is StyleResource
    val isColor get() = this is Color
    val isAttributeResource get() = this is AttributeResource
    val isResource get() = this is Resource
    val isBinding get() = this is Binding
    val isDrawable get() = this is DrawableValue

    // Extension functions for type casting
    fun asObject(): ObjectValue = castOrThrow(isObject, "ObjectValue")
    fun asArray(): Array = castOrThrow(isArray, "Array")
    fun asPrimitive(): Primitive = castOrThrow(isPrimitive, "Primitive")
    fun asNull(): Null = castOrThrow(isNull, "Null")
    fun asLayout(): Layout = castOrThrow(isLayout, "Layout")
    fun asDimension(): Dimension = castOrThrow(isDimension, "Dimension")
    fun asStyleResource(): StyleResource = castOrThrow(isStyleResource, "StyleResource")
    fun asAttributeResource(): AttributeResource =
        castOrThrow(isAttributeResource, "AttributeResource")

    fun asColor(): Color = castOrThrow(isColor, "Color")
    fun asResource(): Resource = castOrThrow(isResource, "Resource")
    fun asBinding(): Binding = castOrThrow(isBinding, "Binding")
    fun asDrawable(): DrawableValue = castOrThrow(isDrawable, "Drawable")


    // Helper function for casting with error handling
    private inline fun <reified T : Value> castOrThrow(condition: Boolean, typeName: String): T {
        if (condition) return this as T
        throw IllegalStateException("Not a $typeName: $this")
    }


    // Default implementations for getAs... methods (can be overridden in subclasses)
    open fun getAsBoolean(): Boolean = throw UnsupportedOperationException(this::class.simpleName)
    open fun getAsString(): String = throw UnsupportedOperationException(this::class.simpleName)
    open fun getAsDouble(): Double = throw UnsupportedOperationException(this::class.simpleName)
    open fun getAsFloat(): Float = throw UnsupportedOperationException(this::class.simpleName)
    open fun getAsLong(): Long = throw UnsupportedOperationException(this::class.simpleName)
    open fun getAsInt(): Int = throw UnsupportedOperationException(this::class.simpleName)
    open fun getAsCharacter(): Char = throw UnsupportedOperationException(this::class.simpleName)
}