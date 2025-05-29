package com.voyager.data.sources.local.db

import android.content.Context
import androidx.room.Room

/**
 * Singleton provider for the [AppDatabase] instance.
 *
 * This object ensures that only one instance of the Room database is created and used
 * throughout the application, following a thread-safe lazy initialization pattern.
 */
internal object DatabaseProvider {
    /**
     * Volatile annotation ensures that writes to INSTANCE are immediately visible to other threads.
     */
    @Volatile
    private var INSTANCE: AppDatabase? = null

    /**
     * Returns the singleton instance of [AppDatabase].
     *
     * If the instance does not exist, it is created in a thread-safe manner using a synchronized block.
     * The database is built using Room's databaseBuilder, configured with the application context
     * and the database name "view_nodes_database".
     *
     * @param context The Android [Context] used to get the application context for database initialization.
     *                Using `context.applicationContext` is important to prevent memory leaks
     *                associated with activity or other short-lived contexts.
     * @return The singleton [AppDatabase] instance.
     */
    fun getInstance(context: Context): AppDatabase {
        // Double-checked locking pattern for thread-safe singleton initialization.
        return INSTANCE ?: synchronized(this) {
            // Second check, in case another thread initialized INSTANCE while the current thread was waiting for the lock.
            INSTANCE ?: run {
                val instance = Room.databaseBuilder(
                    context.applicationContext, // Use application context
                    AppDatabase::class.java,
                    "view_nodes_database" // Name of the database file
                )
                // Add any migration strategies or other configurations here if needed in the future.
                // .addMigrations(...)
                // .fallbackToDestructiveMigration() // Example: if migrations are not set up
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}