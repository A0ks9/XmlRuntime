package com.flipkart.android.proteus.toolbox

import java.util.HashMap

/**
 * Kotlin implementation of a bidirectional map (BiMap) using HashMaps for storage.
 *
 * This class provides a BiMap interface implementation where keys and values
 * are stored in HashMaps for efficient lookups. It ensures that both keys and
 * values are unique within the map, maintaining the bidirectional mapping property.
 *
 * Type parameters:
 * - K: The type of keys in the BiMap.
 * - V: The type of values in the BiMap.
 */
class HashBiMap<K, V> : BiMap<K, V> {

    private val map: HashMap<K, V> // Forward map: key -> value
    private val inverse: HashMap<V, K> // Inverse map: value -> key

    /**
     * Constructs an empty HashBiMap with the default initial capacity (16) and load factor (0.75).
     */
    constructor() { // Primary constructor if no initial capacity/load factor is needed
        map = HashMap()
        inverse = HashMap()
    }

    /**
     * Constructs an empty HashBiMap with the specified initial capacity and default load factor (0.75).
     *
     * @param initialCapacity The initial capacity of the HashMap.
     */
    constructor(initialCapacity: Int) { // Secondary constructor for initial capacity
        map = HashMap(initialCapacity)
        inverse = HashMap(initialCapacity)
    }

    /**
     * Constructs an empty HashBiMap with the specified initial capacity and load factor.
     *
     * @param initialCapacity The initial capacity of the HashMap.
     * @param loadFactor      The load factor of the HashMap.
     */
    constructor(
        initialCapacity: Int, loadFactor: Float
    ) { // Secondary constructor for initial capacity and load factor
        map = HashMap(initialCapacity, loadFactor)
        inverse = HashMap(initialCapacity, loadFactor)
    }

    /**
     * Associates the specified [value] with the specified [key] in this BiMap.
     * If the BiMap previously contained a mapping for the [key], the old value is replaced.
     * This is the standard `put` operation without forcing uniqueness of values.
     *
     * @param key   The key with which the specified [value] is to be associated. Null keys are permitted.
     * @param value The value to be associated with the specified [key]. Null values are permitted.
     * @return The previous value associated with the [key], or null if there was no mapping for the [key].
     */
    override fun put(key: K?, value: V?): V? {
        return put(key, value, false) // Calls the main put method with force=false
    }

    /**
     * Associates the specified [value] with the specified [key] in this BiMap, with optional value uniqueness enforcement.
     * If the BiMap previously contained a mapping for the [key], the old value is replaced.
     * If [force] is true and the [value] already exists as a value in the BiMap (mapped to some other key),
     * an [IllegalStateException] is thrown to indicate a violation of value uniqueness.
     *
     * @param key   The key with which the specified [value] is to be associated. Null keys are permitted.
     * @param value The value to be associated with the specified [key]. Null values are permitted.
     * @param force If true, enforces that the [value] is not already present as a value in the BiMap.
     * @return The previous value associated with the [key], or null if there was no mapping for the [key].
     * @throws IllegalStateException if [force] is true and the [value] is already present as a value.
     */
    override fun put(key: K?, value: V?, force: Boolean): V? {
        if (force && inverse.containsKey(value)) { // Check if forcing uniqueness and value already exists
            throw IllegalStateException("$value is already exists!") // Throw exception if value already exists and force is true
        }
        inverse[value!!] = key!! // Put the value-key mapping in the inverse map
        return map.put(
            key, value
        ) // Put the key-value mapping in the forward map and return the previous value
    }

    /**
     * Retrieves the value associated with the specified [key] from the BiMap.
     *
     * @param key The key whose associated value is to be returned. Must not be null.
     * @return The value to which the specified [key] is mapped, or null if this BiMap contains no mapping for the [key].
     */
    override fun getValue(key: K): V? {
        return map[key] // Simply get the value from the forward map using the key
    }

    /**
     * Retrieves the key associated with the specified [value] from the BiMap (inverse lookup).
     *
     * @param value The value whose associated key is to be returned. Must not be null.
     * @return The key to which the specified [value] is mapped, or null if this BiMap contains no mapping for the [value].
     */
    override fun getKey(value: V): K? {
        return inverse[value] // Simply get the key from the inverse map using the value
    }

    /**
     * Copies all of the mappings from the specified [map] to this BiMap.
     * For each entry in the input [map], it calls the [put] method of this BiMap to add the key-value pair.
     *
     * @param map Mappings to be stored in this BiMap. Must not be null.
     */
    override fun putAll(map: Map<K, V>) {
        map.forEach { (key, value) ->
            put(
                key, value
            )
        } // Iterate through the input map and put each entry
    }

    /**
     * Returns a [Set] view of the values contained in this BiMap.
     * The returned set is backed by the inverse map's key set, which effectively represents the values of the BiMap.
     * Changes to the BiMap are reflected in the set, and vice-versa (to the extent that set operations are supported).
     *
     * @return A set view of the values contained in this BiMap. Never returns null.
     */
    override fun values(): Set<V> {
        return inverse.keys // The keys of the inverse map are the values of the forward map
    }

    /**
     * Returns the inverse BiMap of this BiMap.
     * The inverse BiMap maps values to keys. This method creates a new HashBiMap that represents the inverse mapping.
     * Operations on the inverse BiMap do not directly affect this BiMap, as a new instance is created.
     *
     * @return The inverse view of this BiMap as a new HashBiMap instance. Never returns null.
     */
    override fun inverse(): BiMap<V, K> {
        return HashBiMap<V, K>(inverse.size).apply { // Create a new HashBiMap for the inverse mapping
            this@HashBiMap.map.forEach { (key, value) -> // Iterate through the original map
                put(value, key) // Put the reversed key-value pair into the new inverse map
            }
        }
    }
}