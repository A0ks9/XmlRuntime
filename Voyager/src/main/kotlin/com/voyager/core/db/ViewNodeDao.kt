package com.voyager.core.db

import androidx.room.*
import com.voyager.core.model.ViewNode

/**
 * Data Access Object (DAO) for ViewNode entities.
 * This interface defines database operations for managing view nodes,
 * including CRUD operations and bulk operations.
 *
 * Features:
 * - Insert operations with conflict resolution
 * - Update operations with transaction support
 * - Query operations with filtering
 * - Delete operations
 * - Bulk operations
 *
 * Example Usage:
 * ```kotlin
 * val dao = database.viewNodeDao()
 * 
 * // Insert a view node
 * dao.insertViewNode(viewNode)
 * 
 * // Get a view node by activity
 * val node = dao.getViewNode("MainActivity")
 * 
 * // Update a view node
 * dao.updateViewsState(viewNode)
 * 
 * // Delete a view node
 * dao.deleteViewNode("MainActivity")
 * ```
 */
@Dao
interface ViewNodeDao {
    /**
     * Inserts a view node into the database.
     * If a view node with the same activity name exists, it will be replaced.
     *
     * @param viewNode The view node to insert
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertViewNode(viewNode: ViewNode)

    /**
     * Updates the state of a view node.
     * This operation is wrapped in a transaction to ensure data consistency.
     *
     * @param viewNode The view node to update
     */
    @Transaction
    suspend fun updateViewsState(viewNode: ViewNode) = insertViewNode(viewNode)

    /**
     * Retrieves a view node by activity name.
     *
     * @param activity The name of the activity
     * @return The view node if found, null otherwise
     */
    @Query("SELECT * FROM view_nodes WHERE activityName = :activity")
    suspend fun getViewNode(activity: String): ViewNode?

    /**
     * Retrieves all view nodes from the database.
     *
     * @return A list of all view nodes
     */
    @Query("SELECT * FROM view_nodes")
    suspend fun getAllViewNodes(): List<ViewNode>

    /**
     * Deletes a view node by activity name.
     *
     * @param activity The name of the activity
     */
    @Query("DELETE FROM view_nodes WHERE activityName = :activity")
    suspend fun deleteViewNode(activity: String)

    /**
     * Deletes all view nodes from the database.
     */
    @Query("DELETE FROM view_nodes")
    suspend fun deleteAllViewNodes()
} 