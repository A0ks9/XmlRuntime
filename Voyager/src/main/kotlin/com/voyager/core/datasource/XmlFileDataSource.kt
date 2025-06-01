package com.voyager.core.datasource

import android.content.ContentResolver
import android.net.Uri
import com.voyager.core.data.utils.FileHelper
import java.io.InputStream

class XmlFileDataSource {
    fun convertXml(inputStream: InputStream): String? = FileHelper.parseXML(inputStream)
    fun getFileNameFromUri(contentResolver: ContentResolver, uri: Uri, callback: (String) -> Unit) =
        FileHelper.getFileName(contentResolver, uri, callback)
} 