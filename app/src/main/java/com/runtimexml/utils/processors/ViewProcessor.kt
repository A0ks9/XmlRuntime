package com.runtimexml.utils.processors

import android.content.Context
import android.view.View

class ViewProcessor {

    companion object {
        private val viewCreators = mutableMapOf<String, (Context) -> View>()

        @JvmStatic
        fun <T> register(clazz: Class<T>, creator: (Context) -> View) {
            val className = clazz.simpleName
            viewCreators[className] = creator
        }

        @JvmStatic
        fun createView(className: String, context: Context): View? =
            viewCreators[className]?.invoke(context)
    }
}