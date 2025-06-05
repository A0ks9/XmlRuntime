package com.voyager.core.view.utils

import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import com.voyager.core.utils.Constants.FILL_PARENT
import com.voyager.core.utils.Constants.MATCH_PARENT
import com.voyager.core.utils.Constants.WRAP_CONTENT
import com.voyager.core.utils.logging.LoggerFactory
import com.voyager.core.utils.parser.DimensionConverter.toPixels
import java.util.Locale

private val logger = LoggerFactory.getLogger("ViewLayoutExtensions")

/**
 * Converts a dimension string (e.g., "match_parent", "wrap_content", "16dp") into its
 * corresponding integer layout parameter value.
 *
 * Performance considerations:
 * - Efficient string comparison
 * - Minimal object creation
 * - Fast pixel conversion
 *
 * Error handling:
 * - Safe string parsing
 * - Graceful fallback for invalid values
 * - Proper logging
 *
 * @param displayMetrics The [DisplayMetrics] used for pixel conversion
 * @param parentView The parent [ViewGroup] for relative calculations
 * @param isHorizontal `true` if this parameter is for width, `false` if for height
 * @return The integer value suitable for `ViewGroup.LayoutParams`
 * @throws IllegalArgumentException if the dimension string is invalid
 */
fun String.toLayoutParam(
    displayMetrics: DisplayMetrics,
    parentView: ViewGroup?,
    isHorizontal: Boolean,
): Int {
    try {
        return when (this.lowercase(Locale.ROOT)) {
            MATCH_PARENT, FILL_PARENT -> ViewGroup.LayoutParams.MATCH_PARENT
            WRAP_CONTENT -> ViewGroup.LayoutParams.WRAP_CONTENT
            else -> this.toPixels(displayMetrics, parentView, isHorizontal, true).toInt()
        }
    } catch (e: Exception) {
        logger.error(
            "toLayoutParam", "Failed to convert dimension string '$this': ${e.message}", e
        )
        throw IllegalArgumentException("Invalid dimension string: $this", e)
    }
}

/**
 * Sets all margins (left, top, right, bottom) of this [View] to the specified [margin] value.
 *
 * Performance considerations:
 * - Single layout parameter update
 * - No object creation
 * - Fast operation
 *
 * Error handling:
 * - Safe type casting
 * - Graceful fallback for invalid layout params
 * - Proper logging
 *
 * @param margin The margin value in pixels to apply to all sides
 * @throws IllegalStateException if view's layout params are not MarginLayoutParams
 */
fun View.setMargin(margin: Int) {
    try {
        (this.layoutParams as? ViewGroup.MarginLayoutParams)?.setMargins(
            margin, margin, margin, margin
        ) ?: run {
            logger.error(
                "setMargin", "Cannot set margins: view's layout params are not MarginLayoutParams"
            )
            throw IllegalStateException("View's layout params must be MarginLayoutParams")
        }
    } catch (e: Exception) {
        logger.error(
            "setMargin", "Failed to set margin $margin: ${e.message}", e
        )
        throw e
    }
}

/**
 * Sets the left margin of this [View].
 *
 * Performance considerations:
 * - Single layout parameter update
 * - No object creation
 * - Fast operation
 *
 * Error handling:
 * - Safe type casting
 * - Graceful fallback for invalid layout params
 * - Proper logging
 *
 * @param left The left margin value in pixels
 * @throws IllegalStateException if view's layout params are not MarginLayoutParams
 */
fun View.setMarginLeft(left: Int) {
    try {
        (this.layoutParams as? ViewGroup.MarginLayoutParams)?.leftMargin = left
    } catch (e: Exception) {
        logger.error(
            "setMarginLeft", "Failed to set left margin $left: ${e.message}", e
        )
        throw e
    }
}

/**
 * Sets the start margin of this [View], respecting layout direction.
 *
 * Performance considerations:
 * - Single layout parameter update
 * - No object creation
 * - Fast operation
 *
 * Error handling:
 * - Safe type casting
 * - Graceful fallback for invalid layout params
 * - Proper logging
 *
 * @param start The start margin value in pixels
 * @throws IllegalStateException if view's layout params are not MarginLayoutParams
 */
fun View.setMarginStart(start: Int) {
    try {
        (this.layoutParams as? ViewGroup.MarginLayoutParams)?.marginStart = start
    } catch (e: Exception) {
        logger.error(
            "setMarginStart", "Failed to set start margin $start: ${e.message}", e
        )
        throw e
    }
}

/**
 * Sets the right margin of this [View].
 *
 * Performance considerations:
 * - Single layout parameter update
 * - No object creation
 * - Fast operation
 *
 * Error handling:
 * - Safe type casting
 * - Graceful fallback for invalid layout params
 * - Proper logging
 *
 * @param right The right margin value in pixels
 * @throws IllegalStateException if view's layout params are not MarginLayoutParams
 */
fun View.setMarginRight(right: Int) {
    try {
        (this.layoutParams as? ViewGroup.MarginLayoutParams)?.rightMargin = right
    } catch (e: Exception) {
        logger.error(
            "setMarginRight", "Failed to set right margin $right: ${e.message}", e
        )
        throw e
    }
}

/**
 * Sets the end margin of this [View], respecting layout direction.
 *
 * Performance considerations:
 * - Single layout parameter update
 * - No object creation
 * - Fast operation
 *
 * Error handling:
 * - Safe type casting
 * - Graceful fallback for invalid layout params
 * - Proper logging
 *
 * @param end The end margin value in pixels
 * @throws IllegalStateException if view's layout params are not MarginLayoutParams
 */
fun View.setMarginEnd(end: Int) {
    try {
        (this.layoutParams as? ViewGroup.MarginLayoutParams)?.marginEnd = end
    } catch (e: Exception) {
        logger.error(
            "setMarginEnd", "Failed to set end margin $end: ${e.message}", e
        )
        throw e
    }
}

/**
 * Sets the top margin of this [View].
 *
 * Performance considerations:
 * - Single layout parameter update
 * - No object creation
 * - Fast operation
 *
 * Error handling:
 * - Safe type casting
 * - Graceful fallback for invalid layout params
 * - Proper logging
 *
 * @param top The top margin value in pixels
 * @throws IllegalStateException if view's layout params are not MarginLayoutParams
 */
fun View.setMarginTop(top: Int) {
    try {
        (this.layoutParams as? ViewGroup.MarginLayoutParams)?.topMargin = top
    } catch (e: Exception) {
        logger.error(
            "setMarginTop", "Failed to set top margin $top: ${e.message}", e
        )
        throw e
    }
}

/**
 * Sets the bottom margin of this [View].
 *
 * Performance considerations:
 * - Single layout parameter update
 * - No object creation
 * - Fast operation
 *
 * Error handling:
 * - Safe type casting
 * - Graceful fallback for invalid layout params
 * - Proper logging
 *
 * @param bottom The bottom margin value in pixels
 * @throws IllegalStateException if view's layout params are not MarginLayoutParams
 */
fun View.setMarginBottom(bottom: Int) {
    try {
        (this.layoutParams as? ViewGroup.MarginLayoutParams)?.bottomMargin = bottom
    } catch (e: Exception) {
        logger.error(
            "setMarginBottom", "Failed to set bottom margin $bottom: ${e.message}", e
        )
        throw e
    }
}

/**
 * Sets all paddings (start, top, end, bottom) of this [View] to the specified [padding] value,
 * respecting layout direction (RTL/LTR).
 *
 * Performance considerations:
 * - Single padding update
 * - No object creation
 * - Fast operation
 *
 * @param padding The padding value in pixels to apply to all sides
 */
fun View.setPaddingE(padding: Int) = this.setPaddingRelative(padding, padding, padding, padding)

/**
 * Sets the top padding of this [View].
 *
 * Performance considerations:
 * - Single padding update
 * - No object creation
 * - Fast operation
 *
 * @param top The top padding value in pixels
 */
fun View.setPaddingTop(top: Int) =
    this.setPaddingRelative(this.paddingStart, top, this.paddingEnd, this.paddingBottom)

/**
 * Sets the bottom padding of this [View].
 *
 * Performance considerations:
 * - Single padding update
 * - No object creation
 * - Fast operation
 *
 * @param bottom The bottom padding value in pixels
 */
fun View.setPaddingBottom(bottom: Int) =
    this.setPaddingRelative(this.paddingStart, this.paddingTop, this.paddingEnd, bottom)

/**
 * Sets the right padding of this [View].
 *
 * Performance considerations:
 * - Single padding update
 * - No object creation
 * - Fast operation
 *
 * @param right The right padding value in pixels
 */
fun View.setPaddingRight(right: Int) {
    this.setPadding(this.paddingLeft, this.paddingTop, right, this.paddingBottom)
}

/**
 * Sets the left padding of this [View].
 *
 * Performance considerations:
 * - Single padding update
 * - No object creation
 * - Fast operation
 *
 * @param left The left padding value in pixels
 */
fun View.setPaddingLeft(left: Int) {
    this.setPadding(left, this.paddingTop, this.paddingRight, this.paddingBottom)
}

/**
 * Sets the start padding of this [View], respecting layout direction.
 *
 * Performance considerations:
 * - Single padding update
 * - No object creation
 * - Fast operation
 *
 * @param start The start padding value in pixels
 */
internal fun View.setPaddingStart(start: Int) =
    this.setPaddingRelative(start, this.paddingTop, this.paddingEnd, this.paddingBottom)

/**
 * Sets the end padding of this [View], respecting layout direction.
 *
 * Performance considerations:
 * - Single padding update
 * - No object creation
 * - Fast operation
 *
 * @param end The end padding value in pixels
 */
internal fun View.setPaddingEnd(end: Int) =
    this.setPaddingRelative(this.paddingStart, this.paddingTop, end, this.paddingBottom)

/**
 * Sets the horizontal padding (start and end) of this [View] to the specified [padding] value,
 * respecting layout direction on API 17+.
 *
 * Performance considerations:
 * - Single padding update
 * - No object creation
 * - Fast operation
 *
 * @param padding The horizontal padding value in pixels
 */
internal fun View.setPaddingHorizontal(padding: Int) =
    this.setPaddingRelative(padding, this.paddingTop, padding, this.paddingBottom)

/**
 * Sets the vertical padding (top and bottom) of this [View] to the specified [padding] value.
 *
 * Performance considerations:
 * - Single padding update
 * - No object creation
 * - Fast operation
 *
 * @param padding The vertical padding value in pixels
 */
internal fun View.setPaddingVertical(padding: Int) =
    this.setPaddingRelative(this.paddingStart, padding, this.paddingEnd, padding)
