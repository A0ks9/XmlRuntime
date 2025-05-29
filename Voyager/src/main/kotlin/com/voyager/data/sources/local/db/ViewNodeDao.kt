package com.voyager.data.sources.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.voyager.data.models.ViewNode

/**
 * Data Access Object (DAO) for [ViewNode] entities.
 *
 * This interface provides methods to interact with the `view_nodes` table in the Room database,
 * defining operations for inserting, updating, querying, and deleting view node data.
 * All methods are suspending functions, intended to be called from coroutines.
 */
@Dao
internal interface ViewNodeDao {

    /**
     * Inserts a [ViewNode] into the database. If the view node already exists, it is replaced.
     * This is a suspending function.
     *
     * @param viewNode The [ViewNode] to insert or replace.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertViewNode(viewNode: ViewNode)

    /**
     * Updates the state of views by inserting or replacing the given [ViewNode].
     * This operation is performed within a database transaction.
     * This is a suspending function.
     *
     * Note: This method currently calls [insertViewNode] which uses `OnConflictStrategy.REPLACE`.
     * This effectively makes it an "upsert" operation for the given [viewNode].
     *
     * @param viewNode The [ViewNode] containing the new state to be persisted.
     */
    @Transaction
    suspend fun updateViewsState(viewNode: ViewNode) {
        // This implementation relies on insertViewNode's REPLACE strategy.
        // If more complex update logic is needed (e.g., partial updates without replacing
        // the entire node if other fields should be preserved), a custom @Update method
        // or more specific queries might be required.
        insertViewNode(viewNode)
    }

    /**
     * Retrieves a specific [ViewNode] from the database based on its activity name.
     * Since `activityName` is the primary key, this will return at most one node.
     * This is a suspending function.
     *
     * @param activity The activity name (which is the primary key) of the [ViewNode] to retrieve.
     * @return The [ViewNode] if found, or `null` otherwise.
     */
    @Query("SELECT * FROM view_nodes WHERE activityName = :activity")
    suspend fun getViewNode(activity: String): ViewNode?

    /**
     * Retrieves all [ViewNode]s from the database.
     * Returns an empty list if the table is empty.
     * This is a suspending function.
     *
     * @return A list of all [ViewNode]s. The list is empty if no nodes are found.
     */
    @Query("SELECT * FROM view_nodes")
    suspend fun getAllViewNodes(): List<ViewNode> // Changed from List<ViewNode>?

    /**
     * Deletes a specific [ViewNode] from the database based on its activity name.
     * This is a suspending function.
     *
     * @param activity The activity name (primary key) of the [ViewNode] to delete.
     */
    @Query("DELETE FROM view_nodes WHERE activityName = :activity")
    suspend fun deleteViewNode(activity: String)

    /**
     * Deletes all [ViewNode]s from the database.
     * This is a suspending function.
     */
    @Query("DELETE FROM view_nodes")
    suspend fun deleteAllViewNodes()
}