/**
 * Extension functions and utilities for the Voyager framework.
 *
 * This file provides high-performance extensions for Android views and common operations,
 * with optimized memory usage and efficient caching mechanisms.
 *
 * Key features:
 * - Efficient view ID caching
 * - Optimized layout parameter handling
 * - Memory-efficient string operations
 * - Thread-safe operations
 * - Comprehensive view utilities
 *
 * Performance optimizations:
 * - Concurrent caching for view IDs
 * - Weak references for activity names
 * - Efficient string matching
 * - Optimized view traversal
 * - Reduced object creation
 *
 * Usage example:
 * ```kotlin
 * // View ID operations
 * val viewId = view.getViewID("my_view")
 * val foundView = view.findViewByIdString("my_view")
 *
 * // Layout parameter operations
 * view.setMargin(16)
 * view.setPadding(8)
 *
 * // String operations
 * val isBoolean = "true".isBoolean()
 * val isColor = "#FF0000".isColor()
 * ```
 *
 * @author Abdelrahman Omar
 * @since 1.0.0
 */

package com.voyager.utils

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import androidx.core.text.layoutDirection
import java.util.Locale
import java.util.WeakHashMap
import java.util.concurrent.ConcurrentHashMap

// Constants for string operations
private const val EMPTY_VIEW_ID_RESULT = -1
private const val MATCH_PARENT = "match_parent"
private const val FILL_PARENT = "fill_parent"
private const val WRAP_CONTENT = "wrap_content"

// Caching mechanisms
private val booleanValuesSet =
    hashSetOf("true", "false", "0", "1", "yes", "no", "t", "f", "on", "off")
private val viewIdRegex = Regex("^@\\+?(?:android:id/|id/|id\\\\/)?(.+)$")
private val viewIdCache = ConcurrentHashMap<String, Int>()
private val activityNameCache = WeakHashMap<Context, String>()

/**
 * Checks if a string represents a boolean value.
 *
 * @return true if the string represents a boolean value, false otherwise
 */
internal fun String.isBoolean(): Boolean = booleanValuesSet.contains(lowercase())

/**
 * Checks if a string represents a color value.
 *
 * @return true if the string represents a color value, false otherwise
 */
internal fun String.isColor(): Boolean = startsWith("#") || startsWith("@color/")

/**
 * Extracts the view ID from a string representation.
 *
 * @return The extracted view ID or an empty string if not found
 */
internal fun String.extractViewId(): String =
    viewIdRegex.find(this)?.groupValues?.getOrNull(1) ?: ""

/**
 * Gets a view ID from the view hierarchy using caching for better performance.
 *
 * @param id The ID to search for
 * @return The view ID or EMPTY_VIEW_ID_RESULT if not found
 */
internal fun View.getViewID(id: String): Int {
    val cacheKey = id + hashCode()
    return viewIdCache.getOrPut(cacheKey) {
        val queue = ArrayDeque<View>()
        queue.add(this)

        while (queue.isNotEmpty()) {
            val currentView = queue.removeFirst()
            (currentView.tag as? GeneratedView)?.viewID?.get(id)?.let { return@getOrPut it }

            if (currentView is ViewGroup) {
                for (i in 0 until currentView.childCount) {
                    queue.add(currentView.getChildAt(i))
                }
            }
        }
        EMPTY_VIEW_ID_RESULT
    }
}

/**
 * Gets the parent view of the current view.
 *
 * @return The parent ViewGroup or null if not found
 */
internal fun View.getParentView(): ViewGroup? = parent as? ViewGroup

/**
 * Converts a string to a layout parameter value.
 *
 * @param displayMetrics The display metrics for density conversion
 * @param parentView The parent view for relative measurements
 * @param isHorizontal Whether the parameter is for width (true) or height (false)
 * @return The converted layout parameter value
 */
internal fun String.toLayoutParam(
    displayMetrics: DisplayMetrics,
    parentView: ViewGroup?,
    isHorizontal: Boolean,
): Int = when (this) {
    MATCH_PARENT, FILL_PARENT -> ViewGroup.LayoutParams.MATCH_PARENT
    WRAP_CONTENT -> ViewGroup.LayoutParams.WRAP_CONTENT
    else -> toPixels(displayMetrics, parentView, isHorizontal, true) as Int
}

/**
 * Finds a view by its string ID.
 *
 * @param id The string ID of the view
 * @return The found view or null if not found
 */
fun View.findViewByIdString(id: String): View? =
    getViewID(id).takeIf { it != EMPTY_VIEW_ID_RESULT }?.let(::findViewById)

/**
 * Partitions a map into two maps based on a predicate.
 *
 * @param predicate The predicate to test each entry
 * @return A pair of maps containing the matching and non-matching entries
 */
internal fun <K, V> Map<K, V>.partition(predicate: (Map.Entry<K, V>) -> Boolean): Pair<Map<K, V>, Map<K, V>> =
    filter(predicate) to filterNot(predicate)

/**
 * Gets the activity name from a context.
 *
 * @return The activity name or "Unknown" if not found
 */
internal fun Context.getActivityName(): String = activityNameCache.getOrPut(this) {
    var currentContext: Context = this
    while (currentContext is ContextWrapper) {
        if (currentContext is Activity) return@getOrPut currentContext::class.java.simpleName
        currentContext = currentContext.baseContext
    }
    "Unknown"
}

/**
 * Sets all margins of a view to the same value.
 *
 * @param margin The margin value to apply
 */
internal fun View.setMargin(margin: Int) {
    (layoutParams as? ViewGroup.MarginLayoutParams)?.setMargins(margin, margin, margin, margin)
}

/**
 * Sets the left margin of a view.
 *
 * @param left The left margin value
 */
internal fun View.setMarginLeft(left: Int) {
    (layoutParams as? ViewGroup.MarginLayoutParams)?.leftMargin = left
}

/**
 * Sets the start margin of a view.
 *
 * @param start The start margin value
 */
internal fun View.setMarginStart(start: Int) {
    (layoutParams as? ViewGroup.MarginLayoutParams)?.marginStart = start
}

/**
 * Sets the right margin of a view.
 *
 * @param right The right margin value
 */
internal fun View.setMarginRight(right: Int) {
    (layoutParams as? ViewGroup.MarginLayoutParams)?.rightMargin = right
}

/**
 * Sets the end margin of a view.
 *
 * @param end The end margin value
 */
internal fun View.setMarginEnd(end: Int) {
    (layoutParams as? ViewGroup.MarginLayoutParams)?.marginEnd = end
}

/**
 * Sets the top margin of a view.
 *
 * @param top The top margin value
 */
internal fun View.setMarginTop(top: Int) {
    (layoutParams as? ViewGroup.MarginLayoutParams)?.topMargin = top
}

/**
 * Sets the bottom margin of a view.
 *
 * @param bottom The bottom margin value
 */
internal fun View.setMarginBottom(bottom: Int) {
    (layoutParams as? ViewGroup.MarginLayoutParams)?.bottomMargin = bottom
}

/**
 * Sets all padding of a view to the same value.
 *
 * @param padding The padding value to apply
 */
internal fun View.setPaddingE(padding: Int) {
    val isRtl = Locale.getDefault().layoutDirection == View.LAYOUT_DIRECTION_RTL
    if (isRtl) {
        setPaddingRelative(padding, padding, padding, padding)
    } else {
        setPadding(padding, padding, padding, padding)
    }
}

/**
 * Sets the top padding of a view.
 *
 * @param top The top padding value
 */
internal fun View.setPaddingTop(top: Int) {
    val isRtl = Locale.getDefault().layoutDirection == View.LAYOUT_DIRECTION_RTL
    if (isRtl) {
        setPaddingRelative(paddingStart, top, paddingEnd, paddingBottom)
    } else {
        setPadding(paddingLeft, top, paddingRight, paddingBottom)
    }
}

/**
 * Sets the bottom padding of a view.
 *
 * @param bottom The bottom padding value
 */
internal fun View.setPaddingBottom(bottom: Int) {
    val isRtl = Locale.getDefault().layoutDirection == View.LAYOUT_DIRECTION_RTL
    if (isRtl) {
        setPaddingRelative(paddingStart, paddingTop, paddingEnd, bottom)
    } else {
        setPadding(paddingLeft, paddingTop, paddingRight, bottom)
    }
}

/**
 * Sets the right padding of a view.
 *
 * @param right The right padding value
 */
internal fun View.setPaddingRight(right: Int) {
    setPadding(paddingLeft, paddingTop, right, paddingBottom)
}

/**
 * Sets the left padding of a view.
 *
 * @param left The left padding value
 */
internal fun View.setPaddingLeft(left: Int) {
    setPadding(left, paddingTop, paddingRight, paddingBottom)
}

/**
 * Sets the start padding of a view.
 *
 * @param start The start padding value
 */
internal fun View.setPaddingStart(start: Int) {
    setPaddingRelative(start, paddingTop, paddingEnd, paddingBottom)
}

/**
 * Sets the end padding of a view.
 *
 * @param end The end padding value
 */
internal fun View.setPaddingEnd(end: Int) {
    setPaddingRelative(paddingStart, paddingTop, end, paddingBottom)
}

/**
 * Sets the horizontal padding of a view.
 *
 * @param padding The horizontal padding value
 */
internal fun View.setPaddingHorizontal(padding: Int) {
    val isRtl = Locale.getDefault().layoutDirection == View.LAYOUT_DIRECTION_RTL
    if (isRtl) {
        setPaddingRelative(padding, paddingTop, padding, paddingBottom)
    } else {
        setPadding(padding, paddingTop, padding, paddingBottom)
    }
}

/**
 * Sets the vertical padding of a view.
 *
 * @param padding The vertical padding value
 */
internal fun View.setPaddingVertical(padding: Int) {
    val isRtl = Locale.getDefault().layoutDirection == View.LAYOUT_DIRECTION_RTL
    if (isRtl) {
        setPaddingRelative(paddingStart, padding, paddingEnd, padding)
    } else {
        setPadding(paddingLeft, padding, paddingRight, padding)
    }
}