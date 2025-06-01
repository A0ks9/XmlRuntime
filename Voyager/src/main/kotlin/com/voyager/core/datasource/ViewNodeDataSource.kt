package com.voyager.core.datasource

import com.voyager.core.model.ViewNode

interface ViewNodeDataSource {
    suspend fun hasViewNode(): Boolean
    suspend fun getViewNode(): ViewNode?
    suspend fun insertViewNode(viewNode: ViewNode)
    suspend fun updateViewNode(viewNode: ViewNode)
} 