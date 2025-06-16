package com.voyager.core.attribute

import android.view.View
import com.voyager.core.model.ConfigManager
import com.voyager.core.utils.logging.LoggerFactory

/**
 * Type-safe handler for applying a specific attribute to a View.
 * This class ensures that attributes are applied to the correct view type
 * with the correct value type, providing type safety at runtime.
 *
 * @property viewClass The class of the View this handler is designed for
 * @property valueClass The class of the value this handler expects
 * @property handler The function that applies the attribute to the view
 */
@PublishedApi
internal class AttributeHandler(
    private val viewClass: Class<out View>,
    private val valueClass: Class<*>,
    private val handler: (View, Any) -> Unit,
) {
    private val logger = LoggerFactory.getLogger(AttributeHandler::class.java.simpleName)
    private val config by lazy { ConfigManager.config }

    /**
     * Processes an attribute value for a view.
     * This method performs type checking and applies the attribute if types match.
     *
     * @param view The view to apply the attribute to
     * @param value The value to apply
     */
    internal fun process(view: View, value: Any) {
        if (viewClass.isInstance(view) && (valueClass.isInstance(value))) {
            try {
                handler(view, value)
                if (config.isLoggingEnabled) {
                    logger.debug(
                        "process",
                        "Successfully applied attribute to ${view::class.java.simpleName}"
                    )
                }
            } catch (e: Exception) {
                if (config.isLoggingEnabled) {
                    logger.error(
                        "process",
                        "Attribute handler failed for ${view::class.java.simpleName} value '$value'",
                        e
                    )
                }
            }
        } else {
            if (config.isLoggingEnabled) {
                logger.warn(
                    "process",
                    "Type mismatch for attribute application. View: ${view::class.java.simpleName} " + "(expected: ${viewClass.simpleName}), Value: ${value.let { it::class.java.simpleName } ?: "null"} " + "(expected: ${valueClass.simpleName})")
            }
        }
    }
} 