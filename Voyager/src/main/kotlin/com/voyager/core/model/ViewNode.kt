package com.voyager.core.model

import android.os.Parcel
import android.os.Parcelable
import androidx.collection.ArrayMap
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.voyager.core.data.utils.serialization.ArrayMapSerializer
import com.voyager.core.db.ViewNodeConverters
import com.voyager.core.exceptions.VoyagerRenderingException
import kotlinx.serialization.Serializable

/**
 * Represents a node in a declarative view hierarchy for Voyager.
 * Optimized for Room, Parcelable, and kotlinx.serialization.
 *
 * Features:
 * - Room database integration
 * - Parcelable serialization
 * - Kotlinx serialization support
 * - Attribute validation
 * - Child node management
 * - Activity name tracking
 *
 * Example Usage:
 * ```kotlin
 * val node = ViewNode(
 *     id = "root",
 *     type = "LinearLayout",
 *     activityName = "MainActivity",
 *     attributes = ArrayMap<String, String>().apply {
 *         put("orientation", "vertical")
 *         put("padding", "16dp")
 *     },
 *     children = listOf(
 *         ViewNode(
 *             type = "TextView",
 *             attributes = ArrayMap<String, String>().apply {
 *                 put("text", "Hello World")
 *             }
 *         )
 *     )
 * )
 * ```
 *
 * @property id Optional unique identifier for the node
 * @property type The type of view this node represents (e.g., "LinearLayout", "TextView")
 * @property activityName The name of the activity this node belongs to
 * @property attributes Map of view attributes and their values
 * @property children List of child view nodes
 * @throws VoyagerRenderingException.MissingAttributeException if required attributes are missing
 * @throws VoyagerRenderingException.InvalidAttributeValueException if attribute values are invalid
 */
@Entity(
    tableName = "view_nodes", indices = [Index(value = ["activityName"], unique = true)]
)
@TypeConverters(ViewNodeConverters::class)
@Serializable
data class ViewNode(
    val id: String? = null,
    val type: String,
    @PrimaryKey var activityName: String = "no_activity",
    @Serializable(with = ArrayMapSerializer::class) val attributes: ArrayMap<String, String>,
    val children: List<ViewNode> = emptyList(),
) : Parcelable {
    constructor(parcel: Parcel) : this(
        id = parcel.readString(),
        type = parcel.readString()
            ?: throw VoyagerRenderingException.MissingAttributeException("type", "unknown"),
        activityName = parcel.readString()
            ?: throw VoyagerRenderingException.MissingAttributeException("activityName", "unknown"),
        attributes = parcel.readArrayMapOptimized(),
        children = parcel.createTypedArrayList(CREATOR) ?: emptyList()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) = with(parcel) {
        writeString(id)
        writeString(type)
        writeString(activityName)
        writeArrayMapOptimized(attributes)
        writeTypedList(children)
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<ViewNode> {
        override fun createFromParcel(parcel: Parcel) = ViewNode(parcel)
        override fun newArray(size: Int) = arrayOfNulls<ViewNode?>(size)
    }
}

/**
 * Extension function for optimized ArrayMap parceling.
 * Writes the map size and key-value pairs to the parcel.
 */
private fun Parcel.writeArrayMapOptimized(map: ArrayMap<String, String>) {
    writeInt(map.size)
    for (i in 0 until map.size) {
        writeString(map.keyAt(i))
        writeString(map.valueAt(i))
    }
}

/**
 * Extension function for optimized ArrayMap unparceling.
 * Reads the map size and key-value pairs from the parcel.
 *
 * @return A new ArrayMap containing the parceled data
 */
private fun Parcel.readArrayMapOptimized(): ArrayMap<String, String> {
    val size = readInt()
    return ArrayMap<String, String>(size).apply {
        repeat(size) {
            val key = readString()
            val value = readString()
            if (key != null && value != null) put(key, value)
        }
    }
} 