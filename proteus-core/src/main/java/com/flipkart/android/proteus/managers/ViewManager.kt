package com.flipkart.android.proteus.managers

import android.view.View
import com.flipkart.android.proteus.BoundAttribute
import com.flipkart.android.proteus.DataContext
import com.flipkart.android.proteus.ProteusContext
import com.flipkart.android.proteus.ProteusView
import com.flipkart.android.proteus.ViewTypeParser
import com.flipkart.android.proteus.value.Layout
import com.flipkart.android.proteus.value.ObjectValue

/**
 * The base class for managing a View within the Proteus framework.
 *
 * A `ViewManager` is responsible for handling data binding and updates for a specific `View` instance
 * created from a layout definition. It acts as a bridge between the layout structure, data context,
 * and the actual Android View.
 *
 * **Key Responsibilities:**
 *
 * *   **Initialization:** Holds references to the `ProteusContext`, `ViewTypeParser`, the managed `View`, `Layout`, and `DataContext`.
 * *   **Data Binding Setup:**  Identifies and stores attributes in the layout that are data-bound (contain bindings).
 * *   **Data Updates:** The `update(data: ObjectValue?)` method is the core function for updating the view based on new data. This includes:
 *     *   Updating the internal `DataContext` to reflect the new data.
 *     *   Iterating through the stored bound attributes and applying the bindings to the managed `View`.
 * *   **View ID Lookup:** Provides `findViewById(id: String)` to retrieve child views within the managed view hierarchy by their string IDs.
 * *   **Context and Layout Access:** Offers getter methods to access the associated `ProteusContext` and `Layout`.
 * *   **Extras Management:** Supports attaching and retrieving extra data associated with the `ViewManager` for custom purposes via `getExtras()` and `setExtras()`.
 *
 * **Core Concepts:**
 *
 * *   **BoundAttribute:** Represents an attribute in the layout that is bound to data. Stored for efficient updates.
 * *   **DataContext:**  Holds the data relevant to the managed View and its children. Updated on each data refresh.
 * *   **ViewTypeParser:** Responsible for actually applying attribute values (including resolving bindings) to the View.
 *
 * **Usage Scenario:**
 *
 * Every `ProteusView` has an associated `ViewManager`. When data is updated in Proteus (e.g., through `ProteusLayoutInflater.render`),
 * the `update(data)` method of the root `ViewManager` is called, which propagates updates down the view hierarchy.
 *
 * Concrete subclasses like `ViewGroupManager` extend `ViewManager` to add specific behavior for different View types
 * (e.g., handling child view updates in `ViewGroup`s).
 *
 * **Example (Conceptual - Internally used by Proteus):**
 *
 * ```kotlin
 * // Assume 'proteusView' is a ProteusView instance and 'newData' is an ObjectValue
 * proteusView.viewManager.update(newData) // Trigger data update for this view and its bindings.
 * ```
 *
 * @param viewManagerContext     The Proteus context.
 * @param viewManagerParser      The view type parser.
 * @param viewManagerView        The Android View instance being managed.
 * @param viewManagerLayout      The layout definition for the View.
 * @param viewManagerDataContext The data context for the View.
 */
open class ViewManager(
    protected val viewManagerContext: ProteusContext,
    protected val viewManagerParser: ViewTypeParser<View>,
    protected val viewManagerView: View,
    protected val viewManagerLayout: Layout,
    protected val viewManagerDataContext: DataContext
) : ProteusView.Manager {

    protected val boundAttributes: List<BoundAttribute>? =
        viewManagerLayout.attributes?.let { layoutAttributes ->
            layoutAttributes.filter { it.value.isBinding }
                .map { BoundAttribute(it.id, it.value.asBinding()) }
                .takeIf { it.isNotEmpty() } // Return null if the list is empty for efficiency
        }


    /**
     *  Optional extras object for storing custom data associated with this ViewManager.
     */
    protected var viewManagerExtras: Any? = null

    /**
     * Updates the view with new data.
     *
     * This method is called when the data context is refreshed or new data is available for binding.
     * It performs the following steps:
     * 1. **Updates Data Context:** Updates the internal `DataContext` with the provided `data`.
     * 2. **Handles Bindings:** If there are bound attributes for this view, it iterates through them and applies each binding using `handleBinding(boundAttribute)`.
     *
     * @param data The new data to update the view with. Can be null.
     */
    override fun update(data: ObjectValue?) {
        data?.let { updateDataContext(it) } // Update data context if data is provided

        boundAttributes?.forEach { boundAttribute ->  // Iterate and handle bindings if they exist
            handleBinding(boundAttribute)
        }
    }

    /**
     * Finds a child view within this View's hierarchy by its string ID.
     *
     * Uses `view.findViewById` and resolves the unique integer ID using `context.getInflater().getUniqueViewId(id)`.
     *
     * @param id The string ID of the view to find.
     * @return The found View, or null if not found.
     */
    override fun findViewById(id: String): View? {
        return viewManagerView.findViewById(context.getInflater().getUniqueViewId(id))
    }

    /**
     * Gets the ProteusContext associated with this ViewManager.
     *
     * @return The ProteusContext.
     */
    override val context: ProteusContext = viewManagerContext

    /**
     * Gets the Layout associated with this ViewManager.
     *
     * @return The Layout.
     */
    override val layout: Layout = viewManagerLayout

    /**
     * Gets the DataContext associated with this ViewManager.
     *
     * @return The DataContext.
     */
    override val dataContext: DataContext = viewManagerDataContext

    /**
     * Gets any extra data that has been set on this ViewManager.
     *
     * @return The extras object, or null if no extras have been set.
     */
    override val extras: Any? = viewManagerExtras

    /**
     * Sets extra data to be associated with this ViewManager.
     *
     * This can be used to store arbitrary objects or information that is relevant to the ViewManager or its associated View.
     *
     * @param extras The extras object to set.
     */
    override fun setExtras(extras: Any?) {
        this.viewManagerExtras = extras
    }

    /**
     * Updates the DataContext with new data.
     *
     * If the DataContext has its own properties (is not just a passthrough), it updates its properties using the new `data`.
     * Otherwise, it directly sets the provided `data` as the DataContext's data.
     *
     * @param data The new data to set in the DataContext.
     */
    private fun updateDataContext(data: ObjectValue) {
        if (dataContext.hasOwnProperties()) {
            dataContext.update(context, data)
        } else {
            dataContext.data = data
        }
    }

    /**
     * Handles a single data binding for a specific attribute.
     *
     * Calls `parser.handleAttribute` to process the binding and apply the resolved attribute value to the managed View.
     *
     * @param boundAttribute The BoundAttribute representing the attribute and its binding.
     */
    private fun handleBinding(boundAttribute: BoundAttribute) {
        viewManagerParser.handleAttribute(
            viewManagerView, boundAttribute.attributeId, boundAttribute.binding
        )
    }
}