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
 * - Thread-safe operations
 * - Performance optimized
 * - Memory efficient
 * - Comprehensive error handling
 *
 * Performance Optimizations:
 * - ConcurrentHashMap for thread-safe ID mapping
 * - Efficient memory management
 * - Minimal object creation
 * - Safe resource handling
 *
 * Best Practices:
 * 1. Always access and modify on the main UI thread
 * 2. Clear delegates when views are recycled
 * 3. Handle null cases appropriately
 * 4. Use appropriate ID mapping strategies
 * 5. Consider memory leaks
 * 6. Use thread-safe operations
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
import android.view.View
import com.voyager.core.utils.logging.LoggerFactory
import com.voyager.core.view.utils.ViewExtensions.getGeneratedViewInfo
import java.util.concurrent.ConcurrentHashMap

/**
 * Internal data class holding Voyager-specific information associated with a [View].
 * This data is typically stored in the [View]'s tag.
 *
 * @see getGeneratedViewInfo
 */
internal data class GeneratedView(
    /**
     * A thread-safe map for storing string-to-integer ID mappings. This is particularly useful for
     * views generated dynamically where IDs might not be statically available in `R.id`.
     *
     * Common Use Cases:
     * - Mapping string IDs from layout definitions to resource IDs
     * - Storing dynamic view references
     * - Managing custom view identification
     *
     * Thread Safety:
     * - Thread-safe for concurrent access using ConcurrentHashMap
     * - Safe to use from any thread
     * - Atomic operations for ID mapping
     *
     * Performance Considerations:
     * - Efficient concurrent access
     * - Minimal memory overhead
     * - Safe resource handling
     */
    var viewID: ConcurrentHashMap<String, Int> = ConcurrentHashMap(),

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
     * Thread Safety:
     * - Should be accessed and modified on the main UI thread
     * - Consider using volatile or AtomicReference for thread safety
     * - Clear when view is recycled
     *
     * Memory Management:
     * - Clear delegate when view is recycled
     * - Use weak references if needed
     * - Consider memory leaks
     */
    @Volatile
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
     * Thread Safety:
     * - Should be accessed and modified on the main UI thread
     * - Consider using volatile or AtomicReference for thread safety
     * - Clear when view is recycled
     *
     * Performance Considerations:
     * - Creating new drawables is expensive
     * - Consider reusing drawables when possible
     * - Clear when view is recycled
     * - Use appropriate caching strategies
     */
    @Volatile
    var bgDrawable: GradientDrawable? = null
) {
    private val logger = LoggerFactory.getLogger(GeneratedView::class.java.simpleName)

    /**
     * Clears all resources associated with this view.
     * Should be called when the view is recycled.
     * Thread-safe operation.
     */
    fun clear() {
        try {
            viewID.clear()
            delegate = null
            bgDrawable = null
            logger.debug("clear", "GeneratedView resources cleared")
        } catch (e: Exception) {
            logger.error("clear", "Failed to clear GeneratedView resources: ${e.message}")
        }
    }

    /**
     * Checks if the view has any associated resources.
     * Thread-safe operation.
     *
     * @return true if the view has no resources, false otherwise
     */
    fun isEmpty(): Boolean = try {
        viewID.isEmpty() && delegate == null && bgDrawable == null
    } catch (e: Exception) {
        logger.error("isEmpty", "Failed to check if GeneratedView is empty: ${e.message}")
        true
    }
}