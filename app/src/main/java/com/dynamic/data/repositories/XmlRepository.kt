package com.dynamic.data.repositories

import android.content.ContentResolver
import android.net.Uri
import com.dynamic.data.sources.remote.XmlFileDataSource

class XmlRepository(private val xmlFileDataSource: XmlFileDataSource) {

    fun convertXmlToJson(xmlPath: String, callback: (String?) -> Unit) {
        xmlFileDataSource.convertXml(xmlPath, callback)
    }

    fun getFileNameFromUri(contentResolver: ContentResolver, uri: Uri, callback: (String) -> Unit) {
        xmlFileDataSource.getFileNameFromUri(
            contentResolver, uri, callback
        )
    }
}