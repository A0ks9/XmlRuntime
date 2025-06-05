package com.voyager.core.repository

import com.voyager.core.exceptions.VoyagerDatabaseException
import com.voyager.core.model.ViewNode
import com.voyager.core.datasource.RoomViewNodeDataSource

/**
 * Repository interface for managing ViewNode data.
 * Provides CRUD operations for ViewNode entities with proper error handling.
 *
 * Features:
 * - ViewNode persistence
 * - Error handling
 * - Thread safety
 * - Performance optimization
 * - Memory efficiency
 *
 * Example Usage:
 * ```kotlin
 * class ViewNodeRepositoryImpl(
 *     private val dataSource: RoomViewNodeDataSource
 * ) : ViewNodeRepository {
 *     override suspend fun hasViewNode(): Boolean = dataSource.hasViewNode()
 *     override suspend fun getViewNode(): ViewNode? = dataSource.getViewNode()
 *     override suspend fun insertViewNode(viewNode: ViewNode) = dataSource.insertViewNode(viewNode)
 *     override suspend fun updateViewNode(viewNode: ViewNode) = dataSource.updateViewNode(viewNode)
 * }
 * ```
 *
 * @throws VoyagerDatabaseException.DatabaseOperationException if database operations fail
 * @throws VoyagerDatabaseException.DatabaseQueryException if queries fail
 */
interface ViewNodeRepository {
    /**
     * Checks if a ViewNode exists in the database.
     *
     * @return true if a ViewNode exists, false otherwise
     * @throws VoyagerDatabaseException.DatabaseQueryException if the query fails
     */
    suspend fun hasViewNode(): Boolean

    /**
     * Retrieves the current ViewNode from the database.
     *
     * @return The current ViewNode, or null if none exists
     * @throws VoyagerDatabaseException.DatabaseQueryException if the query fails
     */
    suspend fun getViewNode(): ViewNode?

    /**
     * Inserts a new ViewNode into the database.
     * If a ViewNode already exists, it will be replaced.
     *
     * @param viewNode The ViewNode to insert
     * @throws VoyagerDatabaseException.DatabaseOperationException if the insertion fails
     */
    suspend fun insertViewNode(viewNode: ViewNode)

    /**
     * Updates an existing ViewNode in the database.
     * If the ViewNode doesn't exist, it will be inserted.
     *
     * @param viewNode The ViewNode to update
     * @throws VoyagerDatabaseException.DatabaseOperationException if the update fails
     */
    suspend fun updateViewNode(viewNode: ViewNode)
}