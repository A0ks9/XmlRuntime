package com.dynamic.utils

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.viewbinding.ViewBinding
import com.dynamic.data.models.ViewState
import com.dynamic.data.sources.local.RoomViewStateDataSource
import com.dynamic.data.sources.local.db.AppDatabase
import com.dynamic.data.sources.local.db.DatabaseProvider
import com.dynamic.data.sources.local.db.ViewStateDao
import com.dynamic.utils.interfaces.ViewHandler
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main

/**
 * Helper class for handling dynamic views from JSON with persistence.
 */
internal object ViewHelper {

    private var jsonConfiguration: JsonCast? = null
    private lateinit var database: AppDatabase
    private lateinit var viewStateDao: ViewStateDao

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
        extras: Bundle?,
        callback: (T?) -> Unit
    ) where T : ViewDataBinding, T : ViewBinding {
        val effectiveJson = jsonConfiguration ?: viewHandler.getJsonConfiguration()
        if (effectiveJson == null) callback.invoke(null)

        val containerView = viewHandler.getContainerLayout()
        database = DatabaseProvider.getInstance(context)
        viewStateDao = database.ViewStateDao()

        CoroutineScope(Main).launch {
            try {
                when {
                    extras != null -> restoreViewsFromState(context, extras, containerView)

                    !RoomViewStateDataSource(context).hasViewStates() -> {
                        if (effectiveJson == null) return@launch
                        Log.d("ViewHelper", "Inflating views from JSON")
                        inflateFromJson(context, effectiveJson, containerView)
                    }

                    else -> {
                        Log.d("ViewHelper", "Restoring views from Room")
                        val viewStates = withContext(IO) { restoreViewsFromRoom(context) }
                        Log.d("ViewHelper", "Restored view states: $viewStates")
                        DynamicLayoutInflation.inflateViewState(
                            context, viewStates ?: emptyList(), containerView
                        )
                    }
                }

                viewHandler.onViewCreated(containerView)
                callback.invoke(binding)
            } catch (e: Exception) {
                Log.e("ViewHelper", "Initialization failed: ${e.message}", e)
                callback.invoke(null) // Indicate failure
            }
        }
    }

    private fun inflateFromJson(context: Context, json: JsonCast, containerView: ViewGroup?) {
        when (json) {
            is JsonCast.JsonO -> DynamicLayoutInflation.inflateJson(
                context, json.jsonObject, containerView
            )

            is JsonCast.JsonA -> DynamicLayoutInflation.inflateJson(
                context, json.jsonArray, containerView
            )

            else -> throw IllegalArgumentException("Invalid JsonCast type")
        }
    }

    private fun restoreViewsFromState(context: Context, extras: Bundle, containerView: ViewGroup?) {
        val viewState = extras.getParcelableArrayListCompat("viewState", ViewState::class.java)
        DynamicLayoutInflation.inflateViewState(context, viewState ?: emptyList(), containerView)
    }

    private suspend fun restoreViewsFromRoom(context: Context): List<ViewState>? {
        return viewStateDao.getViewState(context.getActivityName())
    }

    fun saveInstanceState(context: Context, outState: Bundle) {
        val viewStates = collectViewsState(context.getActivityName())
        Log.d("ViewHelper", "Saving view states: $viewStates")
        outState.putParcelableArrayList("viewState", viewStates)
    }

    fun saveDataWithRoom(context: Context) {
        if (!::viewStateDao.isInitialized) return

        CoroutineScope(IO).launch {
            try {
                val viewStates = collectViewsState(context.getActivityName())
                Log.d(
                    "ViewHelper",
                    "Saving view states to Room for activity: ${context.getActivityName()}"
                )
                viewStateDao.insertViewsState(viewStates)
            } catch (e: Exception) {
                Log.e("ViewHelper", "Error saving view states to Room: ${e.message}", e)
            }
        }
    }

    private fun collectViewsState(activityName: String): ArrayList<ViewState> {
        return DynamicLayoutInflation.viewsState.filter { it.activityName == activityName }
            .toCollection(ArrayList())
    }

    fun setJsonConfiguration(json: JsonCast) {
        jsonConfiguration = json
    }

    private fun <T : Parcelable> Bundle.getParcelableArrayListCompat(
        key: String, clazz: Class<T>
    ): ArrayList<T>? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getParcelableArrayList(key, clazz) // Correct usage for API 33+
        } else {
            @Suppress("DEPRECATION") getParcelableArrayList<T>(key)
        }
    }
}