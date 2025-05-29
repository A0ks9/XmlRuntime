/**
 * Defines the Koin dependency injection module for the Voyager library.
 * This file sets up and provides the necessary dependencies that Voyager requires to operate,
 * such as data sources, repositories, and configuration objects.
 *
 * The [appModule] function is the central piece, constructing a Koin module
 * that applications using Voyager will need to include in their Koin setup.
 */
package com.voyager.di

import com.voyager.data.models.ConfigManager
import com.voyager.data.models.VoyagerConfig
import com.voyager.data.repositories.ViewStateRepository
import com.voyager.data.repositories.XmlRepository
import com.voyager.data.sources.local.RoomViewNodeDataSource
import com.voyager.data.sources.remote.XmlFileDataSource
import com.voyager.utils.interfaces.ResourcesProvider
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

/**
 * Creates and configures the Koin module for the Voyager library.
 * This function defines all the essential dependencies required for Voyager's functionality,
 * allowing them to be injected wherever needed within the library and potentially by the consuming application.
 *
 * @param provider A concrete implementation of [ResourcesProvider]. This is crucial as Voyager
 *                 relies on this provider to dynamically access application resources (like strings,
 *                 drawables, dimensions, etc.) by their names. The consuming application **must** supply
 *                 an instance of this interface.
 * @param isLoggingEnabled A boolean flag indicating whether Voyager's internal logging should be enabled.
 *                         Defaults to `false`. This can be tied to the application's `BuildConfig.DEBUG` flag.
 * @return A Koin [module] containing all the defined dependencies for Voyager.
 */
fun appModule(provider: ResourcesProvider, isLoggingEnabled: Boolean = false) = module {
    /**
     * Provides a singleton instance of [RoomViewNodeDataSource].
     * This data source is responsible for interacting with the Room database to store and retrieve
     * cached [com.voyager.data.models.ViewNode] structures. This caching mechanism helps in
     * speeding up layout inflation for previously processed layouts.
     * It requires an Android context, which is supplied by Koin's `androidContext()`.
     */
    single { RoomViewNodeDataSource(androidContext()) }

    /**
     * Provides a singleton instance of [XmlFileDataSource].
     * This data source is intended for reading layout definitions, presumably from XML files.
     * Note: While named `XmlFileDataSource`, Voyager also supports JSON. The exact scope of this
     * data source (XML only or broader) might need verification against its implementation details.
     * (Self-correction: Based on DynamicLayoutInflation, Voyager handles both XML and JSON via URIs.
     * This data source might be more generally for file-based raw layout data, or specifically for XML parsing if separate from JSON parsing.)
     */
    single { XmlFileDataSource() } // KDoc added

    /**
     * Provides a singleton instance of [ViewStateRepository].
     * This repository likely manages the state of views or view hierarchies, potentially by
     * coordinating data between different data sources (like [RoomViewNodeDataSource] for cached states)
     * and the rest of the library. It depends on [RoomViewNodeDataSource] ( Koin's `get()` ).
     */
    single { ViewStateRepository(get()) }

    /**
     * Provides a singleton instance of [XmlRepository].
     * This repository is likely responsible for fetching and managing layout definitions,
     * possibly focusing on XML sources or acting as a more general layout definition provider.
     * It depends on [XmlFileDataSource] ( Koin's `get()` ).
     */
    single { XmlRepository(get()) }

    /**
     * Provides the singleton instance of [ResourcesProvider] that was passed into the [appModule] function.
     * This is the concrete implementation supplied by the consuming application. Voyager uses this
     * provider to dynamically access application resources (strings, drawables, styles, dimensions, etc.)
     * by their string names, which is essential for resolving resource references in dynamic layouts.
     */
    single { provider }

    /**
     * Provides a singleton instance of [VoyagerConfig].
     * This data class holds global configuration settings for the Voyager library, such as
     * logging enablement status and the [ResourcesProvider] instance.
     * Immediately after creation, `ConfigManager.initialize(it)` is called to make this
     * configuration globally accessible throughout Voyager via the [ConfigManager].
     * It depends on the application-supplied [ResourcesProvider] ( Koin's `get()` ).
     */
    single {
        VoyagerConfig(
            version = "1.0.0-Beta01", // This version might be hardcoded or ideally dynamically set
            isLoggingEnabled = isLoggingEnabled,
            provider = get() // Injects the application-provided ResourcesProvider
        ).also {
            // Initialize the ConfigManager with this configuration object,
            // making it globally available in a static manner if needed.
            ConfigManager.initialize(it)
        }
    }
}