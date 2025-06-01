package com.voyager.core.view.utils

import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import com.voyager.core.utils.Constants.FILL_PARENT
import com.voyager.core.utils.Constants.MATCH_PARENT
import com.voyager.core.utils.Constants.WRAP_CONTENT
import com.voyager.core.utils.parser.toPixels
import java.util.Locale


/**
 * Converts a dimension string (e.g., "match_parent", "wrap_content", "16dp") into its
 * corresponding integer layout parameter value.
 *
 * @param displayMetrics The [DisplayMetrics] used for pixel conversion.
 * @param parentView The parent [ViewGroup] for relative calculations.
 * @param isHorizontal `true` if this parameter is for width, `false` if for height.
 * @return The integer value suitable for `ViewGroup.LayoutParams`.
 */
fun String.toLayoutParam(
    displayMetrics: DisplayMetrics,
    parentView: ViewGroup?,
    isHorizontal: Boolean,
): Int = when (this.lowercase(Locale.ROOT)) {
    MATCH_PARENT, FILL_PARENT -> ViewGroup.LayoutParams.MATCH_PARENT
    WRAP_CONTENT -> ViewGroup.LayoutParams.WRAP_CONTENT
    else -> this.toPixels(displayMetrics, parentView, isHorizontal, true).toInt()
}

/**
 * Sets all margins (left, top, right, bottom) of this [View] to the specified [margin] value.
 *
 * @param margin The margin value in pixels to apply to all sides.
 */
fun View.setMargin(margin: Int) {
    (this.layoutParams as? ViewGroup.MarginLayoutParams)?.setMargins(
        margin, margin, margin, margin
    )
}

/**
 * Sets the left margin of this [View].
 *
 * @param left The left margin value in pixels.
 */
fun View.setMarginLeft(left: Int) {
    (this.layoutParams as? ViewGroup.MarginLayoutParams)?.leftMargin = left
}

/**
 * Sets the start margin of this [View], respecting layout direction.
 *
 * @param start The start margin value in pixels.
 */
fun View.setMarginStart(start: Int) {
    (this.layoutParams as? ViewGroup.MarginLayoutParams)?.marginStart = start
}

/**
 * Sets the right margin of this [View].
 *
 * @param right The right margin value in pixels.
 */
fun View.setMarginRight(right: Int) {
    (this.layoutParams as? ViewGroup.MarginLayoutParams)?.rightMargin = right
}

/**
 * Sets the end margin of this [View], respecting layout direction.
 *
 * @param end The end margin value in pixels.
 */
fun View.setMarginEnd(end: Int) {
    (this.layoutParams as? ViewGroup.MarginLayoutParams)?.marginEnd = end
}

/**
 * Sets the top margin of this [View].
 *
 * @param top The top margin value in pixels.
 */
fun View.setMarginTop(top: Int) {
    (this.layoutParams as? ViewGroup.MarginLayoutParams)?.topMargin = top
}

/**
 * Sets the bottom margin of this [View].
 *
 * @param bottom The bottom margin value in pixels.
 */
fun View.setMarginBottom(bottom: Int) {
    (this.layoutParams as? ViewGroup.MarginLayoutParams)?.bottomMargin = bottom
}

/**
 * Sets all paddings (start, top, end, bottom) of this [View] to the specified [padding] value,
 * respecting layout direction (RTL/LTR).
 *
 * @param padding The padding value in pixels to apply to all sides.
 */
fun View.setPaddingE(padding: Int) = this.setPaddingRelative(padding, padding, padding, padding)

/**
 * Sets the top padding of this [View].
 *
 * @param top The top padding value in pixels.
 */
fun View.setPaddingTop(top: Int) =
    this.setPaddingRelative(this.paddingStart, top, this.paddingEnd, this.paddingBottom)

/**
 * Sets the bottom padding of this [View].
 *
 * @param bottom The bottom padding value in pixels.
 */
fun View.setPaddingBottom(bottom: Int) =
    this.setPaddingRelative(this.paddingStart, this.paddingTop, this.paddingEnd, bottom)

/**
 * Sets the right padding of this [View].
 *
 * @param right The right padding value in pixels.
 */
fun View.setPaddingRight(right: Int) {
    this.setPadding(this.paddingLeft, this.paddingTop, right, this.paddingBottom)
}

/**
 * Sets the left padding of this [View].
 *
 * @param left The left padding value in pixels.
 */
fun View.setPaddingLeft(left: Int) {
    this.setPadding(left, this.paddingTop, this.paddingRight, this.paddingBottom)
}

/**
 * Sets the start padding of this [View], respecting layout direction.
 *
 * Performance Considerations:
 * - Single padding update
 * - No object creation
 * - Fast operation
 *
 * @param start The start padding value in pixels.
 */
internal fun View.setPaddingStart(start: Int) =
    this.setPaddingRelative(start, this.paddingTop, this.paddingEnd, this.paddingBottom)

/**
 * Sets the end padding of this [View], respecting layout direction.
 *
 * Performance Considerations:
 * - Single padding update
 * - No object creation
 * - Fast operation
 *
 * @param end The end padding value in pixels.
 */
internal fun View.setPaddingEnd(end: Int) =
    this.setPaddingRelative(this.paddingStart, this.paddingTop, end, this.paddingBottom)

/**
 * Sets the horizontal padding (start and end) of this [View] to the specified [padding] value,
 * respecting layout direction on API 17+.
 *
 * Performance Considerations:
 * - Single padding update
 * - No object creation
 * - Fast operation
 *
 * @param padding The horizontal padding value in pixels.
 */
internal fun View.setPaddingHorizontal(padding: Int) =
    this.setPaddingRelative(padding, this.paddingTop, padding, this.paddingBottom)

/**
 * Sets the vertical padding (top and bottom) of this [View] to the specified [padding] value.
 *
 * Performance Considerations:
 * - Single padding update
 * - No object creation
 * - Fast operation
 *
 * @param padding The vertical padding value in pixels.
 */
internal fun View.setPaddingVertical(padding: Int) =
    this.setPaddingRelative(this.paddingStart, padding, this.paddingEnd, padding)
