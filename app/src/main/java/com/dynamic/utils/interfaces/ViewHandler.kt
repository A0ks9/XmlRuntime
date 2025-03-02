package com.dynamic.utils.interfaces

import android.content.Context
import android.view.ViewGroup
import androidx.core.bundle.Bundle
import androidx.databinding.ViewDataBinding
import androidx.viewbinding.ViewBinding
import com.dynamic.utils.ViewHelper

interface ViewHandler {
    fun getContainerLayout(): ViewGroup?
    fun getJsonConfiguration(): String?
    fun onViewCreated(parentView: ViewGroup?) {}

    companion object {
        @JvmStatic
        fun <T> initialize(
            binding: T,
            viewHandler: ViewHandler,
            context: Context,
            theme: Int,
            extras: Bundle?,
            callback: (T?) -> Unit
        ) where T : ViewDataBinding, T : ViewBinding {
            ViewHelper.init(binding, viewHandler, context, theme, extras, callback)
        }

        @JvmStatic
        fun saveInstanceState(context: Context, outState: Bundle) {
            ViewHelper.saveInstanceState(outState)
        }

        @JvmStatic
        fun saveDataWithRoom(context: Context) {
            ViewHelper.saveDataWithRoom(context)
        }

        @JvmStatic
        fun setJsonConfiguration(json: String) {
            ViewHelper.setJsonConfiguration(json)
        }
    }
}