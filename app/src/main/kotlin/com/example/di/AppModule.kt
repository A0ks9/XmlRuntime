package com.example.di

import com.example.data.repository.XmlRepositoryImpl
import com.example.domain.repository.XmlRepository
import com.example.domain.usecase.ParseXmlUseCase
import com.example.domain.usecase.RenderXmlUseCase
import com.example.presentation.viewmodel.XmlViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val AppModule = module {
    // Repositories
    single<XmlRepository> { XmlRepositoryImpl() }
    
    // Use Cases
    single { ParseXmlUseCase(get()) }
    single { RenderXmlUseCase(get()) }
    
    // ViewModels
    viewModel { XmlViewModel(get(), get()) }
}