package com.voyager.core.db

import android.content.Context
import androidx.room.Room
import com.voyager.core.model.ConfigManager
import com.voyager.core.utils.logging.LoggerFactory

/**
 * Singleton provider for the Room database instance.
 * This class ensures a single database instance is used throughout the application,
 * with proper thread safety and initialization.
 *
 * Features:
 * - Thread-safe singleton pattern
 * - Lazy initialization
 * - Database configuration
 * - Error handling
 * - Logging support
 *
 * Example Usage:
 * ```kotlin
 * val database = DatabaseProvider.getInstance(context)
 * val viewNodeDao = database.viewNodeDao()
 * ```
 */
object DatabaseProvider {
    private val logger = LoggerFactory.getLogger(DatabaseProvider::class.java.simpleName)
    private val config = ConfigManager.config

    /** Volatile instance to ensure thread safety */
    @Volatile
    private var INSTANCE: AppDatabase? = null

    /**
     * Gets the singleton instance of the database.
     * This method ensures thread-safe initialization of the database.
     *
     * @param context The application context
     * @return The database instance
     * @throws IllegalStateException if the database initialization fails
     */
    fun getInstance(context: Context): AppDatabase =
        INSTANCE ?: synchronized(this) {
            INSTANCE ?: try {
                if (config.isLoggingEnabled) {
                    logger.debug("getInstance", "Initializing database")
                }
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "view_nodes_database"
                ).build().also { 
                    INSTANCE = it
                    if (config.isLoggingEnabled) {
                        logger.debug("getInstance", "Database initialized successfully")
                    }
                }
            } catch (e: Exception) {
                if (config.isLoggingEnabled) {
                    logger.error("getInstance", "Failed to initialize database", e)
                }
                throw IllegalStateException("Failed to initialize database", e)
            }
        }
} 