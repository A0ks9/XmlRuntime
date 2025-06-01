package com.example.domain.usecase

import com.example.domain.repository.XmlRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class RenderXmlUseCase(private val repository: XmlRepository) {
    suspend operator fun invoke(xmlContent: String): Result<Any> {
        return repository.renderXml(xmlContent)
    }

    fun observeRendering(): Flow<Any> = repository.observeXmlChanges()
} 