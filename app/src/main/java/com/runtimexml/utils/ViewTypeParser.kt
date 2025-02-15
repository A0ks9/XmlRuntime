package com.runtimexml.utils

import com.runtimexml.utils.processors.AttributeProcessor

abstract class ViewTypeParser : AttributeProcessor() {

    init {
        addAttributes()
    }

    protected abstract fun getViewType(): String
    protected abstract fun addAttributes()
}