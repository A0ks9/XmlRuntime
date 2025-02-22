package com.runtimexml.utils.processors

import android.view.View
import com.runtimexml.utils.interfaces.AttributeProcessorRegistry

abstract class ViewAttributeParser : AttributeRegistry() {

    init {
        addAttributes()
    }

    abstract fun getViewType(): String
    abstract fun addAttributes()

    companion object {
        @JvmStatic
        fun <V : View, T> registerAttribute(
            attributeName: String, attributeProcessor: AttributeProcessorRegistry<V, T>
        ) {
            AttributeRegistry.registerAttribute(attributeName, attributeProcessor)
        }

        @JvmStatic
        fun <V : View, T> registerAttributes(attributeMap: Map<String, AttributeProcessorRegistry<V, T>>) {
            AttributeRegistry.registerAttributes(attributeMap)
        }

        @JvmStatic
        fun <V : View, T> registerAttributes(
            vararg attributeNames: String, attributeProcessor: AttributeProcessorRegistry<V, T>
        ) {
            AttributeRegistry.registerAttributes(
                *attributeNames, attributeProcessor = attributeProcessor
            )
        }
    }
}