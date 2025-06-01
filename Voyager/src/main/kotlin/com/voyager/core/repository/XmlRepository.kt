package com.voyager.core.repository

import android.content.ContentResolver
import android.net.Uri
import com.voyager.core.datasource.XmlFileDataSource
import java.io.InputStream

interface XmlRepository {
    fun convertXmlToJson(inputStream: InputStream): String?
    fun getFileNameFromUri(contentResolver: ContentResolver, uri: Uri, callback: (String) -> Unit)
}

class XmlRepositoryImpl(
    private val xmlFileDataSource: XmlFileDataSource
) : XmlRepository {
    override fun convertXmlToJson(inputStream: InputStream): String? = xmlFileDataSource.convertXml(inputStream)
    override fun getFileNameFromUri(contentResolver: ContentResolver, uri: Uri, callback: (String) -> Unit) =
        xmlFileDataSource.getFileNameFromUri(contentResolver, uri, callback)
} 