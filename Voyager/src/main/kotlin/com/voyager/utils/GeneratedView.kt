/**
 * `GeneratedView` is an internal data class used to store Voyager-specific information
 * associated with an Android [android.view.View] instance, typically via the view's tag.
 *
 * It acts as a container for:
 *  - `viewID`: A map to store string identifiers to integer resource ID mappings, potentially
 *    useful for views generated dynamically where IDs might not be statically available in `R`
 *    or for other custom ID management.
 *  - `delegate`: An optional arbitrary object that can be assigned to a view to handle events
 *    or other view-specific logic, promoting decoupling.
 *  - `bgDrawable`: An optional [GradientDrawable] that might be dynamically created and
 *    associated with the view for its background.
 *
 * Instances of this class are typically retrieved or created using the `View.getGeneratedViewInfo()`
 * extension function in `Utils.kt`.
 *
 * **Note on Thread Safety:**
 * While the KDoc for `viewID` previously mentioned "Thread-safe mapping", standard [HashMap] is not
 * thread-safe. It is assumed that modifications to `GeneratedView` instances (and the views
 * they are tagged to) occur on the main UI thread, which is standard practice for Android view manipulation.
 * If multi-threaded modification were a requirement, `viewID` would need to be a thread-safe map like
 * `ConcurrentHashMap`.
 *
 * @property viewID A [HashMap] mapping string identifiers (e.g., from a layout definition)
 *                  to integer resource IDs (e.g., `R.id.some_id`). Defaults to an empty HashMap.
 *                  Assumed to be accessed and modified on the main UI thread.
 * @property delegate An optional `Any` reference that can be used to delegate actions or events
 *                    from the view. Defaults to `null`.
 * @property bgDrawable An optional [GradientDrawable] that can be dynamically generated and
 *                      set as the view's background. Defaults to `null`.
 *
 * @author Abdelrahman Omar
 * @since 1.0.0
 */
package com.voyager.utils

import android.graphics.drawable.GradientDrawable
import java.util.HashMap // Explicit import for clarity, though often not needed for HashMap

/**
 * Internal data class holding Voyager-specific information associated with a [android.view.View].
 * This data is typically stored in the [android.view.View]'s tag.
 *
 * @see Utils.getGeneratedViewInfo
 */
internal data class GeneratedView(
    /**
     * A map for storing string-to-integer ID mappings. For example, mapping a string ID
     * from a dynamic layout definition to an actual Android resource ID.
     * Expected to be accessed and modified on the main UI thread.
     */
    var viewID: HashMap<String, Int> = HashMap(),

    /**
     * An optional delegate object associated with the view. This can be used for various
     * purposes, such as handling callbacks, events, or custom view logic, allowing for
     * better separation of concerns.
     */
    var delegate: Any? = null,

    /**
     * An optional [GradientDrawable] that may be dynamically created and associated with
     * this view, often for setting its background.
     */
    var bgDrawable: GradientDrawable? = null
)