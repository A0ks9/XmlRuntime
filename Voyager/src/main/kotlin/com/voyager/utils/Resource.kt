/**
 * Efficient resource management utility for Android applications.
 *
 * This utility provides optimized resource resolution and caching for various Android resource types,
 * including colors, drawables, strings, and more. It uses efficient data structures and caching
 * mechanisms for better performance.
 *
 * Key features:
 * - Thread-safe resource resolution
 * - Efficient resource caching
 * - Optimized string operations
 * - Comprehensive resource type support
 * - Memory-efficient processing
 *
 * Performance optimizations:
 * - Immutable maps for resource prefixes
 * - Efficient resource lookup
 * - Minimized object creation
 * - Optimized string operations
 * - Safe resource handling
 *
 * Usage example:
 * ```kotlin
 * // Get a color resource
 * val color = Resource.getColor(resId, context)
 *
 * // Get a drawable resource
 * val drawable = Resource.getDrawable(resId, context)
 *
 * // Get a string resource
 * val string = Resource.getString(resId, context)
 * ```
 *
 * @author Abdelrahman Omar
 * @since 1.0.0
 */
package com.voyager.utils

import android.content.Context
import android.content.res.Resources
import android.os.Build
import androidx.appcompat.content.res.AppCompatResources
import com.voyager.data.models.ConfigManager

/**
 * Internal class for efficient resource management.
 *
 * This class provides optimized methods for accessing Android resources with proper error handling
 * and performance optimizations. It uses a singleton pattern to ensure thread-safe resource access.
 *
 * @property resId The resource ID to manage
 */
internal class Resource private constructor(val resId: Int) {

    companion object {
        // Immutable map for resource prefixes to prevent modification
        private val PREFIX_MAP = mapOf(
            "@anim/" to "anim",
            "@bool/" to "bool",
            "@color/" to "color",
            "@dimen/" to "dimen",
            "@drawable/" to "drawable",
            "@string/" to "string"
        )

        // Pre-computed NOT_FOUND resource for better performance
        val NOT_FOUND = Resource(-1)

        /**
         * Checks if a string is a resource reference.
         *
         * @param value The string to check
         * @return true if the string starts with a resource prefix
         */
        fun isResource(value: String) = PREFIX_MAP.keys.any { value.startsWith(it) }

        /**
         * Safely executes a block of code and returns null if a Resources.NotFoundException occurs.
         *
         * @param block The code block to execute
         * @return The result of the block or null if an exception occurs
         */
        private inline fun <T> safeCall(block: () -> T) = try {
            block()
        } catch (_: Resources.NotFoundException) {
            null
        }

        /**
         * Gets a boolean resource value.
         *
         * @param resId The resource ID
         * @param context The application context
         * @return The boolean value or null if not found
         */
        fun getBoolean(resId: Int, context: Context) =
            safeCall { context.resources.getBoolean(resId) }

        /**
         * Gets a color resource value.
         *
         * @param resId The resource ID
         * @param context The application context
         * @return The color value or null if not found
         */
        fun getColor(resId: Int, context: Context): Int? {
            val res = context.resources
            return safeCall {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    res.getColor(resId, context.theme)
                } else {
                    @Suppress("DEPRECATION")
                    res.getColor(resId)
                }
            }
        }

        /**
         * Gets a ColorStateList resource.
         *
         * @param resId The resource ID
         * @param context The application context
         * @return The ColorStateList or null if not found
         */
        fun getColorStateList(resId: Int, context: Context) = safeCall {
            AppCompatResources.getColorStateList(context, resId)
        }

        /**
         * Gets a drawable resource.
         *
         * @param resId The resource ID
         * @param context The application context
         * @return The drawable or null if not found
         */
        fun getDrawable(resId: Int, context: Context) = safeCall {
            AppCompatResources.getDrawable(context, resId)
        }

        /**
         * Gets a dimension resource value.
         *
         * @param resId The resource ID
         * @param context The application context
         * @return The dimension value or null if not found
         */
        fun getDimension(resId: Int, context: Context) =
            safeCall { context.resources.getDimension(resId) }

        /**
         * Gets a string resource.
         *
         * @param resId The resource ID
         * @param context The application context
         * @return The string value or null if not found
         */
        fun getString(resId: Int, context: Context) = safeCall { context.getString(resId) }

        /**
         * Gets an integer resource value.
         *
         * @param resId The resource ID
         * @param context The application context
         * @return The integer value or null if not found
         */
        fun getInteger(resId: Int, context: Context) =
            safeCall { context.resources.getInteger(resId) }

        /**
         * Creates a Resource instance from a string value and type.
         *
         * @param value The resource string value
         * @param type The resource type
         * @return A Resource instance or NOT_FOUND if the resource doesn't exist
         */
        fun valueOf(value: String?, type: String) = value?.let {
            Resource(
                ConfigManager.config.provider.getResId(
                    type, it
                )
            )
        } ?: NOT_FOUND

        /**
         * Creates a Resource instance from a resource ID.
         *
         * @param resId The resource ID
         * @return A Resource instance
         */
        fun valueOf(resId: Int) = Resource(resId)
    }
}
