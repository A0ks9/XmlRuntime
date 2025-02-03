package com.flipkart.android.proteus

import com.flipkart.android.proteus.value.Value

/**
 * Kotlin implementation of Styles, a subclass of HashMap for managing Proteus styles.
 *
 * `Styles` extends `HashMap<String, Map<String, Value>>` and provides convenient methods
 * for retrieving styles and checking for style existence. Each style is represented as a `Map<String, Value>`,
 * where keys are attribute names (Strings) and values are `Value` objects representing attribute values.
 * The outer HashMap (Styles) is keyed by style names (Strings).
 */
class Styles :
    HashMap<String, Map<String, Value>>() { // Kotlin class declaration inheriting from HashMap

    /**
     * Retrieves a style `Map<String, Value>` by its name.
     * This is a convenience method that simply calls the `get(name)` method of the HashMap.
     *
     * @param name The name of the style to retrieve.
     * @return     The Map<String, Value> representing the style attributes, or null if no style with the given name is found.
     */
    fun getStyle(name: String): Map<String, Value>? { // Method to get style by name, returns nullable Map?
        return get(name) // Calls HashMap's get(name) method to retrieve the style
    }

    /**
     * Checks if a style with the given name exists in this Styles object.
     * This is a convenience method that simply calls the `containsKey(name)` method of the HashMap.
     *
     * @param name The name of the style to check for.
     * @return     true if a style with the given name exists, false otherwise.
     */
    fun contains(name: String): Boolean { // Method to check if style exists, returns Boolean
        return containsKey(name) // Calls HashMap's containsKey(name) to check for style existence
    }
}