package com.voyager.data.sources.local

import android.content.Context
import com.voyager.data.models.ViewNode
import com.voyager.data.sources.local.db.DatabaseProvider
import com.voyager.data.sources.local.db.ViewNodeDao
import com.voyager.utils.getActivityName
import kotlinx.coroutines.*

/**
 * Local data source for [ViewNode] objects, utilizing a Room database (`ViewNodeDao`)
 * and providing a simple in-memory cache for the current activity's [ViewNode].
 *
 * This class is responsible for abstracting the direct interaction with the Room DAO
 * and aims to optimize data retrieval by caching the [ViewNode] associated with the
 * current activity context. All database operations are performed on the `Dispatchers.IO` context.
 *
 * @param context The Android [Context] used to initialize the database and retrieve the activity name.
 *                It's important that this context can provide a relevant activity name for caching
 *                and data retrieval if the data is activity-specific.
 */
class RoomViewNodeDataSource(context: Context) {

    private val viewNodeDao: ViewNodeDao = DatabaseProvider.getInstance(context).ViewNodeDao()
    private val activityName: String =
        context.getActivityName()  // Retrieve activity name once (reduces function calls)

    // Note on caching: `cachedViewNodes` is accessed and modified within `withContext(Dispatchers.IO)` blocks.
    // While `Dispatchers.IO` limits concurrency for DB operations executed on it,
    // the non-atomic check-then-act on `cachedViewNodes` (e.g., in `getViewNode`) across different
    // coroutines (if public methods are called concurrently from different dispatchers before switching to IO)
    // could theoretically lead to minor inconsistencies or redundant DB reads if not carefully managed.
    // For the current typical use case (caching a single ViewNode, operations mostly on IO dispatcher),
    // this risk is considered low. If more complex caching or stricter consistency is required,
    // a Mutex or other synchronization mechanism for `cachedViewNodes` read/writes might be necessary.
    private var cachedViewNodes: ViewNode? = null  // Caching to speed up repeated access

    /**
     * Checks if a [ViewNode] associated with the current activity exists in the database.
     * This operation also updates the cache if a node is found.
     * Executes database operations on `Dispatchers.IO`.
     * This is a suspending function.
     *
     * @return `true` if the [ViewNode] exists, `false` otherwise.
     */
    suspend fun hasViewNode(): Boolean = withContext(Dispatchers.IO) {
        // Attempt to refresh cache from DB to give a definite answer.
        cachedViewNodes = viewNodeDao.getViewNode(activityName)
        return@withContext cachedViewNodes != null
    }

    /**
     * Retrieves the [ViewNode] associated with the current activity.
     * It first checks the in-memory cache. If not found, it queries the database
     * and updates the cache.
     * Executes database operations on `Dispatchers.IO`.
     * This is a suspending function.
     *
     * @return The [ViewNode] if found in cache or database, or `null` otherwise.
     */
    suspend fun getViewNode(): ViewNode? = withContext(Dispatchers.IO) {
        // Check cache first
        cachedViewNodes?.let { return@withContext it }
        // If not in cache, query database and update cache
        cachedViewNodes = viewNodeDao.getViewNode(activityName)
        return@withContext cachedViewNodes
    }

    /**
     * Inserts a [ViewNode] into the database and updates the in-memory cache.
     * Executes database operations on `Dispatchers.IO`.
     * This is a suspending function.
     *
     * @param viewNode The [ViewNode] to insert.
     */
    suspend fun insertViewNode(viewNode: ViewNode) = withContext(Dispatchers.IO) {
        cachedViewNodes = viewNode  // Update cache with new data
        viewNodeDao.insertViewNode(viewNode)
    }

    /**
     * Updates an existing [ViewNode] in the database and updates the in-memory cache.
     * This typically involves replacing the existing node if it shares the same primary key.
     * Executes database operations on `Dispatchers.IO`.
     * This is a suspending function.
     *
     * @param viewNode The [ViewNode] to update.
     */
    suspend fun updateViewNode(viewNode: ViewNode) = withContext(Dispatchers.IO) {
        cachedViewNodes = viewNode  // Update cache with updated data
        viewNodeDao.updateViewsState(viewNode)
    }
}