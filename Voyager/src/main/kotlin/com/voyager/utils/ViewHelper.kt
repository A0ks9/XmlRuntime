/**
 * High-performance helper class for handling dynamic views from JSON with persistence.
 *
 * This utility provides optimized methods for view creation, state management,
 * and persistence using Room database and Bundle storage.
 *
 * Key features:
 * - Efficient view inflation
 * - Optimized state persistence
 * - Memory-efficient processing
 * - Thread-safe operations
 * - Comprehensive error handling
 *
 * Performance optimizations:
 * - Efficient view hierarchy management
 * - Optimized database operations
 * - Minimized object creation
 * - Efficient memory usage
 * - Safe resource handling
 *
 * Usage example:
 * ```kotlin
 * val viewHelper = ViewHelper.Builder(context)
 *     .setTheme(R.style.AppTheme)
 *     .setViewHandler(viewHandler)
 *     .setCallback { viewGroup ->
 *         // Handle the generated view
 *     }
 *     .setProvider(resourcesProvider)
 *     .build()
 * ```
 *
 * @author Abdelrahman Omar
 * @since 1.0.0
 */
package com.voyager.utils

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.ViewGroup
import androidx.appcompat.view.ContextThemeWrapper
import com.voyager.data.models.ConfigManager
import com.voyager.data.models.ViewNode
import com.voyager.data.models.ViewNodeParser.fromJson
import com.voyager.data.models.VoyagerConfig
import com.voyager.data.sources.local.RoomViewNodeDataSource
import com.voyager.data.sources.local.db.AppDatabase
import com.voyager.data.sources.local.db.DatabaseProvider
import com.voyager.data.sources.local.db.ViewNodeDao
import com.voyager.utils.DynamicLayoutInflation.inflate
import com.voyager.utils.interfaces.ResourcesProvider
import com.voyager.utils.interfaces.ViewHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference

/**
 * Helper class for handling dynamic views from JSON with persistence using Builder pattern.
 *
 * @property context The Android context
 * @property theme Theme resource ID to apply to views
 * @property viewHandler Handles view-related operations
 * @property extras Optional bundle for additional data
 * @property jsonConfiguration Optional JSON layout configuration
 * @property callback Callback function for generated views
 */
class ViewHelper private constructor(
    private val context: Context,
    private val theme: Int,
    private val viewHandler: ViewHandler,
    private val extras: Bundle?,
    private var jsonConfiguration: String? = null,
    private val callback: (Any?) -> Unit,
) {
    init {
        initialize()
    }

    companion object {
        private const val TAG = "ViewHelper"
        private const val VIEW_STATE_KEY = "viewState"
        private const val VIEW_NODE_KEY = "viewNode"

        @Volatile
        private var instance: WeakReference<ViewHelper>? = null

        /**
         * Retrieves the current instance of ViewHelper if available.
         *
         * @return Current ViewHelper instance or null
         */
        private fun getInstance(): ViewHelper? = instance?.get()

        /**
         * Saves the current view state to a Bundle.
         *
         * @param outState Bundle to save the view state
         */
        internal fun saveCurrentViewNode(outState: Bundle) {
            collectViewsNode()?.let {
                outState.putParcelable(VIEW_STATE_KEY, it)
            }
        }

        /**
         * Collects the current view node from DynamicLayoutInflation.
         *
         * @return Current ViewNode or null
         */
        private fun collectViewsNode() = DynamicLayoutInflation.viewNode

        /**
         * Saves the view node to Room database.
         *
         * @param context Android context
         */
        internal fun saveViewNodeToRoom(context: Context) {
            val database: AppDatabase = DatabaseProvider.getInstance(context)
            val viewStateDao: ViewNodeDao = database.ViewNodeDao()

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    collectViewsNode()?.let {
                        viewStateDao.insertViewNode(it)
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error saving view nodes to Room: ${e.message}", e)
                }
            }
        }

        /**
         * Releases the singleton instance and clears memory.
         */
        fun releaseInstance() {
            instance?.clear()
            instance = null
            Log.d(TAG, "Instance released")
        }

        /**
         * Sets the JSON configuration for the current instance.
         *
         * @param jsonConfig JSON configuration string
         */
        fun setJsonConfiguration(jsonConfig: String) {
            getInstance()?.jsonConfiguration = jsonConfig
        }
    }

    /**
     * Initializes the ViewHelper by determining the source of view configuration.
     */
    private fun initialize() {
        val effectiveJson = jsonConfiguration ?: viewHandler.getJsonConfiguration()
        val containerView = viewHandler.getContainerLayout()
        val contextThemeWrapper = ContextThemeWrapper(context, theme)
        instance = WeakReference(this)

        CoroutineScope(Dispatchers.Main).launch {
            try {
                when {
                    extras != null -> {
                        restoreViewsFromBundle(contextThemeWrapper, extras, containerView)
                    }

                    !RoomViewNodeDataSource(context).hasViewNode() -> {
                        if (effectiveJson == null) {
                            callback.invoke(null)
                            return@launch
                        }

                        fromJson(effectiveJson)?.let { node ->
                            inflate(contextThemeWrapper, node, containerView)
                        }
                    }

                    else -> {
                        val viewNode = withContext(Dispatchers.IO) { restoreViewsFromRoom(context) }
                        viewNode?.let {
                            inflate(contextThemeWrapper, it, containerView)
                        }
                    }
                }

                viewHandler.onViewCreated(containerView)
                callback(containerView)
            } catch (e: Exception) {
                Log.e(TAG, "Initialization failed: ${e.message}", e)
                callback.invoke(null)
            }
        }
    }

    /**
     * Restores views from a saved Bundle.
     *
     * @param context Context with the desired theme
     * @param extras Bundle containing the saved view state
     * @param containerView Parent container to inflate the views
     */
    private fun restoreViewsFromBundle(
        context: ContextThemeWrapper,
        extras: Bundle,
        containerView: ViewGroup?,
    ) {
        val viewNode = extras.getParcelableCompat(VIEW_NODE_KEY, ViewNode::class.java) ?: return
        inflate(context, viewNode, containerView)
    }

    /**
     * Retrieves the view state from the Room database.
     *
     * @param context Android context
     * @return The restored ViewNode or null if not found
     */
    private suspend fun restoreViewsFromRoom(context: Context): ViewNode? {
        val database: AppDatabase = DatabaseProvider.getInstance(context)
        val viewStateDao: ViewNodeDao = database.ViewNodeDao()
        return viewStateDao.getViewNode(context.getActivityName())
    }

    /**
     * Compatibility method for retrieving a Parcelable object from a Bundle.
     *
     * @param key Key for the Parcelable object
     * @param clazz Class type of the Parcelable object
     * @return Parcelable object or null
     */
    private fun <T : Parcelable> Bundle.getParcelableCompat(key: String, clazz: Class<T>): T? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getParcelable(key, clazz)
        } else {
            @Suppress("DEPRECATION") getParcelable(key)
        }
    }

    /**
     * Builder class for constructing a ViewHelper instance.
     */
    class Builder(private val context: Context) {
        private var theme: Int = 0
        private lateinit var viewHandler: ViewHandler
        private var extras: Bundle? = null
        private var jsonConfiguration: String? = null
        private lateinit var callback: (ViewGroup?) -> Unit
        private lateinit var resourcesProvider: ResourcesProvider
        private var isLoggingEnabled: Boolean = false

        /**
         * Sets the theme resource ID.
         *
         * @param theme Theme resource ID
         * @return Builder instance
         */
        fun setTheme(theme: Int) = apply { this.theme = theme }

        /**
         * Sets the ViewHandler instance.
         *
         * @param viewHandler ViewHandler instance
         * @return Builder instance
         */
        fun setViewHandler(viewHandler: ViewHandler) = apply { this.viewHandler = viewHandler }

        /**
         * Sets the extras Bundle.
         *
         * @param extras Bundle instance
         * @return Builder instance
         */
        fun setExtras(extras: Bundle?) = apply { this.extras = extras }

        /**
         * Sets the callback function.
         *
         * @param callback Callback function
         * @return Builder instance
         */
        fun setCallback(callback: (ViewGroup?) -> Unit) = apply { this.callback = callback }

        /**
         * Sets the ResourcesProvider instance.
         *
         * @param resourcesProvider ResourcesProvider instance
         * @return Builder instance
         */
        fun setProvider(resourcesProvider: ResourcesProvider) =
            apply { this.resourcesProvider = resourcesProvider }

        /**
         * Sets the JSON configuration.
         *
         * @param json JSON configuration string
         * @return Builder instance
         */
        fun setJsonConfiguration(json: String) = apply { this.jsonConfiguration = json }

        /**
         * Sets whether logging is enabled.
         *
         * @param enabled Whether logging is enabled
         * @return Builder instance
         */
        fun setLoggingEnabled(enabled: Boolean) = apply { this.isLoggingEnabled = enabled }

        /**
         * Builds the ViewHelper instance.
         *
         * @return ViewHelper instance
         * @throws IllegalStateException if required parameters are not set
         */
        fun build(): ViewHelper {
            ConfigManager.initialize(
                VoyagerConfig(
                    provider = resourcesProvider
                )
            )
            require(::viewHandler.isInitialized) { "ViewHandler is required" }
            require(::callback.isInitialized) { "Callback is required" }
            @Suppress("UNCHECKED_CAST") val viewHelper = ViewHelper(
                context, theme, viewHandler, extras, jsonConfiguration, callback as (Any?) -> Unit
            )
            instance = WeakReference(viewHelper)
            return viewHelper
        }
    }
}