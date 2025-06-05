package com.voyager.core.attribute

import android.view.View
import com.voyager.core.model.ConfigManager
import com.voyager.core.utils.CollectionUtils.partition
import com.voyager.core.utils.logging.LoggerFactory

/**
 * Public API for attribute processing in Voyager.
 * This object is responsible for applying attributes to views in a specific order
 * to ensure proper view initialization and layout. It handles the processing of
 * different types of attributes (ID, normal, constraint, and bias) in a specific
 * sequence to maintain proper view hierarchy and layout constraints.
 */
internal object AttributeProcessor {
    private val logger = LoggerFactory.getLogger(AttributeProcessor::class.java.simpleName)
    private val bitmask = BitmaskManager()
    private val config by lazy { ConfigManager.config }

    /**
     * Processes a map of attributes and applies them to the given view.
     * Attributes are processed in the following order:
     * 1. ID attribute (must be set first)
     * 2. Normal attributes (standard view attributes)
     * 3. Pure ConstraintLayout attributes (constraints without bias)
     * 4. Bias attributes (for ConstraintLayout)
     *
     * This ordering ensures that views are properly initialized and constraints
     * are applied in the correct sequence.
     *
     * @param view The target view to apply attributes to
     * @param attrs Map of attribute names to their values
     */
    internal fun processAttributes(view: View, attrs: Map<String, Any?>) {
        if (config.isLoggingEnabled) {
            logger.debug("processAttributes", "Processing ${attrs.size} attributes for ${view.javaClass.simpleName}")
        }

        bitmask.clear()
        
        // 1. Apply ID attribute first
        attrs.entries.find { AttributeOrder.isIDAttribute(it.key) }?.let { (name, value) ->
            if (config.isLoggingEnabled) {
                logger.debug("processAttributes", "Processing ID attribute: $name = $value")
            }
            processInternalAttributes(view, name, value)
        }

        // Partition attributes into different categories
        val (constraintAttrs, normalAttrs) = attrs.filterNot {
            AttributeOrder.isIDAttribute(it.key)
        }.partition { AttributeOrder.isConstraintLayoutAttribute(it.key) }

        val (pureConstraintAttrs, biasAttrs) = constraintAttrs.partition {
            AttributeOrder.isConstraint(it.key)
        }

        // 2. Apply normal attributes
        if (config.isLoggingEnabled && normalAttrs.isNotEmpty()) {
            logger.debug("processAttributes", "Processing ${normalAttrs.size} normal attributes")
        }
        normalAttrs.forEach { (name, value) -> 
            processInternalAttributes(view, name, value)
        }

        // 3. Apply pure ConstraintLayout attributes
        if (config.isLoggingEnabled && pureConstraintAttrs.isNotEmpty()) {
            logger.debug("processAttributes", "Processing ${pureConstraintAttrs.size} pure constraint attributes")
        }
        pureConstraintAttrs.forEach { (name, value) ->
            processInternalAttributes(view, name, value)
        }

        // 4. Apply bias attributes
        if (config.isLoggingEnabled && biasAttrs.isNotEmpty()) {
            logger.debug("processAttributes", "Processing ${biasAttrs.size} bias attributes")
        }
        biasAttrs.forEach { (name, value) -> 
            processInternalAttributes(view, name, value)
        }
    }

    /**
     * Processes a single attribute internally using the registered handlers.
     * This method ensures that each attribute is only processed once per view
     * using a bitmask for tracking. It also handles logging and error cases.
     *
     * @param view The target view
     * @param name The attribute name
     * @param value The attribute value
     */
    private fun processInternalAttributes(view: View, name: String, value: Any?) {
        val id = AttributeRegistry.ids[name] ?: run {
            if (config.isLoggingEnabled) {
                logger.warn("processInternalAttributes", "No handler registered for attribute: $name")
            }
            return
        }

        if (bitmask.setIfNotSet(id)) {
            AttributeRegistry.getHandler(id)?.process(view, value) ?: run {
                if (config.isLoggingEnabled) {
                    logger.error("processInternalAttributes", "Handler not found for attribute: $name")
                }
            }
        } else if (config.isLoggingEnabled) {
            logger.debug("processInternalAttributes", "Attribute already processed: $name")
        }
    }
} 