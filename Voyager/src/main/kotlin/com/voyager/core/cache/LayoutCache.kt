package com.voyager.core.cache

import android.util.LruCache
import com.voyager.core.model.ConfigManager
import com.voyager.core.model.ViewNode
import com.voyager.core.utils.logging.LoggerFactory
import java.util.concurrent.ConcurrentHashMap

/**
 * Efficient cache implementation for storing parsed layouts in the Voyager framework.
 * This cache uses a dual-storage approach to optimize both memory usage and performance:
 * 1. LruCache for memory-efficient storage with automatic eviction of least recently used items
 * 2. ConcurrentHashMap for thread-safe permanent storage of all layouts
 *
 * Key Features:
 * - Memory-efficient caching with automatic eviction
 * - Thread-safe operations
 * - Dual-layer storage strategy
 * - Detailed logging for debugging
 * - Configurable cache size
 *
 * Example Usage:
 * ```kotlin
 * val cache = LayoutCache(maxSize = 50)
 * // Cache a layout
 * cache.put(xmlContent, viewNode)
 * // Retrieve a layout
 * val layout = cache.get(xmlContent)
 * // Clear cache
 * cache.clear()
 * ```
 *
 * @property maxSize The maximum number of items the LruCache can hold (default: 50)
 */
internal class LayoutCache(
    private val maxSize: Int = 50
) {
    private val logger = LoggerFactory.getLogger(LayoutCache::class.java.simpleName)
    private val config = ConfigManager.config

    /** LruCache for memory-efficient storage with automatic eviction */
    private val cache = LruCache<String, ViewNode>(maxSize)
    
    /** Thread-safe permanent storage for all cached layouts */
    private val cacheMap = ConcurrentHashMap<String, ViewNode>()

    init {
        if (config.isLoggingEnabled) {
            logger.debug("init", "Initialized LayoutCache with maxSize: $maxSize")
        }
    }

    /**
     * Retrieves a cached layout by its XML content.
     * First checks the LruCache for fast access, then falls back to the permanent storage.
     * This operation is thread-safe.
     *
     * @param xmlContent The XML content used as the cache key
     * @return The cached ViewNode or null if not found
     */
    fun get(xmlContent: String): ViewNode? {
        val result = cache.get(xmlContent) ?: cacheMap[xmlContent]
        if (config.isLoggingEnabled) {
            if (result != null) {
                logger.debug(
                    "get",
                    "Cache hit for XML content (length: ${xmlContent.length}, " +
                    "cache size: ${cache.size()}, total size: ${cacheMap.size})"
                )
            } else {
                logger.debug(
                    "get",
                    "Cache miss for XML content (length: ${xmlContent.length}, " +
                    "cache size: ${cache.size()}, total size: ${cacheMap.size})"
                )
            }
        }
        return result
    }

    /**
     * Stores a layout in both the LruCache and permanent storage.
     * If the LruCache is full, it will automatically evict the least recently used item.
     * This operation is thread-safe.
     *
     * @param xmlContent The XML content used as the cache key
     * @param layout The ViewNode to cache
     */
    fun put(xmlContent: String, layout: ViewNode) {
        if (config.isLoggingEnabled) {
            logger.debug(
                "put",
                "Caching layout for XML content (length: ${xmlContent.length}, " +
                "current cache size: ${cache.size()}, total size: ${cacheMap.size})"
            )
        }
        cache.put(xmlContent, layout)
        cacheMap[xmlContent] = layout
    }

    /**
     * Retrieves all cached layouts from the permanent storage.
     * This operation is thread-safe due to the use of ConcurrentHashMap.
     * Note that this returns all layouts, including those that may have been
     * evicted from the LruCache.
     *
     * @return List of all cached ViewNodes
     */
    fun getAll(): List<ViewNode> {
        val layouts = cacheMap.values.toList()
        if (config.isLoggingEnabled) {
            logger.debug(
                "getAll",
                "Retrieved ${layouts.size} cached layouts " +
                "(LruCache size: ${cache.size()}, total size: ${cacheMap.size})"
            )
        }
        return layouts
    }

    /**
     * Clears both the LruCache and permanent storage.
     * This operation is thread-safe and removes all cached layouts.
     */
    fun clear() {
        if (config.isLoggingEnabled) {
            logger.debug(
                "clear",
                "Clearing cache (LruCache size: ${cache.size()}, total size: ${cacheMap.size})"
            )
        }
        cache.evictAll()
        cacheMap.clear()
    }

    /**
     * Gets the current number of items in the LruCache.
     * Note that this may be less than the total number of cached items
     * as some may only exist in the permanent storage.
     *
     * @return The number of items in the LruCache
     */
    fun size(): Int {
        val currentSize = cache.size()
        if (config.isLoggingEnabled) {
            logger.debug(
                "size",
                "Current LruCache size: $currentSize, total size: ${cacheMap.size}"
            )
        }
        return currentSize
    }

    /**
     * Gets the maximum number of items the LruCache can hold.
     * This is the value specified during initialization.
     * Note that the total number of cached items may exceed this value
     * as the permanent storage has no size limit.
     *
     * @return The maximum LruCache capacity
     */
    fun maxSize(): Int {
        if (config.isLoggingEnabled) {
            logger.debug(
                "maxSize",
                "LruCache max size: $maxSize, current size: ${cache.size()}, " +
                "total size: ${cacheMap.size}"
            )
        }
        return maxSize
    }
}