package com.dynamic.data.repositories

import com.dynamic.data.models.ViewNode
import com.dynamic.data.sources.local.RoomViewNodeDataSource

class ViewStateRepository(private val roomViewStateDataSource: RoomViewNodeDataSource) {

    suspend fun hasViewNode(): Boolean {
        return roomViewStateDataSource.hasViewNode()
    }

    suspend fun getViewNode(): ViewNode? {
        return roomViewStateDataSource.getViewNode()
    }

    suspend fun insertViewNode(viewStates: ViewNode) {
        roomViewStateDataSource.insertViewNode(viewStates)
    }

    suspend fun updateViewNode(viewStates: ViewNode) {
        roomViewStateDataSource.updateViewNode(viewStates)
    }
}