package com.voyager.utils.processors

import android.view.View
import androidx.appcompat.view.ContextThemeWrapper

/**
 * Abstract base class for parsing and processing view attributes.
 * This class provides functionality to register and handle attribute processors for different view types.
 */
abstract class ViewAttributeParser {

    init {
        addAttributes()
    }

    /**
     * Returns the type identifier for the view being parsed.
     * @return String representing the view type
     */
    abstract fun getViewType(): String

    /**
     * Creates a new view instance with the given context.
     * @param context The themed context wrapper to create the view with
     * @return A new View instance
     */
    protected abstract fun createView(context: ContextThemeWrapper): View

    /**
     * Initializes and registers the attributes for this view type.
     * Should be implemented to set up all attribute processors.
     */
    abstract fun addAttributes()

    companion object {
        /**
         * Registers a single attribute processor for a specific view type.
         * @param V The view type to register the attribute for
         * @param T The type of the attribute value
         * @param attributeName The name of the attribute to register
         * @param attributeProcessor The processor function that handles the attribute
         */
        @JvmStatic
        inline fun <reified V : View, reified T> registerAttribute(
            attributeName: String,
            noinline attributeProcessor: (V, T?) -> Unit,
        ) {
            AttributeProcessor.registerAttribute(attributeName, attributeProcessor)
        }

        /**
         * Registers multiple attribute processors for a specific view type.
         * @param V The view type to register the attributes for
         * @param T The type of the attribute values
         * @param attributeMap Map of attribute names to their processor functions
         */
        @JvmStatic
        inline fun <reified V : View, reified T> registerAttributes(attributeMap: Map<String, (V, T?) -> Unit>) {
            attributeMap.forEach { (attr, handler) ->
                AttributeProcessor.registerAttribute(
                    attr,
                    handler
                )
            }
        }
    }
}