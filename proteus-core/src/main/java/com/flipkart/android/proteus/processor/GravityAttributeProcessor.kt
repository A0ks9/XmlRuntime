package com.flipkart.android.proteus.processor

import android.content.Context
import android.content.res.TypedArray
import android.view.Gravity
import android.view.View
import androidx.annotation.IntDef
import com.flipkart.android.proteus.parser.ParseHelper
import com.flipkart.android.proteus.value.AttributeResource
import com.flipkart.android.proteus.value.Primitive
import com.flipkart.android.proteus.value.Resource
import com.flipkart.android.proteus.value.StyleResource
import com.flipkart.android.proteus.value.Value

/**
 * Abstract class for processing gravity attributes on Android Views.
 *
 * `GravityAttributeProcessor` is an abstract `AttributeProcessor` specialized for handling gravity-related attributes
 * (like `android:gravity`, `android:layout_gravity`) in Proteus layouts. It provides implementations to parse gravity values
 * from different attribute value types (Value, Resource, AttributeResource, StyleResource) and delegates the actual
 * gravity setting to the abstract `setGravity()` method, which must be implemented by concrete subclasses.
 *
 * @param V The type of Android View this GravityAttributeProcessor is designed to handle, must extend `View`.
 */
abstract class GravityAttributeProcessor<V : View> :
    AttributeProcessor<V>() { // Kotlin abstract class declaration inheriting from AttributeProcessor

    companion object { // Companion object for static members
        @JvmField
        val NO_GRAVITY =
            Primitive(Gravity.NO_GRAVITY) // Static constant for NO_GRAVITY Primitive Value
    }

    /**
     * Handles a Value type attribute to set the gravity on the View.
     * Parses the gravity value from the Value. If it's a number, it uses the integer value directly.
     * If it's a string, it parses the string to a gravity integer constant using `ParseHelper.parseGravity()`.
     *
     * @param view  The Android View to which gravity should be applied.
     * @param value The Value representing the gravity attribute.
     */
    override fun handleValue(
        view: V?, value: Value
    ) { // Override handleValue to set gravity from Value

        val gravity = when { // Determine gravity based on Value type
            value.isPrimitive && value.asPrimitive.isNumber() -> value.asInt() // If primitive number, use integer value
            value.isPrimitive -> ParseHelper.parseGravity(value.asString()) // If primitive string, parse gravity from string
            else -> Gravity.NO_GRAVITY // Default to NO_GRAVITY for other Value types
        }
        setGravity(view!!, gravity) // Call abstract setGravity method to apply gravity to the View
    }

    /**
     * Handles a Resource type attribute to set gravity.
     * Retrieves the gravity integer value from the Resource and applies it to the View.
     *
     * @param view     The Android View.
     * @param resource The Resource object containing the gravity integer value.
     */
    override fun handleResource(
        view: V?, resource: Resource
    ) { // Override handleResource to set gravity from Resource
        val gravityResource = resource.getInteger(view!!.context) // Get integer value from Resource
        val gravity = gravityResource
            ?: Gravity.NO_GRAVITY // Use resource value if not null, otherwise default to NO_GRAVITY
        setGravity(view, gravity) // Call abstract setGravity method to apply gravity to the View
    }

    /**
     * Handles an AttributeResource type attribute to set gravity.
     * Extracts the gravity value from the AttributeResource's TypedArray and applies it.
     *
     * @param view      The Android View.
     * @param attribute The AttributeResource object.
     */
    override fun handleAttributeResource(
        view: V?, attribute: AttributeResource
    ) { // Override handleAttributeResource to set gravity from AttributeResource
        val typedArray =
            attribute.apply(view!!.context) // Apply AttributeResource to get TypedArray
        set(view, typedArray) // Call private set method to handle TypedArray and set gravity
    }

    /**
     * Handles a StyleResource type attribute to set gravity.
     * Extracts the gravity value from the StyleResource's TypedArray and applies it.
     *
     * @param view  The Android View.
     * @param style The StyleResource object.
     */
    override fun handleStyleResource(
        view: V?, style: StyleResource
    ) { // Override handleStyleResource to set gravity from StyleResource
        val typedArray = style.apply(view!!.context) // Apply StyleResource to get TypedArray
        set(view, typedArray) // Call private set method to handle TypedArray and set gravity
    }

    /**
     * Private method to set gravity on the View from a TypedArray.
     * Extracts the gravity integer from the TypedArray at index 0 and applies it using the abstract `setGravity()` method.
     * Defaults to `Gravity.NO_GRAVITY` if the value is not found in the TypedArray.
     *
     * @param view The Android View.
     * @param a    The TypedArray from which to extract the gravity value.
     */
    private fun set(view: V, a: TypedArray) { // Private method to set gravity from TypedArray
        val gravityValue = a.getInt(
            0, Gravity.NO_GRAVITY
        ) // Get gravity int from TypedArray at index 0, default to NO_GRAVITY
        setGravity(
            view, gravityValue
        ) // Call abstract setGravity method to apply gravity to the View
        a.recycle() // Recycle the TypedArray after use
    }

    /**
     * Abstract method to be implemented by concrete GravityAttributeProcessor subclasses to set the gravity on the View.
     *
     * @param view    The Android View on which to set the gravity.
     * @param gravity The gravity value to be set. Uses the custom `@Gravity` annotation for type safety and documentation.
     */
    abstract fun setGravity(
        view: V, @ProteusGravity gravity: Int
    ) // Abstract method to set gravity, using @Gravity annotation for type safety

    /**
     * Overrides compile to convert a Value to a Primitive Gravity Value
     *
     * @param value   value to be compiled if null it returns NO_GRAVITY
     * @param context context
     * @return  compiled value which is Primitive NO_GRAVITY
     */
    override fun compile(value: Value?, context: Context): Value {
        return value?.asString()?.let { ParseHelper.getGravity(it) } ?: NO_GRAVITY
    }

    /**
     * Annotation interface `Gravity` for type-safe gravity attribute values.
     *
     * This annotation is intended for use with parameters of `setGravity()` method and other places where gravity values are expected.
     * It is inspired by and functionally similar to Android's built-in `android.view.Gravity` constants but serves as a type-safe
     * marker for attribute processing within the Proteus framework.
     */
    @IntDef( // IntDef to restrict possible values
        Gravity.NO_GRAVITY,
        Gravity.TOP,
        Gravity.BOTTOM,
        Gravity.LEFT,
        Gravity.RIGHT,
        Gravity.START,
        Gravity.END,
        Gravity.CENTER_VERTICAL,
        Gravity.FILL_VERTICAL,
        Gravity.CENTER_HORIZONTAL,
        Gravity.FILL_HORIZONTAL,
        Gravity.CENTER,
        Gravity.FILL
    )
    @Retention(AnnotationRetention.SOURCE) // Indicate that the annotation is for source code only, not retained at runtime
    @Target( // Specify where the annotation can be applied
        AnnotationTarget.FIELD,
        AnnotationTarget.PROPERTY_GETTER,
        AnnotationTarget.VALUE_PARAMETER,
        AnnotationTarget.LOCAL_VARIABLE,
        AnnotationTarget.CLASS
    )
    annotation class ProteusGravity // Annotation class declaration - @Gravity
}