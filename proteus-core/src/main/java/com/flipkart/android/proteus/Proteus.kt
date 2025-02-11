package com.flipkart.android.proteus

import android.content.Context
import android.view.View
import androidx.annotation.Size

/**
 * Kotlin final class representing the core Proteus instance.
 *
 * This class manages view types, parsers, and functions, providing the central point
 * for creating Proteus contexts and inflating layouts. It is designed to be immutable and thread-safe.
 *
 * @property functions The [FunctionManager] instance holding all registered functions.
 * @property types A map of registered view types, keyed by type name.
 * @property parsers A map of [ViewTypeParser] instances, derived from the [types], for each registered view type.
 * @constructor Creates a new [Proteus] instance with the given types and functions.
 * @param types     The map of view types to register. Must not be null.
 * @param functions The map of functions to register. Must not be null.
 */
class Proteus(
    val functions: FunctionManager, // Converted to Kotlin property, made immutable using val
    private val types: Map<String, Type> // Converted to Kotlin property, made private and immutable
) {

    private val parsers: Map<String, ViewTypeParser<View>> // Converted to Kotlin property, inferred type, wildcard for generics

    init { // Constructor body becomes init block in Kotlin
        parsers = createParsersMap(types) // Initialize parsers map in init block
    }

    /**
     * Checks if a view type is registered in Proteus.
     *
     * @param type The name of the view type to check. Must not be null and have a minimum length of 1 character.
     * @return `true` if the view type is registered, `false` otherwise.
     */
    fun has(@Size(min = 1) type: String): Boolean { // Converted to Kotlin function
        return types.containsKey(type) // Direct map containsKey check
    }

    /**
     * Retrieves the [ViewTypeParser.AttributeSet.Attribute] for a given attribute name and view type.
     *
     * @param name The name of the attribute. Must not be null and have a minimum length of 1 character.
     * @param type The name of the view type. Must not be null and have a minimum length of 1 character.
     * @return The Attribute object if found for the given name and type, or null otherwise.
     */
    fun getAttributeId(
        @Size(min = 1) name: String, @Size(min = 1) type: String
    ): ViewTypeParser.AttributeSet.Attribute? { // Converted to Kotlin function, nullable return type
        return types[type]?.getAttributeId(name) // Safe map access using ?. and chained safe call
    }

    /**
     * Creates a map of view type parsers from the given map of view types.
     */
    private fun createParsersMap(types: Map<String, Type>): Map<String, ViewTypeParser<View>> { // Converted to Kotlin function, inferred return type, wildcard generics
        return types.mapValues { entry -> entry.value.parser } // Using mapValues for concise map transformation
    }

    /**
     * Creates a [ProteusContext] using the default builder with the given base context.
     *
     * @param base The base Android Context. Must not be null.
     * @return A new ProteusContext instance. Never returns null.
     */
    fun createContext(base: Context): ProteusContext { // Converted to Kotlin function
        return createContextBuilder(base).build() // Call builder and build context
    }

    /**
     * Creates a [ProteusContext.Builder] for constructing a [ProteusContext].
     *
     * @param base The base Android Context to be used in the builder. Must not be null.
     * @return A new ProteusContext.Builder instance. Never returns null.
     */
    fun createContextBuilder(base: Context): ProteusContext.Builder { // Converted to Kotlin function
        return ProteusContext.Builder(
            base, parsers, functions
        ) // Create and return builder instance
    }

    /**
     * Kotlin data class representing a Proteus View Type definition.
     *
     * This class holds information about a specific view type, including its ID, type name, parser, and attributes.
     * It's designed as a data class for concise representation and easy data handling.
     *
     * @property id The integer ID of the view type.
     * @property type The string name of the view type. Must not be null.
     * @property parser The [ViewTypeParser] instance for this view type. Must not be null.
     * @property attributes The [ViewTypeParser.AttributeSet] defining attributes for this view type. Must not be null.
     * @constructor Creates a new [Type] instance.
     * @param id         The integer ID.
     * @param type       The type name.
     * @param parser     The ViewTypeParser.
     * @param attributes The AttributeSet.
     */
    data class Type( // Converted inner class to data class for conciseness
        val id: Int, // Converted to Kotlin property, made immutable using val
        val type: String, // Converted to Kotlin property, made immutable using val
        val parser: ViewTypeParser<View>, // Converted to Kotlin property, made immutable using val, wildcard for generics
        private val attributes: ViewTypeParser.AttributeSet // Converted to Kotlin property, made private and immutable
    ) {

        /**
         * Retrieves the [ViewTypeParser.AttributeSet.Attribute] for a given attribute name for this view type.
         *
         * @param name The name of the attribute to retrieve.
         * @return The Attribute object if found, or null otherwise.
         */
        fun getAttributeId(name: String): ViewTypeParser.AttributeSet.Attribute? { // Converted to Kotlin function, nullable return type
            return attributes.getAttribute(name) // Delegate to attributes.getAttribute
        }
    }

    companion object {
        private const val TAG = "Proteus" // No change needed - private static final String constant
    }
}