package com.example.di

import com.example.ui.viewModels.MainViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module


val originalAppModule = module {
    viewModelOf(::MainViewModel)
}