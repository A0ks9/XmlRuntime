package com.runtimexml.utils

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import com.runtimexml.utils.interfaces.ViewHandler
import com.runtimexml.utils.interfaces.ViewStateDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Abstract class for handling dynamic views from JSON, ensuring persistence across app restarts.
 * Developers must implement this in their `AppCompatActivity`.
 */
internal class ViewHelper {


    companion object {
        private var jsonConfiguration: JsonCast? = null
        private lateinit var database: AppDatabase
        private lateinit var viewStateDao: ViewStateDao

        /**
         * Initializes and restores views before activity loads its XML.
         */
        fun init(
            viewHandler: ViewHandler, context: Context, extras: Bundle?, callback: (ViewGroup?) -> Unit
        ) {
            val effectiveJson = jsonConfiguration ?: viewHandler.getJsonConfiguration()
            if (effectiveJson == null)
                callback(null)

            val containerView = viewHandler.getContainerLayout()
            database = DatabaseProvider.getInstance(context)
            viewStateDao = database.ViewStateDao()

            CoroutineScope(Dispatchers.Main).launch {
                if (extras != null) {
                    restoreViewsFromState(context, extras, containerView)
                } else {
                    if (!RoomHelper.hasViewStates(context)) {
                        if (effectiveJson == null) {
                            viewHandler.onViewCreated(null)
                            return@launch
                        }
                        Log.d("ViewHelper", "Inflating views from JSON")
                        inflateFromJson(context, effectiveJson, containerView)
                    } else {
                        Log.d("ViewHelper", "Restoring views from Room")
                        val viewStates = withContext(Dispatchers.IO) { restoreViewsFromRoom(context) }
                        Log.d("ViewHelper", "Restored view states: $viewStates")
                        DynamicLayoutInflation.inflateViewState(
                            context, withContext(Dispatchers.IO) {
                                restoreViewsFromRoom(context)
                            }!!, containerView
                        )
                    }
                }
                viewHandler.onViewCreated(containerView)
                callback(containerView)
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

        private fun restoreViewsFromState(
            context: Context, extras: Bundle, containerView: ViewGroup?
        ) {
            val viewState =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) extras.getParcelableArrayList(
                    "viewState", ViewState::class.java
                )
                else @Suppress("DEPRECATION") extras.getParcelableArrayList<ViewState>("viewState")
            if (viewState != null) {
                DynamicLayoutInflation.inflateViewState(context, viewState, containerView)
            }
        }

        private suspend fun restoreViewsFromRoom(context: Context): List<ViewState>? {
            return viewStateDao.getViewState(context.getActivityName())
        }

        @JvmStatic
        fun saveInstanceState(context: Context, outState: Bundle) {
            val viewStates = collectViewsState(context.getActivityName())
            Log.d("ViewHelper", "Saving view states: $viewStates")
            outState.putParcelableArrayList("viewState", viewStates)
        }

        @JvmStatic
        fun saveDataWithRoom(context: Context) {
            if (::viewStateDao.isInitialized.not()) return
            CoroutineScope(Dispatchers.IO).launch {
                val viewStates = collectViewsState(context.getActivityName())
                Log.d("ViewHelper", "activityName: ${context.getActivityName()}")
                Log.d("ViewHelper", "Saving view states to Room: $viewStates")
                viewStateDao.insertViewsState(viewStates)
            }
        }

        private fun collectViewsState(activityName: String): ArrayList<ViewState> {
            return DynamicLayoutInflation.viewsState.filter { it.activityName == activityName }
                .toCollection(ArrayList())
        }

        @JvmStatic
        fun setJsonConfiguration(json: JsonCast) {
            jsonConfiguration = json
        }
    }
}
