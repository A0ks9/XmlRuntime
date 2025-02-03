package com.flipkart.android.proteus.processor

import android.content.Context
import android.view.View
import android.widget.TextView
import com.flipkart.android.proteus.ProteusContext
import com.flipkart.android.proteus.ProteusView
import com.flipkart.android.proteus.value.AttributeResource
import com.flipkart.android.proteus.value.Dimension
import com.flipkart.android.proteus.value.Resource
import com.flipkart.android.proteus.value.StyleResource
import com.flipkart.android.proteus.value.Value

/**
 * An abstract [AttributeProcessor] for handling dimension attributes.
 *
 * @param T The type of View this processor handles (e.g., [View], [TextView]).
 */
open class DimensionAttributeProcessor<T : View>(private val setDimension: (T?, Float) -> Unit) :
    AttributeProcessor<T>() {

    companion object {
        /**
         * Evaluates a dimension value and returns the result as a float.
         *
         * @param value The dimension value to evaluate.
         * @param view The [ProteusView] to evaluate the dimension for.
         * @return The evaluated dimension as a float.
         */
        fun evaluate(value: Value?, view: ProteusView): Float {
            if (value == null) {
                return Dimension.ZERO.apply(view.asView.context)
            }

            val result = floatArrayOf(0f)
            val processor = DimensionAttributeProcessor<View> { view, dimension ->
                result[0] = dimension
            }
            processor.process(view.asView, value)

            return result[0]
        }

        /**
         * Compiles a dimension value into a [Value].
         *
         * @param value The dimension value to compile.
         * @param context The [Context] used for compilation.
         * @return The compiled dimension value.
         */
        fun staticCompile(value: Value?, context: Context): Value {
            if (value == null || !value.isPrimitive) {
                return Dimension.ZERO
            }
            if (value.isDimension) {
                return value
            }
            val precompiled = AttributeProcessor.staticPreCompile(value.asPrimitive, context, null)
            if (precompiled != null) {
                return precompiled
            }
            return Dimension.valueOf(value.asString())
        }
    }

    /**
     * Handles a dimension value and sets it on the view.
     *
     * @param view The view to set the dimension on.
     * @param value The dimension value to process.
     */
    override fun handleValue(view: T?, value: Value) {
        when {
            value.isDimension -> setDimension(view, value.asDimension().apply(view!!.context))
            value.isPrimitive -> process(
                view, precompile(
                    value, view!!.context, (view.context as ProteusContext).getFunctionManager()
                )!!
            )
        }
    }

    /**
     * Handles a dimension resource and sets it on the view.
     *
     * @param view The view to set the dimension on.
     * @param resource The dimension resource to process.
     */
    override fun handleResource(view: T?, resource: Resource) {
        val dimension = resource.getDimension(view!!.context)
        setDimension(view, dimension ?: 0f)
    }

    /**
     * Handles a dimension attribute resource and sets it on the view.
     *
     * @param view The view to set the dimension on.
     * @param attribute The dimension attribute resource to process.
     */
    override fun handleAttributeResource(view: T?, attribute: AttributeResource) {
        val dimension = attribute.apply(view!!.context).getDimensionPixelSize(0, 0)
        setDimension(view, dimension.toFloat())
    }

    /**
     * Handles a dimension style resource and sets it on the view.
     *
     * @param view The view to set the dimension on.
     * @param style The dimension style resource to process.
     */
    override fun handleStyleResource(view: T?, style: StyleResource) {
        val dimension = style.apply(view!!.context).getDimensionPixelSize(0, 0)
        setDimension(view, dimension.toFloat())
    }

    /**
     * Compiles a dimension value into a [Value].
     *
     * @param value The dimension value to compile.
     * @param context The [Context] used for compilation.
     * @return The compiled dimension value.
     */
    override fun compile(value: Value?, context: Context): Value {
        return staticCompile(value, context)
    }
}