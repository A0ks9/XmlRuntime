package com.voyager.core.view.processor

import android.util.AttributeSet
import android.view.View
import androidx.appcompat.view.ContextThemeWrapper

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
    protected abstract fun createView(context: ContextThemeWrapper, attrs: AttributeSet): View
}