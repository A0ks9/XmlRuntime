package com.voyager.core.view

import android.content.Context
import android.view.View
import androidx.appcompat.view.ContextThemeWrapper
import com.voyager.core.utils.logging.LoggerFactory
import java.util.concurrent.ConcurrentHashMap

/**
 * Utility for creating views using reflection in the Voyager framework.
 * 
 * Key features:
 * - Dynamic view creation via reflection
 * - Constructor caching for performance
 * - Thread-safe operations
 * - Comprehensive error handling
 * 
 * Performance optimizations:
 * - Constructor caching
 * - Efficient reflection usage
 * - Minimal object creation
 * - Thread-safe caching
 * 
 * Best practices:
 * - Use appropriate view types
 * - Handle reflection errors gracefully
 * - Implement proper error handling
 * - Use appropriate logging
 * 
 * Example usage:
 * ```kotlin
 * // Create a view using reflection
 * val view = ReflectionViewCreator.createView(context, "com.example.CustomView")
 * ```
 */
object ReflectionViewCreator {
    private val logger = LoggerFactory.getLogger(ReflectionViewCreator::class.java.simpleName)
    private val constructorCache = ConcurrentHashMap<String, (ContextThemeWrapper) -> View>()

    /**
     * Creates a view instance using reflection.
     * 
     * Performance considerations:
     * - Uses constructor caching
     * - Efficient reflection usage
     * - Thread-safe operation
     * 
     * Error handling:
     * - Safe reflection operations
     * - Detailed error messages
     * - Proper logging
     * 
     * @param context The context to create the view with
     * @param type The fully qualified class name of the view
     * @return The created view, or null if creation fails
     * @throws IllegalArgumentException if view creation fails
     */
    fun createView(context: ContextThemeWrapper, type: String): View? {
        return try {
            // Check cache first
            constructorCache[type]?.let { constructor ->
                logger.info("createView", "Using cached constructor for type: $type")
                return constructor(context)
            }

            // Find and cache constructor
            val clazz = Class.forName(type).kotlin
            val ktor = clazz.constructors.firstOrNull { 
                it.parameters.size == 1 && it.parameters[0].type.classifier == Context::class 
            } ?: throw NoSuchMethodException(
                "No constructor with a single Context parameter found for $type. " +
                "Custom views must have a public constructor(Context) or constructor(Context, AttributeSet)."
            )

            // Create and cache view constructor
            val viewConstructor: (ContextThemeWrapper) -> View = { ctx ->
                try {
                    ktor.call(ctx) as View
                } catch (e: Exception) {
                    logger.error(
                        "createView",
                        "Failed to invoke constructor for $type: ${e.message}",
                        e
                    )
                    throw IllegalArgumentException(
                        "Failed to invoke constructor for $type.",
                        e
                    )
                }
            }

            // Cache the constructor
            constructorCache[type] = viewConstructor
            logger.info("createView", "Cached constructor for type: $type")

            // Register with DefaultViewRegistry
            DefaultViewRegistry.viewCreators[type] = viewConstructor
            logger.info("createView", "Registered view creator for type: $type")

            // Create and return view
            viewConstructor(context).also {
                logger.info("createView", "Successfully created view of type: $type")
            }
        } catch (e: Exception) {
            logger.error(
                "createView",
                "Error creating view via reflection for type: $type. Details: ${e.message}",
                e
            )
            throw IllegalArgumentException(
                "Error creating view via reflection for type: $type. Details: ${e.message}",
                e
            )
        }
    }

    /**
     * Clears the constructor cache.
     * 
     * Performance considerations:
     * - Thread-safe operation
     * - Efficient cache clearing
     * 
     * Error handling:
     * - Safe cache clearing
     * - Proper logging
     */
    fun clearCache() {
        try {
            constructorCache.clear()
            logger.info("clearCache", "Successfully cleared constructor cache")
        } catch (e: Exception) {
            logger.error(
                "clearCache",
                "Failed to clear constructor cache: ${e.message}",
                e
            )
        }
    }
} 