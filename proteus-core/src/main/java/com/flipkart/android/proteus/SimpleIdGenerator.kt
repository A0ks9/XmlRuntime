package com.flipkart.android.proteus

import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import java.util.HashMap
import java.util.concurrent.atomic.AtomicInteger

/**
 * Kotlin implementation of SimpleIdGenerator, responsible for generating unique View IDs.
 *
 * `SimpleIdGenerator` implements the `IdGenerator` interface and provides a simple mechanism
 * for generating unique integer IDs for Android Views, similar to Android's auto-generated `R.id` values.
 * It uses an `AtomicInteger` to ensure thread-safe ID generation and a `HashMap` to store and reuse IDs for given keys.
 * It also implements `Parcelable` for state persistence across Activity/Fragment lifecycle events.
 */
class SimpleIdGenerator : IdGenerator,
    Parcelable { // Kotlin class declaration implementing IdGenerator and Parcelable

    /**
     * Creator for Parcelable implementation.
     * Used to create instances of SimpleIdGenerator from a Parcel.
     */
    companion object { // Companion object for static members, including CREATOR
        @JvmField
        val CREATOR: Creator<SimpleIdGenerator> =
            object : Creator<SimpleIdGenerator> { // Anonymous inner class implementing Creator
                override fun createFromParcel(source: Parcel): SimpleIdGenerator { // Override createFromParcel to create from Parcel
                    return SimpleIdGenerator(source) // Call secondary constructor to create from Parcel
                }

                override fun newArray(size: Int): Array<SimpleIdGenerator?> { // Override newArray to create array of SimpleIdGenerator
                    return arrayOfNulls(size) // Return an array of SimpleIdGenerator of the given size
                }
            }
    }

    /**
     * HashMap to store string ID keys and their corresponding generated integer IDs.
     * This allows reusing the same ID for the same key across multiple inflations.
     */
    private val idMap = HashMap<String, Int>() // HashMap to store ID mappings

    /**
     * AtomicInteger to generate monotonically increasing unique integer IDs.
     * Ensures thread-safe ID generation.
     */
    private val sNextGeneratedId: AtomicInteger // AtomicInteger for thread-safe ID generation

    /**
     * Primary constructor for SimpleIdGenerator.
     * Initializes the AtomicInteger with a starting value of 1.
     */
    constructor() { // Primary constructor
        sNextGeneratedId = AtomicInteger(1) // Initialize AtomicInteger starting from 1
    }

    /**
     * Secondary constructor for SimpleIdGenerator, used for Parcelable creation.
     * Reads the state (sNextGeneratedId and idMap) from a Parcel.
     *
     * @param source Parcel from which to read the state.
     */
    constructor(source: Parcel) { // Secondary constructor for Parcelable
        sNextGeneratedId = AtomicInteger(source.readInt()) // Read sNextGeneratedId from Parcel
        @Suppress(
            "UNCHECKED_CAST", "DEPRECATION"
        ) // Suppress warnings for unchecked cast and deprecation of readHashMap
        idMap.putAll(source.readHashMap(ClassLoader.getSystemClassLoader()) as HashMap<String, Int>) // Read idMap from Parcel
    }

    /**
     * Flatten this object in to a Parcel.
     * Called by the Android system when the Parcelable object needs to be written to a Parcel,
     * typically during state saving.
     *
     * @param dest  The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     *              May be 0 or {@link #PARCELABLE_WRITE_RETURN_VALUE}.
     */
    override fun writeToParcel(
        dest: Parcel, flags: Int
    ) { // Implementation of writeToParcel for Parcelable
        dest.writeInt(sNextGeneratedId.get()) // Write sNextGeneratedId to Parcel
        dest.writeMap(idMap) // Write idMap to Parcel
    }

    /**
     * Generates and returns a unique ID for the given ID key.
     * If an ID already exists for the key, it returns the existing ID; otherwise, it generates a new unique ID,
     * stores it for the key, and returns the new ID.
     *
     * @param idKey The string key for which to generate a unique ID.
     * @return      A unique integer ID for use with {@link android.view.View#setId(int)}.
     */
    override fun getUnique(idKey: String): Int { // Implementation of getUnique from IdGenerator
        return idMap.getOrPut(idKey) { // Use getOrPut for thread-safe and efficient ID retrieval/generation
            generateViewId() // Generate a new ID if key is not present
        }
    }

    /**
     * Generates a value suitable for use as a View ID.
     * This value will not collide with ID values generated at inflate time by aapt for R.id.
     * (Logic adapted from Android View source code API 17+)
     *
     * @return A generated unique ID value.
     */
    private fun generateViewId(): Int { // Private method to generate a View ID
        while (true) { // Loop to ensure atomic ID generation
            val result = sNextGeneratedId.get() // Get current ID value

            // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
            var newValue = result + 1 // Increment ID value
            if (newValue > 0x00FFFFFF) { // Check for overflow (above max aapt-generated ID)
                newValue = 1 // Roll over to 1, not 0 (Android View behavior)
            }
            if (sNextGeneratedId.compareAndSet(
                    result, newValue
                )
            ) { // Atomically update and check if successful
                return result // Return the generated ID if update was successful
            }
            // If compareAndSet fails, it means another thread changed sNextGeneratedId concurrently, so loop again
        }
    }

    /**
     * Describe the kinds of special objects contained in this Parcelable's marshalled representation.
     * In this case, there are no special objects, so it returns 0.
     *
     * @return A bitmask indicating the set of special object types marshalled by the Parcelable. Always 0 for SimpleIdGenerator.
     */
    override fun describeContents(): Int { // Implementation of describeContents for Parcelable
        return 0 // Indicate no special content
    }
}