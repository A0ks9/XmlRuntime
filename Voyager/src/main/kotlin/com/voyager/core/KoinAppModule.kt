package com.voyager.core

import com.voyager.core.model.ConfigManager
import com.voyager.core.model.VoyagerConfig
import com.voyager.core.repository.ViewNodeRepository
import com.voyager.core.data.repository.ViewNodeRepositoryImpl // Use implementation from data
import com.voyager.core.repository.XmlRepository
import com.voyager.core.data.repository.XmlRepositoryImpl // Use implementation from data
import com.voyager.core.datasource.ViewNodeDataSource // Use interface
import com.voyager.core.data.datasource.local.RoomViewNodeDataSource // Use implementation from data
import com.voyager.core.datasource.XmlDataSource // Use interface
import com.voyager.core.data.datasource.remote.XmlFileDataSource // Use implementation from data
import com.voyager.core.domain.usecase.ConvertXmlToJsonUseCase
import com.voyager.core.domain.usecase.GetFileNameFromUriUseCase
import com.voyager.core.domain.usecase.GetViewNodeUseCase
import com.voyager.core.domain.usecase.HasViewNodeUseCase
import com.voyager.core.domain.usecase.InsertViewNodeUseCase
import com.voyager.core.domain.usecase.UpdateViewNodeUseCase
import com.voyager.core.data.ResourcesProvider
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

/**
 * Koin module defining dependencies for the Voyager framework.
 */
fun appModule(provider: ResourcesProvider, isLoggingEnabled: Boolean = false) = module {
    // Data Sources
    single<ViewNodeDataSource> { RoomViewNodeDataSource(androidContext()) }
    single<XmlDataSource> { XmlFileDataSource() }

    // Repositories
    single<ViewNodeRepository> { ViewNodeRepositoryImpl(get()) }
    single<XmlRepository> { XmlRepositoryImpl(get()) }

    // Use Cases (Domain Layer)
    single { GetViewNodeUseCase(get()) }
    single { HasViewNodeUseCase(get()) }
    single { InsertViewNodeUseCase(get()) }
    single { UpdateViewNodeUseCase(get()) }
    single { ConvertXmlToJsonUseCase(get()) }
    single { GetFileNameFromUriUseCase(get()) }

    // Resources Provider
    single { provider }

    // Configuration
    single { 
        VoyagerConfig(
            version = "1.0.0-Beta01", 
            isLoggingEnabled = isLoggingEnabled, 
            provider = get()
        ).also { 
            ConfigManager.initialize(it)
        }
    }
} 