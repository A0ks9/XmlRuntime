package com.flipkart.android.proteus.toolbox

/**
 * Kotlin interface representing a bidirectional map (BiMap).
 *
 * A BiMap is a map that maintains a unique mapping between keys and values,
 * meaning both keys and values are unique within the map. It allows you to
 * retrieve a value by its key (forward mapping) and a key by its value
 * (inverse mapping).
 *
 * Type parameters:
 * - K: The type of keys in the BiMap.
 * - V: The type of values in the BiMap.
 */
interface BiMap<K, V> {

    /**
     * Associates the specified [value] with the specified [key] in this BiMap.
     * If the BiMap previously contained a mapping for the [key], the old value is replaced.
     * Also, if the BiMap previously contained a mapping for the [value] as a key, that entry is also removed
     * to maintain the bidirectional uniqueness.
     *
     * @param key The key with which the specified [value] is to be associated. Null keys are permitted.
     * @param value The value to be associated with the specified [key]. Null values are permitted.
     * @return The previous value associated with the [key], or null if there was no mapping for the [key].
     *         Returns null if [value] was previously associated with some key.
     */
    fun put(key: K?, value: V?): V?

    /**
     * Associates the specified [value] with the specified [key] in this BiMap.
     * If the BiMap previously contained a mapping for the [key], the old value is replaced.
     * Also, if the BiMap previously contained a mapping for the [value] as a key, that entry is also removed
     * to maintain the bidirectional uniqueness.
     *
     * @param key The key with which the specified [value] is to be associated. Null keys are permitted.
     * @param value The value to be associated with the specified [key]. Null values are permitted.
     * @param force If true, and if a value already exists in the bimap, it will be removed to allow the new key-value pair
     * @return The previous value associated with the [key], or null if there was no mapping for the [key].
     *         Returns null if [value] was previously associated with some key.
     */
    fun put(key: K?, value: V?, force: Boolean): V?

    /**
     * Returns the value to which the specified [key] is mapped in this BiMap.
     *
     * @param key The key whose associated value is to be returned. Must not be null.
     * @return The value to which the specified [key] is mapped, or null if this BiMap contains no mapping for the [key].
     */
    fun getValue(key: K): V?

    /**
     * Returns the key to which the specified [value] is mapped in this BiMap.
     *
     * @param value The value whose associated key is to be returned. Must not be null.
     * @return The key to which the specified [value] is mapped, or null if this BiMap contains no mapping for the [value].
     */
    fun getKey(value: V): K?

    /**
     * Copies all of the mappings from the specified [map] to this BiMap.
     * The effect of this call is equivalent to that of calling [put] on this BiMap once for each mapping in the specified map.
     *
     * @param map Mappings to be stored in this BiMap. Must not be null.
     */
    fun putAll(map: Map<K, V>)

    /**
     * Returns a [Set] view of the values contained in this BiMap.
     * The set is backed by the BiMap, so changes to the BiMap are reflected in the set, and vice-versa.
     *
     * @return A set view of the values contained in this BiMap. Never returns null.
     */
    fun values(): Set<V>

    /**
     * Returns the inverse BiMap of this BiMap.
     * The inverse BiMap maps values to keys. Operations on the inverse BiMap affect this BiMap, and vice-versa.
     *
     * @return The inverse view of this BiMap. Never returns null.
     */
    fun inverse(): BiMap<V, K>
}