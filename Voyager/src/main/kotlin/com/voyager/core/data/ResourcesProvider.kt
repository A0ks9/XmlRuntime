package com.voyager.core.data

/**
 * Interface for providing resource IDs in the Voyager framework.
 *
 * This interface defines a contract for retrieving Android resource IDs by their type and name.
 * It is used to abstract resource resolution, allowing for different implementations that can
 * handle resource lookup in various ways (e.g., from local resources, remote resources, or
 * dynamic resource generation).
 *
 * Key Features:
 * - Type-safe resource resolution
 * - Flexible resource lookup implementation
 * - Support for dynamic resource management
 * - Integration with Android's resource system
 * - Thread-safe operations
 *
 * Example Usage:
 * ```kotlin
 * class LocalResourcesProvider(
 *     private val context: Context
 * ) : ResourcesProvider {
 *     override fun getResId(type: String, name: String): Int {
 *         return context.resources.getIdentifier(name, type, context.packageName)
 *     }
 * }
 * ```
 *
 * Resource Types:
 * - "layout" - Layout resources (e.g., "activity_main")
 * - "drawable" - Drawable resources (e.g., "ic_launcher")
 * - "string" - String resources (e.g., "app_name")
 * - "color" - Color resources (e.g., "primary_color")
 * - "dimen" - Dimension resources (e.g., "margin_normal")
 * - "style" - Style resources (e.g., "AppTheme")
 * - "attr" - Attribute resources (e.g., "android:attr/colorPrimary")
 *
 * @author Abdelrahman Omar
 * @since 1.0.0
 */
interface ResourcesProvider {
    /**
     * Retrieves the resource ID for a given resource type and name.
     *
     * This method provides a unified way to resolve resource IDs across different
     * resource types in the Android system. It handles the lookup of resources
     * based on their type and name, returning the corresponding resource ID.
     *
     * Resource types include but are not limited to:
     * - "layout" for layout resources
     * - "drawable" for drawable resources
     * - "string" for string resources
     * - "color" for color resources
     * - "dimen" for dimension resources
     * - "style" for style resources
     * - "attr" for attribute resources
     *
     * @param type The type of the resource (e.g., "layout", "drawable")
     * @param name The name of the resource without the type prefix
     * @return The resource ID if found, or 0 if the resource is not found
     * @throws IllegalArgumentException if type or name is empty
     */
    fun getResId(type: String, name: String): Int
}