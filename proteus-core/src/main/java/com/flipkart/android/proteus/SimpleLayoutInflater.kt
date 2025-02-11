package com.flipkart.android.proteus

import android.util.Log
import android.view.View
import android.view.ViewGroup
import com.flipkart.android.proteus.exceptions.ProteusInflateException
import com.flipkart.android.proteus.value.Layout
import com.flipkart.android.proteus.value.ObjectValue
import com.flipkart.android.proteus.value.Value

/**
 * Kotlin implementation of SimpleLayoutInflater, a basic implementation of ProteusLayoutInflater.
 *
 * `SimpleLayoutInflater` provides a straightforward way to inflate Proteus layouts, handling view creation,
 * attribute processing, and view management. It uses a `ProteusContext` for resources and an `IdGenerator`
 * for generating unique View IDs. It implements the `ProteusLayoutInflater` interface.
 *
 * @param context     The ProteusContext associated with this LayoutInflater. Must be non-null.
 * @param simpleIdGenerator The IdGenerator to be used for generating unique View IDs. Must be non-null.
 */
open class SimpleLayoutInflater( // Kotlin class declaration, marked 'open' to allow subclassing
    protected val context: ProteusContext, // ProteusContext (non-null, protected for subclass access)
    protected val simpleIdGenerator: IdGenerator // IdGenerator (non-null, protected for subclass access)
) : ProteusLayoutInflater { // Implementing ProteusLayoutInflater interface

    companion object { // Companion object for constants and static members
        private const val TAG = "SimpleLayoutInflater" // Tag for logging
    }

    /**
     * Returns the ViewTypeParser for the specified view type from the ProteusContext.
     *
     * @param type The name of the view type. Must be non-null.
     * @return     The ViewTypeParser associated with the view type, or null if not found.
     */
    override fun getParser(type: String): ViewTypeParser<View>? { // Implementation of getParser from ProteusLayoutInflater
        return context.getParser(type) // Retrieves ViewTypeParser from ProteusContext
    }

    /**
     * Inflates a ProteusView from a Layout object, with data binding and attribute processing.
     * This is the core inflation method that orchestrates view creation, attribute handling, and view management.
     *
     * @param layout    The Layout object defining the layout to inflate. Must be non-null.
     * @param data      The ObjectValue data for data binding. Must be non-null.
     * @param parent    The parent ViewGroup to which the inflated view will be added. Nullable.
     * @param dataIndex An index of data, if it's associated with an array. Default is 0.
     * @return          A new ProteusView instance. Must be non-null.
     */
    override fun inflate( // Implementation of inflate with parent ViewGroup from ProteusLayoutInflater
        layout: Layout, data: ObjectValue, parent: ViewGroup?, // Using Kotlin's nullable ViewGroup?
        dataIndex: Int
    ): ProteusView {

        val parser = getParser(layout.type) // Get ViewTypeParser for the layout type
        if (parser == null) { // If no parser found for the type
            return onUnknownViewEncountered(
                layout.type, layout, data, dataIndex
            ) // Handle unknown view type via callback
        }

        val view = createView(
            parser, layout, data, parent, dataIndex
        ) // Create the ProteusView using the parser

        if (view.viewManager == null) { // If ViewManager is not already set (usually not for newly created views)
            onAfterCreateView(parser, view, parent, dataIndex) // Perform post-creation logic
            val viewManager = createViewManager(
                parser, view, layout, data, parent, dataIndex
            ) // Create ViewManager
            view.setViewManager(viewManager) // Set the ViewManager on the ProteusView
        }

        if (layout.attributes != null) { // If the layout has attributes defined
            for (attribute in layout.attributes) { // Iterate through each attribute
                handleAttribute(
                    parser, view, attribute.id, attribute.value
                ) // Handle and apply the attribute
            }
        }
        return view // Return the fully inflated and processed ProteusView
    }

    /**
     * Inflates a ProteusView from a Layout object (overload without parent ViewGroup).
     * Calls the main `inflate` method with parent set to null.
     */
    override fun inflate(
        layout: Layout, data: ObjectValue, dataIndex: Int
    ): ProteusView { // Implementation of inflate without parent ViewGroup
        return inflate(layout, data, null, dataIndex) // Call main inflate method with null parent
    }

    /**
     * Inflates a ProteusView from a Layout object (overload with Layout and data only).
     * Calls the main `inflate` method with parent set to null and dataIndex to -1 (default).
     */
    override fun inflate(
        layout: Layout, data: ObjectValue
    ): ProteusView { // Implementation of inflate with Layout and data only
        return inflate(
            layout, data, null, -1
        ) // Call main inflate method with null parent and default dataIndex
    }

    /**
     * Inflates a ProteusView by layout name, retrieves the Layout from ProteusContext, then inflates.
     *
     * @param name      The name of the layout to inflate. Must be non-null.
     * @param data      The ObjectValue data for data binding. Must be non-null.
     * @param parent    The parent ViewGroup. Nullable.
     * @param dataIndex Data index. Default is 0.
     * @return          A new ProteusView instance. Must be non-null.
     * @throws ProteusInflateException if the layout with the given name is not found.
     */
    override fun inflate( // Implementation of inflate by layout name with parent ViewGroup
        name: String, data: ObjectValue, parent: ViewGroup?, // Using Kotlin's nullable ViewGroup?
        dataIndex: Int
    ): ProteusView {
        val layout = context.getLayout(name) // Get Layout from ProteusContext by name
        if (layout == null) { // If layout is not found
            throw ProteusInflateException("layout : '$name' not found") // Throw exception if layout is not found
        }
        return inflate(
            layout, data, parent, dataIndex
        ) // Call main inflate method with retrieved Layout
    }

    /**
     * Inflates a ProteusView by layout name (overload without parent ViewGroup).
     * Calls the main `inflate` by name method with parent set to null.
     */
    override fun inflate(
        name: String, data: ObjectValue, dataIndex: Int
    ): ProteusView { // Implementation of inflate by layout name without parent ViewGroup
        return inflate(
            name, data, null, dataIndex
        ) // Call main inflate by name method with null parent
    }

    /**
     * Inflates a ProteusView by layout name (overload with name and data only).
     * Calls the main `inflate` by name method with parent set to null and dataIndex to -1 (default).
     */
    override fun inflate(
        name: String, data: ObjectValue
    ): ProteusView { // Implementation of inflate by layout name with name and data only
        return inflate(
            name, data, null, -1
        ) // Call main inflate by name method with null parent and default dataIndex
    }

    /**
     * Returns a unique View ID using the injected IdGenerator.
     *
     * @param id The string ID for which to generate a unique View ID. Must be non-null.
     * @return   A unique integer View ID.
     */
    override fun getUniqueViewId(id: String): Int { // Implementation of getUniqueViewId from ProteusLayoutInflater
        return idGenerator.getUnique(id) // Get unique ID from IdGenerator
    }

    /**
     * Returns the IdGenerator instance used by this LayoutInflater.
     *
     * @return The IdGenerator instance. Must be non-null.
     */
    override val idGenerator: IdGenerator =
        simpleIdGenerator // IdGenerator is used to generate unique View IDs

    /**
     * Creates a new Android View (wrapped in ProteusView) using the provided ViewTypeParser.
     *
     * @param parser    The ViewTypeParser for the type of View to create. Must be non-null.
     * @param layout    The Layout object for the View. Must be non-null.
     * @param data      The ObjectValue data. Must be non-null.
     * @param parent    The parent ViewGroup. Nullable.
     * @param dataIndex Data index.
     * @return          A new ProteusView instance. Must be non-null.
     */
    protected open fun createView( // Open function to create ProteusView, can be overridden in subclasses
        parser: ViewTypeParser<*>,
        layout: Layout,
        data: ObjectValue,
        parent: ViewGroup?, // Using Kotlin's nullable ViewGroup?
        dataIndex: Int
    ): ProteusView {
        return parser.createView(
            context, layout, data, parent, dataIndex
        ) // Delegate view creation to ViewTypeParser
    }

    /**
     * Creates a ViewManager for the created ProteusView using the provided ViewTypeParser.
     *
     * @param parser    The ViewTypeParser for the type of View. Must be non-null.
     * @param view      The ProteusView instance. Must be non-null.
     * @param layout    The Layout object. Must be non-null.
     * @param data      The ObjectValue data. Must be non-null.
     * @param parent    The parent ViewGroup. Nullable.
     * @param dataIndex Data index.
     * @return          A new ProteusView.Manager instance. Must be non-null.
     */
    protected open fun createViewManager( // Open function to create ViewManager, can be overridden
        parser: ViewTypeParser<View>,
        view: ProteusView,
        layout: Layout,
        data: ObjectValue,
        parent: ViewGroup?, // Using Kotlin's nullable ViewGroup?
        dataIndex: Int
    ): ProteusView.Manager {
        return parser.createViewManager(
            context, view, layout, data, parser, parent, dataIndex
        ) // Delegate ViewManager creation to ViewTypeParser, passing 'this' as caller
    }

    /**
     * Called after a ProteusView is created, before ViewManager is attached.
     * Allows subclasses to perform post-creation logic, like setting default layout parameters.
     *
     * @param parser The ViewTypeParser used to create the view. Must be non-null.
     * @param view   The created ProteusView instance. Must be non-null.
     * @param parent The parent ViewGroup. Nullable.
     * @param index  Data index.
     */
    protected open fun onAfterCreateView( // Open function for post-creation logic, can be overridden
        parser: ViewTypeParser<*>,
        view: ProteusView,
        parent: ViewGroup?, // Using Kotlin's nullable ViewGroup?
        index: Int
    ) {
        parser.onAfterCreateView(
            view, parent, index
        ) // Delegate post-creation logic to ViewTypeParser
    }

    /**
     * Handles the scenario when an unknown view type is encountered during inflation.
     * It checks for a callback in ProteusContext and delegates the handling to the callback if available.
     * If no callback is set or the callback returns null, it throws a ProteusInflateException.
     *
     * @param type      The unknown view type name.
     * @param layout    The Layout object for the unknown view.
     * @param data      The ObjectValue data.
     * @param dataIndex Data index.
     * @return          A ProteusView instance (typically created by the callback). Must be non-null if callback handles the type.
     * @throws ProteusInflateException if no callback is set or callback returns null.
     */
    protected open fun onUnknownViewEncountered( // Open function to handle unknown view types, can be overridden
        type: String, layout: Layout, data: ObjectValue, dataIndex: Int
    ): ProteusView {
        if (ProteusConstants.isLoggingEnabled()) { // Check if logging is enabled
            Log.d(TAG, "No ViewTypeParser for: $type") // Log unknown view type
        }
        val callback = context.getCallback() // Get callback from ProteusContext
        return if (callback != null) { // Check if callback is set
            callback.onUnknownViewType(
                context, type, layout, data, dataIndex
            ) // Throw exception if callback returns null
        } else {
            throw ProteusInflateException("Layout contains type: 'include' but inflater callback is null") // Throw exception if no callback is set
        }
    }

    /**
     * Handles a single attribute for a ProteusView.
     * It delegates the actual attribute handling to the ViewTypeParser associated with the view.
     *
     * @param parser    The ViewTypeParser for the View type. Must be non-null.
     * @param view      The ProteusView instance. Must be non-null.
     * @param attribute The attribute ID to handle.
     * @param value     The Value representing the attribute's value. Must be non-null.
     * @return          true if the attribute was handled, false otherwise (typically if the parser doesn't handle the attribute).
     */
    protected open fun handleAttribute( // Open function to handle attribute, can be overridden
        parser: ViewTypeParser<View>, view: ProteusView, attribute: Int, value: Value
    ): Boolean {
        if (ProteusConstants.isLoggingEnabled()) { // Check if logging is enabled
            Log.d(TAG, "Handle '$attribute' : $value") // Log attribute handling
        }
        @Suppress("UNCHECKED_CAST") // Suppress unchecked cast - cast is safe in this context due to generic type constraints
        return parser.handleAttribute(
            view.asView, attribute, value
        ) // Delegate attribute handling to ViewTypeParser
    }
}