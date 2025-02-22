package com.runtimexml.utils.interfaces

import android.content.Context
import android.os.Bundle
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.viewbinding.ViewBinding
import com.runtimexml.utils.JsonCast
import com.runtimexml.utils.ViewHelper

interface ViewHandler {
    fun getContainerLayout(): ViewGroup?
    fun getJsonConfiguration(): JsonCast?
    fun onViewCreated(parentView: ViewGroup?) {}

    companion object {
        @JvmStatic
        fun <T> initialize(
            binding: T,
            viewHandler: ViewHandler,
            context: Context,
            extras: Bundle?,
            callback: (T?) -> Unit
        ) where T : ViewDataBinding, T : ViewBinding {
            ViewHelper.init(binding, viewHandler, context, extras, callback)
        }

        @JvmStatic
        fun saveInstanceState(context: Context, outState: Bundle) {
            ViewHelper.saveInstanceState(context, outState)
        }

        @JvmStatic
        fun saveDataWithRoom(context: Context) {
            ViewHelper.saveDataWithRoom(context)
        }

        @JvmStatic
        fun setJsonConfiguration(json: JsonCast) {
            ViewHelper.setJsonConfiguration(json)
        }
    }
}