package com.voyager.data.models

import kotlinx.serialization.json.Json

internal object ViewNodeParser {

    private val JsonParser = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    fun fromJson(json: String?): ViewNode? = if (json.isNullOrBlank()) null else JsonParser.decodeFromString(json)
}