package com.flipkart.android.proteus

import com.flipkart.android.proteus.value.Value

/**
 * Abstract class for managing styles within the Proteus framework.
 *
 * `StyleManager` is responsible for providing access to styles, which are collections of attributes
 * that can be applied to Proteus views for consistent styling. Subclasses are expected to implement
 * the `getStyles()` method to provide the actual style data.
 */
abstract class StyleManager { // Kotlin abstract class declaration

    /**
     * Abstract and protected method to be implemented by subclasses to provide access to the Styles object.
     * Styles object (likely a container like a Map) holds all defined styles.
     *
     * @return The Styles object, or null if styles are not available or an error occurred while loading. Nullable.
     */
    protected abstract fun getStyles(): Styles? // Abstract protected method to get Styles, returns nullable Styles?

    /**
     * Retrieves a specific style by name.
     *
     * @param name The name of the style to retrieve. Must be non-null.
     * @return     A Map<String, Value> representing the style attributes, or null if the style is not found or Styles object is null. Nullable.
     */
    operator fun get(name: String): Map<String, Value>? { // Public method to get a style by name, returns nullable Map?
        return getStyles()?.get(name) // Safely call getStyles() and then get(name), using Kotlin's safe call operator ?. Returns null if either getStyles() or get(name) returns null.
    }
}