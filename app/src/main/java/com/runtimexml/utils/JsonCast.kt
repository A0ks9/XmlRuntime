package com.runtimexml.utils

import org.json.JSONArray
import org.json.JSONObject

sealed class JsonCast {
    data class JsonO(val jsonObject: JSONObject) : JsonCast()
    data class JsonA(val jsonArray: JSONArray) : JsonCast()
}