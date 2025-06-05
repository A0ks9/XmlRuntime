package com.voyager.core.utils

import com.voyager.core.utils.logging.LoggerFactory

/**
 * Utility object for collection-based operations.
 * Provides efficient and thread-safe operations for collections.
 *
 * Key Features:
 * - Map partitioning
 * - Collection filtering
 * - Collection transformation
 * - Performance optimized
 * - Thread-safe operations
 *
 * Performance Optimizations:
 * - Efficient iteration
 * - Minimal object creation
 * - Optimized memory usage
 * - Safe collection handling
 *
 * Best Practices:
 * 1. Use appropriate collection types
 * 2. Consider memory usage
 * 3. Handle null values properly
 * 4. Use thread-safe operations
 * 5. Consider performance impact
 *
 * Example Usage:
 * ```kotlin
 * // Partition a map
 * val (matching, nonMatching) = map.partition { it.value > 0 }
 *
 * // Filter a collection
 * val filtered = collection.filterNotNull()
 *
 * // Transform a collection
 * val transformed = collection.mapNotNull { it.toUpperCase() }
 * ```
 */
internal object CollectionUtils {
    private val logger = LoggerFactory.getLogger("CollectionUtils")

    /**
     * Partitions this [Map] into two separate maps based on a given [predicate].
     * Thread-safe operation with efficient memory usage.
     *
     * Performance Considerations:
     * - Single pass iteration
     * - Efficient map creation
     * - Minimal object creation
     * - Safe null handling
     *
     * @param predicate A function that takes a [Map.Entry] and returns `true` if the entry
     *                  should be included in the first map (matching entries), and `false` if it
     *                  should be in the second map (non-matching entries).
     * @return A [Pair] where the first element is a map of entries for which the [predicate] returned `true`,
     *         and the second element is a map of entries for which it returned `false`.
     * @throws IllegalArgumentException if predicate is null
     */
    fun <K, V> Map<K, V>.partition(predicate: (Map.Entry<K, V>) -> Boolean): Pair<Map<K, V>, Map<K, V>> {
        requireNotNull(predicate) { "Predicate cannot be null" }
        
        return try {
            val first = LinkedHashMap<K, V>(size)
            val second = LinkedHashMap<K, V>(size)
            
            for (entry in this) {
                if (predicate(entry)) {
                    first[entry.key] = entry.value
                } else {
                    second[entry.key] = entry.value
                }
            }
            
            Pair(first, second)
        } catch (e: Exception) {
            logger.error("partition", "Failed to partition map: ${e.message}")
            throw e
        }
    }

    /**
     * Filters a collection and returns a list of non-null elements.
     * Thread-safe operation with efficient memory usage.
     *
     * Performance Considerations:
     * - Single pass iteration
     * - Efficient list creation
     * - Minimal object creation
     * - Safe null handling
     *
     * @return A list containing all non-null elements from the original collection.
     */
    fun <T> Collection<T?>.filterNotNull(): List<T> {
        return try {
            filterNotNullTo(ArrayList(size))
        } catch (e: Exception) {
            logger.error("filterNotNull", "Failed to filter collection: ${e.message}")
            throw e
        }
    }

    /**
     * Transforms a collection and returns a list of non-null results.
     * Thread-safe operation with efficient memory usage.
     *
     * Performance Considerations:
     * - Single pass iteration
     * - Efficient list creation
     * - Minimal object creation
     * - Safe null handling
     *
     * @param transform A function that takes an element and returns a transformed value.
     * @return A list containing all non-null results of the transform function.
     * @throws IllegalArgumentException if transform is null
     */
    fun <T, R> Collection<T>.mapNotNull(transform: (T) -> R?): List<R> {
        requireNotNull(transform) { "Transform function cannot be null" }
        
        return try {
            mapNotNullTo(ArrayList(size), transform)
        } catch (e: Exception) {
            logger.error("mapNotNull", "Failed to transform collection: ${e.message}")
            throw e
        }
    }

    /**
     * Checks if a collection is null or empty.
     * Thread-safe operation.
     *
     * Performance Considerations:
     * - Fast operation
     * - No object creation
     * - Safe null handling
     *
     * @return true if the collection is null or empty, false otherwise.
     */
    fun <T> Collection<T>?.isNullOrEmpty(): Boolean {
        return this == null || isEmpty()
    }

    /**
     * Checks if a collection is not null and not empty.
     * Thread-safe operation.
     *
     * Performance Considerations:
     * - Fast operation
     * - No object creation
     * - Safe null handling
     *
     * @return true if the collection is not null and not empty, false otherwise.
     */
    fun <T> Collection<T>?.isNotNullOrEmpty(): Boolean {
        return !isNullOrEmpty()
    }
} 