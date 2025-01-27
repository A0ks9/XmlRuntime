package com.runtimexml.utils.interfaces

import android.view.View
import android.view.ViewGroup

fun interface ViewParamAction {
    fun apply(view: View?, value: String, parent: ViewGroup?, attrs: Map<String, String>)
}