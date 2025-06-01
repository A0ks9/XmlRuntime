package com.voyager.core.view

import android.view.View
import androidx.appcompat.view.ContextThemeWrapper
import java.util.concurrent.ConcurrentHashMap

object CustomViewRegistry {
    private val customCreators = ConcurrentHashMap<String, (ContextThemeWrapper) -> View>()

    fun registerView(type: String, creator: (ContextThemeWrapper) -> View) {
        customCreators[type] = creator
    }

    fun createView(context: ContextThemeWrapper, type: String): View? {
        return customCreators[type]?.invoke(context)
    }
} 