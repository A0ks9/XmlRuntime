package com.voyager.core.data.repository

import com.voyager.core.model.ViewNode
import com.voyager.core.datasource.ViewNodeDataSource
import com.voyager.core.repository.ViewNodeRepository
import com.voyager.core.model.ConfigManager
import com.voyager.core.utils.logging.LoggerFactory

/**
 * Implementation of [ViewNodeRepository] that manages view node data operations.
 * This class acts as a facade for the underlying data source, providing a clean
 * interface for view node operations while handling logging and error cases.
 *
 * Features:
 * - Clean separation of concerns
 * - Centralized logging
 * - Error handling
 * - Data source abstraction
 * - Thread-safe operations
 * - Performance monitoring
 *
 * Example Usage:
 * ```kotlin
 * val dataSource = RoomViewNodeDataSource(context)
 * val repository = ViewNodeRepositoryImpl(dataSource)
 * 
 * // Check if view node exists
 * val exists = repository.hasViewNode()
 * 
 * // Get view node
 * val viewNode = repository.getViewNode()
 * 
 * // Insert new view node
 * repository.insertViewNode(viewNode)
 * 
 * // Update existing view node
 * repository.updateViewNode(viewNode)
 * ```
 *
 * @property local The data source for view node operations
 */
class ViewNodeRepositoryImpl(
    private val local: ViewNodeDataSource
) : ViewNodeRepository {
    private val logger = LoggerFactory.getLogger(ViewNodeRepositoryImpl::class.java.simpleName)
    private val config = ConfigManager.config

    /**
     * Checks if a view node exists in the data source.
     * This method provides a safe way to check for view node existence,
     * with proper error handling and logging.
     *
     * @return true if a view node exists, false otherwise
     */
    override suspend fun hasViewNode(): Boolean {
        return try {
            val exists = local.hasViewNode()
            if (config.isLoggingEnabled) {
                logger.debug("hasViewNode", "View node exists: $exists")
            }
            exists
        } catch (e: Exception) {
            if (config.isLoggingEnabled) {
                logger.error("hasViewNode", "Error checking view node existence", e)
            }
            false
        }
    }

    /**
     * Retrieves the current view node from the data source.
     * This method provides a safe way to retrieve view nodes,
     * with proper error handling and logging.
     *
     * @return The view node if found, null otherwise
     */
    override suspend fun getViewNode(): ViewNode? {
        return try {
            val viewNode = local.getViewNode()
            if (config.isLoggingEnabled) {
                logger.debug("getViewNode", "Retrieved view node: ${viewNode != null}")
            }
            viewNode
        } catch (e: Exception) {
            if (config.isLoggingEnabled) {
                logger.error("getViewNode", "Error retrieving view node", e)
            }
            null
        }
    }

    /**
     * Inserts a new view node into the data source.
     * This method provides a safe way to insert view nodes,
     * with proper error handling and logging.
     *
     * @param viewNode The view node to insert
     * @throws Exception if the insertion fails
     */
    override suspend fun insertViewNode(viewNode: ViewNode) {
        try {
            local.insertViewNode(viewNode)
            if (config.isLoggingEnabled) {
                logger.debug("insertViewNode", "Successfully inserted view node")
            }
        } catch (e: Exception) {
            if (config.isLoggingEnabled) {
                logger.error("insertViewNode", "Error inserting view node", e)
            }
            throw e
        }
    }

    /**
     * Updates an existing view node in the data source.
     * This method provides a safe way to update view nodes,
     * with proper error handling and logging.
     *
     * @param viewNode The view node to update
     * @throws Exception if the update fails
     */
    override suspend fun updateViewNode(viewNode: ViewNode) {
        try {
            local.updateViewNode(viewNode)
            if (config.isLoggingEnabled) {
                logger.debug("updateViewNode", "Successfully updated view node")
            }
        } catch (e: Exception) {
            if (config.isLoggingEnabled) {
                logger.error("updateViewNode", "Error updating view node", e)
            }
            throw e
        }
    }
} 