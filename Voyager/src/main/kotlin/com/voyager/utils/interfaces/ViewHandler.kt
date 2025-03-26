package com.voyager.utils.interfaces

import android.content.Context
import android.os.Bundle
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.viewbinding.ViewBinding
import com.voyager.utils.ViewHelper

/**
 * Interface defining the core functionality for handling dynamic view management in the Voyager framework.
 * 
 * This interface provides a contract for managing dynamic view creation, inflation, and state management
 * in Android applications. It supports both ViewBinding and DataBinding, allowing for flexible view
 * handling and state persistence.
 *
 * Key features:
 * - Dynamic view inflation from JSON configuration
 * - View state persistence with Room database
 * - Support for ViewBinding and DataBinding
 * - Lifecycle-aware view management
 * - State restoration capabilities
 *
 * Example usage:
 * ```kotlin
 * class MainActivity : AppCompatActivity(), ViewHandler {
 *     override fun getContainerLayout(): ViewGroup? = binding.container
 *     
 *     override fun getJsonConfiguration(): String? = assets.open("layout.json").bufferedReader().use { it.readText() }
 *     
 *     override fun onViewCreated(parentView: ViewGroup?) {
 *         // Handle view creation
 *     }
 * }
 * ```
 *
 * @author Abdelrahman Omar
 * @since 1.0.0
 */
interface ViewHandler {

    /**
     * Provides the container layout where dynamic views will be added.
     * 
     * This method returns the root ViewGroup that will serve as the container
     * for all dynamically created views. The container should be properly
     * initialized and ready to receive child views.
     *
     * @return The container ViewGroup, or null if not available
     */
    fun getContainerLayout(): ViewGroup?

    /**
     * Retrieves the JSON configuration for dynamic view inflation.
     * 
     * This method provides the JSON string that defines the structure and
     * attributes of views to be created dynamically. The JSON should follow
     * the Voyager framework's view node format.
     *
     * @return The JSON configuration string, or null if not available
     */
    fun getJsonConfiguration(): String?

    /**
     * Called after the view is created and added to the view hierarchy.
     * 
     * This callback method is invoked after all views have been created
     * and added to the container. It provides an opportunity to perform
     * any additional setup or initialization of the created views.
     *
     * @param parentView The root ViewGroup containing all created views
     */
    fun onViewCreated(parentView: ViewGroup?) {}

    companion object {
        /**
         * Initializes the ViewHelper with the given parameters.
         * 
         * This method sets up the ViewHelper with all necessary components
         * for dynamic view creation and management. It supports both ViewBinding
         * and DataBinding through a generic type parameter.
         *
         * @param T The type of binding (must implement both ViewDataBinding and ViewBinding)
         * @param binding The view binding instance
         * @param viewHandler The ViewHandler implementation
         * @param context The application context
         * @param theme The theme resource ID
         * @param extras Optional Bundle for additional data
         * @param provider The ResourcesProvider implementation
         * @param callback Optional callback for view creation completion
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
         * Saves the current view state to the given Bundle.
         * 
         * This method persists the current state of all dynamic views to
         * the provided Bundle, allowing for state restoration when the
         * activity or fragment is recreated.
         *
         * @param outState The Bundle to save the view state to
         */
        @JvmStatic
        fun saveInstanceState(outState: Bundle) {
            ViewHelper.saveCurrentViewNode(outState)
        }

        /**
         * Persists the current view state to the Room database.
         * 
         * This method saves the current state of all dynamic views to
         * the Room database, providing a persistent storage solution
         * for view states across application restarts.
         *
         * @param context The application context
         */
        @JvmStatic
        fun saveDataWithRoom(context: Context) {
            ViewHelper.saveViewNodeToRoom(context)
        }

        /**
         * Sets the JSON configuration for dynamic view inflation.
         * 
         * This method updates the JSON configuration that defines the
         * structure and attributes of views to be created dynamically.
         *
         * @param jsonConfig The new JSON configuration string
         */
        @JvmStatic
        fun setJsonConfiguration(jsonConfig: String) {
            ViewHelper.setJsonConfiguration(jsonConfig)
        }

        /**
         * Releases the current ViewHelper instance and cleans up resources.
         * 
         * This method should be called when the ViewHelper is no longer
         * needed, typically in the onDestroy() method of an Activity or
         * Fragment, to prevent memory leaks.
         */
        @JvmStatic
        fun releaseInstance() {
            ViewHelper.releaseInstance()
        }
    }
}
