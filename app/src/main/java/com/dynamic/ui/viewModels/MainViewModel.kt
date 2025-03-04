package com.dynamic.ui.viewModels

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dynamic.data.repositories.ViewStateRepository
import com.dynamic.data.repositories.XmlRepository
import com.dynamic.utils.FileHelper.getPath
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import java.io.IOException

class MainViewModel(
    private val xmlRepository: XmlRepository, private val viewStateRepository: ViewStateRepository
) : ViewModel() {

    private val _buttonText = MutableLiveData("Choose File")
    val buttonText: LiveData<String> = _buttonText

    private val _showXmlText = MutableLiveData("Show Xml")
    val showXmlText: LiveData<String> = _showXmlText

    private val _enableShowing = MutableLiveData(false)
    val enableShowing: LiveData<Boolean> = _enableShowing

    private val _selectedFile = MutableLiveData<Uri>()
    val selectedFile: LiveData<Uri> = _selectedFile

    private val _parsedJson = MutableLiveData<String?>()
    val parsedJson: LiveData<String?> = _parsedJson

    private val _createdFileUri = MutableLiveData<Uri?>()
    val createdFileUri: LiveData<Uri?> = _createdFileUri

    private val _isFileCreated = MutableLiveData(false)
    val isFileCreated: LiveData<Boolean> = _isFileCreated

    private val _isFileSelected = MutableLiveData(false)
    val isFileSelected: LiveData<Boolean> = _isFileSelected

    private val _fileNameToCreate = MutableLiveData<String?>()
    val fileNameToCreate: LiveData<String?> = _fileNameToCreate


    fun setShowXmlText(text: String) {
        _showXmlText.value = text
    }

    fun enableShowingButton(enable: Boolean) {
        _enableShowing.value = enable
    }

    fun setSelectedFileUri(uri: Uri) {
        _selectedFile.value = uri
        _buttonText.value = "Convert File"
        _isFileSelected.value = true
    }

    fun convertXmlToJson(context: Context) {
        viewModelScope.launch {
            val uri = _selectedFile.value ?: return@launch
            val path = getPath(context, uri) ?: return@launch
            xmlRepository.convertXmlToJson(path) { jsonResult ->
                _parsedJson.value = jsonResult
                xmlRepository.getFileNameFromUri(context.contentResolver, uri) { fileName ->
                    // Handle file creation logic here or in a Use Case if more complex
                    createDocument(
                        fileName.replace(".xml", ".json")
                    ) // Pass filename for creation
                }
            }
        }
    }

    private fun createDocument(fileName: String) {
        // Logic to trigger file creation intent in Activity, pass fileName
        // You'll likely use an ActivityResultLauncher here in MainActivity
        // and observe a LiveData from ViewModel to trigger it.
        // For now, let's just hold the filename or trigger for Activity.
        _fileNameToCreate.value = fileName
    }


    fun writeToFile(uri: Uri, contentResolver: ContentResolver) {
        viewModelScope.launch(Dispatchers.IO) {
            val jsonContent = _parsedJson.value ?: throw IllegalStateException("Parsed JSON is null")
            try {
                contentResolver.openOutputStream(uri)?.use { outputStream ->
                    outputStream.write(jsonContent.toByteArray(Charsets.UTF_8))
                    _createdFileUri.postValue(uri)
                    _isFileCreated.postValue(true)
                     _enableShowing.postValue(true)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}