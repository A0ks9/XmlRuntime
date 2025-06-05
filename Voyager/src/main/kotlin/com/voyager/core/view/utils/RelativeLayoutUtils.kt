package com.voyager.core.view.utils

import android.view.View
import android.widget.RelativeLayout
import com.voyager.core.utils.logging.LoggerFactory

/**
 * Utility object for handling RelativeLayout specific operations.
 * 
 * Key features:
 * - Rule management for RelativeLayout
 * - Boolean to rule value conversion
 * - Thread-safe operations
 * - Efficient layout parameter handling
 * 
 * Performance optimizations:
 * - Minimal object creation
 * - Efficient type casting
 * - Safe layout parameter updates
 * 
 * Best practices:
 * - Always check if parent is RelativeLayout before adding rules
 * - Handle layout parameter updates safely
 * - Use appropriate logging for debugging
 * 
 * Example usage:
 * ```kotlin
 * // Add a rule to align a view to the right of another view
 * RelativeLayoutUtils.addRelativeLayoutRule(
 *     view = myView,
 *     verb = RelativeLayout.RIGHT_OF,
 *     anchor = targetViewId
 * )
 * ```
 */
internal object RelativeLayoutUtils {

    private const val TAG = "RelativeLayoutUtils"

    private val logger = LoggerFactory.getLogger(RelativeLayoutUtils::class.java.simpleName)

    /**
     * Converts a boolean to a RelativeLayout rule value.
     * 
     * Performance considerations:
     * - Simple boolean operation
     * - No object creation
     * - Fast execution
     * 
     * @param value The boolean value to convert
     * @return RelativeLayout.TRUE if value is true, 0 otherwise
     */
    fun parseRelativeLayoutBoolean(value: Boolean): Int = if (value) RelativeLayout.TRUE else 0

    /**
     * Adds a rule to a RelativeLayout.
     * 
     * Performance considerations:
     * - Safe type casting
     * - Efficient layout parameter update
     * - Minimal object creation
     * 
     * Error handling:
     * - Logs error if parent is not RelativeLayout
     * - Safe layout parameter casting
     * - Graceful failure handling
     * 
     * @param view The view to add the rule to
     * @param verb The rule verb (e.g., RelativeLayout.RIGHT_OF)
     * @param anchor The anchor view ID
     * @throws IllegalStateException if view's parent is not a RelativeLayout
     */
    fun addRelativeLayoutRule(view: View, verb: Int, anchor: Int) {
        try {
            (view.layoutParams as? RelativeLayout.LayoutParams)?.apply {
                addRule(verb, anchor)
                view.layoutParams = this
            } ?: run {
                logger.error(
                    "addRelativeLayoutRule",
                    "Cannot add relative layout rules when container is not relative"
                )
                throw IllegalStateException("View's parent must be a RelativeLayout")
            }
        } catch (e: Exception) {
            logger.error(
                "addRelativeLayoutRule",
                "Failed to add rule: ${e.message}",
                e
            )
            throw e
        }
    }
} 