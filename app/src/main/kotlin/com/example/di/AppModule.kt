package com.example.di

import com.example.ui.viewModels.MainViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val AppModule = module {
    viewModel { MainViewModel() }
}