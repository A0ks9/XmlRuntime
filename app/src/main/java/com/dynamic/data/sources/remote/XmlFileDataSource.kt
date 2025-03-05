package com.dynamic.data.sources.remote

import android.content.ContentResolver
import android.net.Uri
import com.dynamic.utils.FileHelper
import com.dynamic.utils.FileHelper.parseXML
import java.io.InputStream

class XmlFileDataSource {

    fun convertXml(inputStream: InputStream): String? = parseXML(inputStream)

    fun getFileNameFromUri(contentResolver: ContentResolver, uri: Uri, callback: (String) -> Unit) {
        FileHelper.getFileName(contentResolver, uri, callback)
    }
}