package com.runtimexml.di

import com.runtimexml.data.repositories.ViewStateRepository
import com.runtimexml.data.repositories.XmlRepository
import com.runtimexml.data.sources.local.RoomViewStateDataSource
import com.runtimexml.data.sources.remote.XmlFileDataSource
import com.runtimexml.ui.viewModels.MainViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    // Data Sources
    single { RoomViewStateDataSource(androidContext()) }
    single { XmlFileDataSource() }

    // Repositories
    single { ViewStateRepository(get()) }
    single { XmlRepository(get()) }

    // ViewModels
    viewModelOf(::MainViewModel)
}