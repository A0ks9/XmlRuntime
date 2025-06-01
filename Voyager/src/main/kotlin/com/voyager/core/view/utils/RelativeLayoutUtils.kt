package com.voyager.core.view.utils

import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import com.voyager.core.utils.logging.LoggerFactory

/**
 * Utility object for handling RelativeLayout specific operations.
 */
internal object RelativeLayoutUtils {

    private const val TAG = "RelativeLayoutUtils"

    private val logger = LoggerFactory.getLogger(RelativeLayoutUtils::class.java.simpleName)

    /**
     * Converts a boolean to a RelativeLayout rule value.
     *
     * @param value The boolean value
     * @return The corresponding RelativeLayout rule value
     */
    fun parseRelativeLayoutBoolean(value: Boolean): Int = if (value) RelativeLayout.TRUE else 0

    /**
     * Adds a rule to a RelativeLayout.
     *
     * @param view The view to add the rule to
     * @param verb The rule verb
     * @param anchor The anchor view ID
     */
    fun addRelativeLayoutRule(view: View, verb: Int, anchor: Int) {
        (view.layoutParams as? RelativeLayout.LayoutParams)?.apply {
            addRule(verb, anchor)
            view.layoutParams = this
        } ?: logger.e(TAG, "cannot add relative layout rules when container is not relative")
    }
} 