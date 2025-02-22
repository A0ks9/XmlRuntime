package com.runtimexml.ui.viewModels

import android.content.ContentResolver
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.runtimexml.data.repositories.XmlRepository
import com.runtimexml.data.repositories.ViewStateRepository
import kotlinx.coroutines.launch
import org.json.JSONObject

class MainViewModel(
    private val xmlRepository: XmlRepository,
    private val viewStateRepository: ViewStateRepository
) : ViewModel() {

    private val _buttonText = MutableLiveData("Choose File")
    val buttonText: LiveData<String> = _buttonText

    private val _showXmlText = MutableLiveData("Show Xml")
    val showXmlText: LiveData<String> = _showXmlText

    private val _enableShowing = MutableLiveData(false)
    val enableShowing: LiveData<Boolean> = _enableShowing

    private val _selectedFile = MutableLiveData<Uri>()
    val selectedFile: LiveData<Uri> = _selectedFile

    private val _parsedJson = MutableLiveData<JSONObject?>()
    val parsedJson: LiveData<JSONObject?> = _parsedJson

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

    fun convertXmlToJson(contentResolver: ContentResolver) {
        viewModelScope.launch {
            val uri = _selectedFile.value ?: return@launch
            val jsonResult = xmlRepository.convertXmlToJson(contentResolver, uri)
            _parsedJson.value = jsonResult
            val fileName = xmlRepository.getFileNameFromUri(uri, contentResolver)
            // Handle file creation logic here or in a Use Case if more complex
            createDocument(fileName?.replace(".xml", ".json") ?: "output.json") // Pass filename for creation
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
        viewModelScope.launch {
            val jsonContent = _parsedJson.value?.toString(4) ?: return@launch
            try {
                contentResolver.openOutputStream(uri)?.use { outputStream ->
                    outputStream.write(jsonContent.toByteArray())
                    _createdFileUri.value = uri
                    _isFileCreated.value = true
                    _enableShowing.value = true
                }
            } catch (e: Exception) {
                // Handle file writing error (e.g., show error message to user)
                e.printStackTrace()
            }
        }
    }
}