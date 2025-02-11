package com.flipkart.android.proteus

import android.view.ViewGroup
import com.flipkart.android.proteus.value.DrawableValue
import com.flipkart.android.proteus.value.Layout
import com.flipkart.android.proteus.value.ObjectValue
import com.flipkart.android.proteus.value.Value

/**
 * Kotlin interface for ProteusLayoutInflater, defining methods for inflating Proteus views and managing view parsers and IDs.
 *
 * `ProteusLayoutInflater` is responsible for taking a `Layout` description and data, and creating a corresponding
 * `ProteusView` (and underlying Android View) with data binding and attribute processing applied.
 * It also provides mechanisms for handling unknown view types, user interactions, and asynchronous image loading.
 */
interface ProteusLayoutInflater { // Kotlin interface declaration

    /**
     * Inflates a ProteusView.
     *
     * @param layout    The Layout object defining the layout for the View to be inflated. Must be non-null.
     * @param data      The ObjectValue data to be used for data binding in the View. Must be non-null.
     * @param parent    The intended parent ViewGroup for the inflated View. Nullable.
     * @param dataIndex An index of data if it's associated with an array. Default is 0.
     * @return          A new ProteusView instance. Must be non-null.
     */

    fun inflate( // Method for inflating ProteusView with parent
        layout: Layout, data: ObjectValue, parent: ViewGroup?, // Using Kotlin's nullable ViewGroup?
        dataIndex: Int
    ): ProteusView

    /**
     * Inflates a ProteusView (overload without parent ViewGroup).
     *
     * @param layout    The Layout object. Must be non-null.
     * @param data      The ObjectValue data. Must be non-null.
     * @param dataIndex Data index. Default is 0.
     * @return          A new ProteusView instance. Must be non-null.
     */

    fun inflate( // Method overload without parent ViewGroup
        layout: Layout, data: ObjectValue, dataIndex: Int
    ): ProteusView

    /**
     * Inflates a ProteusView (overload with Layout and data only).
     *
     * @param layout The Layout object. Must be non-null.
     * @param data   The ObjectValue data. Must be non-null.
     * @return       A new ProteusView instance. Must be non-null.
     */

    fun inflate( // Method overload with Layout and data only
        layout: Layout, data: ObjectValue
    ): ProteusView

    /**
     * Inflates a ProteusView by layout name.
     *
     * @param name      The name of the layout to be inflated. Must be non-null.
     * @param data      The ObjectValue data. Must be non-null.
     * @param parent    The intended parent ViewGroup. Nullable.
     * @param dataIndex Data index. Default is 0.
     * @return          A new ProteusView instance. Must be non-null.
     */

    fun inflate( // Method overload inflating by layout name with parent
        name: String, data: ObjectValue, parent: ViewGroup?, // Using Kotlin's nullable ViewGroup?
        dataIndex: Int
    ): ProteusView

    /**
     * Inflates a ProteusView by layout name (overload without parent ViewGroup).
     *
     * @param name      The name of the layout. Must be non-null.
     * @param data      The ObjectValue data. Must be non-null.
     * @param dataIndex Data index. Default is 0.
     * @return          A new ProteusView instance. Must be non-null.
     */

    fun inflate( // Method overload inflating by layout name without parent ViewGroup
        name: String, data: ObjectValue, dataIndex: Int
    ): ProteusView

    /**
     * Inflates a ProteusView by layout name (overload with name and data only).
     *
     * @param name The name of the layout. Must be non-null.
     * @param data The ObjectValue data. Must be non-null.
     * @return     A new ProteusView instance. Must be non-null.
     */

    fun inflate( // Method overload inflating by layout name with name and data only
        name: String, data: ObjectValue
    ): ProteusView

    /**
     * Returns the ViewTypeParser for the specified view type.
     *
     * @param type The name of the view type. Must be non-null.
     * @return     The ViewTypeParser associated with the view type, or null if not found.
     */

    fun getParser(type: String): ViewTypeParser<*>? // Returns nullable ViewTypeParser with wildcard generic type

    /**
     * Generates a unique View ID for the given string ID.
     *
     * @param id The string ID for which to generate a unique View ID. Must be non-null.
     * @return   An integer View ID. Never returns -1.
     */
    fun getUniqueViewId(id: String): Int

    /**
     * Returns the IdGenerator instance associated with this LayoutInflater.
     * IdGenerator is used to generate unique View IDs.
     *
     * @return The IdGenerator instance. Must be non-null.
     */

    val idGenerator: IdGenerator

    /**
     * Callback interface for ProteusLayoutInflater events.
     * Allows handling of unknown view types and user interaction events.
     */
    interface Callback { // Callback interface declaration

        /**
         * Called when the inflater encounters an unknown view type (not registered with the inflater).
         *
         * @param context The ProteusContext. Must be non-null.
         * @param type    The type name of the unknown view. Must be non-null.
         * @param layout  The Layout object for the unknown view. Must be non-null.
         * @param data    The ObjectValue data for the view. Must be non-null.
         * @param index   The data index.
         * @return        A ProteusView instance to be used for the unknown view type. Must be non-null.
         */

        fun onUnknownViewType( // Callback for unknown view types
            context: ProteusContext, type: String, layout: Layout, data: ObjectValue, index: Int
        ): ProteusView

        /**
         * Called when a user interaction event occurs on a Proteus view.
         *
         * @param event The name of the event (e.g., "click", "longClick"). Must be non-null.
         * @param value The Value associated with the event attribute in the layout. Nullable.
         * @param view  The ProteusView instance on which the event occurred. Must be non-null.
         */
        fun onEvent( // Callback for user interaction events
            event: String, value: Value?, // Using Kotlin's nullable Value?
            view: ProteusView
        )
    }

    /**
     * Interface for asynchronous image loading within Proteus.
     * Implementations of this interface handle the actual image loading logic (e.g., using network libraries).
     */
    interface ImageLoader { // ImageLoader interface declaration

        /**
         * Asynchronously loads a bitmap/drawable from a URL.
         *
         * @param view     The ProteusView (typically ImageView or subclasses) that will display the image. Must be non-null.
         * @param url      The URL of the image to load. Must be non-null.
         * @param callback The AsyncCallback to be invoked when the bitmap/drawable is loaded or if there's an error. Must be non-null.
         */
        fun getBitmap( // Method for asynchronous bitmap loading
            view: ProteusView, url: String, callback: DrawableValue.AsyncCallback
        )
    }
}