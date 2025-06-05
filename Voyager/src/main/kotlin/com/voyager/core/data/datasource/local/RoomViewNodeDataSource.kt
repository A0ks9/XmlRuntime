package com.voyager.core.data.datasource.local

import android.content.Context
import com.voyager.core.datasource.ViewNodeDataSource
import com.voyager.core.db.DatabaseProvider
import com.voyager.core.db.ViewNodeDao
import com.voyager.core.model.ViewNode
import com.voyager.core.model.ConfigManager
import com.voyager.core.utils.ContextUtils.name
import com.voyager.core.utils.logging.LoggerFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Room-based implementation of [ViewNodeDataSource] for local storage of view nodes.
 * This class provides a persistent storage solution for view nodes using Room database,
 * with in-memory caching for improved performance.
 *
 * Features:
 * - In-memory caching for frequently accessed view nodes
 * - Coroutine-based asynchronous operations
 * - Thread-safe database access
 * - Automatic activity name resolution
 *
 * @property context The application context used for database access and activity name resolution
 */
class RoomViewNodeDataSource(
    private val context: Context
) : ViewNodeDataSource {
    private val logger = LoggerFactory.getLogger(RoomViewNodeDataSource::class.java.simpleName)
    private val config = ConfigManager.config

    /** Room DAO for database operations */
    private val viewNodeDao: ViewNodeDao = DatabaseProvider.getInstance(context).viewNodeDao()
    
    /** Activity name derived from the context */
    private val activityName: String = context.name
    
    /** In-memory cache for the current view node */
    private var cachedViewNode: ViewNode? = null

    /**
     * Checks if a view node exists for the current activity.
     * Uses the in-memory cache first, then falls back to database query.
     *
     * @return true if a view node exists, false otherwise
     */
    override suspend fun hasViewNode(): Boolean = withContext(Dispatchers.IO) {
        try {
            cachedViewNode = viewNodeDao.getViewNode(activityName)
            if (config.isLoggingEnabled) {
                logger.debug("hasViewNode", "View node exists: ${cachedViewNode != null}")
            }
            cachedViewNode != null
        } catch (e: Exception) {
            if (config.isLoggingEnabled) {
                logger.error("hasViewNode", "Error checking view node existence", e)
            }
            false
        }
    }

    /**
     * Retrieves the view node for the current activity.
     * Uses the in-memory cache first, then falls back to database query.
     *
     * @return The view node if found, null otherwise
     */
    override suspend fun getViewNode(): ViewNode? = withContext(Dispatchers.IO) {
        try {
            cachedViewNode ?: viewNodeDao.getViewNode(activityName).also { 
                cachedViewNode = it
                if (config.isLoggingEnabled) {
                    logger.debug("getViewNode", "Retrieved view node from database")
                }
            }
        } catch (e: Exception) {
            if (config.isLoggingEnabled) {
                logger.error("getViewNode", "Error retrieving view node", e)
            }
            null
        }
    }

    /**
     * Inserts a new view node into the database.
     * Updates the in-memory cache after successful insertion.
     *
     * @param viewNode The view node to insert
     */
    override suspend fun insertViewNode(viewNode: ViewNode) = withContext(Dispatchers.IO) {
        try {
            cachedViewNode = viewNode
            viewNodeDao.insertViewNode(viewNode)
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
     * Updates an existing view node in the database.
     * Updates the in-memory cache after successful update.
     *
     * @param viewNode The view node to update
     */
    override suspend fun updateViewNode(viewNode: ViewNode) = withContext(Dispatchers.IO) {
        try {
            cachedViewNode = viewNode
            viewNodeDao.updateViewsState(viewNode)
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