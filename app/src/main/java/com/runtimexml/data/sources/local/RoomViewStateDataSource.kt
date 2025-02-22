package com.runtimexml.data.sources.local

import android.content.Context
import com.runtimexml.data.models.ViewState
import com.runtimexml.data.sources.local.db.DatabaseProvider
import com.runtimexml.data.sources.local.db.ViewStateDao
import com.runtimexml.utils.getActivityName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RoomViewStateDataSource(private val context: Context) {

    private val viewStateDao: ViewStateDao by lazy {
        DatabaseProvider.getInstance(context).ViewStateDao()
    }

    suspend fun hasViewStates(): Boolean = withContext(Dispatchers.IO) {
        val viewStates = viewStateDao.getViewState(context.getActivityName())
        return@withContext (viewStates != null && viewStates.isNotEmpty())
    }

    suspend fun getViewStates(): List<ViewState>? =
        withContext(Dispatchers.IO) {
            return@withContext viewStateDao.getViewState(context.getActivityName())
        }

    suspend fun insertViewStates(viewStates: List<ViewState>) = withContext(Dispatchers.IO) {
        viewStateDao.insertViewsState(viewStates)
    }

    suspend fun updateViewStates(viewStates: List<ViewState>) = withContext(Dispatchers.IO) {
        viewStateDao.updateViewsState(viewStates)
    }
}