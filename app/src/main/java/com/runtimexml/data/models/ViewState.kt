package com.runtimexml.data.models

import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONArray

@Entity(
    tableName = "view_states",
    primaryKeys = ["activityName", "id"],
    indices = [Index(value = ["id"], unique = true), Index(value = ["activityName"])]
)
data class ViewState(
    val id: String, val activityName: String, // Store the activity name
    val type: String, val attributesJson: String, // Store attributes as JSON string
    var children: String? = null // Store children as JSON string
) : Parcelable {

    // Convert attributes JSON to Map<String, Any>
    fun getAttributes(): HashMap<String, String> {
        val type = object : TypeToken<HashMap<String, String>>() {}.type
        return Gson().fromJson(attributesJson, type)
    }

    fun retrieveChildren(): JSONArray? = try {
        JSONArray(children)
    } catch (error: Exception) {
        Log.e("ViewState", "Error retrieving children", error)
        null
    }

    // Writing object to Parcel
    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(id)
        dest.writeString(activityName)
        dest.writeString(type)
        dest.writeString(attributesJson) // Store JSON string directly
    }

    override fun describeContents(): Int = 0

    // Companion object for Parcelable implementation
    companion object CREATOR : Parcelable.Creator<ViewState> {
        override fun createFromParcel(parcel: Parcel): ViewState {
            return ViewState(
                parcel.readString() ?: "",
                parcel.readString() ?: "",
                parcel.readString() ?: "",
                parcel.readString() ?: ""
            )
        }

        override fun newArray(size: Int): Array<ViewState?> {
            return arrayOfNulls(size)
        }

        // Factory function to create ViewState from a Map<String, Any>
        fun from(
            id: String,
            activityName: String,
            type: String,
            attributes: Map<String, Any>,
            children: Map<String, Any>? = null
        ): ViewState {
            return ViewState(
                id, activityName, type, Gson().toJson(attributes), Gson().toJson(children)
            )
        }
    }
}