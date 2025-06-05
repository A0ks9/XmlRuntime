package com.voyager.core.model

import com.voyager.core.exceptions.VoyagerConfigException
import com.voyager.core.utils.logging.LoggerFactory

/**
 * Manages the global configuration for the Voyager framework.
 * Provides thread-safe access to configuration settings and initialization.
 *
 * Features:
 * - Thread-safe configuration management
 * - Single initialization point
 * - Lazy configuration access
 * - Detailed error handling
 * - Logging support
 *
 * Example Usage:
 * ```kotlin
 * // Initialize configuration
 * ConfigManager.initialize(VoyagerConfig(
 *     version = "1.0.0",
 *     isLoggingEnabled = true,
 *     provider = CustomResourcesProvider()
 * ))
 *
 * // Access configuration
 * val config = ConfigManager.config
 * ```
 *
 * @throws VoyagerConfigException.MissingConfigException if configuration is accessed before initialization
 * @throws VoyagerConfigException.InvalidConfigValueException if configuration is initialized multiple times
 */
object ConfigManager {
    private val logger = LoggerFactory.getLogger("ConfigManager")
    private var _config: VoyagerConfig? = null

    /**
     * Gets the current configuration.
     * Throws an exception if configuration hasn't been initialized.
     *
     * @return The current VoyagerConfig instance
     * @throws VoyagerConfigException.MissingConfigException if configuration is not initialized
     */
    val config: VoyagerConfig
        get() = _config
            ?: throw VoyagerConfigException.MissingConfigException("VoyagerConfig not initialized")

    /**
     * Initializes the configuration with the provided settings.
     * Can only be called once.
     *
     * @param config The configuration to initialize with
     * @throws VoyagerConfigException.InvalidConfigValueException if configuration is already initialized
     */
    fun initialize(config: VoyagerConfig) {
        if (_config != null) {
            val error = "VoyagerConfig already initialized"
            logger.error(message = error)
            throw VoyagerConfigException.InvalidConfigValueException(
                "config", "already initialized"
            )
        }
        _config = config
        logger.debug(message = "VoyagerConfig initialized with version: ${config.version}")
    }
}