package com.voyager.core.data.repository

import com.voyager.core.model.ViewNode
import com.voyager.core.datasource.ViewNodeDataSource // Use the interface
import com.voyager.core.repository.ViewNodeRepository // Use the interface

class ViewNodeRepositoryImpl(
    private val local: ViewNodeDataSource // Accept the interface
) : ViewNodeRepository {
    override suspend fun hasViewNode() = local.hasViewNode()
    override suspend fun getViewNode() = local.getViewNode()
    override suspend fun insertViewNode(viewNode: ViewNode) = local.insertViewNode(viewNode)
    override suspend fun updateViewNode(viewNode: ViewNode) = local.updateViewNode(viewNode)
} 