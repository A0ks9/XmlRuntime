package com.flipkart.android.proteus

import android.view.View
import com.flipkart.android.proteus.value.Layout
import com.flipkart.android.proteus.value.Value

/**
 * Kotlin implementation of ProteusResources, responsible for holding and managing resources used by the Proteus framework.
 *
 * `ProteusResources` acts as a central repository for various resources required during Proteus layout inflation and processing.
 * It holds maps of `ViewTypeParser` instances, and managers for layouts, functions, and styles.
 *
 * @param parsers         A Map of ViewTypeParsers, keyed by their type name. Must be non-null.
 * @param layoutManager   Optional LayoutManager for retrieving Layouts by name. Nullable.
 * @param functionManager The FunctionManager for retrieving Functions by name. Must be non-null.
 * @param styleManager    Optional StyleManager for retrieving Styles by name. Nullable.
 */
class ProteusResources(
    private val parsers: Map<String, ViewTypeParser<View>>, // Parsers Map (non-null) - using wildcard generic type
    private val layoutManager: LayoutManager?, // LayoutManager (nullable)
    private val functionManager: FunctionManager, // FunctionManager (non-null)
    private val styleManager: StyleManager? // StyleManager (nullable)
) { // Kotlin class declaration with primary constructor

    /**
     * Returns the FunctionManager instance held by this ProteusResources.
     * FunctionManager manages custom functions used in Proteus layouts and expressions.
     *
     * @return The FunctionManager instance. Must be non-null.
     */

    fun getFunctionManager(): FunctionManager { // Returns non-null FunctionManager
        return functionManager // Returns the FunctionManager
    }

    /**
     * Retrieves a specific Function by name from the FunctionManager.
     *
     * @param name The name of the Function to retrieve. Must be non-null.
     * @return     The Function instance with the given name. Must be non-null as FunctionManager should handle missing functions.
     */

    fun getFunction(name: String): Function { // Returns non-null Function
        return functionManager[name] // Retrieves Function from FunctionManager by name
    }

    /**
     * Retrieves a Layout by name using the LayoutManager.
     * Layouts define the structure and attributes of Proteus views.
     *
     * @param name The name of the Layout to retrieve. Must be non-null.
     * @return     The Layout instance with the given name, or null if no LayoutManager is set or no Layout with that name is found.
     */

    fun getLayout(name: String): Layout? { // Returns nullable Layout
        return layoutManager?.get(name) // Retrieves Layout from LayoutManager by name, if LayoutManager is not null
    }

    /**
     * Returns the Map of ViewTypeParsers held by this ProteusResources.
     * ViewTypeParsers are responsible for creating and configuring specific types of views.
     *
     * @return The Map of ViewTypeParsers. Must be non-null.
     */

    fun getParsers(): Map<String, ViewTypeParser<View>> { // Returns non-null Parsers Map
        return parsers // Returns the Parsers Map
    }

    /**
     * Retrieves a Style by name using the StyleManager.
     * Styles are maps of attributes that can be applied to Proteus views for consistent styling.
     *
     * @param name The name of the Style to retrieve.
     * @return     A Map<String, Value> representing the Style, or null if no StyleManager is set or no Style with that name is found.
     */

    fun getStyle(name: String): Map<String, Value>? { // Returns nullable Style Map
        return styleManager?.get(name) // Retrieves Style from StyleManager by name, if StyleManager is not null
    }
}