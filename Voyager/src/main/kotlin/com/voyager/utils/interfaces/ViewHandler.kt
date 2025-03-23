package com.voyager.utils.interfaces

import android.content.Context
import android.os.Bundle
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.viewbinding.ViewBinding
import com.voyager.utils.ViewHelper

/**
 * Interface defining the required methods for handling views.
 */
interface ViewHandler {

    /**
     * Provides the container layout where dynamic views will be added.
     */
    fun getContainerLayout(): ViewGroup?

    /**
     * Retrieves the JSON configuration for dynamic view inflation.
     */
    fun getJsonConfiguration(): String?

    /**
     * Called after the view is created.
     */
    fun onViewCreated(parentView: ViewGroup?) {}

    companion object {

        /**
         * Initializes the ViewHelper with the given parameters.
         */
        @JvmStatic
        fun <T> initialize(
            binding: T,
            viewHandler: ViewHandler,
            context: Context,
            theme: Int,
            extras: Bundle?,
            provider: ResourcesProvider,
            callback: (ViewGroup?) -> Unit,
        ) where T : ViewDataBinding, T : ViewBinding {
            ViewHelper.Builder(context).setViewHandler(viewHandler).setTheme(theme)
                .setExtras(extras).setCallback(callback).setProvider(provider).build()
        }

        /**
         * Saves the current view state to the given [Bundle].
         */
        @JvmStatic
        fun saveInstanceState(outState: Bundle) {
            ViewHelper.saveCurrentViewNode(outState)

        }

        /**
         * Persists the current view state to the Room database.
         */
        @JvmStatic
        fun saveDataWithRoom(context: Context) {
            ViewHelper.saveViewNodeToRoom(context)
        }

        /**
         * Sets the JSON configuration for dynamic view inflation.
         */
        @JvmStatic
        fun setJsonConfiguration(jsonConfig: String) {
            ViewHelper.setJsonConfiguration(jsonConfig)
        }

        @JvmStatic
        fun releaseInstance() {
            ViewHelper.releaseInstance()
        }
    }
}
