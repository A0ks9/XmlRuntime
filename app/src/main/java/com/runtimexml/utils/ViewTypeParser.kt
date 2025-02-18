package com.runtimexml.utils

import com.runtimexml.utils.processors.AttributeRegistry

abstract class ViewTypeParser : AttributeRegistry() {

    init {
        addAttributes()
    }

    protected abstract fun getViewType(): String
    protected abstract fun addAttributes()
}