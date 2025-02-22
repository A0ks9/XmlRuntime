package com.runtimexml.data.repositories

import com.runtimexml.data.models.ViewState
import com.runtimexml.data.sources.local.RoomViewStateDataSource

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