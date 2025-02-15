package com.runtimexml.utils

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup

/**
 * Base view that extends View and provides a `getParentView()` function.
 */
class BaseView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    val realView: View = this

    /**
     * Safely gets the parent of this view as a ViewGroup.
     * @return The parent ViewGroup, or null if the parent is not a ViewGroup.
     */
    fun getParentView(): ViewGroup? {
        return parent as? ViewGroup
    }
}
