package com.voyager.data.models

import java.util.logging.Logger // Added import for Logger

/**
 * A singleton object responsible for holding and providing access to the global [VoyagerConfig] instance.
 *
 * The `ConfigManager` ensures that Voyager's configuration is initialized once and then
 * accessible throughout the library via the [config] property. This pattern helps in managing
 * global settings like resource providers and logging preferences consistently.
 *
 * Usage:
 * 1. Initialize early in your application lifecycle (e.g., in your Application class or
 *    when setting up dependency injection):
 *    ```kotlin
 *    val myResourcesProvider = // ... your implementation of ResourcesProvider
 *    val voyagerConfig = VoyagerConfig(provider = myResourcesProvider, isLoggingEnabled = BuildConfig.DEBUG)
 *    ConfigManager.initialize(voyagerConfig)
 *    ```
 * 2. Access the configuration anywhere in the library or application:
 *    ```kotlin
 *    val currentConfig = ConfigManager.config
 *    if (currentConfig.isLoggingEnabled) {
 *        // ... log something ...
 *    }
 *    ```
 */
object ConfigManager {
    /**
     * The internal, nullable backing field for the [VoyagerConfig] instance.
     * It is `null` until [initialize] is called.
     */
    private var _config: VoyagerConfig? = null

    // Added logger
    private val logger = Logger.getLogger(ConfigManager::class.java.name)

    /**
     * Provides access to the globally initialized [VoyagerConfig] instance.
     *
     * @throws IllegalStateException if accessed before [initialize] has been called.
     * @return The initialized [VoyagerConfig] instance.
     */
    val config: VoyagerConfig
        get() = _config ?: throw IllegalStateException(
            "VoyagerConfig not initialized. Call ConfigManager.initialize(config) first."
        )

    /**
     * Initializes the [ConfigManager] with the provided [VoyagerConfig].
     *
     * This method should ideally be called only once, typically at application startup
     * (e.g., in your `Application.onCreate()` or when setting up dependency injection).
     * If `initialize` is called again after the configuration has already been set,
     * a warning will be logged, and the new configuration attempt will be ignored,
     * preserving the original settings.
     *
     * @param newConfig The [VoyagerConfig] instance to set as the global configuration.
     */
    fun initialize(newConfig: VoyagerConfig) {
        // Check if already initialized
        if (_config == null) {
            _config = newConfig
            logger.info("VoyagerConfig initialized successfully.")
        } else {
            // Log a warning and ignore re-initialization
            logger.warning(
                "VoyagerConfig already initialized. Ignoring new initialization attempt. " +
                        "Current config will be retained."
            )
        }
    }
}