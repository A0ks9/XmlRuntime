package com.voyager.core.datasource

import android.content.ContentResolver
import android.net.Uri
import java.io.InputStream

interface XmlDataSource {
    fun convertXml(inputStream: InputStream): String?
    fun getFileNameFromUri(contentResolver: ContentResolver, uri: Uri, callback: (String) -> Unit)
} 