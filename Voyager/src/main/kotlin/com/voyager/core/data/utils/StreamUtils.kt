package com.voyager.core.data.utils

import android.net.Uri
import com.voyager.core.model.ConfigManager
import com.voyager.core.utils.logging.LoggerFactory
import java.io.InputStream
import java.security.MessageDigest
import java.util.concurrent.ConcurrentHashMap

/**
 * Utility object for stream and file hashing operations in the Voyager framework.
 * This object provides efficient methods for computing SHA-256 hashes of input streams
 * with caching support to avoid redundant computations.
 *
 * Key Features:
 * - SHA-256 hash computation
 * - Thread-safe caching
 * - Memory-efficient buffer handling
 * - Detailed logging
 *
 * Example Usage:
 * ```kotlin
 * val inputStream = context.contentResolver.openInputStream(uri)
 * val hash = inputStream?.sha256(uri)
 * ```
 *
 * @author Abdelrahman Omar
 * @since 1.0.0
 */
internal object StreamUtils {
    private val logger = LoggerFactory.getLogger(StreamUtils::class.java.simpleName)
    private val config = ConfigManager.config

    /** Buffer size for efficient stream reading (8KB) */
    private const val BUFFER_SIZE = 8 * 1024

    /** Thread-safe cache for storing computed hashes */
    private val hashCache = ConcurrentHashMap<Uri, String>()

    /**
     * Computes the SHA-256 hash of the InputStream content.
     * This extension function provides a convenient way to hash stream contents
     * with caching support to avoid redundant computations.
     *
     * The function:
     * 1. Checks the cache first for existing hash
     * 2. If not cached, computes the hash using a buffer
     * 3. Caches the result before returning
     *
     * @receiver The InputStream to hash
     * @param xmlUri The Uri associated with the stream, used for caching
     * @return The SHA-256 hash as a hexadecimal string
     * @throws IllegalStateException if the stream cannot be read
     */
    fun InputStream.sha256(xmlUri: Uri): String {
        // Check cache first
        hashCache.get(xmlUri)?.let {
            if (config.isLoggingEnabled) {
                logger.debug("sha256", "Cache hit for URI: $xmlUri")
            }
            return it
        }

        if (config.isLoggingEnabled) {
            logger.debug("sha256", "Computing hash for URI: $xmlUri")
        }

        val digest = MessageDigest.getInstance("SHA-256")
        val buffer = ByteArray(BUFFER_SIZE)
        var bytesRead: Int
        var totalBytesRead = 0L

        try {
            while (this.read(buffer).also { bytesRead = it } != -1) {
                digest.update(buffer, 0, bytesRead)
                totalBytesRead += bytesRead
            }

            val hashed = digest.digest().joinToString("") { "%02x".format(it) }
            
            // Cache the result before returning
            hashCache.put(xmlUri, hashed)
            
            if (config.isLoggingEnabled) {
                logger.debug(
                    "sha256",
                    "Computed hash for URI: $xmlUri (size: $totalBytesRead bytes)"
                )
            }
            
            return hashed
        } catch (e: Exception) {
            if (config.isLoggingEnabled) {
                logger.error("sha256", "Error computing hash for URI: $xmlUri", e)
            }
            throw IllegalStateException("Failed to compute hash for URI: $xmlUri", e)
        }
    }

    /**
     * Clears the hash cache.
     * This method is useful when memory pressure is high or when
     * cached hashes are no longer needed.
     */
    fun clearCache() {
        if (config.isLoggingEnabled) {
            logger.debug("clearCache", "Clearing hash cache (size: ${hashCache.size})")
        }
        hashCache.clear()
    }
} 