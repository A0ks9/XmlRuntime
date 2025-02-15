package com.runtimexml.utils.interfaces

import com.runtimexml.utils.BaseView

fun interface AttributeProcessorRegistry<T> {
    fun apply(view: BaseView, value: T?)
}