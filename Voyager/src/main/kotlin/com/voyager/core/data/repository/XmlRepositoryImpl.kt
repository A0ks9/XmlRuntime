package com.voyager.core.data.repository

import android.content.ContentResolver
import android.net.Uri
import com.voyager.core.datasource.XmlDataSource // Use the interface
import com.voyager.core.repository.XmlRepository // Use the interface
import java.io.InputStream

class XmlRepositoryImpl(
    private val xmlFileDataSource: XmlDataSource // Accept the interface
) : XmlRepository {
    override fun convertXmlToJson(inputStream: InputStream): String? = xmlFileDataSource.convertXml(inputStream)
    override fun getFileNameFromUri(contentResolver: ContentResolver, uri: Uri, callback: (String) -> Unit) =
        xmlFileDataSource.getFileNameFromUri(contentResolver, uri, callback)
} 