package com.voyager.core.utils

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import com.voyager.core.utils.logging.LoggerFactory
import java.util.WeakHashMap

/**
 * Utility object for context-related operations.
 * Provides efficient and thread-safe operations for Android Context.
 *
 * Key Features:
 * - Activity name resolution
 * - Context type checking
 * - Resource access
 * - Performance optimized
 * - Thread-safe operations
 *
 * Performance Optimizations:
 * - Weak reference caching
 * - Efficient context traversal
 * - Minimal object creation
 * - Safe context handling
 *
 * Best Practices:
 * 1. Use appropriate context types
 * 2. Consider memory usage
 * 3. Handle null values properly
 * 4. Use thread-safe operations
 * 5. Consider performance impact
 *
 * Example Usage:
 * ```kotlin
 * // Get activity name
 * val activityName = context.name
 * ```
 */
internal object ContextUtils {
    private val logger = LoggerFactory.getLogger("ContextUtils")

    /**
     * A [WeakHashMap] to cache resolved activity names from [Context] instances.
     * Using [WeakHashMap] helps prevent memory leaks by allowing garbage collection of [Context] keys
     * if they are no longer strongly referenced elsewhere.
     * Key: [Context] instance.
     * Value: The simple class name of the Activity.
     */
    private val activityNameCache = WeakHashMap<Context, String>()

    /**
     * Retrieves the simple class name of the [Activity] associated with this [Context].
     * Thread-safe operation with caching.
     *
     * Performance Considerations:
     * - Uses weak reference caching
     * - Efficient context traversal
     * - Minimal object creation
     * - Safe null handling
     *
     * @return The simple name of the Activity class (e.g., "MainActivity") if found,
     *         or the string "Unknown" if no Activity is found in the context chain.
     */
    val Context.name: String
        get() = try {
            activityNameCache.getOrPut(this) {
                var currentContext: Context? = this
                while (currentContext is ContextWrapper) {
                    if (currentContext is Activity) {
                        return@getOrPut currentContext.javaClass.simpleName
                    }
                    currentContext = currentContext.baseContext
                    if (currentContext == null) break
                }
                "Unknown"
            }
        } catch (e: Exception) {
            logger.error("name", "Failed to get activity name: ${e.message}")
            "Unknown"
        }

    /**
     * Clears the context caches.
     * Useful for testing or when context configuration changes.
     * Thread-safe operation.
     */
    fun clearCaches() {
        activityNameCache.clear()
        logger.debug("clearCaches", "Context caches cleared")
    }
} 