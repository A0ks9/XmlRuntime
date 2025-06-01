package com.voyager.core.attribute

import android.view.View
import com.voyager.core.utils.CollectionUtils.partition

/**
 * Public API for attribute processing in Voyager.
 */
object AttributeProcessor {
    private val bitmask = BitmaskManager()

    fun processAttributes(view: View, attrs: Map<String, Any?>) {
        bitmask.clear()
        // 1. Apply ID attribute first
        attrs.entries.find { AttributeOrder.isIDAttribute(it.key) }?.let { (name, value) ->
            processInternalAttributes(view, name, value)
        }
        // Partition attributes
        val (constraintAttrs, normalAttrs) = attrs.filterNot {
            AttributeOrder.isIDAttribute(
                it.key
            )
        }.partition { AttributeOrder.isConstraintLayoutAttribute(it.key) }

        val (pureConstraintAttrs, biasAttrs) = constraintAttrs.partition {
            AttributeOrder.isConstraint(
                it.key
            )
        }
        // 2. Apply normal attributes
        normalAttrs.forEach { (name, value) -> processInternalAttributes(view, name, value) }
        // 3. Apply pure ConstraintLayout attributes
        pureConstraintAttrs.forEach { (name, value) ->
            processInternalAttributes(
                view, name, value
            )
        }
        // 4. Apply bias attributes
        biasAttrs.forEach { (name, value) -> processInternalAttributes(view, name, value) }
    }

    fun processInternalAttributes(view: View, name: String, value: Any?) {
        val id = AttributeRegistry.ids[name] ?: return
        if (bitmask.setIfNotSet(id)) {
            (AttributeRegistry.getHandler(id) as? AttributeHandler<View, Any?>)?.process(
                view,
                value
            )
        }
    }
} 