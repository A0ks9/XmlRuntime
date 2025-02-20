package com.runtimexml.utils.interfaces

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.runtimexml.utils.ViewState

@Dao
internal interface ViewStateDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertViewState(viewState: ViewState)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertViewsState(viewStates: List<ViewState>)

    @Transaction
    suspend fun updateViewsState(viewState: List<ViewState>) {
        insertViewsState(viewState)
    }

    @Query("SELECT * FROM view_states WHERE activityName = :activity AND id = :id")
    suspend fun getViewState(activity: String, id: String): ViewState?

    @Query("SELECT * FROM view_states WHERE activityName = :activity")
    suspend fun getViewState(activity: String): List<ViewState>?

    @Query("SELECT * FROM view_states")
    suspend fun getAllViewStates(): List<ViewState>?

    @Query("DELETE FROM view_states WHERE activityName = :activity AND id = :id")
    suspend fun deleteViewState(activity: String, id: String)

    @Query("DELETE FROM view_states WHERE activityName = :activity")
    suspend fun deleteViewState(activity: String)

    @Query("DELETE FROM view_states")
    suspend fun deleteAllViewStates()
}