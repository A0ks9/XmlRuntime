package com.voyager.utils.view.processors

import android.content.Context
import android.util.LruCache
import android.view.View
import com.voyager.data.models.ConfigManager
import java.util.concurrent.ConcurrentHashMap

/**
 * A high-performance style resource processor for the Voyager framework.
 * 
 * This class provides efficient style resource resolution and application to views,
 * using caching and optimized resource lookup strategies. It supports both direct
 * style IDs and style resource references.
 *
 * Key features:
 * - Thread-safe style resource caching
 * - Optimized resource ID resolution
 * - Memory-efficient class caching
 * - Fast style application to views
 *
 * Example usage:
 * ```kotlin
 * // Apply a style from a resource reference
 * StyleResource.valueOf("@style/MyStyle", context)?.apply(myView, context)
 * 
 * // Apply a style directly from an ID
 * StyleResource.valueOf(styleId, attributeId).apply(myView, context)
 * ```
 *
 * @author Abdelrahman Omar
 * @since 1.0.0
 */
class StyleResource private constructor(val styleId: Int) {

    companion object {
        /**
         * Represents a null or invalid style resource.
         */
        @JvmField
        val NULL = StyleResource(-1)

        /**
         * Thread-safe cache for style resource IDs.
         * Pre-sized to reduce resizing operations.
         */
        private val styleMap = ConcurrentHashMap<String, Int>(32)

        /**
         * Thread-safe cache for R class references.
         * Pre-sized to reduce resizing operations.
         */
        private val classCache = ConcurrentHashMap<String, Class<*>>(16)

        /**
         * Configuration manager instance.
         */
        private val config = ConfigManager.config

        /**
         * Constant for style resource prefix.
         */
        private const val STYLE_PREFIX = "@style/"

        /**
         * Checks if a value represents a style resource reference.
         *
         * @param value The value to check
         * @return true if the value starts with "@style/"
         */
        fun isStyleResource(value: String): Boolean = value.startsWith(STYLE_PREFIX)

        /**
         * Creates a StyleResource from a style reference string.
         *
         * @param value The style reference (e.g., "@style/MyStyle")
         * @param context The context for resource resolution
         * @return A StyleResource instance, or null if resolution fails
         */
        @JvmStatic
        fun valueOf(value: String, context: Context): StyleResource? =
            StyleCache.cache.getOrPut(value) { createStyleResource(value, context) ?: NULL }
                .takeIf { it !== NULL }

        /**
         * Creates a StyleResource from a style ID.
         *
         * @param styleId The style resource ID
         * @param attributeId The attribute ID (unused, kept for API compatibility)
         * @return A StyleResource instance
         */
        @JvmStatic
        fun valueOf(styleId: Int, attributeId: Int) = StyleResource(styleId)

        /**
         * Creates a StyleResource from a style reference string.
         *
         * @param value The style reference
         * @param context The context for resource resolution
         * @return A StyleResource instance, or null if resolution fails
         */
        private fun createStyleResource(value: String, context: Context): StyleResource? {
            return when {
                value.startsWith(STYLE_PREFIX) -> StyleResource(
                    getResourceId(
                        context,
                        value.substringAfter(STYLE_PREFIX),
                        "style"
                    )
                )
                else -> null
            }
        }

        /**
         * Gets a resource ID using optimized lookup strategies.
         *
         * @param context The context for resource resolution
         * @param name The resource name
         * @param type The resource type
         * @return The resource ID
         */
        private fun getResourceId(context: Context, name: String, type: String): Int {
            // Try cached value first
            val cachedId = styleMap[name]
            if (cachedId != null) return cachedId

            // Try provider
            val providerId = config.provider.getResId(type, name)
            if (providerId != -1) {
                styleMap[name] = providerId
                return providerId
            }

            // Fallback to reflection
            val className = "${context.packageName}.R\$$type"
            return classCache.getOrPut(className) { Class.forName(className) }
                .getField(name)
                .getInt(null)
                .also { styleMap[name] = it }
        }
    }

    /**
     * Applies the style to a view using ContextThemeWrapper.
     *
     * @param view The view to apply the style to
     * @param context The context for theme application
     */
    fun apply(view: View, context: Context) {
        // TODO: Implement style application using ContextThemeWrapper
    }

    /**
     * Thread-safe LRU cache for StyleResource instances.
     * Size limited to prevent memory leaks.
     */
    private object StyleCache {
        val cache = LruCache<String, StyleResource>(64)
    }
}

/**
 * Extension function to provide a thread-safe getOrPut operation for LruCache.
 *
 * @param K The key type
 * @param V The value type
 * @param key The key to look up
 * @param defaultValue The function to compute the default value
 * @return The existing value or the computed default value
 */
fun <K, V> LruCache<K, V>.getOrPut(key: K, defaultValue: () -> V): V =
    get(key) ?: synchronized(this) {
        get(key) ?: defaultValue().also { put(key, it) }
    }
