package com.runtimexml.utils.interfaces

import android.content.Context
import android.os.Bundle
import android.view.ViewGroup
import com.runtimexml.utils.JsonCast
import com.runtimexml.utils.ViewHelper

//create a json file for the views and its instances and how it handled with the instance parameters to save the data across app close

interface ViewHandler {
    fun getContainerLayout(): ViewGroup?
    fun getJsonConfiguration(): JsonCast? = null
    fun onViewCreated(parentView: ViewGroup?) {}

    companion object {
        @JvmStatic
        fun init(
            viewHandler: ViewHandler,
            context: Context,
            extras: Bundle?,
            callback: (ViewGroup?) -> Unit
        ) {
            ViewHelper.init(viewHandler, context, extras, callback)
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
