package com.voyager.data.models

import android.os.Parcel
import android.os.Parcelable
import androidx.collection.ArrayMap
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.voyager.data.database.ViewNodeConverters
import com.voyager.utils.ArrayMapSerializer
import kotlinx.serialization.Serializable

/**
 * `ViewNode` is a data class representing a single UI element within Voyager's internal view hierarchy.
 * It encapsulates the properties of a UI component, such as its type (e.g., "Button", "TextView"),
 * its attributes (e.g., "android:text", "app:layout_width"), and its child `ViewNode`s.
 *
 * This class serves multiple purposes:
 * - **Internal Representation:** It's the primary structure used by Voyager to understand and construct
 *   the view hierarchy from parsed layout definitions (XML or JSON).
 * - **Persistence (`@Entity`):** Annotated as a Room entity (`@Entity`), `ViewNode` instances can be cached
 *   in a local database. This allows Voyager to store pre-parsed layout structures for faster
 *   inflation in subsequent sessions, using `activityName` as the primary key for unique identification
 *   of a layout structure. [ViewNodeConverters] are used by Room to handle complex types like `ArrayMap` and `List<ViewNode>`.
 * - **Inter-Process Communication (`Parcelable`):** Implements the `Parcelable` interface, enabling `ViewNode`
 *   objects to be passed between Android components (e.g., Activities, Services) via Intents or Bundles.
 *   Custom optimized serialization logic for `ArrayMap` ([writeArrayMapOptimized], [readArrayMapOptimized])
 *   is used for better performance.
 * - **Memory Efficiency:** Utilizes [ArrayMap] for storing `attributes`. `ArrayMap` is generally more
 *   memory-efficient than `HashMap` for smaller collections, which is often the case for view attributes.
 * - **Serialization (`@Serializable`):** Also supports Kotlinx Serialization for other serialization needs,
 *   using a custom [ArrayMapSerializer].
 *
 * Example:
 * ```kotlin
 * val buttonNode = ViewNode(
 *     id = "myButton",
 *     type = "Button",
 *     activityName = "MyActivityLayout", // Serves as a unique key for this layout structure
 *     attributes = ArrayMap<String, String>().apply {
 *         put("android:layout_width", "wrap_content")
 *         put("android:text", "Click Me")
 *     },
 *     children = emptyList() // No children for this button
 * )
 * ```
 *
 * @property id An optional unique identifier for this specific view node (e.g., the `android:id` value).
 *              Can be `null` if the view element does not have an ID.
 * @property type The type of the UI element this node represents (e.g., "TextView", "LinearLayout",
 *                "com.example.CustomView"). This string is used by `ViewProcessor` to instantiate the actual view.
 * @property activityName The name or identifier for the layout or activity this `ViewNode` tree belongs to.
 *                      Used as the `@PrimaryKey` for Room database persistence, ensuring that each
 *                      layout structure (root `ViewNode` and its children) can be uniquely identified and cached.
 * @property attributes A map storing the attributes of this UI element. Keys are attribute names
 *                      (e.g., "android:text", "app:layout_constraintTop_toTopOf"), and values are their
 *                      corresponding string representations. Uses [ArrayMap] for memory efficiency.
 * @property children A list of child `ViewNode`s, representing the nested structure of the UI.
 *                    An empty list indicates this node has no children.
 *
 * @see com.voyager.utils.processors.ViewProcessor
 * @see com.voyager.utils.DynamicLayoutInflation
 * @see ViewNodeConverters
 * @see ArrayMapSerializer
 * @author Abdelrahman Omar
 * @since 1.0.0
 */
@Entity(
    tableName = "view_nodes", indices = [Index(value = ["activityName"], unique = true)]
)
@TypeConverters(ViewNodeConverters::class)
@Serializable
data class ViewNode(
    var id: String? = null,
    val type: String,
    @PrimaryKey var activityName: String,
    @Serializable(with = ArrayMapSerializer::class) val attributes: ArrayMap<String, String>,
    var children: List<ViewNode> = emptyList(),
) : Parcelable {

    /**
     * Secondary constructor used for deserializing a `ViewNode` from a [Parcel].
     * This is part of the `Parcelable` implementation, enabling the object to be reconstructed
     * after being passed through IPC mechanisms. It reads the properties in the same order
     * they were written by [writeToParcel].
     *
     * @param parcel The [Parcel] from which to read the `ViewNode` data.
     * @throws IllegalStateException if essential non-nullable fields like `type` or `activityName`
     *         are found to be null during deserialization from the parcel, indicating a malformed parcel.
     */
    constructor(parcel: Parcel) : this(
        id = parcel.readString(),
        type = parcel.readString() ?: throw IllegalStateException("Parcelled ViewNode 'type' cannot be null"),
        activityName = parcel.readString()
            ?: throw IllegalStateException("Parcelled ViewNode 'activityName' cannot be null"),
        attributes = parcel.readArrayMapOptimized(), // Uses custom optimized deserialization for ArrayMap
        children = parcel.createTypedArrayList(CREATOR) ?: emptyList() // Reads a list of child ViewNodes
    )

    /**
     * Flattens this object into a [Parcel].
     * This method is part of the `Parcelable` interface and is responsible for writing the
     * `ViewNode`'s properties to the provided [Parcel] in a specific order.
     * The custom [writeArrayMapOptimized] extension function is used for serializing the `attributes` map.
     *
     * @param parcel The [Parcel] in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     *              May be 0 or `Parcelable.PARCELABLE_WRITE_RETURN_VALUE`.
     */
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(type)
        parcel.writeString(activityName)
        parcel.writeArrayMapOptimized(attributes) // Uses custom optimized serialization for ArrayMap
        parcel.writeTypedList(children) // Writes a list of child ViewNodes
    }

    /**
     * Describes the kinds of special objects contained in this `Parcelable` instance's marshaled representation.
     * For `ViewNode`, this typically returns 0 as it does not contain file descriptors or other special objects
     * that need specific handling beyond standard `Parcelable` mechanisms for its direct properties.
     *
     * @return A bitmask indicating the set of special object types marshaled by this `Parcelable` object instance.
     *         Returns 0 if there are no special objects.
     */
    override fun describeContents(): Int = 0

    /**
     * A companion object implementing the [Parcelable.Creator] interface.
     * This `CREATOR` is essential for reconstructing `ViewNode` objects from a [Parcel].
     * Android's `Parcelable` framework uses this object to generate instances of `ViewNode`
     * and arrays of `ViewNode`.
     */
    companion object CREATOR : Parcelable.Creator<ViewNode> {
        /**
         * Creates a new instance of the `ViewNode` class, instantiating it from the given [Parcel]
         * whose data had been previously written by [Parcelable.writeToParcel].
         *
         * @param parcel The Parcel to read the object's data from.
         * @return A new instance of the `ViewNode` class.
         */
        override fun createFromParcel(parcel: Parcel): ViewNode = ViewNode(parcel)

        /**
         * Creates a new array of the `ViewNode` class.
         *
         * @param size Size of the array to create.
         * @return An array of the `ViewNode` class, with every entry initialized to `null`.
         */
        override fun newArray(size: Int): Array<ViewNode?> = arrayOfNulls(size)
    }
}

/**
 * Writes an [ArrayMap] with String keys and String values to a [Parcel] in an optimized manner.
 *
 * This private extension function first writes the size of the map, then iterates through
 * its key-value pairs using direct index-based access (`keyAt`, `valueAt`), which is generally
 * more performant for `ArrayMap` than iterator-based access, especially for serialization.
 *
 * @receiver The [Parcel] to write the `ArrayMap` data into.
 * @param map The [ArrayMap<String, String>] to be serialized.
 */
private fun Parcel.writeArrayMapOptimized(map: ArrayMap<String, String>) {
    val size = map.size
    writeInt(size) // Write the number of entries in the map.

    // Iterate using index access for potentially better performance with ArrayMap.
    for (i in 0 until size) {
        writeString(map.keyAt(i))
        writeString(map.valueAt(i))
    }
}

/**
 * Reads an [ArrayMap] with String keys and String values from a [Parcel] in an optimized manner.
 *
 * This private extension function first reads the size of the map, then pre-allocates an [ArrayMap]
 * with that exact size to avoid re-hashing or internal array resizing during population.
 * It then reads the key-value pairs and populates the map.
 *
 * @receiver The [Parcel] from which to read the `ArrayMap` data.
 * @return A new [ArrayMap<String, String>] instance deserialized from the Parcel.
 *         Returns an empty map if the serialized size was 0 or if keys/values are unexpectedly null.
 */
private fun Parcel.readArrayMapOptimized(): ArrayMap<String, String> {
    val size = readInt() // Read the number of entries.
    // Pre-allocate ArrayMap with the exact size for efficiency.
    val map = ArrayMap<String, String>(size)

    // Populate the map by reading each key-value pair.
    repeat(size) {
        val key = readString()
        val value = readString()
        // Ensure keys and values are not null before putting them into the map,
        // though Parcel.writeString should handle nulls by writing a specific marker.
        if (key != null && value != null) {
            map.put(key, value)
        }
        // If key or value is unexpectedly null from a non-null write, it might indicate parcel corruption
        // or an issue with how nulls were written. For robust deserialization, one might log a warning here.
    }
    return map
}