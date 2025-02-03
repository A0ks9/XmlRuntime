package com.flipkart.android.proteus

import android.view.View
import com.flipkart.android.proteus.value.Layout
import com.flipkart.android.proteus.value.ObjectValue

/**
 * Kotlin interface for ProteusView, representing a view within the Proteus framework.
 *
 * `ProteusView` is the core interface for all views managed by the Proteus framework. It provides methods to:
 * - Access and set its `ViewManager` (responsible for data binding and attribute updates).
 * - Retrieve the underlying Android `View` instance.
 * - Define the `Manager` interface (inner interface) for managing Proteus views.
 */
interface ProteusView { // Kotlin interface declaration

    /**
     * Returns the ViewManager associated with this ProteusView.
     * ViewManager is responsible for managing data binding, attribute updates, and interactions for the view.
     *
     * @return The ViewManager instance. Must be non-null.
     */
    val viewManager: Manager // Returns non-null ViewManager

    /**
     * Sets the ViewManager for this ProteusView.
     * This method is typically called during Proteus view inflation to associate a Manager with the newly created view.
     *
     * @param manager The ViewManager instance to set. Must be non-null.
     */
    fun setViewManager(manager: Manager) // Setter for ViewManager

    /**
     * Returns the underlying Android View instance represented by this ProteusView.
     * This allows access to the native Android View for direct manipulation or integration with Android APIs.
     *
     * @return The Android View instance. Must be non-null.
     */
    val asView: View // Returns non-null Android View

    /**
     * Inner interface `Manager` defines the contract for managing a ProteusView.
     * Implementations of `Manager` are responsible for handling data updates, view lookups,
     * accessing context and layout information, and managing extra data associated with the view.
     */
    interface Manager { // Inner interface Manager declaration

        /**
         * Update the {@link View} with new data.
         * This method is called to refresh the view's content based on changes in the data context.
         *
         * @param data New data for the view
         */
        fun update(data: ObjectValue?) // Method to update View with new data, data can be nullable

        /**
         * Look for a child view with the given id.  If this view has the given
         * id, return this view. Similar to {@link View#findViewById(int)}. Since
         * Proteus is a runtime inflater, layouts use String ids instead of int and it
         * generates unique int ids using the {@link IdGenerator}.
         *
         * @param id The  string id to search for.
         * @return The view that has the given id in the hierarchy or null
         */
        fun findViewById(id: String): View? // Method to find child View by string ID, returns nullable View

        /**
         * @return The Proteus Context associated with this Manager.
         */
        val context: ProteusContext // Returns non-null ProteusContext

        /**
         * @return The Layout of View which is hosting this manager.
         */
        val layout: Layout // Returns non-null Layout

        /**
         * @return The Data Context of the view which is hosting this manager.
         */
        val dataContext: DataContext // Returns non-null DataContext

        /**
         * Returns this proteus view's extras.
         *
         * @return the Object stored in this view as a extra, or {@code null} if not set
         * @see #setExtras(Object)
         */
        val extras: Any? // Returns nullable extra data Object

        /**
         * Sets the extra associated with this view. A extra can be used to mark a view in its hierarchy
         * and does not have to be unique within the hierarchy. Extras can also be used to store data
         * within a proteus view without resorting to another data structure.
         * It is similar to {@link View#setTag(Object)}
         *
         * @param extras The object to set as the extra.
         * @see #setExtras(Object)
         */
        fun setExtras(extras: Any?) // Setter for extra data Object, can be nullable
    }
}