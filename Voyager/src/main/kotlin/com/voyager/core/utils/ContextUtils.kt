package com.voyager.core.utils

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import java.util.WeakHashMap

/**
 * Utility object for context-related operations.
 */
internal object ContextUtils {

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
     *
     * Performance Considerations:
     * - Uses weak reference caching
     * - Efficient context traversal
     * - Minimal object creation
     *
     * @return The simple name of the Activity class (e.g., "MainActivity") if found,
     *         or the string "Unknown" if no Activity is found in the context chain.
     */
    fun Context.getActivityName(): String = activityNameCache.getOrPut(this) {
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
} 