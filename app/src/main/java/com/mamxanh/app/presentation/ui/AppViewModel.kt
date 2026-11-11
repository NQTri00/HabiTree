package com.mamxanh.app.presentation.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppViewModel(private val savedState: SavedStateHandle) : ViewModel() {
    private fun destination(name: String?): AppDestination? = AppDestination.entries.firstOrNull { it.name == name }

    private val _uiState = MutableStateFlow(AppUiState(
        destination = destination(savedState["destination"]) ?: AppDestination.HOME,
        backStack = savedState.get<ArrayList<String>>("backStack").orEmpty().mapNotNull(::destination),
        selectedTreeId = savedState["selectedTreeId"],
        inventoryOpen = savedState["inventoryOpen"] ?: false,
        inventoryPlotId = savedState["inventoryPlotId"],
        plantingSeedId = savedState["plantingSeedId"]
    ))
    val uiState = _uiState.asStateFlow()

    fun dispatch(intent: AppIntent) {
        val current = _uiState.value
        _uiState.value = when (intent) {
            is AppIntent.Navigate -> if (intent.destination == current.destination && current.selectedTreeId == null && !current.inventoryOpen) current else current.copy(
                destination = intent.destination, selectedTreeId = null, inventoryOpen = false,
                inventoryPlotId = null, plantingSeedId = null,
                backStack = if (intent.destination == current.destination) current.backStack else current.backStack + current.destination
            )
            is AppIntent.OpenTree -> current.copy(selectedTreeId = intent.id)
            is AppIntent.OpenInventory -> current.copy(inventoryOpen = true, inventoryPlotId = intent.plotId, plantingSeedId = null)
            is AppIntent.SelectInventorySeed -> current.copy(inventoryOpen = false,
                destination = AppDestination.GARDEN, selectedTreeId = null, plantingSeedId = intent.seedId)
            AppIntent.DismissPlanting -> current.copy(plantingSeedId = null, inventoryPlotId = null)
            AppIntent.Back -> if (current.inventoryOpen) current.copy(inventoryOpen = false, inventoryPlotId = null)
                else if (current.selectedTreeId != null) current.copy(selectedTreeId = null)
                else if (current.backStack.isNotEmpty()) current.copy(destination = current.backStack.last(), backStack = current.backStack.dropLast(1))
                else current
            AppIntent.DismissMessage -> current.copy(message = "")
            is AppIntent.ShowMessage -> current.copy(message = intent.message)
        }
        // Store only navigation primitives; transient feedback is not replayed after process death.
        savedState["destination"] = _uiState.value.destination.name
        savedState["backStack"] = ArrayList(_uiState.value.backStack.map { it.name })
        savedState["selectedTreeId"] = _uiState.value.selectedTreeId
        savedState["inventoryOpen"] = _uiState.value.inventoryOpen
        savedState["inventoryPlotId"] = _uiState.value.inventoryPlotId
        savedState["plantingSeedId"] = _uiState.value.plantingSeedId
    }
}
