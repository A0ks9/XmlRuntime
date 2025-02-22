package com.runtimexml.data.repositories

import android.content.ContentResolver
import android.net.Uri
import com.runtimexml.data.sources.remote.XmlFileDataSource
import org.json.JSONObject

class XmlRepository(private val xmlFileDataSource: XmlFileDataSource) {

    fun convertXmlToJson(contentResolver: ContentResolver, uri: Uri): JSONObject? {
        return xmlFileDataSource.convertXml(contentResolver, uri)
    }

    fun getFileNameFromUri(uri: Uri, contentResolver: ContentResolver): String? {
        return xmlFileDataSource.getFileNameFromUri(uri, contentResolver)
    }
}