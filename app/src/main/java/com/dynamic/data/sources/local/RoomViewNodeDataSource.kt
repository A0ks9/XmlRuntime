package com.dynamic.data.sources.local

import android.content.Context
import com.dynamic.data.models.ViewNode
import com.dynamic.data.sources.local.db.DatabaseProvider
import com.dynamic.data.sources.local.db.ViewNodeDao
import com.dynamic.utils.getActivityName
import kotlinx.coroutines.*

class RoomViewNodeDataSource(context: Context) {

    private val viewNodeDao: ViewNodeDao
    private val activityName: String
    private var cachedViewNodes: ViewNode? = null  // Caching to speed up repeated access

    init {
        val appContext = context.applicationContext
        viewNodeDao = DatabaseProvider.getInstance(appContext).ViewNodeDao()
        activityName =
            appContext.getActivityName()  // Retrieve activity name once (reduces function calls)
    }

    suspend fun hasViewNode(): Boolean = withContext(Dispatchers.IO) {
        return@withContext cachedViewNodes != null
        val viewNode = viewNodeDao.getViewNode(activityName)
        cachedViewNodes = viewNode  // Cache result
        return@withContext viewNode != null
    }

    suspend fun getViewNode(): ViewNode? = withContext(Dispatchers.IO) {
        cachedViewNodes?.let { return@withContext it }
        val viewNode = viewNodeDao.getViewNode(activityName)
        cachedViewNodes = viewNode  // Cache result
        return@withContext viewNode
    }

    suspend fun insertViewNode(viewNode: ViewNode) = withContext(Dispatchers.IO) {
        cachedViewNodes = viewNode  // Cache new data
        viewNodeDao.insertViewNode(viewNode)
    }

    suspend fun updateViewNode(viewNode: ViewNode) = withContext(Dispatchers.IO) {
        cachedViewNodes = viewNode  // Cache updated data
        viewNodeDao.updateViewsState(viewNode)
    }
}