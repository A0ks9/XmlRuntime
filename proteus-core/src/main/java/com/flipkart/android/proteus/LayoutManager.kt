package com.flipkart.android.proteus

import com.flipkart.android.proteus.value.Layout

/**
 * Kotlin abstract class for managing layouts in Proteus.
 *
 * This abstract class defines a base structure for layout management, providing a method
 * to retrieve a [Layout] object by name. Subclasses are expected to implement the [getLayouts] method
 * to provide the actual mapping of layout names to [Layout] objects.
 */
abstract class LayoutManager { // Converted to Kotlin abstract class

    /**
     * Abstract method to be implemented by subclasses to provide a map of layout names to Layout objects.
     *
     * Subclasses should return a [Map] where keys are layout names (Strings) and values are corresponding [Layout] instances.
     * This map represents the collection of layouts managed by this LayoutManager.
     *
     * @return A Map of layout names to Layout objects, or null if no layouts are managed.
     */
    protected abstract fun getLayouts(): Map<String, Layout>? // Converted to Kotlin abstract function, nullable return type

    /**
     * Retrieves a [Layout] object by its [name].
     *
     * This method gets the layout map from [getLayouts] and attempts to retrieve the [Layout] associated with the given [name].
     * If [getLayouts] returns null or if no layout is found for the given [name], this method returns null.
     *
     * @param name The name of the layout to retrieve. Must not be null.
     * @return The [Layout] object associated with the [name], or null if no layout is found or if layout management is not initialized.
     */
    fun get(name: String): Layout? { // Converted to Kotlin function, nullable return type
        return getLayouts()?.get(name) // Safe call and elvis operator for concise null handling
    }
}