/**
 * `GeneratedView` is an internal data class used to store Voyager-specific information
 * associated with an Android [android.view.View] instance, typically via the view's tag.
 *
 * This class serves as a metadata container for dynamically generated views, providing
 * a way to store additional information that isn't directly available through the standard
 * Android View API.
 *
 * Key Features:
 * - View ID mapping for dynamic layouts
 * - Event delegation support
 * - Dynamic background management
 * - Thread-safe usage on UI thread
 *
 * Best Practices:
 * 1. Always access and modify on the main UI thread
 * 2. Clear delegates when views are recycled
 * 3. Handle null cases appropriately
 * 4. Use appropriate ID mapping strategies
 *
 * Example Usage:
 * ```kotlin
 * // Get or create GeneratedView info
 * val viewInfo = myView.getGeneratedViewInfo()
 *
 * // Map a string ID to a resource ID
 * viewInfo.viewID["button_id"] = R.id.my_button
 *
 * // Set a delegate for event handling
 * viewInfo.delegate = MyViewDelegate()
 *
 * // Set a dynamic background
 * viewInfo.bgDrawable = GradientDrawable().apply {
 *     setColor(Color.BLUE)
 *     cornerRadius = 8f
 * }
 * ```
 *
 * @author Abdelrahman Omar
 * @since 1.0.0
 */
package com.voyager.core.view.model

import android.graphics.drawable.GradientDrawable
import java.util.HashMap
import android.view.View
import com.voyager.core.view.utils.ViewExtensions.getGeneratedViewInfo


/**
 * Internal data class holding Voyager-specific information associated with a [View].
 * This data is typically stored in the [View]'s tag.
 *
 * @see getGeneratedViewInfo
 */
internal data class GeneratedView(
    /**
     * A map for storing string-to-integer ID mappings. This is particularly useful for
     * views generated dynamically where IDs might not be statically available in `R.id`.
     *
     * Common Use Cases:
     * - Mapping string IDs from layout definitions to resource IDs
     * - Storing dynamic view references
     * - Managing custom view identification
     *
     * Thread Safety:
     * - Expected to be accessed and modified on the main UI thread
     * - Not thread-safe for concurrent access
     * - Consider using ConcurrentHashMap if multi-threaded access is needed
     */
    var viewID: HashMap<String, Int> = HashMap(),

    /**
     * An optional delegate object associated with the view. This can be used for various
     * purposes, such as handling callbacks, events, or custom view logic.
     *
     * Common Use Cases:
     * - Event handling delegation
     * - Custom view behavior implementation
     * - State management
     * - Callback handling
     *
     * Important Notes:
     * - Should be cleared when view is recycled
     * - Can be any type of object
     * - Consider memory leaks when using delegates
     */
    var delegate: Any? = null,

    /**
     * An optional [GradientDrawable] that may be dynamically created and associated with
     * this view, often for setting its background.
     *
     * Common Use Cases:
     * - Dynamic background creation
     * - Custom shape drawing
     * - Gradient effects
     * - Rounded corners
     *
     * Performance Considerations:
     * - Creating new drawables is expensive
     * - Consider reusing drawables when possible
     * - Clear when view is recycled
     */
    var bgDrawable: GradientDrawable? = null
)