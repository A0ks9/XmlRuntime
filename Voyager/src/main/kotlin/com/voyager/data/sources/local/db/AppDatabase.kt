package com.voyager.data.sources.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.voyager.data.models.ViewNode

/**
 * The central Room database definition for the Voyager module.
 *
 * This abstract class defines the database configuration, including its entities,
 * version, and provides access to the Data Access Objects (DAOs).
 *
 * Entities:
 * - [ViewNode]: Represents the view hierarchy data.
 *
 * Version: 1 (Ensure this is incremented and migrations are provided upon schema changes).
 *
 * Export Schema: Currently `false`. For production applications or libraries intended for
 * long-term maintainability, it is generally recommended to set `exportSchema` to `true`
 * and manage schema versions in your version control system. This aids in debugging
 * and understanding schema history.
 */
@Database(entities = [ViewNode::class], version = 1, exportSchema = false)
internal abstract class AppDatabase : RoomDatabase() {
    /**
     * Provides access to the [ViewNodeDao] for interacting with [ViewNode] data.
     *
     * @return An instance of [ViewNodeDao].
     */
    abstract fun ViewNodeDao(): ViewNodeDao
}