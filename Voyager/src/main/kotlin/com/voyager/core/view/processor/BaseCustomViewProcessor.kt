package com.voyager.core.view.processor

import android.view.View
import androidx.appcompat.view.ContextThemeWrapper
import com.voyager.core.attribute.AttributeRegistry

/**
 * Abstract base class for parsing and processing view attributes.
 * Provides a foundation for implementing custom view processors with efficient attribute handling.
 *
 * Key Features:
 * - Custom view type registration
 * - Attribute processor management
 * - Thread-safe operations
 * - Performance optimized
 * - Memory efficient
 * - Comprehensive error handling
 *
 * Performance Optimizations:
 * - Efficient attribute registration
 * - Minimal object creation
 * - Safe resource handling
 * - Optimized view creation
 *
 * Best Practices:
 * 1. Implement all abstract methods
 * 2. Handle null values appropriately
 * 3. Consider view lifecycle
 * 4. Use thread-safe operations
 * 5. Consider memory leaks
 * 6. Implement proper error handling
 *
 * Example Usage:
 * ```kotlin
 * class MyCustomViewProcessor : BaseCustomViewProcessor() {
 *     override fun getViewType() = "MyCustomView"
 *     
 *     override fun createView(context: ContextThemeWrapper): View {
 *         return MyCustomView(context)
 *     }
 *     
 *     override fun addAttributes() {
 *         registerAttributes<MyCustomView, String>(mapOf(
 *             "customAttribute" to { view, value -> 
 *                 view.setCustomValue(value)
 *             }
 *         ))
 *     }
 * }
 * ```
 *
 * @author Abdelrahman Omar
 * @since 1.0.0
 */
abstract class BaseCustomViewProcessor {

    /**
     * Returns the type identifier for the view being parsed.
     * This identifier is used to match the view type in the XML/JSON layout.
     *
     * Performance Considerations:
     * - Should be a constant value
     * - Avoid complex computations
     * - Cache the result if needed
     *
     * @return String representing the view type
     */
    abstract fun getViewType(): String

    /**
     * Creates a new view instance with the given context.
     * This method should handle view creation efficiently and safely.
     *
     * Performance Considerations:
     * - Minimize object creation
     * - Use appropriate view constructors
     * - Consider view recycling
     * - Handle theme attributes properly
     *
     * @param context The themed context wrapper to create the view with
     * @return A new View instance
     * @throws IllegalStateException if view creation fails
     */
    protected abstract fun createView(context: ContextThemeWrapper): View

    /**
     * Initializes and registers the attributes for this view type.
     * Should be implemented to set up all attribute processors efficiently.
     *
     * Performance Considerations:
     * - Register attributes only once
     * - Use efficient attribute handlers
     * - Consider attribute dependencies
     * - Handle attribute validation
     *
     * @throws IllegalStateException if attribute registration fails
     */
    abstract fun addAttributes()

    companion object {
        /**
         * Registers multiple attribute processors for a specific view type.
         * Thread-safe operation with efficient attribute handling.
         *
         * Performance Considerations:
         * - Efficient attribute mapping
         * - Minimal object creation
         * - Safe attribute handling
         * - Thread-safe registration
         *
         * @param V The view type to register the attributes for
         * @param T The type of the attribute values
         * @param attributeMap Map of attribute names to their processor functions
         * @throws IllegalArgumentException if attributeMap is empty
         * @throws IllegalStateException if registration fails
         */
        @JvmStatic
        inline fun <reified V : View, reified T> registerAttributes(attributeMap: Map<String, (V, T?) -> Unit>) {
            try {
                if (attributeMap.isEmpty()) {
                    throw IllegalArgumentException("Attribute map cannot be empty")
                }

                attributeMap.forEach { (attr, handler) ->
                    AttributeRegistry.register(attr, handler)
                }
            } catch (e: Exception) {
                throw IllegalStateException("Failed to register attributes: ${e.message}", e)
            }
        }
    }
}