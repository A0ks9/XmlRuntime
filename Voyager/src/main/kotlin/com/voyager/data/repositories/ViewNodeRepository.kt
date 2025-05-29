package com.voyager.data.repositories

import com.voyager.data.models.ViewNode
import com.voyager.data.sources.local.RoomViewNodeDataSource

/**
 * Repository responsible for managing [ViewNode] data persistence and retrieval.
 *
 * This class abstracts the underlying data source, providing a clean API for
 * view node operations.
 *
 * @property roomViewNodeDataSource The local data source for view node operations, likely Room-based.
 */
class ViewNodeRepository(private val roomViewNodeDataSource: RoomViewNodeDataSource) {

    /**
     * Checks if any [ViewNode] exists in the data source.
     *
     * @return `true` if at least one view node exists, `false` otherwise.
     *         This is a suspending function.
     */
    suspend fun hasViewNode(): Boolean {
        return roomViewNodeDataSource.hasViewNode()
    }

    /**
     * Retrieves a [ViewNode] from the data source.
     *
     * As per current underlying [RoomViewNodeDataSource] capabilities, this likely retrieves
     * a general or the primary [ViewNode] if multiple are not distinctly addressable by this method.
     *
     * @return The [ViewNode] if found, or `null` if no view node is available.
     *         This is a suspending function.
     */
    suspend fun getViewNode(): ViewNode? {
        return roomViewNodeDataSource.getViewNode()
    }

    /**
     * Inserts a [ViewNode] into the data source.
     *
     * If a view node with the same primary key already exists, its behavior (replace, ignore)
     * depends on the underlying data source implementation.
     *
     * @param viewNode The [ViewNode] to insert.
     *        This is a suspending function.
     */
    suspend fun insertViewNode(viewNode: ViewNode) {
        roomViewNodeDataSource.insertViewNode(viewNode)
    }

    /**
     * Updates an existing [ViewNode] in the data source.
     *
     * The view node is typically identified by its primary key for the update operation.
     *
     * @param viewNode The [ViewNode] to update.
     *        This is a suspending function.
     */
    suspend fun updateViewNode(viewNode: ViewNode) {
        roomViewNodeDataSource.updateViewNode(viewNode)
    }
}