package com.dynamic.data.sources.remote

import android.content.ContentResolver
import android.net.Uri
import com.dynamic.utils.FileHelper
import com.dynamic.utils.FileHelper.parseXML
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class XmlFileDataSource {

    fun convertXml(xmlPath: String, callback: (String?) -> Unit) {
        CoroutineScope(Dispatchers.Main).launch {
            val node = parseXML(xmlPath)
            callback(node)
        }
    }

    fun getFileNameFromUri(contentResolver: ContentResolver, uri: Uri, callback: (String) -> Unit) {
        FileHelper.getFileName(contentResolver, uri, callback)
    }
}