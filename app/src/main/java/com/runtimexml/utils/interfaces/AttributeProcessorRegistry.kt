package com.runtimexml.utils.interfaces

import android.view.View
import com.runtimexml.utils.BaseView

fun interface AttributeProcessorRegistry<T> {
    fun apply(view: View, value: T?)
}