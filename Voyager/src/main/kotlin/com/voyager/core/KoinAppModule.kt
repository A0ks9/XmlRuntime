package com.voyager.core

import android.content.Context
import com.voyager.core.cache.LayoutCache
import com.voyager.core.data.ResourcesProvider
import com.voyager.core.model.ConfigManager
import com.voyager.core.model.VoyagerConfig
import com.voyager.core.utils.logging.LoggerFactory
import org.koin.core.error.InstanceCreationException
import org.koin.dsl.module

/**
 * Koin module defining dependencies for the Voyager framework.
 *
 * Key features:
 * - Dependency injection setup
 * - Resource management
 * - Configuration handling
 * - Thread management
 * - Cache management
 *
 * Performance optimizations:
 * - Efficient dependency resolution
 * - Resource pooling
 * - Thread pool management
 * - Cache optimization
 *
 * Best practices:
 * - Use appropriate scopes
 * - Handle dependencies properly
 * - Implement proper error handling
 * - Use appropriate logging
 *
 * Example usage:
 * ```kotlin
 * // Initialize the module
 * startKoin {
 *     modules(appModule(resourcesProvider, isLoggingEnabled = true, themeResId = R.style.AppTheme))
 * }
 * ```
 */
fun appModule(
    provider: ResourcesProvider,
    caching: Boolean = true,
    isLoggingEnabled: Boolean = false,
    themeResId: Int,
) = module {
    val logger = LoggerFactory.getLogger("KoinAppModule")

    // Resources Provider
    single {
        try {
            provider.also {
                logger.info("init", "Initialized ResourcesProvider")
            }
        } catch (e: Exception) {
            logger.error(
                "init", "Failed to initialize ResourcesProvider: ${e.message}", e
            )
            throw InstanceCreationException("Failed to initialize ResourcesProvider", e)
        }
    }

    // Cache
    single {
        try {
            LayoutCache().also {
                logger.info("init", "Initialized LayoutCache")
            }
        } catch (e: Exception) {
            logger.error(
                "init", "Failed to initialize LayoutCache: ${e.message}", e
            )
            throw InstanceCreationException("Failed to initialize LayoutCache", e)
        }
    }

    // Configuration
    single {
        try {
            VoyagerConfig(
                caching = caching, isLoggingEnabled = isLoggingEnabled, provider = get()
            ).also {
                ConfigManager.initialize(it)
                logger.info("init", "Initialized VoyagerConfig")
            }
        } catch (e: Exception) {
            logger.error(
                "init", "Failed to initialize VoyagerConfig: ${e.message}", e
            )
            throw InstanceCreationException("Failed to initialize VoyagerConfig", e)
        }
    }

    // Voyager Core
    factory { (context: Context) ->
        try {
            Voyager(context, themeResId, get()).also {
                logger.info("init", "Initialized Voyager Core")
            }
        } catch (e: Exception) {
            logger.error(
                "init", "Failed to initialize Voyager Core: ${e.message}", e
            )
            throw InstanceCreationException("Failed to initialize Voyager Core", e)
        }
    }
}