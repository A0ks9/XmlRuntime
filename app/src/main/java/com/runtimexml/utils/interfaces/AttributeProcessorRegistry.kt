package com.runtimexml.utils.interfaces

import android.view.View

fun interface AttributeProcessorRegistry<V : View, T> {
    fun apply(view: V, value: T)
}