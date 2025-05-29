/**
 * `Utils` is an internal utility object providing core helper functions for view operations
 * and view hierarchy traversal within the Voyager framework.
 *
 * It includes methods for:
 * - Managing a [GeneratedView] object associated with a [View]'s tag to store Voyager-specific data.
 * - Efficiently iterating over a [ViewGroup]'s children.
 * - Resolving string-based view identifiers to integer resource IDs.
 *
 * Note: Reflection-based event handling utilities previously in this object have been moved to [ReflectionUtils].
 * JSON parsing functions were also removed earlier as parsing is now centralized in [com.voyager.data.models.ViewNodeParser].
 *
 * @author Abdelrahman Omar
 * @since 1.0.0
 */
package com.voyager.utils

import android.view.View
import android.view.ViewGroup
// GeneratedView is in the same package, no import needed.
// Explicit import can be: import com.voyager.utils.GeneratedView
import java.util.logging.Logger


/**
 * Internal utility object `Utils` providing core helper functions for view operations
 * and view hierarchy traversal.
 *
 * This object is intended for use only within the Voyager module.
 * Its scope has been reduced to focus on non-reflection view utilities.
 * For reflection-based event handling, see [ReflectionUtils].
 */
internal object Utils {

    private val logger by lazy { Logger.getLogger(Utils::class.java.name) }

    /**
     * Enumeration for specifying drawable positions within a view (e.g., TextView compound drawables).
     * This enum might be used by attribute handlers or view processors.
     */
    enum class DrawablePosition {
        /** Drawable at the start (left in LTR, right in RTL). */
        START,
        /** Drawable at the end (right in LTR, left in RTL). */
        END,
        /** Drawable at the top. */
        TOP,
        /** Drawable at the bottom. */
        BOTTOM
    }

    // Reflection-related methods and constants have been moved to ReflectionUtils.kt

    /**
     * Retrieves a [GeneratedView] instance associated with this [View].
     * If the view's tag does not already hold a [GeneratedView], a new one is created,
     * set as the view's tag, and then returned.
     *
     * [GeneratedView] is a simple data holder class typically used by Voyager to store
     * custom information related to a dynamically generated or processed view, such as
     * a delegate object for event handling or resolved view IDs.
     *
     * @return The existing or newly created [GeneratedView] instance from the view's tag.
     * @see [GeneratedView]
     */
    fun View.getGeneratedViewInfo(): GeneratedView =
     *
     * @return The existing GeneratedView or a new instance
     */
    fun View.getGeneratedViewInfo(): GeneratedView =
        (tag as? GeneratedView) ?: GeneratedView().also { tag = it }

    /**
     * Recursively searches for a view's integer ID based on a string identifier.
     *
     * This private utility function is used to find a runtime integer ID (e.g., an Android resource ID)
     * that corresponds to a string ID. The mapping from string ID to integer ID is expected
     * to be stored in the `viewID` map of a [GeneratedView] object, which should be
     * attached as a tag to the relevant views in the hierarchy.
     *
     * The search proceeds as follows:
     * 1. If the current `rootView` is not a [ViewGroup], the search cannot proceed further down
     *    this path, so -1 is returned (unless the ID is found directly on this non-ViewGroup view's tag).
     * 2. It checks the `rootView`'s own tag: if it's a [GeneratedView] instance and its
     *    `viewID` map contains the provided string `id`, the corresponding integer ID is returned.
     * 3. If not found on the `rootView` itself and if `rootView` is a `ViewGroup`, it recursively
     *    calls itself for each child of the `rootView` (obtained efficiently via [childrenSequence]).
     * 4. The function returns the first valid (non -1) integer ID found during the recursive search.
     * 5. If the string `id` is not found in the `viewID` map of any [GeneratedView] instance
     *    attached to views in the hierarchy starting from `rootView`, it returns -1.
     *
     * This mechanism is typically used by Voyager to resolve string-based ID references (e.g., from
     * layout definitions or attribute values like layout constraints) to actual runtime view IDs,
     * especially when views are generated dynamically.
     *
     * @param rootView The [View] from which to start the search. The search includes this view and,
     *                 if it's a [ViewGroup], all its descendants.
     * @param id The string identifier whose corresponding integer ID is being sought.
     * @return The integer ID (typically a resource ID) if found; otherwise, -1.
     * @see GeneratedView.viewID
     * @see View.getGeneratedViewInfo
     * @see childrenSequence for efficient child iteration.
     */
    private fun getViewID(rootView: View, id: String): Int {
        // Check current view's tag first, regardless of whether it's a ViewGroup or not.
        (rootView.tag as? GeneratedView)?.viewID?.get(id)?.let {
            logger.finer("getViewID: Found ID '$id' -> $it in tag of view: $rootView")
            return it // Found in current view's GeneratedView
        }

        // If not found in the current view's tag, and if it's a ViewGroup, search children.
        if (rootView is ViewGroup) {
            // Using a sequence for potentially more efficient short-circuiting if ID is found early.
            return rootView.childrenSequence()
                .mapNotNull { childView ->
                    val foundId = getViewID(childView, id) // Recursive call
                    if (foundId != -1) foundId else null
                }
                .firstOrNull() ?: -1 // Return first non-null (i.e., found ID), or -1 if not found in children
        }

        return -1 // Not found in this view (if not a ViewGroup or no tag match)
    }

    /**
     * Creates an efficient [Sequence] for iterating over the children of this [ViewGroup].
     *
     * This extension function provides a memory-efficient way to iterate through child views,
     * as it creates an iterator that fetches children one by one (`getChildAt(index++)`)
     * rather than allocating a new list or array of all children upfront (which `ViewGroup.children` extension from `androidx.core.view` does).
     * This can be beneficial in performance-sensitive loops or when dealing with ViewGroups
     * that might have a very large number of children.
     *
     * Usage:
     * ```kotlin
     * myViewGroup.childrenSequence().forEach { childView ->
     *     // process childView
     * }
     * ```
     *
     * @return A [Sequence] of [View] objects representing the children of this [ViewGroup].
     */
    private fun ViewGroup.childrenSequence(): Sequence<View> = object : Sequence<View> {
        override fun iterator(): Iterator<View> = object : Iterator<View> {
            private var currentIndex = 0
            override fun hasNext(): Boolean = currentIndex < childCount
            override fun next(): View = getChildAt(currentIndex++)
                ?: throw IndexOutOfBoundsException("Child at index $currentIndex does not exist.") // Should not happen if hasNext is true
        }
    }
}