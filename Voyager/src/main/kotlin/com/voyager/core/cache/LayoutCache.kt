package com.voyager.core.cache

import android.util.LruCache
import com.voyager.core.model.ViewNode
import java.util.concurrent.ConcurrentHashMap

/**
 * Efficient cache for storing parsed layouts.
 * Uses LruCache for memory management and ConcurrentHashMap for thread safety.
 */
class LayoutCache(
    private val maxSize: Int = 50
) {
    private val cache = LruCache<String, ViewNode>(maxSize)
    private val cacheMap = ConcurrentHashMap<String, ViewNode>()

    /**
     * Gets a cached layout by its XML content.
     * @param xmlContent The XML content used as the key
     * @return The cached layout or null if not found
     */
    fun get(xmlContent: String): ViewNode? {
        return cache.get(xmlContent) ?: cacheMap[xmlContent]
    }

    /**
     * Puts a layout in the cache.
     * @param xmlContent The XML content used as the key
     * @param layout The layout to cache
     */
    fun put(xmlContent: String, layout: ViewNode) {
        cache.put(xmlContent, layout)
        cacheMap[xmlContent] = layout
    }

    /**
     * Gets all cached layouts.
     * @return List of all cached layouts
     */
    fun getAll(): List<ViewNode> {
        return cacheMap.values.toList()
    }

    /**
     * Clears the cache.
     */
    fun clear() {
        cache.evictAll()
        cacheMap.clear()
    }

    /**
     * Gets the current cache size.
     * @return The number of items in the cache
     */
    fun size(): Int {
        return cache.size()
    }

    /**
     * Gets the maximum cache size.
     * @return The maximum number of items the cache can hold
     */
    fun maxSize(): Int {
        return maxSize
    }
}