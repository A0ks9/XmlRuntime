package com.voyager.data.repositories

import android.content.ContentResolver
import android.net.Uri
import com.voyager.data.sources.remote.XmlFileDataSource
import java.io.InputStream

class XmlRepository(private val xmlFileDataSource: XmlFileDataSource) {

    fun convertXmlToJson(inputStream: InputStream): String? = xmlFileDataSource.convertXml(inputStream)

    fun getFileNameFromUri(contentResolver: ContentResolver, uri: Uri, callback: (String) -> Unit) {
        xmlFileDataSource.getFileNameFromUri(
            contentResolver, uri, callback
        )
    }
}