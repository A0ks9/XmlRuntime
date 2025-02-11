package com.flipkart.android.proteus.processor

import android.view.View
import android.widget.TextView
import com.flipkart.android.proteus.value.AttributeResource
import com.flipkart.android.proteus.value.Resource
import com.flipkart.android.proteus.value.StyleResource
import com.flipkart.android.proteus.value.Value

/**
 * An open [AttributeProcessor] for handling number attributes.
 *
 * @param V The type of View this processor handles (e.g., [View], [TextView]).
 */
open class NumberAttributeProcessor<V : View>(private val setNumber: (V, Number) -> Unit) :
    AttributeProcessor<V>() {

    /**
     * Handles a number value and sets it on the view.
     *
     * @param view The view to set the number on.
     * @param value The number value to process.
     */
    override fun handleValue(view: V?, value: Value) {
        if (value.isPrimitive) {
            setNumber(view!!, value.asPrimitive.asNumber())
        }
    }

    /**
     * Handles a number resource and sets it on the view.
     *
     * @param view The view to set the number on.
     * @param resource The number resource to process.
     */
    override fun handleResource(view: V?, resource: Resource) {
        val number = resource.getInteger(view!!.context) ?: 0
        setNumber(view, number)
    }

    /**
     * Handles a number attribute resource and sets it on the view.
     *
     * @param view The view to set the number on.
     * @param attribute The number attribute resource to process.
     */
    override fun handleAttributeResource(view: V?, attribute: AttributeResource) {
        val number = attribute.apply(view!!.context).getFloat(0, 0f)
        setNumber(view, number)
    }

    /**
     * Handles a number style resource and sets it on the view.
     *
     * @param view The view to set the number on.
     * @param style The number style resource to process.
     */
    override fun handleStyleResource(view: V?, style: StyleResource) {
        val number = style.apply(view!!.context).getFloat(0, 0f)
        setNumber(view, number)
    }
}