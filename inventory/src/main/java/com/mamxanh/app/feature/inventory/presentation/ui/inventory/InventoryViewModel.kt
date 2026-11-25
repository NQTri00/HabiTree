package com.mamxanh.app.feature.inventory.presentation.ui.inventory

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.mamxanh.app.core.presentation.BaseViewModel
import com.mamxanh.app.feature.inventory.domain.repository.InventoryRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class InventoryViewModel(
    private val repository: InventoryRepository,
    private val savedState: SavedStateHandle,
) : BaseViewModel<InventoryState, InventoryEvent, InventoryAction>(
        InventoryState(tab = savedState["inventory"] ?: "seeds"),
    ) {
    override fun handleAction(action: InventoryAction) {
        val current = state.value
        when (action) {
            InventoryAction.Refresh -> viewModelScope.launch { refresh() }
            InventoryAction.OpenShop -> send(InventoryEvent.OpenShop(current.tab))
            InventoryAction.FertilizerHelp -> send(InventoryEvent.FertilizerHelp)
            is InventoryAction.SelectTab -> {
                savedState["inventory"] = action.tab
                mutableState.value = current.copy(tab = action.tab)
            }
            is InventoryAction.Plant -> {
                if (current.contains(action.seed)) send(InventoryEvent.Plant(action.seed))
            }
            is InventoryAction.Accessory -> {
                if (current.contains(action.sku)) send(InventoryEvent.Accessory(action.sku, action.target))
            }
            is InventoryAction.EquipCan -> equip(action.id)
        }
    }

    private fun send(event: InventoryEvent) {
        viewModelScope.launch { emitEvent(event) }
    }

    private suspend fun refresh() {
        try {
            val (quantities, accessories, equippedCan) =
                withContext(Dispatchers.IO) {
                    Triple(repository.quantities(), repository.accessoryQuantities(), repository.equippedCan())
                }
            mutableState.value =
                state.value.copy(
                    quantities = quantities,
                    accessories = accessories,
                    equippedCan = equippedCan,
                    loading = false,
                )
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (_: Exception) {
            mutableState.value = state.value.copy(loading = false)
            emitEvent(InventoryEvent.Feedback("", false))
        }
    }

    private fun equip(id: String) {
        if (state.value.busy || !state.value.contains(id)) return
        mutableState.value = state.value.copy(busy = true)
        viewModelScope.launch {
            try {
                val result = withContext(Dispatchers.IO) { repository.equipWateringCan(id) }
                emitEvent(InventoryEvent.Feedback(result.message, result.success))
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                emitEvent(InventoryEvent.Feedback("", false))
            } finally {
                mutableState.value = state.value.copy(busy = false)
            }
        }
    }
}
