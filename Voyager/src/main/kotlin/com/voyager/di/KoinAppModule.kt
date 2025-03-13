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

fun appModule(provider: ResourcesProvider, isLoggingEnabled: Boolean = false) = module {
    // Data Sources
    single { RoomViewNodeDataSource(androidContext()) }
    single { XmlFileDataSource() }

    // Repositories
    single { ViewStateRepository(get()) }
    single { XmlRepository(get()) }

    single { provider }
    single {
        VoyagerConfig(
            version = "1.0.0-Beta01", isLoggingEnabled = isLoggingEnabled, provider = get()
        ).also {
            ConfigManager.initialize(it)
        }
    }
}