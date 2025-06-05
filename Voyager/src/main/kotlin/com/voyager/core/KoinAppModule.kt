package com.voyager.core

import android.content.Context
import com.voyager.core.cache.LayoutCache
import com.voyager.core.data.ResourcesProvider
import com.voyager.core.data.datasource.local.RoomViewNodeDataSource
import com.voyager.core.data.repository.ViewNodeRepositoryImpl
import com.voyager.core.datasource.ViewNodeDataSource
import com.voyager.core.domain.usecase.ConvertXmlToJsonUseCase
import com.voyager.core.domain.usecase.GetFileNameFromUriUseCase
import com.voyager.core.domain.usecase.GetViewNodeUseCase
import com.voyager.core.domain.usecase.HasViewNodeUseCase
import com.voyager.core.domain.usecase.InsertViewNodeUseCase
import com.voyager.core.domain.usecase.UpdateViewNodeUseCase
import com.voyager.core.model.ConfigManager
import com.voyager.core.model.VoyagerConfig
import com.voyager.core.repository.ViewNodeRepository
import com.voyager.core.threading.DispatcherProvider
import com.voyager.core.utils.logging.LoggerFactory
import org.koin.android.ext.koin.androidContext
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
fun appModule(provider: ResourcesProvider, isLoggingEnabled: Boolean = false, themeResId: Int) =
    module {
        val logger = LoggerFactory.getLogger("KoinAppModule")

        // Data Sources
        single<ViewNodeDataSource> {
            try {
                RoomViewNodeDataSource(androidContext()).also {
                    logger.info("init", "Initialized RoomViewNodeDataSource")
                }
            } catch (e: Exception) {
                logger.error(
                    "init", "Failed to initialize RoomViewNodeDataSource: ${e.message}", e
                )
                throw InstanceCreationException("Failed to initialize RoomViewNodeDataSource", e)
            }
        }

        // Repositories
        single<ViewNodeRepository> {
            try {
                ViewNodeRepositoryImpl(get()).also {
                    logger.info("init", "Initialized ViewNodeRepository")
                }
            } catch (e: Exception) {
                logger.error(
                    "init", "Failed to initialize ViewNodeRepository: ${e.message}", e
                )
                throw InstanceCreationException("Failed to initialize ViewNodeRepository", e)
            }
        }

        // Use Cases (Domain Layer)
        single {
            try {
                GetViewNodeUseCase(get()).also {
                    logger.info("init", "Initialized GetViewNodeUseCase")
                }
            } catch (e: Exception) {
                logger.error(
                    "init", "Failed to initialize GetViewNodeUseCase: ${e.message}", e
                )
                throw InstanceCreationException("Failed to initialize GetViewNodeUseCase", e)
            }
        }

        single {
            try {
                HasViewNodeUseCase(get()).also {
                    logger.info("init", "Initialized HasViewNodeUseCase")
                }
            } catch (e: Exception) {
                logger.error(
                    "init", "Failed to initialize HasViewNodeUseCase: ${e.message}", e
                )
                throw InstanceCreationException("Failed to initialize HasViewNodeUseCase", e)
            }
        }

        single {
            try {
                InsertViewNodeUseCase(get()).also {
                    logger.info("init", "Initialized InsertViewNodeUseCase")
                }
            } catch (e: Exception) {
                logger.error(
                    "init", "Failed to initialize InsertViewNodeUseCase: ${e.message}", e
                )
                throw InstanceCreationException("Failed to initialize InsertViewNodeUseCase", e)
            }
        }

        single {
            try {
                UpdateViewNodeUseCase(get()).also {
                    logger.info("init", "Initialized UpdateViewNodeUseCase")
                }
            } catch (e: Exception) {
                logger.error(
                    "init", "Failed to initialize UpdateViewNodeUseCase: ${e.message}", e
                )
                throw InstanceCreationException("Failed to initialize UpdateViewNodeUseCase", e)
            }
        }

        single {
            try {
                ConvertXmlToJsonUseCase(get()).also {
                    logger.info("init", "Initialized ConvertXmlToJsonUseCase")
                }
            } catch (e: Exception) {
                logger.error(
                    "init", "Failed to initialize ConvertXmlToJsonUseCase: ${e.message}", e
                )
                throw InstanceCreationException("Failed to initialize ConvertXmlToJsonUseCase", e)
            }
        }

        single {
            try {
                GetFileNameFromUriUseCase(get()).also {
                    logger.info("init", "Initialized GetFileNameFromUriUseCase")
                }
            } catch (e: Exception) {
                logger.error(
                    "init", "Failed to initialize GetFileNameFromUriUseCase: ${e.message}", e
                )
                throw InstanceCreationException("Failed to initialize GetFileNameFromUriUseCase", e)
            }
        }

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

        // Threading
        single {
            try {
                DispatcherProvider().also {
                    logger.info("init", "Initialized DispatcherProvider")
                }
            } catch (e: Exception) {
                logger.error(
                    "init", "Failed to initialize DispatcherProvider: ${e.message}", e
                )
                throw InstanceCreationException("Failed to initialize DispatcherProvider", e)
            }
        }

        // Configuration
        single {
            try {
                VoyagerConfig(
                    version = "1.0.0-Beta01", isLoggingEnabled = isLoggingEnabled, provider = get()
                ).also {
                    ConfigManager.initialize(it)
                    logger.info("init", "Initialized VoyagerConfig with version: ${it.version}")
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
                Voyager(context, themeResId, get(), get()).also {
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