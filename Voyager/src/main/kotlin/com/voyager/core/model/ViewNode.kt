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
import kotlinx.serialization.Serializable

/**
 * Represents a node in a declarative view hierarchy for Voyager.
 * Optimized for Room, Parcelable, and kotlinx.serialization.
 */
@Entity(
    tableName = "view_nodes",
    indices = [Index(value = ["activityName"], unique = true)]
)
@TypeConverters(ViewNodeConverters::class)
@Serializable
data class ViewNode(
    val id: String? = null,
    val type: String,
    @PrimaryKey val activityName: String,
    @Serializable(with = ArrayMapSerializer::class)
    val attributes: ArrayMap<String, String>,
    val children: List<ViewNode> = emptyList(),
) : Parcelable {
    constructor(parcel: Parcel) : this(
        id = parcel.readString(),
        type = parcel.readString() ?: error("type is null"),
        activityName = parcel.readString() ?: error("activityName is null"),
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

// Extension for optimized ArrayMap parceling
private fun Parcel.writeArrayMapOptimized(map: ArrayMap<String, String>) {
    writeInt(map.size)
    for (i in 0 until map.size) {
        writeString(map.keyAt(i))
        writeString(map.valueAt(i))
    }
}
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