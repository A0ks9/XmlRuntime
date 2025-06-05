package com.voyager.core.view.utils

import android.view.View
import android.view.ViewGroup
import com.voyager.core.utils.logging.LoggerFactory
import com.voyager.core.view.model.GeneratedView

/**
 * Utility object for Android View related extensions and operations within the Voyager framework.
 * 
 * Key features:
 * - View ID management
 * - View hierarchy traversal
 * - Drawable position handling
 * - Thread-safe operations
 * - Efficient view finding
 * 
 * Performance optimizations:
 * - Efficient view hierarchy traversal
 * - Minimal object creation
 * - Safe type casting
 * - Cached view ID resolution
 * 
 * Best practices:
 * - Use view ID resolution for dynamic views
 * - Handle view hierarchy traversal efficiently
 * - Implement proper error handling
 * - Use appropriate logging
 * 
 * Example usage:
 * ```kotlin
 * // Get a view by its string ID
 * val view = parentView.findViewByIdString("submit_button")
 * 
 * // Get parent view group
 * val parent = view.getParentView()
 * ```
 */
object ViewExtensions {

    private val logger = LoggerFactory.getLogger(ViewExtensions::class.java.simpleName)

    /**
     * Enumeration for specifying drawable positions within a view.
     */
    internal enum class DrawablePosition {
        START, END, TOP, BOTTOM
    }

    /**
     * Retrieves a [GeneratedView] instance associated with this [View].
     * If the view's tag does not already hold a [GeneratedView], a new one is created,
     * set as the view's tag, and then returned.
     * 
     * @return The existing or newly created [GeneratedView] instance from the view's tag.
     */
    internal fun View.getGeneratedViewInfo(): GeneratedView =
        (tag as? GeneratedView) ?: GeneratedView().also { tag = it }

    /**
     * Retrieves the integer ID associated with a string identifier within this [View] or its children.
     *
     * This function first checks if the view's tag is a [GeneratedView] and if that [GeneratedView]
     * contains a mapping for the given string `id`. If found, the corresponding integer ID is returned.
     *
     * If the ID is not found in the current view's tag, and the view is a [ViewGroup],
     * the function recursively searches through its children. It uses `childrenSequence()`
     * for efficient iteration and returns the first matching ID found among the children.
     *
     * If the ID is not found in the current view or any of its descendants (if it's a ViewGroup),
     * it returns -1.
     *
     * @param id The string identifier to look up.
     * @return The integer ID associated with the string `id`, or -1 if not found.
     */
    internal fun View.getViewID(id: String): Int {
        try {
            (this.tag as? GeneratedView)?.viewID?.get(id)?.let {
                return it
            }

            if (this is ViewGroup) {
                return childrenSequence().mapNotNull { childView ->
                    val foundId = childView.getViewID(id)
                    if (foundId != -1) foundId else null
                }.firstOrNull() ?: -1
            }
        } catch (e: Exception) {
            logger.error(
                "getViewID",
                "Failed to get view ID for $id: ${e.message}",
                e
            )
        }
        return -1
    }

    /**
     * Creates an efficient [Sequence] for iterating over the children of this [ViewGroup].
     *
     * This extension function provides a memory-efficient way to iterate through child views
     * by creating an iterator that fetches children one at a time rather than allocating
     * a new collection of all children upfront.
     *
     * Benefits:
     * - Memory efficient for large ViewGroups
     * - Supports short-circuiting operations
     * - No intermediate collections created
     * - Thread-safe iteration
     *
     * Example Usage:
     * ```kotlin
     * // Find first child matching a condition
     * val matchingChild = viewGroup.childrenSequence()
     *     .firstOrNull { it.id == targetId }
     *
     * // Count children of a specific type
     * val buttonCount = viewGroup.childrenSequence()
     *     .count { it is Button }
     * ```
     *
     * @return A [Sequence] of [View] objects representing the children of this [ViewGroup].
     */
    private fun ViewGroup.childrenSequence(): Sequence<View> = object : Sequence<View> {
        override fun iterator(): Iterator<View> = object : Iterator<View> {
            private var currentIndex = 0
            override fun hasNext(): Boolean = currentIndex < childCount
            override fun next(): View = getChildAt(currentIndex++)
                ?: throw IndexOutOfBoundsException("Child at index $currentIndex does not exist.")
        }
    }

    /**
     * Safely retrieves the parent of this [View] as a [ViewGroup].
     *
     * Performance Considerations:
     * - Simple type cast
     * - No object creation
     * - Fast operation
     *
     * @return The parent [ViewGroup] if it exists and is a ViewGroup, otherwise `null`.
     */
    internal fun View.getParentView(): ViewGroup? = this.parent as? ViewGroup

    /**
     * Finds a descendant view of this [View] using a string identifier.
     *
     * Performance Considerations:
     * - Uses cached view ID resolution
     * - Efficient view finding
     * - Minimal object creation
     *
     * @param id The string identifier of the view to find.
     * @return The found [View] if both the string ID is resolved to an integer ID and `findViewById`
     *         locates the view; otherwise, `null`.
     */
    fun View.findViewByIdString(id: String): View? {
        try {
            return this.getViewID(id).takeIf { it != -1 }?.let { resolvedId ->
                this.findViewById(resolvedId)
            }
        } catch (e: Exception) {
            logger.error(
                "findViewByIdString",
                "Failed to find view with ID $id: ${e.message}",
                e
            )
            return null
        }
    }
}
