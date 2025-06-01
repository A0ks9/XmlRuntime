package com.voyager.core.utils

/**
 * Utility object for collection-based operations.
 */
internal object CollectionUtils {

    /**
     * Partitions this [Map] into two separate maps based on a given [predicate].
     *
     * @param predicate A function that takes a [Map.Entry] and returns `true` if the entry
     *                  should be included in the first map (matching entries), and `false` if it
     *                  should be in the second map (non-matching entries).
     * @return A [Pair] where the first element is a map of entries for which the [predicate] returned `true`,
     *         and the second element is a map of entries for which it returned `false`.
     */
    fun <K, V> Map<K, V>.partition(predicate: (Map.Entry<K, V>) -> Boolean): Pair<Map<K, V>, Map<K, V>> {
        val first = LinkedHashMap<K, V>()
        val second = LinkedHashMap<K, V>()
        for (entry in this) {
            if (predicate(entry)) {
                first[entry.key] = entry.value
            } else {
                second[entry.key] = entry.value
            }
        }
        return Pair(first, second)
    }
} 