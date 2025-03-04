package com.dynamic.data.sources.remote

import android.content.ContentResolver
import android.net.Uri
import com.dynamic.utils.FileHelper
import com.dynamic.utils.FileHelper.parseXML

class XmlFileDataSource {

    fun convertXml(xmlPath: String): String? = parseXML(xmlPath)

    fun getFileNameFromUri(contentResolver: ContentResolver, uri: Uri, callback: (String) -> Unit) {
        FileHelper.getFileName(contentResolver, uri, callback)
    }
}