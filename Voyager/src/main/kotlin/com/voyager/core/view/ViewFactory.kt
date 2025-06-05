package com.voyager.core.view

import android.content.Context
import android.view.View
import androidx.appcompat.view.ContextThemeWrapper
import com.voyager.core.utils.logging.LoggerFactory

/**
 * Factory for creating views in the Voyager framework.
 * 
 * Key features:
 * - Unified view creation interface
 * - Multiple view creation strategies
 * - Automatic package qualification
 * - Comprehensive error handling
 * 
 * Performance optimizations:
 * - Efficient context wrapping
 * - Minimal object creation
 * - Fast view lookup
 * - Optimized package qualification
 * 
 * Best practices:
 * - Use appropriate view types
 * - Handle view creation errors gracefully
 * - Implement proper error handling
 * - Use appropriate logging
 * 
 * Example usage:
 * ```kotlin
 * // Create a view using the factory
 * val view = ViewFactory.createView(context, "TextView")
 * ```
 */
object ViewFactory {
    private const val DEFAULT_ANDROID_WIDGET_PACKAGE = "android.widget."
    private val logger = LoggerFactory.getLogger(ViewFactory::class.java.simpleName)

    /**
     * Creates a view instance using the appropriate strategy.
     * 
     * Performance considerations:
     * - Efficient context wrapping
     * - Minimal object creation
     * - Fast view lookup
     * 
     * Error handling:
     * - Safe view creation
     * - Detailed error messages
     * - Proper logging
     * 
     * @param context The context to create the view with
     * @param type The view type, optionally qualified with package
     * @return The created view
     * @throws IllegalArgumentException if view creation fails
     */
    fun createView(context: Context, type: String): View {
        val qualifiedType = type.qualifiedPackage()
        logger.info("createView", "Creating view of type: $qualifiedType")

        try {
            // Wrap context if needed
            val ctx = context as? ContextThemeWrapper ?: ContextThemeWrapper(context, 0)
            logger.debug("createView", "Using context: ${ctx.javaClass.simpleName}")

            // Try default view registry first
            DefaultViewRegistry.createView(ctx, qualifiedType)?.let {
                logger.info("createView", "Created view using DefaultViewRegistry")
                return it
            }

            // Try custom view registry next
            CustomViewRegistry.createView(ctx, qualifiedType)?.let {
                logger.info("createView", "Created view using CustomViewRegistry")
                return it
            }

            // Try reflection as last resort
            ReflectionViewCreator.createView(ctx, qualifiedType)?.let {
                logger.info("createView", "Created view using ReflectionViewCreator")
                return it
            }

            // If all strategies fail, throw exception
            val error = "Could not create view for type: $qualifiedType"
            logger.error("createView", error)
            throw IllegalArgumentException(error)
        } catch (e: Exception) {
            logger.error(
                "createView",
                "Failed to create view of type $qualifiedType: ${e.message}",
                e
            )
            throw e
        }
    }

    /**
     * Qualifies a view type with the default Android widget package if needed.
     * 
     * Performance considerations:
     * - Efficient string operations
     * - Minimal object creation
     * 
     * @param type The view type to qualify
     * @return The qualified view type
     */
    private fun String.qualifiedPackage() =
        if (contains('.')) this else "$DEFAULT_ANDROID_WIDGET_PACKAGE$this"
} 