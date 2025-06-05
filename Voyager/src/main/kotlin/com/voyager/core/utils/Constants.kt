package com.voyager.core.utils

/**
 * Internal object holding string constants used throughout the Voyager framework.
 * Constants are organized by category for better maintainability.
 *
 * Key Features:
 * - Categorized constants
 * - Thread-safe access
 * - Memory efficient
 * - Performance optimized
 *
 * Categories:
 * - Visibility
 * - Alignment
 * - Layout
 * - Text
 * - Image
 * - Style
 * - Behavior
 * - Dimensions
 * - Attributes
 *
 * Best Practices:
 * 1. Use appropriate constants
 * 2. Consider memory usage
 * 3. Use thread-safe operations
 * 4. Consider performance impact
 *
 * Example Usage:
 * ```kotlin
 * // Set visibility
 * view.visibility = Constants.VISIBLE
 *
 * // Set alignment
 * view.gravity = Constants.CENTER
 *
 * // Set layout
 * view.layoutParams = Constants.MATCH_PARENT
 * ```
 */
internal object Constants {
    // Visibility constants
    const val VISIBLE = "visible"
    const val INVISIBLE = "invisible"
    const val GONE = "gone"

    // Alignment constants
    const val CENTER = "center"
    const val CENTER_HORIZONTAL = "center_horizontal"
    const val CENTER_VERTICAL = "center_vertical"
    const val LEFT = "left"
    const val RIGHT = "right"
    const val TOP = "top"
    const val BOTTOM = "bottom"
    const val START = "start"
    const val END = "end"

    // Layout constants
    const val FILL = "fill"
    const val FILL_VERTICAL = "fill_vertical"
    const val FILL_HORIZONTAL = "fill_horizontal"
    const val CLIP_VERTICAL = "clip_vertical"
    const val CLIP_HORIZONTAL = "clip_horizontal"
    const val MATCH_PARENT = "match_parent"
    const val FILL_PARENT = "fill_parent"
    const val WRAP_CONTENT = "wrap_content"

    // Text constants
    const val MIDDLE = "middle"
    const val BEGINNING = "beginning"
    const val MARQUEE = "marquee"
    const val BOLD = "bold"
    const val ITALIC = "italic"
    const val BOLD_ITALIC = "bold|italic"
    const val TEXT = "text"
    const val HINT = "hint"

    // Text alignment constants
    const val TEXT_ALIGNMENT_INHERIT = "inherit"
    const val TEXT_ALIGNMENT_GRAVITY = "gravity"
    const val TEXT_ALIGNMENT_CENTER = "center"
    const val TEXT_ALIGNMENT_TEXT_START = "textStart"
    const val TEXT_ALIGNMENT_TEXT_END = "textEnd"
    const val TEXT_ALIGNMENT_VIEW_START = "viewStart"
    const val TEXT_ALIGNMENT_VIEW_END = "viewEnd"

    // Image constants
    const val CENTER_CROP = "centerCrop"
    const val CENTER_INSIDE = "centerInside"
    const val FIT_CENTER = "fitCenter"
    const val FIT_END = "fitEnd"
    const val FIT_START = "fitStart"
    const val FIT_XY = "fitXY"
    const val MATRIX = "matrix"

    // Style constants
    const val ADD = "add"
    const val MULTIPLY = "multiply"
    const val SCREEN = "screen"
    const val SRC_ATOP = "src_atop"
    const val SRC_IN = "src_in"
    const val SRC_OVER = "src_over"

    // Behavior constants
    const val AUTO = "auto"
    const val HIGH = "high"
    const val LOW = "low"
    const val ALWAYS = "always"
    const val IF_CONTENT_SCROLLS = "ifContentScrolls"
    const val NEVER = "never"
    const val YES = "yes"
    const val NO = "no"
    const val NO_HIDE_DESCENDANTS = "noHideDescendants"

    // Dimension constants
    const val MARGIN = "margin"
    const val MAX_WIDTH = "maxWidth"
    const val MAX_HEIGHT = "maxHeight"

    // Shadow constants
    const val SHADOW_COLOR = "shadowColor"
    const val SHADOW_RADIUS = "shadowRadius"
    const val SHADOW_DX = "shadowDx"
    const val SHADOW_DY = "shadowDy"

    // Scroll constants
    const val SCROLLBARS = "scrollbars"
    const val OVER_SCROLL_MODE = "overScrollMode"

    // Animation constants
    const val TWEEN_LOCAL_RESOURCE_STR = "@anim/"

    /**
     * Checks if a string matches any of the visibility constants.
     * Thread-safe operation.
     *
     * @param value The string to check
     * @return true if the string matches a visibility constant, false otherwise
     */
    fun isVisibilityConstant(value: String): Boolean {
        return value == VISIBLE || value == INVISIBLE || value == GONE
    }

    /**
     * Checks if a string matches any of the alignment constants.
     * Thread-safe operation.
     *
     * @param value The string to check
     * @return true if the string matches an alignment constant, false otherwise
     */
    fun isAlignmentConstant(value: String): Boolean {
        return value == CENTER || value == CENTER_HORIZONTAL || value == CENTER_VERTICAL ||
                value == LEFT || value == RIGHT || value == TOP || value == BOTTOM ||
                value == START || value == END
    }

    /**
     * Checks if a string matches any of the layout constants.
     * Thread-safe operation.
     *
     * @param value The string to check
     * @return true if the string matches a layout constant, false otherwise
     */
    fun isLayoutConstant(value: String): Boolean {
        return value == MATCH_PARENT || value == FILL_PARENT || value == WRAP_CONTENT
    }
} 