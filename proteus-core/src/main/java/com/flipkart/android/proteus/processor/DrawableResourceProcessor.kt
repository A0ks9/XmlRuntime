package com.flipkart.android.proteus.processor

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import com.flipkart.android.proteus.ProteusContext
import com.flipkart.android.proteus.ProteusView
import com.flipkart.android.proteus.value.AttributeResource
import com.flipkart.android.proteus.value.DrawableValue
import com.flipkart.android.proteus.value.Resource
import com.flipkart.android.proteus.value.StyleResource
import com.flipkart.android.proteus.value.Value

/**
 * A class [AttributeProcessor] for handling drawable resources.
 *
 * @param V The type of View this processor handles (e.g., [View], [ImageView]).
 */
open class DrawableResourceProcessor<V : View>(
    private val setDrawable: (
        V, Drawable
    ) -> Unit
) : AttributeProcessor<V>() {

    companion object {
        /**
         * Evaluates a drawable value and returns the result as a [Drawable].
         *
         * @param value The drawable value to evaluate.
         * @param view The [ProteusView] to evaluate the drawable for.
         * @return The evaluated drawable, or `null` if the value is invalid.
         */
        fun evaluate(value: Value?, view: ProteusView): Drawable? {
            if (value == null) return null

            val result = arrayOfNulls<Drawable>(1)
            val processor = DrawableResourceProcessor<View> { view, drawable ->
                result[0] = drawable
            }
            processor.process(view.asView, value)

            return result[0]
        }

        /**
         * Compiles a drawable value into a [Value].
         *
         * @param value The drawable value to compile.
         * @param context The [Context] used for compilation.
         * @return The compiled drawable value.
         */
        fun staticCompile(value: Value?, context: Context): Value {
            return when {
                value == null -> DrawableValue.ColorValue.BLACK
                value.isDrawable -> value
                value.isPrimitive -> {
                    val precompiled =
                        AttributeProcessor.staticPreCompile(value.asPrimitive, context, null)
                    precompiled ?: DrawableValue.valueOf(value.asString())!!
                }

                value.isObject -> DrawableValue.valueOf(value.asObject, context)!!
                else -> DrawableValue.ColorValue.BLACK
            }
        }
    }

    /**
     * Handles a drawable value and sets it on the view.
     *
     * @param view The view to set the drawable on.
     * @param value The drawable value to process.
     */
    override fun handleValue(view: V?, value: Value) {
        if (value.isDrawable) {
            val drawableValue = value.asDrawable()
            val loader = (view as ProteusView).viewManager.context.getLoader()
            drawableValue.apply(view, view.context, loader!!) { drawable ->
                setDrawable(view, drawable)
            }
        } else {
            process(
                view, precompile(
                    value, view!!.context, (view.context as ProteusContext).getFunctionManager()
                )!!
            )
        }
    }

    /**
     * Handles a drawable resource and sets it on the view.
     *
     * @param view The view to set the drawable on.
     * @param resource The drawable resource to process.
     */
    override fun handleResource(view: V?, resource: Resource) {
        resource.getDrawable(view!!.context)?.let { drawable ->
            setDrawable(view, drawable)
        }
    }

    /**
     * Handles a drawable attribute resource and sets it on the view.
     *
     * @param view The view to set the drawable on.
     * @param attribute The drawable attribute resource to process.
     */
    override fun handleAttributeResource(view: V?, attribute: AttributeResource) {
        attribute.apply(view!!.context).getDrawable(0)?.let { drawable ->
            setDrawable(view, drawable)
        }
    }

    /**
     * Handles a drawable style resource and sets it on the view.
     *
     * @param view The view to set the drawable on.
     * @param style The drawable style resource to process.
     */
    override fun handleStyleResource(view: V?, style: StyleResource) {
        style.apply(view!!.context).getDrawable(0)?.let { drawable ->
            setDrawable(view, drawable)
        }
    }

    /**
     * Compiles a drawable value into a [Value].
     *
     * @param value The drawable value to compile.
     * @param context The [Context] used for compilation.
     * @return The compiled drawable value.
     */
    override fun compile(value: Value?, context: Context): Value {
        return staticCompile(value, context)
    }
}