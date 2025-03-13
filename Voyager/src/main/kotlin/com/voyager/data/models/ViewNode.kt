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
 * Represents a node in a view hierarchy.
 *
 * This class is designed to store information about a single UI element,
 * such as a button, text view, or layout, within an activity's view structure.
 * It supports serialization for data persistence and efficient data transfer,
 * including custom serialization for the [attributes] property using [ArrayMapSerializer].
 * It also implements Parcelable for efficient data passing between Android components.
 *
 * @property id A unique identifier for the view node within the activity. This, together with the activity name, forms a unique primary key in the database.
 * @property type The type of the view (e.g., "Button", "TextView", "LinearLayout").
 * @property activityName The name of the activity to which this view node belongs. Used as part of the composite primary key.
 * @property attributes A map of key-value pairs representing the view's attributes (e.g., "text", "color", "visibility"). Uses ArrayMap for memory efficiency.
 * @property children A list of child [ViewNode]s, forming a tree structure representing the view hierarchy.
 *
 * @constructor Creates a new ViewNode instance.
 *
 * @see ArrayMap
 * @see Parcelable
 * @see kotlinx.serialization.Serializable
 * @see ViewNodeConverters
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
    @Serializable(with = ArrayMapSerializer::class) val attributes: ArrayMap<String, String> = ArrayMap(),
    var children: List<ViewNode> = emptyList()
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readArrayMapOptimized(),
        parcel.createTypedArrayList(CREATOR) ?: mutableListOf()
    )

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

// **🚀 Optimized Parcelable Serialization for ArrayMap**
private fun Parcel.writeArrayMapOptimized(map: ArrayMap<String, String>) {
    writeInt(map.size)  // Write size for fast deserialization
    for (i in 0 until map.size) {
        writeString(map.keyAt(i))
        writeString(map.valueAt(i))
    }
}

private fun Parcel.readArrayMapOptimized(): ArrayMap<String, String> {
    val size = readInt()
    val map = ArrayMap<String, String>(size)  // Pre-allocate size for efficiency
    repeat(size) {
        val key = readString()
        val value = readString()
        if (key != null && value != null) {
            map[key] = value
        }
    }
    return map
}
