package com.voyager.core.attribute

import android.view.View
import java.util.logging.Level
import java.util.logging.Logger

/**
 * Type-safe handler for applying a specific attribute to a View.
 */
class AttributeHandler(
    private val viewClass: Class<*>,
    private val valueClass: Class<*>,
    private val handler: (View, Any?) -> Unit,
) {
    private val logger: Logger by lazy { Logger.getLogger(AttributeHandler::class.java.name) }

    fun process(view: View, value: Any?) {
        if (viewClass.isInstance(view) && (value == null || valueClass.isInstance(value))) {
            try {
                handler(view, value)
            } catch (e: Exception) {
                logger.log(
                    Level.WARNING,
                    "Attribute handler failed for ${view::class.java.simpleName} value '$value'",
                    e
                )
            }
        } else {
            logger.warning("Type mismatch for attribute application. View: ${view::class.java.simpleName} (expected: ${viewClass.simpleName}), Value: ${value?.let { it::class.java.simpleName } ?: "null"} (expected: ${valueClass.simpleName})")
        }
    }
} 