package com.voyager.core.db

import androidx.room.*
import com.voyager.core.model.ViewNode

@Dao
interface ViewNodeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertViewNode(viewNode: ViewNode)

    @Transaction
    suspend fun updateViewsState(viewNode: ViewNode) = insertViewNode(viewNode)

    @Query("SELECT * FROM view_nodes WHERE activityName = :activity")
    suspend fun getViewNode(activity: String): ViewNode?

    @Query("SELECT * FROM view_nodes")
    suspend fun getAllViewNodes(): List<ViewNode>?

    @Query("DELETE FROM view_nodes WHERE activityName = :activity")
    suspend fun deleteViewNode(activity: String)

    @Query("DELETE FROM view_nodes")
    suspend fun deleteAllViewNodes()
} 