package com.example.data.repository

import com.example.domain.repository.XmlRepository
import com.voyager.core.Voyager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class XmlRepositoryImpl : XmlRepository {
    private val voyager = Voyager()
    private var cachedXml: String? = null

    override suspend fun parseXml(xmlContent: String): Result<Any> = withContext(Dispatchers.IO) {
        try {
            val result = voyager.parseXml(xmlContent)
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun renderXml(xmlContent: String): Result<Any> = withContext(Dispatchers.IO) {
        try {
            val result = voyager.renderXml(xmlContent)
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun observeXmlChanges(): Flow<Any> = flow {
        cachedXml?.let { xml ->
            emit(xml)
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun cacheXml(xmlContent: String) {
        withContext(Dispatchers.IO) {
            cachedXml = xmlContent
        }
    }

    override suspend fun getCachedXml(): String? = withContext(Dispatchers.IO) {
        cachedXml
    }
} 