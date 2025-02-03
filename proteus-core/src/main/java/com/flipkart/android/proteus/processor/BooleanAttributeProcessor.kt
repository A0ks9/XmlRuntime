package com.flipkart.android.proteus.processor

import android.content.Context
import android.view.View
import com.flipkart.android.proteus.ProteusConstants
import com.flipkart.android.proteus.ProteusContext
import com.flipkart.android.proteus.parser.ParseHelper
import com.flipkart.android.proteus.value.AttributeResource
import com.flipkart.android.proteus.value.Resource
import com.flipkart.android.proteus.value.StyleResource
import com.flipkart.android.proteus.value.Value

/**
 * Kotlin class for processing boolean attributes for Android Views in Proteus.
 *
 * This class extends [AttributeProcessor] and provides a base for attribute processors
 * that handle boolean values. It defines how to process different types of inputs
 * (Value, Resource, AttributeResource, StyleResource) and sets a boolean value on the View.
 *
 * @param V The type of View this attribute processor works with.
 */
open class BooleanAttributeProcessor<V : View>(private val setBoolean: (V, Boolean) -> Unit) :
    AttributeProcessor<V>() { // Converted to Kotlin abstract class

    /**
     * Handles a [Value] input.
     *
     * If the [value] is a Primitive and contains a boolean, it directly calls [setBoolean] to apply the boolean value.
     * Otherwise, it precompiles the [value] and processes it using the default [process] method of [AttributeProcessor].
     *
     * @param view The View to process the attribute for.
     * @param value The Value containing the attribute value.
     */
    override fun handleValue(view: V?, value: Value) { // Nullable Value parameter
        if (value.isPrimitive == true && value.asPrimitive.isBoolean()) { // Safe call and elvis operator for null check and primitive/boolean check
            setBoolean(
                view!!, value.asPrimitive.asBoolean()
            ) // Directly set boolean if it's a boolean primitive
        } else {
            process(
                view, precompile(
                    value,
                    view!!.context,
                    (view.context as ProteusContext).getFunctionManager() // Smart cast to ProteusContext for functionManager access
                )!!
            ) // Fallback to default process for other value types after precompilation
        }
    }

    /**
     * Handles a [Resource] input.
     *
     * Retrieves a boolean value from the [resource] and calls [setBoolean] to apply it to the View.
     * If the resource does not provide a boolean value, it defaults to `false`.
     *
     * @param view The View to process the attribute for.
     * @param resource The Resource object providing the boolean value.
     */
    override fun handleResource(view: V?, resource: Resource) {
        val bool = resource.getBoolean(view?.context!!) // Get boolean value from resource
        setBoolean(
            view, bool == true
        ) // Elvis operator to default to false if resource.getBoolean returns null
    }

    /**
     * Handles an [AttributeResource] input.
     *
     * Applies the [attribute] to obtain a TypedArray, reads the boolean value from it, and calls [setBoolean].
     * Defaults to `false` if the TypedArray does not contain a boolean at index 0.
     *
     * @param view The View to process the attribute for.
     * @param attribute The AttributeResource providing the TypedArray.
     */
    override fun handleAttributeResource(view: V?, attribute: AttributeResource) {
        val typedArray =
            attribute.apply(view?.context!!) // Apply AttributeResource to get TypedArray
        setBoolean(
            view, typedArray.getBoolean(0, false)
        ) // Get boolean from TypedArray, default to false
        typedArray.recycle() // Recycle TypedArray to avoid resource leaks
    }

    /**
     * Handles a [StyleResource] input.
     *
     * Applies the [style] to obtain a TypedArray, reads the boolean value from it, and calls [setBoolean].
     * Defaults to `false` if the TypedArray does not contain a boolean at index 0.
     *
     * @param view The View to process the attribute for.
     * @param style The StyleResource providing the TypedArray.
     */
    override fun handleStyleResource(view: V?, style: StyleResource) {
        val typedArray = style.apply(view?.context!!) // Apply StyleResource to get TypedArray
        setBoolean(
            view, typedArray.getBoolean(0, false)
        ) // Get boolean from TypedArray, default to false
        typedArray.recycle() // Recycle TypedArray to avoid resource leaks
    }

    /**
     * Compiles a [Value] to a boolean [Value].
     *
     * Parses the given [value] to a boolean and returns [ProteusConstants.TRUE] or [ProteusConstants.FALSE] accordingly.
     *
     * @param value The Value to compile. May be null.
     * @param context The Context.
     * @return [ProteusConstants.TRUE] if the parsed boolean is true, [ProteusConstants.FALSE] otherwise.
     */
    override fun compile(
        value: Value?, context: Context
    ): Value { // Nullable Value parameter
        return if (ParseHelper.parseBoolean(value)) ProteusConstants.TRUE else ProteusConstants.FALSE // Simplified using 'if' expression and boolean parsing
    }
}