package com.flipkart.android.proteus

import android.os.Parcelable

/**
 * Kotlin interface for generating unique IDs.
 *
 * Implementations of this interface are responsible for generating and returning unique integer IDs
 * based on a given string key. The IDs are intended for use with `View.setId(int)`.
 *
 * The interface also extends [Parcelable], suggesting that implementations should be capable of being
 * serialized and deserialized, likely for state persistence or passing between components.
 */
interface IdGenerator : Parcelable { // Converted to Kotlin interface, inheriting from Parcelable

    /**
     * Generates and returns a unique ID for the given [id] key.
     *
     * If a unique ID has already been generated and is associated with the provided [id] key,
     * this method should return the existing ID. Otherwise, it should generate a new unique ID,
     * associate it with the [id] key, and return the newly generated ID.
     *
     * Implementations must ensure that IDs generated are unique within the scope of the generator
     * to avoid ID collisions when setting IDs for Android Views using `View.setId(int)`.
     *
     * @param id The string key for which to generate or retrieve a unique ID. Must not be null.
     * @return A unique integer ID for use with `View.setId(int)`.
     *         The returned ID will be the same for subsequent calls with the same [id] key.
     */
    fun getUnique(id: String): Int // Converted to Kotlin function, kept parameter and return type as in Java
}