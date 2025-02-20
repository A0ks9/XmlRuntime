package com.runtimexml.utils

import android.content.Context
import kotlinx.coroutines.runBlocking

object RoomHelper {
    /**
     * Checks if there are any viewStates stored in Room for the given activityName.
     *
     * This function is blocking (using runBlocking) so it can be called from Java.
     */
    @JvmStatic
    fun hasViewStates(context: Context): Boolean {
        val activityName = context.getActivityName()
        val database = DatabaseProvider.getInstance(context)
        val viewStateDao = database.ViewStateDao()
        return runBlocking {
            // Assuming getViewState returns a List<ViewState>?.
            val viewStates = viewStateDao.getViewState(activityName)
            return@runBlocking (viewStates != null && viewStates.isNotEmpty())
        }
    }
}