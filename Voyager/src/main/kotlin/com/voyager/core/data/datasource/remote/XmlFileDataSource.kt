package com.voyager.core.data.datasource.remote

import android.content.ContentResolver
import android.net.Uri
import com.voyager.core.datasource.XmlDataSource
import com.voyager.core.data.utils.FileHelper
import java.io.InputStream

class XmlFileDataSource : XmlDataSource {
    override fun convertXml(inputStream: InputStream): String? = FileHelper.parseXML(inputStream)
    override fun getFileNameFromUri(contentResolver: ContentResolver, uri: Uri, callback: (String) -> Unit) =
        FileHelper.getFileName(contentResolver, uri, callback)
} 