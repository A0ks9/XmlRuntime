package com.example.domain.usecase

import com.example.domain.repository.XmlRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ParseXmlUseCase(private val repository: XmlRepository) {
    suspend operator fun invoke(xmlContent: String): Result<Any> {
        return repository.parseXml(xmlContent)
    }

    fun observeParsing(): Flow<Any> = repository.observeXmlChanges()
} 