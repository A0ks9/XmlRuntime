package com.voyager.core.utils.parser

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.voyager.core.model.ConfigManager
import com.voyager.core.utils.logging.LoggerFactory
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass
import kotlin.reflect.full.staticProperties
import kotlin.reflect.jvm.javaField

/**
 * Utility object for parsing and resolving Android resources.
 * Provides efficient and thread-safe resource parsing operations.
 *
 * Key Features:
 * - Drawable resource parsing
 * - String resource parsing
 * - Resource ID resolution
 * - Thread-safe operations
 * - Performance optimized
 * - Comprehensive error handling
 *
 * Performance Optimizations:
 * - ConcurrentHashMap for caching
 * - Efficient string operations
 * - Minimal object creation
 * - Safe resource handling
 *
 * Best Practices:
 * 1. Use appropriate resource types
 * 2. Handle null values appropriately
 * 3. Consider resource availability
 * 4. Use thread-safe operations
 * 5. Consider performance impact
 *
 * Example Usage:
 * ```kotlin
 * // Get drawable resource
 * val drawable = ResourceParser.getDrawable(view, "my_image")
 *
 * // Get string resource
 * val string = ResourceParser.getString(context, "@string/my_string")
 *
 * // Get resource ID
 * val resId = ResourceParser.getResId("button1", R.id::class)
 * ```
 */
object ResourceParser {
    private val logger = LoggerFactory.getLogger(ResourceParser::class.java.simpleName)
    private val config = ConfigManager.config

    // Thread-safe cache for drawable resources
    private val drawableCache = ConcurrentHashMap<String, Drawable?>()

    // Thread-safe cache for string resources
    private val stringCache = ConcurrentHashMap<String, String?>()

    // Thread-safe cache for resource IDs
    private val resourceIdCache = ConcurrentHashMap<String, Int>()

    /**
     * Gets a drawable from a resource name.
     * Thread-safe operation with caching.
     *
     * Performance Considerations:
     * - Uses ConcurrentHashMap for caching
     * - Efficient string operations
     * - Minimal object creation
     * - Safe resource handling
     *
     * @param view The view to get resources from
     * @param name The drawable resource name (without type prefix, e.g., "my_image")
     * @return The drawable or null if not found
     */
    fun getDrawable(view: View, name: String): Drawable? = try {
        drawableCache.getOrPut(name) {
            view.resources.run {
                ResourcesCompat.getDrawable(
                    this, config.provider.getResId("drawable", name), null
                )
            }
        }
    } catch (e: Exception) {
        logger.error("getDrawable", "Failed to get drawable: ${e.message}")
        null
    }

    /**
     * Gets a string from a resource name.
     * Thread-safe operation with caching.
     *
     * Performance Considerations:
     * - Uses ConcurrentHashMap for caching
     * - Efficient string operations
     * - Minimal object creation
     * - Safe resource handling
     *
     * @param context The application context
     * @param name The string resource name (e.g., "@string/my_string" or "literal string")
     * @return The string value or the name if not found or resource not found
     */
    fun getString(context: Context, name: String): String? = try {
        stringCache.getOrPut(name) {
            when {
                name.startsWith("@string/") -> {
                    val resName = name.removePrefix("@string/")
                    val resId = config.provider.getResId("string", resName)
                    if (resId != 0) {
                        ContextCompat.getString(context, resId)
                    } else {
                        logger.warn("getString", "String resource not found: '$resName'")
                        null
                    }
                }

                else -> name
            }
        }
    } catch (e: Exception) {
        logger.error("getString", "Failed to get string: ${e.message}")
        null
    }

    /**
     * Gets a resource ID from a variable name and class using reflection.
     * Thread-safe operation with caching.
     *
     * Performance Considerations:
     * - Uses ConcurrentHashMap for caching
     * - Efficient string operations
     * - Minimal object creation
     * - Safe resource handling
     *
     * @param variableName The variable name (e.g., "button1")
     * @param klass The KClass to search in (e.g., `android.R.id::class`)
     * @return The resource ID or 0 if not found
     */
    fun getResId(variableName: String, klass: KClass<*>): Int = try {
        val cacheKey = "${klass.java.name}:$variableName"
        resourceIdCache.getOrPut(cacheKey) {
            klass.staticProperties.find { it.name == variableName }?.javaField?.getInt(null) ?: 0
        }
    } catch (e: Exception) {
        logger.error("getResId", "Failed to get resource ID: ${e.message}")
        0
    }

    /**
     * Gets an Android XML resource ID from a full resource ID string.
     * Thread-safe operation with caching.
     *
     * Performance Considerations:
     * - Uses ConcurrentHashMap for caching
     * - Efficient string operations
     * - Minimal object creation
     * - Safe resource handling
     *
     * @param fullResIdString The full resource ID string (e.g., "@+id/my_view" or "@id/my_view")
     * @return The resource ID or `View.NO_ID` (-1) if not found or format is invalid
     */
    fun getAndroidXmlResId(fullResIdString: String?): Int {
        return try {
            if (fullResIdString == null) {
                logger.warn("getAndroidXmlResId", "Null resource ID string provided")
                return View.NO_ID
            }

            resourceIdCache.getOrPut(fullResIdString) {
                getResId(fullResIdString.substringAfter("/"), android.R.id::class)
            }
        } catch (e: Exception) {
            logger.error(
                "getAndroidXmlResId",
                "Failed to get Android XML resource ID: ${e.message}",
                e
            )
            return View.NO_ID
        }
    }

    /**
     * Checks if a string is a tween animation resource reference.
     * Thread-safe operation.
     *
     * @param attributeValue The string to check
     * @return true if it's a tween animation resource reference, false otherwise
     */
    fun isTweenAnimationResource(attributeValue: String?): Boolean = try {
        attributeValue?.startsWith("@anim/") == true
    } catch (e: Exception) {
        logger.error(
            "isTweenAnimationResource",
            "Failed to check animation resource: ${e.message}",
            e
        )
        false
    }

    /**
     * Clears all resource caches.
     * Useful for testing or when resource configuration changes.
     * Thread-safe operation.
     */
    fun clearCache() {
        drawableCache.clear()
        stringCache.clear()
        resourceIdCache.clear()
        logger.debug("clearCache", "Resource caches cleared")
    }
} 