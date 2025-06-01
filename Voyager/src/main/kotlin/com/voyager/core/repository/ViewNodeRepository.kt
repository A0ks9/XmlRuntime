package com.voyager.core.repository

import com.voyager.core.model.ViewNode
import com.voyager.core.datasource.RoomViewNodeDataSource

interface ViewNodeRepository {
    suspend fun hasViewNode(): Boolean
    suspend fun getViewNode(): ViewNode?
    suspend fun insertViewNode(viewNode: ViewNode)
    suspend fun updateViewNode(viewNode: ViewNode)
}

class ViewNodeRepositoryImpl(
    private val local: RoomViewNodeDataSource
) : ViewNodeRepository {
    override suspend fun hasViewNode() = local.hasViewNode()
    override suspend fun getViewNode() = local.getViewNode()
    override suspend fun insertViewNode(viewNode: ViewNode) = local.insertViewNode(viewNode)
    override suspend fun updateViewNode(viewNode: ViewNode) = local.updateViewNode(viewNode)
} 