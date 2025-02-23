package com.dynamic.data.repositories

import com.dynamic.data.models.ViewState
import com.dynamic.data.sources.local.RoomViewStateDataSource

class ViewStateRepository(private val roomViewStateDataSource: RoomViewStateDataSource) {

    suspend fun hasViewStates(): Boolean {
        return roomViewStateDataSource.hasViewStates()
    }

    suspend fun getViewStates(): List<ViewState>? {
        return roomViewStateDataSource.getViewStates()
    }

    suspend fun insertViewStates(viewStates: List<ViewState>) {
        roomViewStateDataSource.insertViewStates(viewStates)
    }

    suspend fun updateViewStates(viewStates: List<ViewState>) {
        roomViewStateDataSource.updateViewStates(viewStates)
    }
}