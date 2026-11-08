package com.mamxanh.app.presentation.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(AppUiState())
    val uiState = _uiState.asStateFlow()

    fun dispatch(intent: AppIntent) {
        val current = _uiState.value
        _uiState.value = when (intent) {
            is AppIntent.Navigate -> if (intent.destination == current.destination) current else current.copy(
                destination = intent.destination, backStack = current.backStack + current.destination
            )
            is AppIntent.OpenTree -> current.copy(selectedTreeId = intent.id)
            AppIntent.Back -> if (current.selectedTreeId != null) current.copy(selectedTreeId = null)
                else if (current.backStack.isNotEmpty()) current.copy(destination = current.backStack.last(), backStack = current.backStack.dropLast(1))
                else current
            AppIntent.DismissMessage -> current.copy(message = "")
            is AppIntent.ShowMessage -> current.copy(message = intent.message)
        }
    }
}
