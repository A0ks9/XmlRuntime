package com.voyager.core.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.voyager.core.model.ViewNode

/**
 * Main database class for the Voyager framework.
 * This class defines the database configuration and provides access to DAOs.
 *
 * Features:
 * - Single table for view nodes
 * - Type converters for complex data types
 * - Room database integration
 * - Schema versioning
 *
 * @property entities List of entity classes to be included in the database
 * @property version Current database version
 * @property exportSchema Whether to export the database schema
 */
@Database(
    entities = [ViewNode::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    /**
     * Provides access to the ViewNode DAO.
     * This method is used to perform database operations on view nodes.
     *
     * @return An instance of ViewNodeDao
     */
    abstract fun viewNodeDao(): ViewNodeDao
} 