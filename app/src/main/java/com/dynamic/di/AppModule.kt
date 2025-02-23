package com.dynamic.di

import com.dynamic.data.repositories.ViewStateRepository
import com.dynamic.data.repositories.XmlRepository
import com.dynamic.data.sources.local.RoomViewStateDataSource
import com.dynamic.data.sources.remote.XmlFileDataSource
import com.dynamic.ui.viewModels.MainViewModel
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