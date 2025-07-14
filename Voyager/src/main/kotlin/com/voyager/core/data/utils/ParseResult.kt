package com.voyager.core.data.utils

import com.voyager.core.model.ViewNode

/**
 * Result class that holds both the parsed JSON string and its SHA256 hash.
 * Used to return both values from the native XML parser in a single pass.
 */
data class ParseResult(
    val jsonString: ViewNode,
    val sha256Hash: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ParseResult

        if (jsonString != other.jsonString) return false
        if (!sha256Hash.contentEquals(other.sha256Hash)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = jsonString.hashCode()
        result = 31 * result + sha256Hash.contentHashCode()
        return result
    }
} 