package com.example.ui.viewModels

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.voyager.data.repositories.ViewStateRepository
import com.voyager.data.repositories.XmlRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(
    private val xmlRepository: XmlRepository, private val viewStateRepository: ViewStateRepository,
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

    private val _requestCreateFileEvent = MutableLiveData<String?>()
    val requestCreateFileEvent: LiveData<String?> = _requestCreateFileEvent

    // LiveData for dynamic EditText
    private val _dynamicEditTextData = MutableLiveData<String>("Default EditText Value")
    val dynamicEditTextData: LiveData<String> = _dynamicEditTextData

    // LiveData for dynamic TextView
    private val _dynamicTextViewData = MutableLiveData<String>("Default TextView Value from VM")
    val dynamicTextViewData: LiveData<String> = _dynamicTextViewData

    fun updateDynamicEditTextData(newText: String) {
        _dynamicEditTextData.value = newText
    }

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
            val selectedFileUri = _selectedFile.value ?: return@launch // Use internal state for URI
            try {
                contentResolver.openInputStream(selectedFileUri)
                    ?.use { inputStream -> // Get InputStream
                        _parsedJson.value = xmlRepository.convertXmlToJson(inputStream)
                            .toString() // Pass InputStream
                        Log.d("Json Parsed", _parsedJson.value.toString())
                        // Use passed contentResolver and selectedFileUri
                        xmlRepository.getFileNameFromUri(contentResolver, selectedFileUri) { fileName ->
                            createDocument(fileName.replace(".xml", ".json"))
                        }
                    } ?: run {
                    Log.e("ConvertXml", "Failed to open input stream for URI: $selectedFileUri")
                    _parsedJson.value = null // Or handle error appropriately
                }
            } catch (e: Exception) {
                Log.e("ConvertXml", "Error converting XML to JSON: ${e.message}", e)
                _parsedJson.value = null // Handle error
            }
        }
    }

    private fun createDocument(fileName: String) {
        // Logic to trigger file creation intent in Activity, pass fileName
        // You'll likely use an ActivityResultLauncher here in MainActivity
        // and observe a LiveData from ViewModel to trigger it.
        // For now, let's just hold the filename or trigger for Activity.
        _requestCreateFileEvent.value = fileName
    }

    /**
     * Call this method from the Activity after the file creation request has been handled
     * to prevent re-triggering the event on configuration changes.
     */
    fun creationRequestHandled() {
        _requestCreateFileEvent.value = null
    }

    fun writeToFile(uri: Uri, contentResolver: ContentResolver) {
        viewModelScope.launch(Dispatchers.IO) {
            val jsonContent =
                _parsedJson.value ?: throw IllegalStateException("Parsed JSON is null")
            Log.d("JSON_CONTENT", jsonContent)
            contentResolver.openOutputStream(uri)?.use { outputStream ->
                outputStream.bufferedWriter(Charsets.UTF_8).use { writer ->
                    writer.write(jsonContent)
                    _createdFileUri.postValue(uri)
                    _isFileCreated.postValue(true)
                    _enableShowing.postValue(true)
                }
            }
        }
    }
}