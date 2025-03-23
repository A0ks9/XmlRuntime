package com.voyager.utils.processors

import android.view.View
import androidx.appcompat.view.ContextThemeWrapper

abstract class ViewAttributeParser {

    init {
        addAttributes()
    }

    abstract fun getViewType(): String
    protected abstract fun createView(context: ContextThemeWrapper): View
    abstract fun addAttributes()

    companion object {
        @JvmStatic
        inline fun <reified V : View, reified T> registerAttribute(
            attributeName: String,
            noinline attributeProcessor: (V, T?) -> Unit,
        ) {
            AttributeProcessor.registerAttribute(attributeName, attributeProcessor)
        }

        @JvmStatic
        inline fun <reified V : View, reified T> registerAttributes(attributeMap: Map<String, (V, T?) -> Unit>) {
            AttributeProcessor.registerAttributes(attributeMap)
        }
    }
}