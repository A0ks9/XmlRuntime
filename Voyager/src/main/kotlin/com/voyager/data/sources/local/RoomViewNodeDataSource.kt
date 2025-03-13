package com.voyager.data.sources.local

import android.content.Context
import com.voyager.data.models.ViewNode
import com.voyager.data.sources.local.db.DatabaseProvider
import com.voyager.data.sources.local.db.ViewNodeDao
import com.voyager.utils.getActivityName
import kotlinx.coroutines.*

class RoomViewNodeDataSource(context: Context) {

    private val viewNodeDao: ViewNodeDao = DatabaseProvider.getInstance(context).ViewNodeDao()
    private val activityName: String =
        context.getActivityName()  // Retrieve activity name once (reduces function calls)
    private var cachedViewNodes: ViewNode? = null  // Caching to speed up repeated access

    suspend fun hasViewNode(): Boolean = withContext(Dispatchers.IO) {
        cachedViewNodes = viewNodeDao.getViewNode(activityName)
        return@withContext cachedViewNodes != null
    }

    suspend fun getViewNode(): ViewNode? = withContext(Dispatchers.IO) {
        cachedViewNodes?.let { return@withContext it }
        cachedViewNodes = viewNodeDao.getViewNode(activityName)
        return@withContext cachedViewNodes
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