/**
 * `ViewNode` is a high-performance data class representing a single node within a declarative
 * view hierarchy used by the Voyager framework. It's designed for efficient storage, serialization
 * (both via `kotlinx.serialization` and Android's `Parcelable` interface), and persistence
 * (as a Room [Entity]).
 *
 * This class is central to how Voyager defines UI structures. Each `ViewNode` encapsulates
 * the type of a UI element (e.g., "Button", "TextView"), its attributes, and its children,
 * forming a tree structure that mirrors the actual Android View hierarchy to be inflated.
 *
 * Key Design Aspects:
 *  - **Immutability (Partial):** Core properties like `type` and `activityName` are immutable.
 *    `id`, `attributes` (the map instance itself), and `children` can be modified, though careful
 *    consideration should be given to this, especially if a `ViewNode` is shared.
 *  - **Performance:**
 *    - Uses [ArrayMap] for `attributes` for better memory efficiency compared to `HashMap` for small maps.
 *    - Optimized `Parcelable` implementation ([writeArrayMapOptimized], [readArrayMapOptimized])
 *      for faster serialization/deserialization when passed between Android components.
 *  - **Persistence:** Annotated as a Room [Entity] (`view_nodes` table) with `activityName` as the
 *    primary key and indexed for efficient querying. [ViewNodeConverters] handle type conversion
 *    for complex fields like `attributes` and `children` for Room.
 *  - **Serialization:** Supports `kotlinx.serialization` for conversion to/from JSON,
 *    using [ArrayMapSerializer] for custom serialization of the `attributes` field.
 *
 * Example Usage:
 * ```kotlin
 * val buttonNode = ViewNode(
 *     id = "submitButton",
 *     type = "Button",
 *     activityName = "UserProfileActivity", // Typically the context where this view exists
 *     attributes = ArrayMap<String, String>().apply {
 *         put("text", "Submit")
 *         put("layout_width", "wrap_content")
 *     },
 *     children = emptyList() // A button usually has no children
 * )
 * ```
 *
 * @property id An optional unique identifier for this view node within its hierarchy or activity.
 *              Used for later retrieval or referencing. Can be `null` if not specified.
 * @property type The type of the UI element this node represents (e.g., "TextView", "LinearLayout",
 *                "com.example.custom.MyCustomView"). This string is used by [com.voyager.utils.processors.ViewProcessor]
 *                to instantiate the actual Android [android.view.View].
 * @property activityName The name of the Activity or a similar context identifier to which this
 *                      `ViewNode` (and its hierarchy, if it's a root) belongs. This serves as the
 *                      **@PrimaryKey** for Room database persistence.
 * @property attributes An [ArrayMap] holding the attributes for this view node (e.g., "text", "layout_width").
 *                      Keys are attribute names (String), and values are their corresponding string representations.
 *                      Uses a custom [ArrayMapSerializer] for `kotlinx.serialization`.
 *                      It's recommended to pre-allocate this map with an expected size if known during creation.
 * @property children A list of child [ViewNode]s, representing the nested structure of the UI.
 *                  Defaults to an empty list if this node has no children.
 *
 * @author Abdelrahman Omar
 * @since 1.0.0
 */
package com.voyager.data.models

import android.os.Parcel
import android.os.Parcelable
import androidx.collection.ArrayMap
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.voyager.data.database.ViewNodeConverters
import com.voyager.utils.serialization.ArrayMapSerializer // Corrected import path
import kotlinx.serialization.Serializable

/**
 * Data class representing a node in a declarative view hierarchy.
 * It is an [Entity] for Room database persistence, [Parcelable] for Android component communication,
 * and [Serializable] for Kotlinx Serialization (e.g., to/from JSON).
 *
 * The `tableName` for Room is "view_nodes". `activityName` serves as the primary key and is indexed
 * for uniqueness, implying one main ViewNode tree per activity/context.
 * [ViewNodeConverters] are used by Room to handle `List<ViewNode>` (for children) and `ArrayMap` (for attributes).
 */
@Entity(
    tableName = "view_nodes",
    indices = [Index(value = ["activityName"], unique = true)]
)
@TypeConverters(ViewNodeConverters::class)
@Serializable
data class ViewNode(
    /** Optional unique identifier for this node within its hierarchy. */
    var id: String? = null,

    /** The type of UI element this node represents (e.g., "Button", "com.example.MyCustomView"). */
    val type: String,

    /**
     * Name of the activity or context this node belongs to.
     * **Primary Key** for Room database storage.
     */
    @PrimaryKey var activityName: String,

    /**
     * Attributes for this view node (e.g., "text", "layout_width").
     * Uses [ArrayMap] for memory efficiency with small maps.
     * Serialized using [ArrayMapSerializer] for `kotlinx.serialization`.
     * It's good practice to pre-allocate with an expected size if known at creation.
     * Example: `attributes = ArrayMap<String, String>(5)`
     */
    @Serializable(with = ArrayMapSerializer::class)
    val attributes: ArrayMap<String, String>,

    /** List of child [ViewNode]s, forming the nested UI structure. Defaults to an empty list. */
    var children: List<ViewNode> = emptyList(),

    ) : Parcelable {

    /**
     * Secondary constructor used for deserializing a `ViewNode` from a [Parcel].
     * It reads the properties in the same order they were written by [writeToParcel].
     * Includes null checks for properties read from the parcel that are non-nullable in the primary constructor,
     * throwing an [IllegalStateException] if essential data is missing.
     *
     * @param parcel The [Parcel] from which to read the `ViewNode` data.
     * @throws IllegalStateException if `type` or `activityName` is null after reading from parcel.
     */
    constructor(parcel: Parcel) : this(
        id = parcel.readString(),
        type = parcel.readString()
            ?: throw IllegalStateException("Parcelled ViewNode 'type' cannot be null."),
        activityName = parcel.readString()
            ?: throw IllegalStateException("Parcelled ViewNode 'activityName' (PrimaryKey) cannot be null."),
        attributes = parcel.readArrayMapOptimized(), // Uses custom optimized Parcelable logic for ArrayMap
        children = parcel.createTypedArrayList(CREATOR) ?: emptyList() // Reads a list of ViewNode children
    )

    /**
     * Writes the `ViewNode` data to the provided [Parcel].
     * This method is part of the [Parcelable] interface implementation and is used to serialize
     * the object for inter-component communication (e.g., passing in an Intent).
     *
     * @param parcel The [Parcel] to which the `ViewNode` data will be written.
     * @param flags Additional flags about how the object should be written. May be 0 or [Parcelable.PARCELABLE_WRITE_RETURN_VALUE].
     */
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(type)
        parcel.writeString(activityName)
        parcel.writeArrayMapOptimized(attributes) // Uses custom optimized Parcelable logic for ArrayMap
        parcel.writeTypedList(children)          // Writes a list of ViewNode children
    }

    /**
     * Describes the kinds of special objects contained in this Parcelable instance's marshaled representation.
     * For `ViewNode`, this typically returns 0 as it does not contain file descriptors.
     *
     * @return A bitmask indicating the set of special object types marshaled by this Parcelable object instance.
     */
    override fun describeContents(): Int = 0

    /**
     * Companion object implementing the [Parcelable.Creator] interface, required to deserialize
     * `ViewNode` instances from a [Parcel].
     */
    companion object CREATOR : Parcelable.Creator<ViewNode> {
        /**
         * Creates a new instance of `ViewNode` by deserializing it from the given [Parcel].
         * Delegates to the secondary constructor `ViewNode(parcel)`.
         *
         * @param parcel The Parcel from which to read the object.
         * @return A new instance of `ViewNode`.
         */
        override fun createFromParcel(parcel: Parcel): ViewNode = ViewNode(parcel)

        /**
         * Creates a new array of `ViewNode`.
         *
         * @param size Size of the array to create.
         * @return An array of `ViewNode?` of the specified size, initialized to nulls.
         */
        override fun newArray(size: Int): Array<ViewNode?> = arrayOfNulls(size)
    }
}

/**
 * Optimized extension function for [Parcel] to write an [ArrayMap<String, String>] efficiently.
 *
 * This method avoids creating intermediate objects (like a `Bundle` or standard `Map`) for serialization.
 * It first writes the size of the map, then iterates through the map using direct key/value access
 * (`keyAt(i)`, `valueAt(i)`), writing each key and value string. This is generally more performant
 * for `ArrayMap` than default `Parcelable` map/bundle serialization.
 *
 * @receiver The [Parcel] to write the map data to.
 * @param map The [ArrayMap<String, String>] to be serialized.
 */
private fun Parcel.writeArrayMapOptimized(map: ArrayMap<String, String>) {
    val size = map.size
    writeInt(size) // Write map size first

    // Iterate using direct index access for ArrayMap performance
    for (i in 0 until size) {
        writeString(map.keyAt(i))
        writeString(map.valueAt(i))
    }
}

/**
 * Optimized extension function for [Parcel] to read (deserialize) an [ArrayMap<String, String>] efficiently.
 *
 * This method first reads the size of the map. It then pre-allocates an [ArrayMap] with this exact size,
 * which can improve performance by avoiding reallocations as elements are added.
 * It then reads key-value pairs in a loop, populating the map.
 *
 * @receiver The [Parcel] from which to read the map data.
 * @return The deserialized [ArrayMap<String, String>].
 */
private fun Parcel.readArrayMapOptimized(): ArrayMap<String, String> {
    val size = readInt() // Read map size first
    // Pre-allocate ArrayMap with the exact size for efficiency
    val map = ArrayMap<String, String>(size)

    // Read key-value pairs
    repeat(size) {
        val key = readString()
        val value = readString()
        // Ensure keys and values are not null before putting them in the map,
        // though Parcel.readString() can return null if a null string was written.
        if (key != null && value != null) {
            map.put(key, value)
        }
        // If keys/values could legitimately be null in the map, the ArrayMap type would be ArrayMap<String?, String?>
        // and this null check might change. For ArrayMap<String, String>, non-null is expected.
    }
    return map
}