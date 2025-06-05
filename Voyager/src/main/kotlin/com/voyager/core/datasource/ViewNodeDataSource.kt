package com.voyager.core.datasource

import com.voyager.core.model.ViewNode

/**
 * Interface defining operations for managing ViewNode data in the Voyager framework.
 * This interface provides a contract for data sources that handle the persistence
 * and retrieval of view node information.
 *
 * Key Features:
 * - View node existence checking
 * - View node retrieval
 * - View node insertion
 * - View node updates
 * - Thread-safe operations
 * - Caching support
 *
 * Example Usage:
 * ```kotlin
 * class RoomViewNodeDataSource(
 *     private val context: Context
 * ) : ViewNodeDataSource {
 *     private val viewNodeDao: ViewNodeDao = DatabaseProvider.getInstance(context).viewNodeDao()
 *     private val activityName: String = context.name
 *     private var cachedViewNode: ViewNode? = null
 *     
 *     override suspend fun hasViewNode(): Boolean {
 *         cachedViewNode = viewNodeDao.getViewNode(activityName)
 *         return cachedViewNode != null
 *     }
 *     
 *     override suspend fun getViewNode(): ViewNode? {
 *         return cachedViewNode ?: viewNodeDao.getViewNode(activityName)
 *             .also { cachedViewNode = it }
 *     }
 *     
 *     override suspend fun insertViewNode(viewNode: ViewNode) {
 *         cachedViewNode = viewNode
 *         viewNodeDao.insertViewNode(viewNode)
 *     }
 *     
 *     override suspend fun updateViewNode(viewNode: ViewNode) {
 *         cachedViewNode = viewNode
 *         viewNodeDao.updateViewsState(viewNode)
 *     }
 * }
 * ```
 *
 * @author Abdelrahman Omar
 * @since 1.0.0
 */
interface ViewNodeDataSource {
    /**
     * Checks if a view node exists in the data source.
     * This method should be implemented to check for the existence of a view node
     * associated with the current context or activity.
     *
     * @return true if a view node exists, false otherwise
     */
    suspend fun hasViewNode(): Boolean

    /**
     * Retrieves the current view node from the data source.
     * This method should return the view node associated with the current context
     * or activity, or null if no view node exists.
     *
     * @return The view node if found, null otherwise
     */
    suspend fun getViewNode(): ViewNode?

    /**
     * Inserts a new view node into the data source.
     * This method should handle the insertion of a new view node, potentially
     * replacing any existing view node for the current context or activity.
     *
     * @param viewNode The view node to insert
     */
    suspend fun insertViewNode(viewNode: ViewNode)

    /**
     * Updates an existing view node in the data source.
     * This method should handle updating the state of an existing view node
     * while preserving its identity.
     *
     * @param viewNode The view node to update
     */
    suspend fun updateViewNode(viewNode: ViewNode)
} 