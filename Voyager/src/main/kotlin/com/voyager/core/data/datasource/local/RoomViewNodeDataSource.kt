package com.voyager.core.data.datasource.local

import android.content.Context
import com.voyager.core.datasource.ViewNodeDataSource
import com.voyager.core.db.DatabaseProvider
import com.voyager.core.db.ViewNodeDao
import com.voyager.core.model.ViewNode
import com.voyager.core.utils.ContextUtils.getActivityName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RoomViewNodeDataSource(context: Context) : ViewNodeDataSource {
    private val viewNodeDao: ViewNodeDao = DatabaseProvider.getInstance(context).viewNodeDao()
    private val activityName: String = context.getActivityName()
    private var cachedViewNode: ViewNode? = null

    override suspend fun hasViewNode(): Boolean = withContext(Dispatchers.IO) {
        cachedViewNode = viewNodeDao.getViewNode(activityName)
        cachedViewNode != null
    }

    override suspend fun getViewNode(): ViewNode? = withContext(Dispatchers.IO) {
        cachedViewNode ?: viewNodeDao.getViewNode(activityName).also { cachedViewNode = it }
    }

    override suspend fun insertViewNode(viewNode: ViewNode) = withContext(Dispatchers.IO) {
        cachedViewNode = viewNode
        viewNodeDao.insertViewNode(viewNode)
    }

    override suspend fun updateViewNode(viewNode: ViewNode) = withContext(Dispatchers.IO) {
        cachedViewNode = viewNode
        viewNodeDao.updateViewsState(viewNode)
    }
} 