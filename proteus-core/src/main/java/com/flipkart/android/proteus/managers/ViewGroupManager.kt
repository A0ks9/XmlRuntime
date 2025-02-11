package com.flipkart.android.proteus.managers

import android.view.View
import android.view.ViewGroup
import com.flipkart.android.proteus.DataContext
import com.flipkart.android.proteus.ProteusContext
import com.flipkart.android.proteus.ProteusView
import com.flipkart.android.proteus.ViewTypeParser
import com.flipkart.android.proteus.value.Layout
import com.flipkart.android.proteus.value.ObjectValue

/**
 * A `ViewManager` specialized for managing `ViewGroup`s.
 *
 * This class extends `ViewManager` to provide specific functionality for `ViewGroup`s.
 * It handles updating child views that are instances of `ProteusView` within the managed `ViewGroup`.
 *
 * The `ViewGroupManager` is responsible for:
 *
 * *   Initializing with a `ProteusContext`, `ViewTypeParser`, `ViewGroup` instance, `Layout`, and `DataContext`.
 * *   Tracking whether the `ViewGroup` has data-bound children (`hasDataBoundChildren`). This flag can be used to optimize updates.
 * *   Overriding the `update(data: ObjectValue?)` method to trigger updates for both the `ViewGroup` itself and its `ProteusView` children.
 * *   Implementing `updateChildren()` to recursively update all direct `ProteusView` children of the `ViewGroup`, but only if `hasDataBoundChildren` is false.
 *     This condition is likely an optimization to prevent unnecessary updates in scenarios where child views are independently data-bound and managed.
 *
 * **Key Features:**
 *
 * *   **ViewGroup Specific:**  Tailored for managing Android `ViewGroup`s and their internal structure within the Proteus framework.
 * *   **Child View Updating:** Provides the `updateChildren()` mechanism to propagate data updates down to child `ProteusView`s.
 * *   **Optimization Flag:** Includes `hasDataBoundChildren` to potentially optimize child updates based on data-binding configurations (though the exact usage depends on the broader Proteus context).
 * *   **Inheritance from ViewManager:** Reuses core `ViewManager` functionalities and extends them for ViewGroup scenarios.
 *
 * **Usage Scenario:**
 *
 * Use `ViewGroupManager` for managing any `ViewGroup` within your Proteus layouts where you need to:
 *
 * 1.  Apply standard `ViewManager` operations (like attribute processing, lifecycle management - inherited from `ViewManager`).
 * 2.  Propagate data updates to direct child views that are also `ProteusView`s, effectively creating hierarchical data binding.
 * 3.  Potentially optimize child updates using the `hasDataBoundChildren` flag (depending on your specific needs and how data binding is structured).
 *
 * **Example Registration (Conceptual):**
 *
 * ```kotlin
 * ViewManagerFactory.register(
 *     "LinearLayout", // or any ViewGroup type in your layout definitions
 *     fun(context: ProteusContext, parser: ViewTypeParser, view: View, layout: Layout, dataContext: DataContext): ViewManager {
 *         return ViewGroupManager(context, parser, view, layout, dataContext)
 *     }
 * )
 * ```
 *
 * @param context     The Proteus context.
 * @param parser      The view type parser.
 * @param view        The managed `ViewGroup` instance.
 * @param layout      The layout associated with the `ViewGroup`.
 * @param dataContext The data context for the `ViewGroup`.
 */
open class ViewGroupManager(
    context: ProteusContext,
    parser: ViewTypeParser<View>,
    view: View,
    layout: Layout,
    dataContext: DataContext
) : ViewManager(context, parser, view, layout, dataContext) {

    /**
     * Flag indicating whether this `ViewGroup` has children that are data-bound.
     *
     * This can be used as an optimization. If `hasDataBoundChildren` is false, `updateChildren()` will attempt to update
     * direct `ProteusView` children. If true, it might be assumed that child updates are handled differently (e.g., by an adapter
     * or some other data-binding mechanism), and `updateChildren()` can be skipped or have different behavior (though in the current
     * implementation, if true, `updateChildren()` simply does nothing in terms of updating children).
     */
    var hasDataBoundChildren: Boolean = false

    init {
        hasDataBoundChildren = false // Initialize in constructor
    }

    /**
     * Overrides the base class's `update` method to also update child views.
     *
     * First, calls the superclass's `update` method to handle updates for the `ViewGroup` itself.
     * Then, calls `updateChildren()` to propagate the data update to the children of this `ViewGroup` that are `ProteusView`s.
     *
     * @param data The data to update the view with. Can be null.
     */
    override fun update(data: ObjectValue?) {
        super.update(data) // Update properties of this ViewGroup first
        updateChildren()    // Then update its ProteusView children
    }

    /**
     * Updates all direct child views of this `ViewGroup` that are instances of `ProteusView`.
     *
     * This method is protected and can be overridden in subclasses to customize child update behavior if needed.
     *
     * It iterates through the direct children of the managed `ViewGroup`. For each child:
     * 1. Checks if the child is an instance of `ProteusView`.
     * 2. If it is a `ProteusView`, it retrieves its `ViewManager` and calls its `update` method, passing the current `DataContext`'s data.
     *
     * **Optimization Logic:** This method only performs child updates if `hasDataBoundChildren` is `false`. This likely assumes that
     * if `hasDataBoundChildren` is `true`, the children are managed or updated through a different data-binding mechanism (e.g., via an adapter)
     * and do not need to be updated by this method. If `hasDataBoundChildren` is intended for a different purpose in your framework,
     * you might need to adjust this logic in subclasses or reconsider its role.
     */
    protected open fun updateChildren() {
        if (!hasDataBoundChildren && viewManagerView is ViewGroup) { // Check if it's a ViewGroup and should update children
            val parent = viewManagerView
            val count = parent.childCount

            for (index in 0 until count) { // Kotlin idiomatic way to loop through indices
                val child = parent.getChildAt(index)
                if (child is ProteusView) {
                    (child as ProteusView).viewManager.update(dataContext.data) // Update ProteusView child
                }
            }
        }
    }
}