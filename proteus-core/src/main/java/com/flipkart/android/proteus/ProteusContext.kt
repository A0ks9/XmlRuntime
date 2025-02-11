package com.flipkart.android.proteus

import android.content.Context
import android.content.ContextWrapper
import android.view.View
import com.flipkart.android.proteus.value.Layout
import com.flipkart.android.proteus.value.Value

/**
 * Kotlin implementation of ProteusContext, providing a context for Proteus view inflation and resource management.
 *
 * `ProteusContext` extends `ContextWrapper` and acts as a custom context within the Proteus framework.
 * It holds references to `ProteusResources`, `ProteusLayoutInflater.Callback`, `ProteusLayoutInflater.ImageLoader`,
 * and manages the `ProteusLayoutInflater` instance. It provides access to resources, functions, layouts, and styles
 * specific to the Proteus framework.
 *
 * @param base      The base Android Context to wrap.
 * @param resources The ProteusResources instance holding framework-specific resources.
 * @param loader    Optional ImageLoader for loading images within Proteus views.
 * @param callback  Optional Callback for handling events during Proteus layout inflation.
 */
open class ProteusContext(
    base: Context, // Base Context to wrap
    private val resources: ProteusResources, // ProteusResources instance (non-null)
    private val loader: ProteusLayoutInflater.ImageLoader?, // ImageLoader (nullable)
    private val callback: ProteusLayoutInflater.Callback? // Callback (nullable)
) : ContextWrapper(base) { // Inherits from ContextWrapper

    /**
     * LayoutInflater instance specific to Proteus for inflating layouts.
     * Initialized lazily when `getInflater()` is called for the first time.
     */
    private var inflater: ProteusLayoutInflater? = null

    /**
     * Returns the callback for Proteus layout inflation events.
     *
     * @return The ProteusLayoutInflater.Callback instance, or null if no callback is set.
     */

    fun getCallback(): ProteusLayoutInflater.Callback? { // Returns nullable Callback
        return callback // Returns the callback instance
    }

    /**
     * Returns the FunctionManager associated with this ProteusContext.
     * FunctionManager manages custom functions that can be used in Proteus layouts and expressions.
     *
     * @return The FunctionManager instance. Must be non-null as it's retrieved from ProteusResources.
     */

    fun getFunctionManager(): FunctionManager { // Returns non-null FunctionManager
        return resources.getFunctionManager() // Returns FunctionManager from resources
    }

    /**
     * Retrieves a specific Function by name from the FunctionManager.
     *
     * @param name The name of the Function to retrieve. Must be non-null.
     * @return     The Function instance with the given name. Must be non-null as FunctionManager should handle missing functions.
     */

    fun getFunction(name: String): Function { // Returns non-null Function
        return resources.getFunction(name) // Gets Function from resources by name
    }

    /**
     * Retrieves a Layout by name from the ProteusResources.
     * Layouts define the structure and attributes of Proteus views.
     *
     * @param name The name of the Layout to retrieve. Must be non-null.
     * @return     The Layout instance with the given name, or null if no Layout with that name is found.
     */

    fun getLayout(name: String): Layout? { // Returns nullable Layout
        return resources.getLayout(name) // Gets Layout from resources by name
    }

    /**
     * Returns the ImageLoader associated with this ProteusContext.
     * ImageLoader is responsible for loading images for ImageViews within Proteus layouts.
     *
     * @return The ProteusLayoutInflater.ImageLoader instance, or null if no ImageLoader is set.
     */

    fun getLoader(): ProteusLayoutInflater.ImageLoader? { // Returns nullable ImageLoader
        return loader // Returns the ImageLoader instance
    }

    /**
     * Gets the ProteusLayoutInflater instance for this context, using a provided ID generator.
     * If the inflater is not yet initialized, it creates a new `SimpleLayoutInflater`.
     *
     * @param idGenerator The IdGenerator to be used by the LayoutInflater for generating View IDs. Must be non-null.
     * @return            The ProteusLayoutInflater instance. Must be non-null.
     */

    fun getInflater(idGenerator: IdGenerator): ProteusLayoutInflater { // Returns non-null LayoutInflater
        if (inflater == null) { // Lazy initialization of inflater
            inflater = SimpleLayoutInflater(
                this, idGenerator
            ) // Create SimpleLayoutInflater if not initialized
        }
        return inflater as ProteusLayoutInflater // Return inflater instance (smart cast to non-null)
    }

    /**
     * Gets the ProteusLayoutInflater instance for this context, using a default `SimpleIdGenerator`.
     * This is a convenience method when a custom ID generator is not needed.
     *
     * @return The ProteusLayoutInflater instance. Must be non-null.
     */

    fun getInflater(): ProteusLayoutInflater { // Returns non-null LayoutInflater
        return getInflater(SimpleIdGenerator()) // Calls getInflater with default SimpleIdGenerator
    }

    /**
     * Retrieves a ViewTypeParser for a given view type name from the ProteusResources.
     * ViewTypeParsers are responsible for creating and configuring specific types of views.
     *
     * @param type The type name of the ViewTypeParser to retrieve.
     * @return     The ViewTypeParser instance for the given type, or null if no parser is registered for that type.
     */

    fun getParser(type: String): ViewTypeParser<View>? { // Returns nullable ViewTypeParser with wildcard generic type
        return resources.getParsers()[type] // Gets ViewTypeParser from resources by type name
    }

    /**
     * Returns the ProteusResources instance associated with this context.
     * ProteusResources holds all the resources (parsers, layouts, functions, styles) for the Proteus framework.
     *
     * @return The ProteusResources instance. Must be non-null.
     */

    fun getProteusResources(): ProteusResources { // Returns non-null ProteusResources
        return resources // Returns the ProteusResources instance
    }

    /**
     * Retrieves a Style by name from the ProteusResources.
     * Styles are maps of attributes that can be applied to Proteus views for consistent styling.
     *
     * @param name The name of the Style to retrieve.
     * @return     A Map<String, Value> representing the Style, or null if no Style with that name is found.
     */

    fun getStyle(name: String): Map<String, Value>? { // Returns nullable Style Map
        return resources.getStyle(name) // Gets Style from resources by name
    }

    /**
     * Builder class for creating instances of ProteusContext.
     * Provides a fluent API to configure and build a ProteusContext.
     */
    class Builder { // Builder class for ProteusContext


        private val base: Context // Base Context (non-null)


        private val functionManager: FunctionManager // FunctionManager (non-null)


        private val parsers: Map<String, ViewTypeParser<View>> // Parsers Map (non-null)


        private var loader: ProteusLayoutInflater.ImageLoader? =
            null // ImageLoader (nullable, default null)


        private var callback: ProteusLayoutInflater.Callback? =
            null // Callback (nullable, default null)


        private var layoutManager: LayoutManager? = null // LayoutManager (nullable, default null)


        private var styleManager: StyleManager? = null // StyleManager (nullable, default null)

        /**
         * Constructor for the Builder.
         *
         * @param context         The base Android Context. Must be non-null.
         * @param parsers         A Map of ViewTypeParsers to be used in the ProteusContext. Must be non-null.
         * @param functionManager The FunctionManager to be used in the ProteusContext. Must be non-null.
         */
        internal constructor( // Internal constructor, meant for framework use
            context: Context, // Base Context
            parsers: Map<String, ViewTypeParser<View>>, // Parsers Map
            functionManager: FunctionManager // FunctionManager
        ) {
            this.base = context
            this.parsers = parsers
            this.functionManager = functionManager
        }

        /**
         * Sets the ImageLoader for the Builder.
         *
         * @param loader The ImageLoader instance. Nullable.
         * @return       This Builder instance for chaining.
         */
        fun setImageLoader(loader: ProteusLayoutInflater.ImageLoader?): Builder { // Setter for ImageLoader
            this.loader = loader
            return this // For builder chaining
        }

        /**
         * Sets the Callback for the Builder.
         *
         * @param callback The Callback instance. Nullable.
         * @return         This Builder instance for chaining.
         */
        fun setCallback(callback: ProteusLayoutInflater.Callback?): Builder { // Setter for Callback
            this.callback = callback
            return this // For builder chaining
        }

        /**
         * Sets the LayoutManager for the Builder.
         *
         * @param layoutManager The LayoutManager instance. Nullable.
         * @return            This Builder instance for chaining.
         */
        fun setLayoutManager(layoutManager: LayoutManager?): Builder { // Setter for LayoutManager
            this.layoutManager = layoutManager
            return this // For builder chaining
        }

        /**
         * Sets the StyleManager for the Builder.
         *
         * @param styleManager The StyleManager instance. Nullable.
         * @return           This Builder instance for chaining.
         */
        fun setStyleManager(styleManager: StyleManager?): Builder { // Setter for StyleManager
            this.styleManager = styleManager
            return this // For builder chaining
        }

        /**
         * Builds and returns a new ProteusContext instance.
         *
         * @return A new ProteusContext instance configured with the Builder's settings.
         */
        fun build(): ProteusContext { // Build method to create ProteusContext
            val resources = ProteusResources(
                parsers, layoutManager, functionManager, styleManager
            ) // Create ProteusResources instance
            return ProteusContext(
                base, resources, loader, callback
            ) // Create and return ProteusContext instance
        }
    }
}