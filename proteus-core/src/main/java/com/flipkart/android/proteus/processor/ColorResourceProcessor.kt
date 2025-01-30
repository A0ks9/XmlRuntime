package com.flipkart.android.proteus.processor

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.view.View
import com.flipkart.android.proteus.value.AttributeResource
import com.flipkart.android.proteus.value.Color
import com.flipkart.android.proteus.value.Resource
import com.flipkart.android.proteus.value.StyleResource
import com.flipkart.android.proteus.value.Value

/**
 * Kotlin abstract class for processing color resources for Android Views in Proteus.
 *
 * This class extends [AttributeProcessor] and serves as a base for attribute processors
 * that handle color values and color state lists. It provides static helper functions
 * for color evaluation and compilation, as well as handling different resource types.
 *
 * @param V The type of View this attribute processor works with.
 */
abstract class ColorResourceProcessor<V : View> :
    AttributeProcessor<V>() { // Converted to Kotlin abstract class

    /**
     * Static method to evaluate a [Value] and return a [Color.Result].
     *
     * This method creates an anonymous [ColorResourceProcessor] to encapsulate the [Color.Result] and then processes the [value].
     *
     * @param value The Value to evaluate to a Color.Result.
     * @param view The ProteusView in context.
     * @return The Color.Result obtained from processing the value.
     */
    companion object { // Converted static methods to Companion Object
        @JvmStatic // To keep it accessible as a static method from Java
        fun evaluate(
            value: Value?, view: ProteusView
        ): Color.Result? { // Nullable Value and return type, renamed view parameter to proteusView for clarity
            var result: Color.Result? = null // Initialize result to null
            val processor = object :
                ColorResourceProcessor<View>() { // Anonymous object in Kotlin instead of anonymous class
                override fun setColor(view: View, color: Int) {
                    result = Color.Result.color(color) // Set the color result
                }

                override fun setColor(view: View, colors: ColorStateList) {
                    result = Color.Result.colors(colors) // Set the color state list result
                }
            }
            processor.process(
                view.asView(), value!!
            ) // Process the value using the anonymous processor
            return result // Return the evaluated result
        }

        /**
         * Static method to compile a [Value] to a static [Color] Value.
         *
         * This method handles different value types (Color, Object, Primitive) to produce a compiled Color Value.
         *
         * @param value The Value to compile. May be null.
         * @param context The Context.
         * @return A compiled Color Value or [Color.Int.BLACK] as a default.
         */
        @JvmStatic // To keep it accessible as a static method from Java
        fun staticCompile(value: Value?, context: Context): Value { // Nullable Value parameter
            return when { // Using Kotlin's 'when' for cleaner conditional logic
                value == null -> Color.Int.BLACK // Return BLACK if value is null
                value.isColor -> value // Return value if it's already a Color
                value.isObject -> Color.valueOf(
                    value.asObject(), context
                ) // Convert Object to Color
                value.isPrimitive -> { // Handle Primitive Value
                    val precompiled = staticPreCompile(
                        value.asPrimitive(), context, null
                    ) // Static pre-compilation
                    precompiled ?: Color.valueOf(
                        value.getAsString(), Color.Int.BLACK
                    ) // Return precompiled value or create Color from string, default to BLACK
                }

                else -> Color.Int.BLACK // Default case: return BLACK
            }
        }
    }

    /**
     * Handles a [Value] input.
     *
     * If the [value] is a Color, it applies the color to the View using [apply].
     * Otherwise, it precompiles the value and processes it using the default [process] method of [AttributeProcessor].
     *
     * @param view The View to process the attribute for.
     * @param value The Value containing the attribute value.
     */
    override fun handleValue(view: V?, value: Value) { // Nullable Value parameter
        if (value.isColor == true) { // Safe call and null check, isColor() check
            apply(view!!, value.asColor()) // Apply the color if it's a Color Value
        } else {
            process(
                view, precompile(
                    value,
                    view?.context!!,
                    (view.context as ProteusContext).functionManager // Smart cast to ProteusContext
                )!!
            ) // Fallback to default process for other value types after precompilation
        }
    }

    /**
     * Handles a [Resource] input.
     *
     * Retrieves a ColorStateList from the resource if available, otherwise tries to get a color int.
     * Calls [setColor] with the ColorStateList or color int, defaulting to [Color.Int.BLACK] if neither is found.
     *
     * @param view The View to process the attribute for.
     * @param resource The Resource object providing the color value.
     */
    override fun handleResource(view: V?, resource: Resource) {
        resource.getColorStateList(view?.context!!)
            ?.let { colors -> // Use let for concise null check and scope function for ColorStateList
                setColor(view, colors) // Set ColorStateList if available
                return // Early return after setting ColorStateList
            }
        val color =
            resource.getColor(view.context) // Try to get color int if ColorStateList is null
        setColor(
            view, color ?: Color.Int.BLACK.value
        ) // Elvis operator to default to BLACK if color int is also null
    }

    /**
     * Handles an [AttributeResource] input.
     *
     * Applies the [attribute] to get a TypedArray and calls the private [set] method to extract and set the color.
     *
     * @param view The View to process the attribute for.
     * @param attribute The AttributeResource providing the TypedArray.
     */
    override fun handleAttributeResource(view: V?, attribute: AttributeResource) {
        val typedArray =
            attribute.apply(view?.context!!) // Apply AttributeResource to get TypedArray
        set(view, typedArray) // Call private set method to handle TypedArray
        typedArray.recycle() // Recycle TypedArray
    }

    /**
     * Handles a [StyleResource] input.
     *
     * Applies the [style] to get a TypedArray and calls the private [set] method to extract and set the color.
     *
     * @param view The View to process the attribute for.
     * @param style The StyleResource providing the TypedArray.
     */
    override fun handleStyleResource(view: V?, style: StyleResource) {
        val typedArray = style.apply(view?.context!!) // Apply StyleResource to get TypedArray
        set(view, typedArray) // Call private set method to handle TypedArray
        typedArray.recycle() // Recycle TypedArray
    }

    /**
     * Private helper function to set the color from a TypedArray.
     *
     * Extracts a ColorStateList or a color int from the TypedArray at index 0 and calls the appropriate [setColor] method.
     *
     * @param view The View to set the color on.
     * @param a The TypedArray containing the color information.
     */
    private fun set(view: V, a: TypedArray) { // Made private in Kotlin
        a.getColorStateList(0)?.let { colors -> // Use let for concise null check for ColorStateList
            setColor(view, colors) // Set ColorStateList if available
            return // Early return after setting ColorStateList
        }
        setColor(
            view, a.getColor(0, Color.Int.BLACK.value)
        ) // Default to BLACK if ColorStateList is null, get color int from TypedArray
    }

    /**
     * Private helper function to apply a [Color] Value.
     *
     * Resolves the [Color] to a [Color.Result] and then calls the appropriate [setColor] method based on whether it contains colors or a single color.
     *
     * @param view The View to apply the color to.
     * @param color The Color Value to apply.
     */
    private fun apply(view: V, color: Color) { // Made private in Kotlin
        val result = color.apply(view.context) // Apply the Color Value
        result.colors?.let { colors -> // Use let for concise null check for colors
            setColor(view, colors) // Set ColorStateList if available
            return // Early return after setting ColorStateList
        }
        setColor(view, result.color) // Set color int if ColorStateList is null
    }

    /**
     * Abstract method to set the color int on the View.
     *
     * Subclasses must implement this to define how a single color int is applied to the View.
     *
     * @param view The View to set the color on.
     * @param color The color int to set.
     */
    abstract fun setColor(view: V, color: Int) // Abstract method to be implemented by subclasses

    /**
     * Abstract method to set the ColorStateList on the View.
     *
     * Subclasses must implement this to define how a ColorStateList is applied to the View.
     *
     * @param view The View to set the ColorStateList on.
     * @param colors The ColorStateList to set.
     */
    abstract fun setColor(
        view: V, colors: ColorStateList
    ) // Abstract method to be implemented by subclasses

    /**
     * Compiles a [Value] to a Color [Value].
     *
     * Uses the static compilation method [staticCompile] to perform the compilation.
     *
     * @param value The Value to compile. May be null.
     * @param context The Context.
     * @return The compiled Color Value.
     */
    override fun compile(
        value: Value?, context: Context
    ): Value { // Nullable Value parameter
        return staticCompile(value, context) // Delegate compilation to staticCompile method
    }
}