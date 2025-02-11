package com.flipkart.android.proteus.managers

import android.view.View
import com.flipkart.android.proteus.DataContext
import com.flipkart.android.proteus.ProteusContext
import com.flipkart.android.proteus.ViewTypeParser
import com.flipkart.android.proteus.value.Layout

/**
 * A specialized `ViewManager` for views that manage their children via an adapter.
 *
 * This class is designed for situations where a View (like RecyclerView, ListView, etc.)
 * uses an adapter to handle the display and updates of its child views. In such cases,
 * Proteus's standard child view updating mechanism is not applicable, as the adapter
 * is responsible for this process.
 *
 * Therefore, `AdapterBasedViewManager` overrides the `updateChildren()` method to
 * explicitly do nothing, effectively delegating child management entirely to the adapter.
 *
 * **Key features:**
 *
 * *   **Adapter-centric management:** Avoids Proteus from directly manipulating children of adapter-based views.
 * *   **Simplified updates:** Skips the default child update process which is irrelevant for adapter-driven views.
 * *   **Concise implementation:** Focuses only on constructor and overriding `updateChildren()` for clarity and efficiency.
 *
 * **Usage scenario:**
 *
 * Use this `ViewManager` for custom views or Android widgets that rely on adapters
 * for displaying and managing lists or collections of data-bound views within Proteus.
 * By using this manager, you ensure that Proteus does not interfere with the adapter's
 * responsibility of updating and recycling child views, preventing potential conflicts or
 * unexpected behavior.
 *
 * **Example Registration (Conceptual):**
 *
 * ```kotlin
 * // Assuming you have a custom view 'MyAdapterView' that extends ViewGroup and uses an Adapter.
 * ViewManagerFactory.register(
 *     "MyAdapterView",
 *     fun(context: ProteusContext, parser: ViewTypeParser, view: View, layout: Layout, dataContext: DataContext): ViewManager {
 *         return AdapterBasedViewManager(context, parser, view, layout, dataContext)
 *     }
 * )
 * ```
 *
 * @param context     The Proteus context.
 * @param parser      The view type parser.
 * @param view        The managed View instance.
 * @param layout      The layout associated with the View.
 * @param dataContext The data context for the View.
 */
class AdapterBasedViewManager(
    context: ProteusContext,
    parser: ViewTypeParser<View>,
    view: View,
    layout: Layout,
    dataContext: DataContext
) : ViewGroupManager(context, parser, view, layout, dataContext) {

    /**
     * Overridden method to prevent default child view updates.
     *
     * For adapter-based views, the adapter manages the children, not Proteus.
     * Therefore, this method is intentionally left empty to disable the default
     * child updating behavior of `ViewGroupManager`.
     */
    override fun updateChildren() {
        // Intentionally empty. Adapter handles child updates.
    }
}