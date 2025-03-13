package com.voyager.utils

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.ViewGroup
import androidx.appcompat.view.ContextThemeWrapper
import androidx.viewbinding.ViewBinding
import com.voyager.data.models.ViewNode
import com.voyager.data.models.ViewNodeParser.fromJson
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
 * @property context The Android context (e.g., Activity, Application)
 * @property theme Theme resource ID to apply to the views
 * @property viewHandler Handles view-related operations such as layout retrieval and JSON management
 * @property extras Optional bundle for passing additional data
 * @property jsonConfiguration Optional JSON layout configuration
 * @property callback Callback function to return the generated views
 */
class ViewHelper private constructor(
    private val context: Context,
    private val theme: Int,
    private val viewHandler: ViewHandler,
    private val extras: Bundle?,
    private var jsonConfiguration: String? = null,
    private val callback: (Any?) -> Unit,
) {

    // Database instance for persistent storage
    private val database: AppDatabase = DatabaseProvider.getInstance(context)
    private val viewStateDao: ViewNodeDao = database.ViewNodeDao()

    init {
        initialize()
    }

    companion object {
        @Volatile
        private var instance: WeakReference<ViewHelper>? = null

        /**
         * Retrieves the current instance of ViewHelper if available.
         *
         * @return Current ViewHelper instance or null
         */
        private fun getInstance(): ViewHelper? = instance?.get()

        /**
         * Saves the current view state to the provided [Bundle].
         *
         * @param outState The bundle to store the view state
         */
        fun saveInstance(outState: Bundle) {
            getInstance()?.saveCurrentViewState(outState)
        }

        /**
         * Persists the current view state into the Room database.
         */
        fun saveToRoom() {
            getInstance()?.saveViewStateToRoom()
        }

        /**
         * Releases the singleton instance and clears the memory.
         */
        fun releaseInstance() {
            instance?.clear()
            instance = null
            Log.d("ViewHelper", "Instance released")
        }

        fun setJsonConfiguration(jsonConfig: String) {
            getInstance()?.jsonConfiguration = jsonConfig
        }
    }

    /**
     * Initializes the ViewHelper by determining the source of the view configuration (JSON, Bundle, or Room database).
     */
    private fun initialize() {
        val effectiveJson = jsonConfiguration ?: viewHandler.getJsonConfiguration()
        if (effectiveJson == null) {
            callback.invoke(null)
            return
        }

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
                Log.e("ViewHelper", "Initialization failed: ${e.message}", e)
                callback.invoke(null)
            }
        }
    }

    /**
     * Restores views from a saved [Bundle].
     *
     * @param context Context with the desired theme
     * @param extras Bundle containing the saved view state
     * @param containerView Parent container to inflate the views
     */
    private fun restoreViewsFromBundle(
        context: ContextThemeWrapper, extras: Bundle, containerView: ViewGroup?,
    ) {
        val viewNode = extras.getParcelableCompat("viewNode", ViewNode::class.java) ?: return
        inflate(context, viewNode, containerView)
    }

    /**
     * Retrieves the view state from the Room database.
     *
     * @param context Android context
     * @return The restored [ViewNode] or null if not found
     */
    private suspend fun restoreViewsFromRoom(context: Context): ViewNode? {
        return viewStateDao.getViewNode(context.getActivityName())
    }

    /**
     * Collects the current view node for saving.
     *
     * @return The current [ViewNode] or null
     */
    private fun collectViewsNode(): ViewNode? = DynamicLayoutInflation.viewNode

    /**
     * Saves the current view state to the provided [Bundle].
     *
     * @param outState Bundle to save the view state
     */
    private fun saveCurrentViewState(outState: Bundle) {
        collectViewsNode()?.let {
            outState.putParcelable("viewState", it)
        }
    }

    /**
     * Saves the current view state to the Room database asynchronously.
     */
    private fun saveViewStateToRoom() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                collectViewsNode()?.let {
                    viewStateDao.insertViewNode(it)
                }
            } catch (e: Exception) {
                Log.e("ViewHelper", "Error saving view nodes to Room: ${e.message}", e)
            }
        }
    }

    /**
     * Compatibility method for retrieving a Parcelable object from a [Bundle].
     *
     * @param key Key for the Parcelable object
     * @param clazz Class type of the Parcelable object
     * @return Parcelable object or null
     */
    private fun <T : Parcelable> Bundle.getParcelableCompat(key: String, clazz: Class<T>): T? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) getParcelable(key, clazz)
        else @Suppress("DEPRECATION") getParcelable(key)
    }

    /**
     * Builder class for constructing a [ViewHelper] instance.
     */
    class Builder<T : ViewBinding>(private val context: Context) {
        private var theme: Int = 0
        private lateinit var viewHandler: ViewHandler
        private var extras: Bundle? = null
        private var jsonConfiguration: String? = null
        private lateinit var callback: (T?) -> Unit
        private lateinit var resourcesProvider: ResourcesProvider

        fun setTheme(theme: Int) = apply { this.theme = theme }
        fun setViewHandler(viewHandler: ViewHandler) = apply { this.viewHandler = viewHandler }
        fun setExtras(extras: Bundle?) = apply { this.extras = extras }
        fun setCallback(callback: (T?) -> Unit) = apply { this.callback = callback }
        fun setProvider(resourcesProvider: ResourcesProvider) =
            apply { this.resourcesProvider = resourcesProvider }

        fun setJsonConfiguration(json: String) = apply { this.jsonConfiguration = json }

        fun build(): ViewHelper {
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