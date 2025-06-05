package com.example.ui.viewModels

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * ViewModel for managing the UI state and business logic of MainActivity.
 * This ViewModel handles:
 * 1. The state of the file selection button
 * 2. The currently selected XML file URI
 * 3. The file selection status
 */
class MainViewModel() : ViewModel() {

    // LiveData for the button text that changes based on file selection state
    private val _buttonText = MutableLiveData("Choose File")
    val buttonText: LiveData<String> = _buttonText

    // LiveData for storing the selected XML file URI
    private val _selectedFile = MutableLiveData<Uri>()
    val selectedFile: LiveData<Uri> = _selectedFile

    // LiveData for tracking whether a file has been selected
    private val _isFileSelected = MutableLiveData(false)
    val isFileSelected: LiveData<Boolean> = _isFileSelected

    /**
     * Updates the ViewModel state when a new file is selected
     * @param uri The URI of the selected XML file
     */
    fun setSelectedFileUri(uri: Uri) {
        _selectedFile.value = uri
        _buttonText.value = "Render Xml"
        _isFileSelected.value = true
    }
}