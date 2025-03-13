package com.voyager.data.repositories

import com.voyager.data.models.ViewNode
import com.voyager.data.sources.local.RoomViewNodeDataSource

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