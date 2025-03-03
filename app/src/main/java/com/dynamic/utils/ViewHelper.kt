package com.dynamic.utils

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.ViewGroup
import androidx.appcompat.view.ContextThemeWrapper
import androidx.databinding.ViewDataBinding
import androidx.viewbinding.ViewBinding
import com.dynamic.data.models.ViewNode
import com.dynamic.data.models.ViewNodeParser.fromJson
import com.dynamic.data.sources.local.RoomViewNodeDataSource
import com.dynamic.data.sources.local.db.AppDatabase
import com.dynamic.data.sources.local.db.DatabaseProvider
import com.dynamic.data.sources.local.db.ViewNodeDao
import com.dynamic.utils.DynamicLayoutInflation.inflate
import com.dynamic.utils.interfaces.ViewHandler
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main

/**
 * Helper class for handling dynamic views from JSON with persistence.
 */
internal object ViewHelper {

    private var jsonConfiguration: String? = null
    private lateinit var database: AppDatabase
    private lateinit var viewStateDao: ViewNodeDao

    /**
     * Initializes and restores dynamic views.
     *
     * @param binding Either a ViewBinding or a ViewDataBinding instance, wrapped in a BindingType.
     * @param viewHandler ViewHandler implementation.
     * @param context The activity context.
     * @param extras Saved instance state bundle.
     * @param callback Callback invoked after initialization, with the same binding type.
     */
    fun <T> init(
        binding: T,
        viewHandler: ViewHandler,
        context: Context,
        theme: Int,
        extras: Bundle?,
        callback: (T?) -> Unit
    ) where T : ViewDataBinding, T : ViewBinding {
        val effectiveJson = jsonConfiguration ?: viewHandler.getJsonConfiguration()
        if (effectiveJson == null) callback.invoke(null)

        val containerView = viewHandler.getContainerLayout()
        database = DatabaseProvider.getInstance(context)
        viewStateDao = database.ViewNodeDao()
        val contextThemeWrapper = ContextThemeWrapper(context, theme)

        CoroutineScope(Main).launch {
            try {
                when {
                    extras != null -> {
                        Log.d("ViewHelper", "Restoring view states from Bundle")
                        restoreViewsFromState(
                            contextThemeWrapper, extras, containerView
                        )
                    }

                    !RoomViewNodeDataSource(context).hasViewNode() -> {
                        Log.d("ViewHelper", "No view nodes found in Room")
                        if (effectiveJson == null) return@launch
                        val node = fromJson(effectiveJson) ?: return@launch
                        Log.d("ViewHelper", "Inflating views from JSON")
                        inflate(contextThemeWrapper, node, containerView)
                    }

                    else -> {
                        Log.d("ViewHelper", "Restoring views from Room")
                        val viewNode = withContext(IO) { restoreViewsFromRoom(context) }
                        Log.d("ViewHelper", "Restored view states: $viewNode")
                        if (viewNode == null) return@launch
                        inflate(
                            contextThemeWrapper, viewNode, containerView
                        )
                    }
                }

                viewHandler.onViewCreated(containerView)
                callback(binding)
            } catch (e: Exception) {
                Log.e("ViewHelper", "Initialization failed: ${e.message}", e)
                callback.invoke(null) // Indicate failure
            }
        }
    }

    private fun restoreViewsFromState(
        context: ContextThemeWrapper, extras: Bundle, containerView: ViewGroup?
    ) {
        val viewNode = extras.getParcelableCompat("viewNode", ViewNode::class.java) ?: return
        inflate(context, viewNode, containerView)
    }

    private suspend fun restoreViewsFromRoom(context: Context): ViewNode? {
        return viewStateDao.getViewNode(context.getActivityName())
    }

    fun saveInstanceState(outState: Bundle) {
        val viewNode = collectViewsNode() ?: return
        Log.d("ViewHelper", "Saving view nodes: $viewNode")
        outState.putParcelable("viewState", viewNode)
    }

    fun saveDataWithRoom(context: Context) {
        if (!::viewStateDao.isInitialized) return

        CoroutineScope(IO).launch {
            try {
                val viewNode = collectViewsNode() ?: return@launch
                Log.d(
                    "ViewHelper",
                    "Saving view nodes to Room for activity: ${context.getActivityName()}"
                )
                viewStateDao.insertViewNode(viewNode)
            } catch (e: Exception) {
                Log.e("ViewHelper", "Error saving view nodes to Room: ${e.message}", e)
            }
        }
    }

    private fun collectViewsNode(): ViewNode? = DynamicLayoutInflation.viewNode

    fun setJsonConfiguration(json: String) {
        jsonConfiguration = json
    }

    private fun <T : Parcelable> Bundle.getParcelableCompat(
        key: String, clazz: Class<T>
    ): T? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getParcelable(key, clazz) // Correct usage for API 33+
        } else {
            @Suppress("DEPRECATION") getParcelable<T>(key)
        }
    }
}