package com.runtimexml.utils

import android.view.View
import android.view.ViewGroup
import java.util.WeakHashMap

internal object ParentViewReference {
    private var parentRef = WeakHashMap<View, ViewGroup?>()

    fun storeParentView(view: View, viewParent: ViewGroup?) {
        val parent = view.parent as? ViewGroup
        if (parent != null && parent == viewParent) {
            parentRef.put(view, parent)
        } else {
            parentRef.put(view, viewParent)
        }
    }

    fun getParentView(view: View): ViewGroup? = parentRef[view]
}