package com.dynamic.data.sources.remote

import android.content.ContentResolver
import android.net.Uri
import com.dynamic.utils.FileHelper
import org.json.JSONObject

class XmlFileDataSource {

    fun convertXml(contentResolver: ContentResolver, uri: Uri): JSONObject? {
        return FileHelper.convertXml(contentResolver, uri)
    }

    fun getFileNameFromUri(uri: Uri, contentResolver: ContentResolver): String? {
        return FileHelper.getFileNameFromUri(uri, contentResolver)
    }
}