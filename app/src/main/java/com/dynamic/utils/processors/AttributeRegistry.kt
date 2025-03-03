package com.dynamic.utils.processors


import android.util.Log
import android.view.View
import com.dynamic.utils.Attributes
import com.dynamic.utils.BaseViewAttributes
import com.dynamic.utils.interfaces.AttributeProcessorRegistry

open class AttributeRegistry {

    companion object {
        private val attributeProcessors = BaseViewAttributes.AttributeProcessors

        /**
         * Configures an [AttributeRegistry] using the provided configuration block.
         *
         * @param processorConfig Configuration block for the [AttributeRegistry].
         * @return The configured [AttributeRegistry].
         */
        @JvmStatic
        fun configureProcessor(processorConfig: AttributeRegistry.() -> Unit): AttributeRegistry {
            return AttributeRegistry().apply(processorConfig)
        }

        /**
         * Adds a single attribute and its associated processor to the registry.
         *
         * @param attributeName The name of the attribute.
         * @param attributeProcessor The processor responsible for applying the attribute.
         * @throws IllegalArgumentException if an attribute with the same name already exists.
         */
        @Suppress("UNCHECKED_CAST")
        @JvmStatic
        fun <V : View, T> registerAttribute(
            attributeName: String, attributeProcessor: AttributeProcessorRegistry<V, T>
        ) {
            require(attributeProcessors.none { it.first == attributeName }) {
                "Attribute with name '$attributeName' already registered."
            }
            attributeProcessors.add(attributeName to attributeProcessor as AttributeProcessorRegistry<*, Any>)
        }

        /**
         * Adds multiple attributes and their associated processors to the registry.
         *
         * @param attributeMap A map of attribute names to their processors.
         * @throws IllegalArgumentException if any attribute name is already registered.
         */
        @JvmStatic
        fun <V : View, T> registerAttributes(attributeMap: Map<String, AttributeProcessorRegistry<V, T>>) {
            attributeMap.forEach { (attr, processor) ->
                registerAttribute(attr, processor)
            }
        }

        /**
         * Adds multiple attributes with the same processor to the registry.
         *
         * @param attributeNames A vararg of attribute names.
         * @param attributeProcessor The processor responsible for applying the attributes.
         * @throws IllegalArgumentException if any attribute name is already registered.
         */
        @JvmStatic
        fun <V : View, T> registerAttributes(
            vararg attributeNames: String, attributeProcessor: AttributeProcessorRegistry<V, T>
        ) {
            attributeNames.forEach { registerAttribute(it, attributeProcessor) }
        }

        /**
         * Applies a single attribute to the target view.
         *
         * @param targetView The view to apply the attribute to.
         * @param attributeName The name of the attribute.
         * @param attributeValue The value of the attribute.
         * @throws IllegalArgumentException if no attributes are registered or if the attribute is not found.
         * @throws ClassCastException if there is a type mismatch during attribute application.
         */
        @JvmStatic
        @Suppress("UNCHECKED_CAST")
        fun <V : View, T> applyAttribute(targetView: V, attributeName: String, attributeValue: T?) {
            require(attributeProcessors.isNotEmpty()) { "No attributes found" }

            val attributeProcessor = attributeProcessors.find { it.first == attributeName }?.second
                ?: throw IllegalArgumentException("Attribute not found: $attributeName")

            try {
                if (attributeValue != null) {
                    (attributeProcessor as AttributeProcessorRegistry<V, Any>).apply(
                        targetView, attributeValue as Any
                    )
                } else {
                    return
                }
            } catch (e: ClassCastException) {
                Log.e(
                    "AttributeProcessor",
                    "Casting error processing $attributeName. Error: ${e.message}"
                )
                throw e
            }
        }

        /**
         * Applies multiple attributes to the target view.
         *
         * @param targetView The view to apply the attributes to.
         * @param attributeValues A map of attribute names to their values.
         * @throws IllegalArgumentException if no attributes are registered.
         */
        @JvmStatic
        fun <V : View, T> applyAttributes(targetView: V, attributeValues: Map<String, T?>) {
            require(attributeProcessors.isNotEmpty()) { "No attributes found" }

            applyAttribute(
                targetView, Attributes.Common.ID, attributeValues[Attributes.Common.ID]
            )

            // Apply Non-ConstraintLayout Attributes:
            val nonConstraintAttributes =
                attributeValues.filter { !it.key.isConstraintLayoutAttribute() && !it.key.isIDAttribute() }
            applyAttributesInternal(targetView, nonConstraintAttributes)

            // Extract ConstraintLayout Attributes:
            val constraintAttributes =
                attributeValues.filter { it.key.isConstraintLayoutAttribute() }

            // Apply ConstraintLayout Constraints (without Bias)
            val constraintOnlyAttributes = constraintAttributes.filter { it.key.isConstraint() }
            applyAttributesInternal(targetView, constraintOnlyAttributes)

            // Apply ConstraintLayout Bias:
            val biasAttributes = constraintAttributes.filter { it.key.isBias() }
            applyAttributesInternal(targetView, biasAttributes)
        }

        /**
         * Internal function to apply a set of attributes to the target view.
         *
         * @param targetView The view to apply the attributes to.
         * @param attributeValues A map of attribute names to their values.
         */
        @Suppress("UNCHECKED_CAST")
        private fun <V : View, T> applyAttributesInternal(
            targetView: V, attributeValues: Map<String, T?>
        ) {
            attributeValues.forEach { (attr, value) ->
                if (attr == "android" || attr == "app") return@forEach
                val attributeProcessor = attributeProcessors.find { it.first == attr }?.second
                    ?: throw IllegalArgumentException("Attribute not found: $attr")

                if (value != null) {
                    try {
                        (attributeProcessor as AttributeProcessorRegistry<V, Any>).apply(
                            targetView, value as Any
                        )
                    } catch (e: ClassCastException) {
                        Log.e(
                            "AttributeProcessor",
                            "Casting error processing $attr. Error: ${e.message}"
                        )
                        // Consider rethrowing or handling differently based on your needs
                        // throw e
                    }
                } else {
                    Log.w("AttributeProcessor", "Skipping null value for attribute: $attr")
                }
            }
        }

        private fun String.isIDAttribute(): Boolean =
            equals(Attributes.Common.ID, ignoreCase = true)

        /**
         * Checks if an attribute name belongs to ConstraintLayout.
         *
         * @return true if the attribute is a ConstraintLayout attribute, false otherwise.
         */
        private fun String.isConstraintLayoutAttribute(): Boolean =
            startsWith("layout_constraint", ignoreCase = true)

        /**
         * Checks if an attribute name is a ConstraintLayout constraint (excluding bias).
         *
         * @return true if the attribute is a ConstraintLayout constraint, false otherwise.
         */
        private fun String.isConstraint(): Boolean =
            isConstraintLayoutAttribute() && !contains("bias", ignoreCase = true)


        /**
         * Checks if an attribute name is a ConstraintLayout bias.
         *
         * @return true if the attribute is a ConstraintLayout bias, false otherwise.
         */
        private fun String.isBias(): Boolean =
            isConstraintLayoutAttribute() && contains("bias", ignoreCase = true)

    }
}