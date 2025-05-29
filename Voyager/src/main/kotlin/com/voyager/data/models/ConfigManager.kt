package com.voyager.data.models

/**
 * Singleton manager for accessing Voyager library configuration.
 *
 * This object provides a centralized way to initialize and access the [VoyagerConfig]
 * throughout the library. It ensures that the configuration is set before it's used.
 */
object ConfigManager {
    private var _config: VoyagerConfig? = null

    /**
     * The current [VoyagerConfig] for the library.
     *
     * @throws IllegalStateException if accessed before [initialize] is called.
     */
    val config: VoyagerConfig
        get() = _config ?: throw IllegalStateException("VoyagerConfig not initialized. Call ConfigManager.initialize(config) first.")

    /**
     * Initializes the [ConfigManager] with the provided [VoyagerConfig].
     *
     * This method must be called once, typically during application startup,
     * before any other Voyager library features that rely on this configuration are used.
     *
     * @param config The [VoyagerConfig] instance to be used by the library.
     */
    fun initialize(config: VoyagerConfig) {
        // Consider thread-safety if initialize can be called from multiple threads,
        // though typically it's called once at startup.
        // Using plain assignment here assuming single-threaded initialization context.
        if (_config != null) {
            // Optional: Log or handle re-initialization if necessary,
            // e.g., throw an IllegalStateException or log a warning.
            // For now, it allows overriding, which might be intended or not.
        }
        _config = config
    }
}