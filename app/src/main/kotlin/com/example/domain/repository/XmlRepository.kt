package com.example.domain.repository

import kotlinx.coroutines.flow.Flow

interface XmlRepository {
    suspend fun parseXml(xmlContent: String): Result<Any>
    suspend fun renderXml(xmlContent: String): Result<Any>
    fun observeXmlChanges(): Flow<Any>
    suspend fun cacheXml(xmlContent: String)
    suspend fun getCachedXml(): String?
} 