package com.mamxanh.app.presentation.ui

import androidx.lifecycle.SavedStateHandle
import com.mamxanh.app.core.presentation.BaseViewModel
import com.mamxanh.app.core.presentation.UiEvent

class AppViewModel(private val savedState: SavedStateHandle) : BaseViewModel<AppUiState, UiEvent, AppIntent>(
    initialState = AppUiState(
        destination = destination(savedState["destination"]) ?: AppDestination.HOME,
        backStack = savedState.get<ArrayList<String>>("backStack").orEmpty().mapNotNull(::destination),
        selectedTreeId = savedState["selectedTreeId"],
        inventoryOpen = savedState["inventoryOpen"] ?: false,
        inventoryPlotId = savedState["inventoryPlotId"],
        plantingSeedId = savedState["plantingSeedId"]
    ),
) {
    val uiState get() = state

    fun resetAfterRestore() {
        mutableState.value=AppUiState()
        dispatch(AppIntent.Navigate(AppDestination.HOME))
    }

    override fun handleAction(action: AppIntent) {
        val intent = action
        val current = mutableState.value
        mutableState.value = when (intent) {
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
        savedState["destination"] = mutableState.value.destination.name
        savedState["backStack"] = ArrayList(mutableState.value.backStack.map { it.name })
        savedState["selectedTreeId"] = mutableState.value.selectedTreeId
        savedState["inventoryOpen"] = mutableState.value.inventoryOpen
        savedState["inventoryPlotId"] = mutableState.value.inventoryPlotId
        savedState["plantingSeedId"] = mutableState.value.plantingSeedId
    }
}

private fun destination(name: String?): AppDestination? = AppDestination.entries.firstOrNull { it.name == name }
