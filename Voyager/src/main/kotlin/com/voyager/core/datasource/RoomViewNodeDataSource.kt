package com.voyager.core.datasource

import android.content.Context
import com.voyager.core.db.DatabaseProvider
import com.voyager.core.db.ViewNodeDao
import com.voyager.core.model.ViewNode
import com.voyager.utils.getActivityName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RoomViewNodeDataSource(context: Context) {
    private val viewNodeDao: ViewNodeDao = DatabaseProvider.getInstance(context).viewNodeDao()
    private val activityName: String = context.getActivityName()
    private var cachedViewNode: ViewNode? = null

    suspend fun hasViewNode(): Boolean = withContext(Dispatchers.IO) {
        cachedViewNode = viewNodeDao.getViewNode(activityName)
        cachedViewNode != null
    }
    suspend fun getViewNode(): ViewNode? = withContext(Dispatchers.IO) {
        cachedViewNode ?: viewNodeDao.getViewNode(activityName).also { cachedViewNode = it }
    }
    suspend fun insertViewNode(viewNode: ViewNode) = withContext(Dispatchers.IO) {
        cachedViewNode = viewNode
        viewNodeDao.insertViewNode(viewNode)
    }
    suspend fun updateViewNode(viewNode: ViewNode) = withContext(Dispatchers.IO) {
        cachedViewNode = viewNode
        viewNodeDao.updateViewsState(viewNode)
    }
} 