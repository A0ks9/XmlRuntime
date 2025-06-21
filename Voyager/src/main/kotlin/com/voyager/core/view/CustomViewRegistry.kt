package com.voyager.core.view

import android.util.AttributeSet
import android.view.View
import androidx.appcompat.view.ContextThemeWrapper
import com.voyager.core.utils.logging.LoggerFactory
import java.util.concurrent.ConcurrentHashMap

/**
 * Registry for managing custom view creators in the Voyager framework.
 * 
 * Key features:
 * - Custom view registration
 * - Thread-safe view creation
 * - Efficient view lookup
 * - Comprehensive error handling
 * 
 * Performance optimizations:
 * - ConcurrentHashMap for thread-safe operations
 * - Efficient view creation
 * - Minimal object creation
 * - Fast view lookup
 * 
 * Best practices:
 * - Register custom views before use
 * - Handle view creation errors gracefully
 * - Implement proper error handling
 * - Use appropriate logging
 * 
 * Example usage:
 * ```kotlin
 * // Register a custom view
 * CustomViewRegistry.registerView("com.example.CustomView") { context ->
 *     CustomView(context)
 * }
 * 
 * // Create a custom view
 * val view = CustomViewRegistry.createView(context, "com.example.CustomView")
 * ```
 */
object CustomViewRegistry {
    private val logger = LoggerFactory.getLogger(CustomViewRegistry::class.java.simpleName)
    private val customCreators = ConcurrentHashMap<String, (ContextThemeWrapper, AttributeSet) -> View>()

    /**
     * Registers a custom view creator for a specific view type.
     * 
     * Performance considerations:
     * - Thread-safe registration
     * - Efficient map operation
     * - No object creation
     * 
     * Error handling:
     * - Safe type registration
     * - Proper logging
     * - Thread safety
     * 
     * @param type The fully qualified class name of the view
     * @param creator A function that creates the view given a context
     * @throws IllegalArgumentException if type is empty or null
     */
    @JvmStatic
    fun registerView(type: String, creator: (ContextThemeWrapper, AttributeSet) -> View) {
        try {
            if (type.isBlank()) {
                throw IllegalArgumentException("View type cannot be empty")
            }
            customCreators[type] = creator
            logger.info("registerView", "Registered custom view creator for type: $type")
        } catch (e: Exception) {
            logger.error(
                "registerView",
                "Failed to register view creator for type $type: ${e.message}",
                e
            )
            throw e
        }
    }

    /**
     * Creates a view instance using a registered custom view creator.
     * 
     * Performance considerations:
     * - Efficient view lookup
     * - Thread-safe operation
     * - Minimal object creation
     * 
     * Error handling:
     * - Safe view creation
     * - Graceful fallback for missing creators
     * - Proper logging
     * 
     * @param context The context to create the view with
     * @param type The fully qualified class name of the view
     * @return The created view, or null if no creator is registered for the type
     */
    internal fun createView(context: ContextThemeWrapper, attrs: AttributeSet, type: String): View? {
        try {
            return customCreators[type]?.invoke(context, attrs)?.also {
                logger.info("createView", "Created custom view of type: $type")
            }
        } catch (e: Exception) {
            logger.error(
                "createView",
                "Failed to create view of type $type: ${e.message}",
                e
            )
            return null
        }
    }
} 