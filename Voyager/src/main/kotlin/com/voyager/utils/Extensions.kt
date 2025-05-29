/**
 * This file contains Kotlin extension functions and utility properties for the Voyager framework,
 * enhancing Android's core classes like [View], [Context], [String], and [Map].
 *
 * These extensions provide convenient shortcuts and utility logic for common tasks such as:
 * - View operations: Finding views by string ID, managing layout parameters (margins, padding)
 *   with RTL awareness.
 * - String utilities: Checking for boolean or color representations, extracting view IDs from strings.
 * - Context utilities: Retrieving activity names with caching.
 * - Collection utilities: Partitioning maps.
 *
 * Several extensions incorporate caching mechanisms ([viewIdCache], [activityNameCache])
 * to optimize performance for repeated calls.
 *
 * @author Abdelrahman Omar
 * @since 1.0.0
 */
package com.voyager.utils

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import androidx.core.text.layoutDirection // Recommended for RTL support consistency
import java.util.Locale
import java.util.WeakHashMap
import java.util.concurrent.ConcurrentHashMap

// --- Constants for String to LayoutParam Conversion ---

/** Represents an invalid or not-found view ID, similar to [View.NO_ID]. */
private const val EMPTY_VIEW_ID_RESULT = -1 // Consistent with View.NO_ID which is typically -1

/** String representation for `ViewGroup.LayoutParams.MATCH_PARENT`. */
private const val MATCH_PARENT = "match_parent"
/** Legacy string representation for `ViewGroup.LayoutParams.MATCH_PARENT`. */
private const val FILL_PARENT = "fill_parent"
/** String representation for `ViewGroup.LayoutParams.WRAP_CONTENT`. */
private const val WRAP_CONTENT = "wrap_content"

// --- Caching Mechanisms ---

/**
 * A set of lowercase string values that are considered boolean representations.
 * Used by [String.isBoolean] for efficient checking.
 */
private val booleanValuesSet =
    hashSetOf("true", "false", "0", "1", "yes", "no", "t", "f", "on", "off")

/**
 * Regex used by [String.extractViewId] to parse string ID references like "@+id/name",
 * "@android:id/name", or simply "name". Captures the actual ID name.
 */
private val viewIdRegex = Regex("^@\\+?(?:android:id/|id/|id\\\\/)?(.+)$") // Added \\ for escaping in KDoc if needed

/**
 * A thread-safe cache for [View.getViewID].
 * Key: A composite key string combining the string ID and the hashcode of the View instance
 *      on which `getViewID` was called (e.g., "button_login" + view.hashCode()).
 *      This implies the cache is per-view-instance for a given string ID.
 * Value: The resolved integer view ID.
 */
private val viewIdCache = ConcurrentHashMap<String, Int>()

/**
 * A [WeakHashMap] to cache resolved activity names from [Context] instances.
 * Using [WeakHashMap] helps prevent memory leaks by allowing garbage collection of [Context] keys
 * if they are no longer strongly referenced elsewhere.
 * Key: [Context] instance.
 * Value: The simple class name of the Activity.
 */
private val activityNameCache = WeakHashMap<Context, String>()

// --- String Extension Functions ---

/**
 * Checks if this string represents a boolean value (case-insensitive).
 * It compares the lowercase string against a predefined set of common boolean representations
 * (e.g., "true", "false", "0", "1", "yes", "no").
 *
 * @return `true` if the string is recognized as a boolean value, `false` otherwise.
 */
internal fun String.isBoolean(): Boolean = booleanValuesSet.contains(this.lowercase(Locale.ROOT))

/**
 * Checks if this string likely represents a color value.
 * It currently checks if the string starts with "#" (for hex colors) or "@color/" (for color resources).
 *
 * @return `true` if the string format suggests a color value, `false` otherwise.
 */
internal fun String.isColor(): Boolean = this.startsWith("#") || this.startsWith("@color/")

/**
 * Extracts the actual ID name from a string that might represent a resource ID
 * (e.g., "@+id/submit_button", "@id/submit_button", "submit_button").
 *
 * It uses [viewIdRegex] to capture the ID name part.
 *
 * @return The extracted ID name (e.g., "submit_button") or an empty string if the
 *         input string does not match the expected ID format.
 */
internal fun String.extractViewId(): String =
    viewIdRegex.find(this)?.groupValues?.getOrNull(1) ?: ""

// --- View Extension Functions ---

/**
 * Retrieves an integer view ID associated with a given string identifier `id` for this [View].
 *
 * This function attempts to find a mapping for the string `id` to an integer ID
 * (presumably an Android resource ID) by checking a [GeneratedView] object stored in this
 * view's tag. The search is performed using a breadth-first traversal starting from this view.
 *
 * **Caching:**
 * Results are cached in [viewIdCache]. The cache key is a combination of the string `id`
 * and this view's `hashCode()`. This means the cache is specific to this particular [View] instance
 * when resolving the same string ID. This is useful if different view instances within a larger hierarchy
 * might have their own local string ID to integer ID mappings.
 *
 * **Mechanism (Breadth-First Search):**
 * 1. If the string `id` is found in the `GeneratedView` tag of the current view, its integer ID is returned.
 * 2. If not found, and if the current view is a [ViewGroup], its children are added to a queue.
 * 3. The search continues iteratively through the queue until the ID is found or the queue is empty.
 *
 * @param id The string identifier to look up (e.g., "button_confirm").
 * @return The resolved integer view ID if found in any [GeneratedView] tag within the hierarchy
 *         starting from this view; otherwise, returns [EMPTY_VIEW_ID_RESULT] (-1).
 * @see GeneratedView.viewID
 * @see Utils.getViewID for the private recursive implementation detail (though this one is iterative).
 */
internal fun View.getViewID(id: String): Int {
    // Cache key is specific to this View instance and the string ID.
    val cacheKey = id + this.hashCode().toString() // Ensure string concatenation
    return viewIdCache.getOrPut(cacheKey) {
        val queue = ArrayDeque<View>()
        queue.add(this) // Start BFS from the current view

        while (queue.isNotEmpty()) {
            val currentView = queue.removeFirst()
            // Check the tag of the current view being processed in BFS
            (currentView.tag as? GeneratedView)?.viewID?.get(id)?.let {
                return@getOrPut it // ID found in the tag of currentView
            }

            if (currentView is ViewGroup) {
                for (i in 0 until currentView.childCount) {
                    currentView.getChildAt(i)?.let { child -> queue.add(child) }
                }
            }
        }
        EMPTY_VIEW_ID_RESULT // ID not found in the hierarchy starting from this view
    }
}


/**
 * Safely retrieves the parent of this [View] as a [ViewGroup].
 *
 * @return The parent [ViewGroup] if it exists and is a ViewGroup, otherwise `null`.
 */
internal fun View.getParentView(): ViewGroup? = this.parent as? ViewGroup

/**
 * Converts a dimension string (e.g., "match_parent", "wrap_content", "16dp") into its
 * corresponding integer layout parameter value (e.g., `ViewGroup.LayoutParams.MATCH_PARENT`,
 * `ViewGroup.LayoutParams.WRAP_CONTENT`, or pixel value).
 *
 * Relies on an external `toPixels` function (not defined in this file) for converting
 * numeric/dp string values to pixels.
 *
 * @param displayMetrics The [DisplayMetrics] used for pixel conversion if the string represents a dimension.
 * @param parentView The parent [ViewGroup], potentially used by `toPixels` for context or relative calculations.
 * @param isHorizontal `true` if this parameter is for width, `false` if for height. This might be
 *                     used by `toPixels` for specific calculations.
 * @return The integer value suitable for `ViewGroup.LayoutParams` (e.g., `width`, `height`).
 * @see com.voyager.utils.Dimens.toPixels (Assuming toPixels is in Dimens.kt based on common practice)
 */
internal fun String.toLayoutParam(
    displayMetrics: DisplayMetrics,
    parentView: ViewGroup?, // Can be null if not needed by toPixels for non-relative dimensions
    isHorizontal: Boolean,
): Int = when (this.lowercase(Locale.ROOT)) { // Use ROOT locale for case-insensitivity of keywords
    MATCH_PARENT, FILL_PARENT -> ViewGroup.LayoutParams.MATCH_PARENT
    WRAP_CONTENT -> ViewGroup.LayoutParams.WRAP_CONTENT
    // Corrected to use the String.toPixels extension from DimensionConverter.kt
    else -> this.toPixels(displayMetrics, parentView, isHorizontal, true).toInt()
}

/**
 * Finds a descendant view of this [View] using a string identifier.
 *
 * This is a convenience function that first resolves the string `id` to an integer resource ID
 * using [View.getViewID] (which leverages Voyager's [GeneratedView] mechanism) and then calls
 * the standard [View.findViewById] with the resolved integer ID.
 *
 * @param id The string identifier of the view to find (e.g., "submitButton").
 * @return The found [View] if both the string ID is resolved to an integer ID and `findViewById`
 *         locates the view; otherwise, `null`.
 */
fun View.findViewByIdString(id: String): View? =
    this.getViewID(id).takeIf { it != EMPTY_VIEW_ID_RESULT }?.let { resolvedId ->
        this.findViewById(resolvedId)
    }

// --- Collection Extension Functions ---

/**
 * Partitions this [Map] into two separate maps based on a given [predicate].
 *
 * @param predicate A function that takes a [Map.Entry] and returns `true` if the entry
 *                  should be included in the first map (matching entries), and `false` if it
 *                  should be in the second map (non-matching entries).
 * @return A [Pair] where the first element is a map of entries for which the [predicate] returned `true`,
 *         and the second element is a map of entries for which it returned `false`.
 *         Uses [LinkedHashMap] to preserve original iteration order if applicable.
 */
internal fun <K, V> Map<K, V>.partition(predicate: (Map.Entry<K, V>) -> Boolean): Pair<Map<K, V>, Map<K, V>> {
    val first = LinkedHashMap<K, V>() // Preserve order if original map had it
    val second = LinkedHashMap<K, V>()
    for (entry in this) {
        if (predicate(entry)) {
            first[entry.key] = entry.value
        } else {
            second[entry.key] = entry.value
        }
    }
    return Pair(first, second)
}

// --- Context Extension Functions ---

/**
 * Retrieves the simple class name of the [Activity] associated with this [Context].
 *
 * It traverses up the [ContextWrapper] chain to find the base [Activity].
 * Results are cached in [activityNameCache] using the [Context] instance as a key (weakly referenced)
 * to improve performance on subsequent calls with the same context.
 *
 * @return The simple name of the Activity class (e.g., "MainActivity") if found,
 *         or the string "Unknown" if no Activity is found in the context chain.
 */
internal fun Context.getActivityName(): String = activityNameCache.getOrPut(this) {
    var currentContext: Context? = this
    while (currentContext is ContextWrapper) {
        if (currentContext is Activity) {
            return@getOrPut currentContext.javaClass.simpleName
        }
        currentContext = currentContext.baseContext
        if (currentContext == null) break // Safety break if baseContext chain ends unexpectedly
    }
    "Unknown" // Fallback if no Activity found
}

// --- View LayoutParams Extension Functions (Margins and Padding) ---

/**
 * Sets all margins (left, top, right, bottom) of this [View] to the specified [margin] value.
 * Assumes the view's `layoutParams` are an instance of [ViewGroup.MarginLayoutParams].
 * If `layoutParams` are not [ViewGroup.MarginLayoutParams], this function has no effect.
 *
 * @param margin The margin value in pixels to apply to all sides.
 */
internal fun View.setMargin(margin: Int) {
    (this.layoutParams as? ViewGroup.MarginLayoutParams)?.setMargins(margin, margin, margin, margin)
}

/**
 * Sets the left margin of this [View].
 * Assumes `layoutParams` are [ViewGroup.MarginLayoutParams].
 * @param left The left margin value in pixels.
 */
internal fun View.setMarginLeft(left: Int) {
    (this.layoutParams as? ViewGroup.MarginLayoutParams)?.leftMargin = left
}

/**
 * Sets the start margin of this [View], respecting layout direction.
 * Requires API level 17 ([android.os.Build.VERSION_CODES.JELLY_BEAN_MR1]) or higher.
 * Assumes `layoutParams` are [ViewGroup.MarginLayoutParams].
 * @param start The start margin value in pixels.
 */
internal fun View.setMarginStart(start: Int) {
    (this.layoutParams as? ViewGroup.MarginLayoutParams)?.marginStart = start
}

/**
 * Sets the right margin of this [View].
 * Assumes `layoutParams` are [ViewGroup.MarginLayoutParams].
 * @param right The right margin value in pixels.
 */
internal fun View.setMarginRight(right: Int) {
    (this.layoutParams as? ViewGroup.MarginLayoutParams)?.rightMargin = right
}

/**
 * Sets the end margin of this [View], respecting layout direction.
 * Requires API level 17 ([android.os.Build.VERSION_CODES.JELLY_BEAN_MR1]) or higher.
 * Assumes `layoutParams` are [ViewGroup.MarginLayoutParams].
 * @param end The end margin value in pixels.
 */
internal fun View.setMarginEnd(end: Int) {
    (this.layoutParams as? ViewGroup.MarginLayoutParams)?.marginEnd = end
}

/**
 * Sets the top margin of this [View].
 * Assumes `layoutParams` are [ViewGroup.MarginLayoutParams].
 * @param top The top margin value in pixels.
 */
internal fun View.setMarginTop(top: Int) {
    (this.layoutParams as? ViewGroup.MarginLayoutParams)?.topMargin = top
}

/**
 * Sets the bottom margin of this [View].
 * Assumes `layoutParams` are [ViewGroup.MarginLayoutParams].
 * @param bottom The bottom margin value in pixels.
 */
internal fun View.setMarginBottom(bottom: Int) {
    (this.layoutParams as? ViewGroup.MarginLayoutParams)?.bottomMargin = bottom
}

/**
 * Sets all paddings (start, top, end, bottom) of this [View] to the specified [padding] value,
 * respecting layout direction (RTL/LTR).
 * Uses `setPaddingRelative` for API 17+ ([android.os.Build.VERSION_CODES.JELLY_BEAN_MR1])
 * and `setPadding` for older versions (though API 21+ is the module target).
 *
 * @param padding The padding value in pixels to apply to all sides.
 */
internal fun View.setPaddingE(padding: Int) { // "E" for "Equal" or "Everywhere"
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
        this.setPaddingRelative(padding, padding, padding, padding)
    } else {
        this.setPadding(padding, padding, padding, padding)
    }
}

/**
 * Sets the top padding of this [View].
 * This function preserves existing start/end/bottom paddings and is RTL-aware on API 17+.
 * @param top The top padding value in pixels.
 */
internal fun View.setPaddingTop(top: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
        this.setPaddingRelative(this.paddingStart, top, this.paddingEnd, this.paddingBottom)
    } else {
        this.setPadding(this.paddingLeft, top, this.paddingRight, this.paddingBottom)
    }
}

/**
 * Sets the bottom padding of this [View].
 * This function preserves existing start/top/end paddings and is RTL-aware on API 17+.
 * @param bottom The bottom padding value in pixels.
 */
internal fun View.setPaddingBottom(bottom: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
        this.setPaddingRelative(this.paddingStart, this.paddingTop, this.paddingEnd, bottom)
    } else {
        this.setPadding(this.paddingLeft, this.paddingTop, this.paddingRight, this.paddingBottom)
    }
}

/**
 * Sets the right padding of this [View].
 * **Note:** For RTL-aware padding, prefer [setPaddingEnd]. This method directly sets absolute right padding.
 * @param right The right padding value in pixels.
 */
internal fun View.setPaddingRight(right: Int) {
    this.setPadding(this.paddingLeft, this.paddingTop, right, this.paddingBottom)
}

/**
 * Sets the left padding of this [View].
 * **Note:** For RTL-aware padding, prefer [setPaddingStart]. This method directly sets absolute left padding.
 * @param left The left padding value in pixels.
 */
internal fun View.setPaddingLeft(left: Int) {
    this.setPadding(left, this.paddingTop, this.paddingRight, this.paddingBottom)
}

/**
 * Sets the start padding of this [View], respecting layout direction.
 * Requires API level 17 ([android.os.Build.VERSION_CODES.JELLY_BEAN_MR1]) or higher for true start padding.
 * On older versions (though module target is 21+), it approximates by setting left/right based on LTR/RTL.
 * @param start The start padding value in pixels.
 */
internal fun View.setPaddingStart(start: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
        this.setPaddingRelative(start, this.paddingTop, this.paddingEnd, this.paddingBottom)
    } else {
        // Fallback for pre-API 17 (though target is 21+): set left if LTR, right if RTL.
        val isRtl = this.layoutDirection == View.LAYOUT_DIRECTION_RTL
        if (isRtl) this.setPadding(this.paddingLeft, this.paddingTop, start, this.paddingBottom) // Effectively end padding
        else this.setPadding(start, this.paddingTop, this.paddingRight, this.paddingBottom)
    }
}

/**
 * Sets the end padding of this [View], respecting layout direction.
 * Requires API level 17 ([android.os.Build.VERSION_CODES.JELLY_BEAN_MR1]) or higher for true end padding.
 * On older versions (though module target is 21+), it approximates by setting right/left based on LTR/RTL.
 * @param end The end padding value in pixels.
 */
internal fun View.setPaddingEnd(end: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
        this.setPaddingRelative(this.paddingStart, this.paddingTop, end, this.paddingBottom)
    } else {
        // Fallback for pre-API 17 (though target is 21+): set right if LTR, left if RTL.
        val isRtl = this.layoutDirection == View.LAYOUT_DIRECTION_RTL
        if (isRtl) this.setPadding(end, this.paddingTop, this.paddingRight, this.paddingBottom) // Effectively start padding
        else this.setPadding(this.paddingLeft, this.paddingTop, end, this.paddingBottom)
    }
}

/**
 * Sets the horizontal padding (start and end) of this [View] to the specified [padding] value,
 * respecting layout direction on API 17+.
 * @param padding The horizontal padding value in pixels.
 */
internal fun View.setPaddingHorizontal(padding: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
        this.setPaddingRelative(padding, this.paddingTop, padding, this.paddingBottom)
    } else {
        this.setPadding(padding, this.paddingTop, padding, this.paddingBottom)
    }
}

/**
 * Sets the vertical padding (top and bottom) of this [View] to the specified [padding] value.
 * @param padding The vertical padding value in pixels.
 */
internal fun View.setPaddingVertical(padding: Int) {
    // For vertical padding, RTL/LTR doesn't matter for top/bottom distinction.
    // However, to be consistent with using setPaddingRelative when available for clarity of intent:
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
        this.setPaddingRelative(this.paddingStart, padding, this.paddingEnd, padding)
    } else {
        this.setPadding(this.paddingLeft, padding, this.paddingRight, padding)
    }
}