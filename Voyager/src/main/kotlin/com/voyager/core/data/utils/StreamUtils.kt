package com.voyager.core.data.utils

import android.net.Uri
import java.io.InputStream
import java.security.MessageDigest
import java.util.concurrent.ConcurrentHashMap

/**
 * Utility object for stream and file hashing operations.
 */
internal object StreamUtils {

    private const val BUFFER_SIZE = 8 * 1024
    private val hashCache = ConcurrentHashMap<Uri, String>()

    /**
     * Computes the SHA-256 hash of the InputStream content.
     *
     * @receiver The InputStream to hash.
     * @param xmlUri The Uri associated with the stream, used for caching.
     * @return The SHA-256 hash as a hexadecimal string.
     */
    fun InputStream.sha256(xmlUri: Uri): String {
        // Check cache first
        hashCache.get(xmlUri)?.let {
            return it
        }

        val digest = MessageDigest.getInstance("SHA-256")
        val buffer = ByteArray(BUFFER_SIZE)
        var bytesRead: Int
        while (this.read(buffer).also { bytesRead = it } != -1) {
            digest.update(buffer, 0, bytesRead)
        }

        val hashed = digest.digest().joinToString("") { "%02x".format(it) }
        
        // Cache the result before returning
        hashCache.put(xmlUri, hashed)
        return hashed
    }
} 