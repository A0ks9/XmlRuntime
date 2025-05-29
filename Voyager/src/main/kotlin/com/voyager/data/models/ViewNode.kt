/**
 * High-performance data class representing a node in a view hierarchy.
 *
 * This class provides optimized storage and serialization of UI element information,
 * supporting efficient data persistence and transfer between Android components.
 *
 * Key features:
 * - Memory-efficient attribute storage using ArrayMap
 * - Optimized Parcelable implementation
 * - Efficient serialization/deserialization
 * - Thread-safe due to immutable properties (id, children, activityName) post-construction.
 *   The `attributes` ArrayMap (also val) is not thread-safe for internal mutation,
 *   so it should not be modified after the ViewNode is created and shared.
 *   All properties are now `val`, promoting immutability.
 * - Comprehensive error handling
 *
 * Performance optimizations:
 * - Pre-allocated collections
 * - Efficient memory usage
 * - Optimized serialization
 * - Minimized object creation
 * - Safe resource handling
 *
 * Usage example:
 * ```kotlin
 * val viewNode = ViewNode(
 *     id = "button_1",
 *     type = "Button",
 *     activityName = "MainActivity",
 *     attributes = ArrayMap<String, String>().apply {
 *         put("text", "Click me")
 *         put("background", "#FF0000")
 *     }
 * )
 * ```
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
import com.voyager.utils.ArrayMapSerializer
import kotlinx.serialization.Serializable

/**
 * Represents a node in a view hierarchy with optimized storage and serialization.
 *
 * @property id Optional unique identifier for the view node within the activity's view hierarchy. Now immutable (val).
 * @property type The type of the view (e.g., "Button", "TextView").
 * @property activityName Name of the activity this view belongs to. Serves as the @PrimaryKey for persistence
 *                      (implying one main view hierarchy stored per activity) and is immutable (val).
 * @property attributes Map of view attributes using memory-efficient ArrayMap. Immutable (val) reference.
 *                      The contents of the ArrayMap should not be modified after ViewNode creation to ensure thread safety.
 * @property children Immutable list (val) of child ViewNodes forming the view hierarchy.
 *                      This ensures the hierarchy structure is not modified post-creation, aiding thread safety.
 */
@Entity(
    tableName = "view_nodes", indices = [Index(value = ["activityName"], unique = true)]
)
@TypeConverters(ViewNodeConverters::class)
@Serializable
data class ViewNode(
    val id: String? = null, // Changed var to val
    val type: String,
    @PrimaryKey val activityName: String, // Changed var to val for better immutability
    @Serializable(with = ArrayMapSerializer::class) val attributes: ArrayMap<String, String>, // Pre-allocate with expected size
    val children: List<ViewNode> = emptyList(), // Changed var to val
) : Parcelable {

    /**
     * Creates a ViewNode from a Parcel with optimized deserialization.
     *
     * @param parcel The Parcel containing the serialized data
     */
    constructor(parcel: Parcel) : this(
        id = parcel.readString(),
        type = parcel.readString() ?: throw IllegalStateException("Type cannot be null"),
        activityName = parcel.readString()
            ?: throw IllegalStateException("Activity name cannot be null"),
        attributes = parcel.readArrayMapOptimized(),
        children = parcel.createTypedArrayList(CREATOR) ?: emptyList()
    )

    /**
     * Writes the ViewNode to a Parcel with optimized serialization.
     *
     * @param parcel The Parcel to write to
     * @param flags Additional flags for serialization
     */
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(type)
        parcel.writeString(activityName)
        parcel.writeArrayMapOptimized(attributes)
        parcel.writeTypedList(children)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<ViewNode> {
        override fun createFromParcel(parcel: Parcel): ViewNode = ViewNode(parcel)
        override fun newArray(size: Int): Array<ViewNode?> = arrayOfNulls(size)
    }
}

/**
 * Optimized Parcelable serialization for ArrayMap.
 * Uses direct array access for better performance.
 *
 * @param map The ArrayMap to serialize
 */
private fun Parcel.writeArrayMapOptimized(map: ArrayMap<String, String>) {
    val size = map.size
    writeInt(size)

    // Use direct array access for better performance
    for (i in 0 until size) {
        writeString(map.keyAt(i))
        writeString(map.valueAt(i))
    }
}

/**
 * Optimized Parcelable deserialization for ArrayMap.
 * Pre-allocates the map with the correct size for efficiency.
 *
 * @return The deserialized ArrayMap
 */
private fun Parcel.readArrayMapOptimized(): ArrayMap<String, String> {
    val size = readInt()
    val map = ArrayMap<String, String>(size) // Pre-allocate with exact size

    // Use direct array access for better performance
    repeat(size) {
        val key = readString()
        val value = readString()
        if (key != null && value != null) {
            map.put(key, value)
        }
    }

    return map
}