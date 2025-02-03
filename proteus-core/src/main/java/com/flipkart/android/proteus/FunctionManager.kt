package com.flipkart.android.proteus

/**
 * Kotlin class for managing a collection of `Function` objects in Proteus.
 *
 * This class holds a map of function names to their corresponding `Function` implementations.
 * It provides a way to retrieve a `Function` by name, defaulting to a no-op function if not found.
 *
 * @property functions A map where keys are function names (Strings) and values are `Function` instances.
 *                     This map stores the registered functions that can be accessed and executed.
 * @constructor Creates a new [FunctionManager] instance with the given map of functions.
 * @param functions The map of functions to be managed. Must not be null.
 */
class FunctionManager(
    private val functions: Map<String, Function> // Converted to Kotlin property, made private and immutable
) {

    /**
     * Retrieves a [Function] by its name.
     *
     * If a function with the given [name] is found in the managed map, it is returned.
     * If no function is found for the given [name], a [Function.NOOP] instance (no-operation function) is returned
     * to avoid null pointer exceptions and provide a default behavior.
     *
     * @param name The name of the function to retrieve. Must not be null.
     * @return The [Function] associated with the given [name], or [Function.NOOP] if not found. Never returns null.
     */
    operator fun get(name: String): Function { // Converted to Kotlin function
        return functions[name]
            ?: Function.NOOP // Using map access and elvis operator for concise retrieval and default
    }
}