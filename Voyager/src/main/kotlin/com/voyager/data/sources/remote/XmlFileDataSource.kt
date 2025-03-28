package com.voyager.data.sources.remote

import android.content.ContentResolver
import android.net.Uri
import com.voyager.utils.FileHelper
import com.voyager.utils.FileHelper.parseXML
import java.io.InputStream

class XmlFileDataSource {

    fun convertXml(inputStream: InputStream): String? = parseXML(inputStream)

    fun getFileNameFromUri(contentResolver: ContentResolver, uri: Uri, callback: (String) -> Unit) {
        FileHelper.getFileName(contentResolver, uri, callback)
    }
}