package com.example.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.usecase.ParseXmlUseCase
import com.example.domain.usecase.RenderXmlUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class XmlUiState {
    object Idle : XmlUiState()
    object Loading : XmlUiState()
    data class Success(val data: Any) : XmlUiState()
    data class Error(val message: String) : XmlUiState()
}

class XmlViewModel(
    private val parseXmlUseCase: ParseXmlUseCase,
    private val renderXmlUseCase: RenderXmlUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<XmlUiState>(XmlUiState.Idle)
    val uiState: StateFlow<XmlUiState> = _uiState.asStateFlow()

    fun parseXml(xmlContent: String) {
        viewModelScope.launch {
            _uiState.value = XmlUiState.Loading
            parseXmlUseCase(xmlContent)
                .onSuccess { result ->
                    _uiState.value = XmlUiState.Success(result)
                }
                .onFailure { error ->
                    _uiState.value = XmlUiState.Error(error.message ?: "Unknown error occurred")
                }
        }
    }

    fun renderXml(xmlContent: String) {
        viewModelScope.launch {
            _uiState.value = XmlUiState.Loading
            renderXmlUseCase(xmlContent)
                .onSuccess { result ->
                    _uiState.value = XmlUiState.Success(result)
                }
                .onFailure { error ->
                    _uiState.value = XmlUiState.Error(error.message ?: "Unknown error occurred")
                }
        }
    }
} 