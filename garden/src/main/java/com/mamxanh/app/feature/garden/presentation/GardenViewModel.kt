package com.mamxanh.app.feature.garden.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mamxanh.app.feature.garden.domain.GardenRepository
import com.mamxanh.app.feature.garden.domain.LoadGarden
import com.mamxanh.app.feature.garden.domain.PlantSeed
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GardenViewModel(private val repository: GardenRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(GardenUiState())
    val uiState = _uiState.asStateFlow()
    private val effectChannel = Channel<GardenEffect>(Channel.BUFFERED)
    val effects = effectChannel.receiveAsFlow()
    init { dispatch(GardenIntent.Refresh) }

    fun dispatch(intent: GardenIntent) {
        when (intent) {
            GardenIntent.Refresh -> viewModelScope.launch {
                val garden = withContext(Dispatchers.IO) { LoadGarden(repository)() }
                _uiState.value = _uiState.value.copy(garden = garden, isLoading = false)
            }
            GardenIntent.OpenShop -> viewModelScope.launch { effectChannel.send(GardenEffect.OpenShop) }
            is GardenIntent.OpenTree -> viewModelScope.launch { effectChannel.send(GardenEffect.OpenTree(intent.id)) }
            GardenIntent.DismissMessage -> _uiState.value = _uiState.value.copy(message = null)
            is GardenIntent.Plant, is GardenIntent.UnlockPlot, is GardenIntent.WaterAll -> {
                if (_uiState.value.isLoading) return
                _uiState.value = _uiState.value.copy(isLoading = true)
                viewModelScope.launch {
                    try {
                        var batchFeedback: String? = null
                        val changed = withContext(Dispatchers.IO) {
                            when (intent) {
                                is GardenIntent.Plant -> PlantSeed(repository)(intent.speciesId, intent.name, intent.plotId)
                                is GardenIntent.UnlockPlot -> repository.unlockPlot(intent.plotId)
                                is GardenIntent.WaterAll -> repository.waterAll(intent.key).also { batchFeedback = it.message }.success
                                else -> false
                            }
                        }
                        val garden = withContext(Dispatchers.IO) { LoadGarden(repository)() }
                        _uiState.value = GardenUiState(garden, false, when {
                            intent is GardenIntent.WaterAll -> batchFeedback
                            changed && intent is GardenIntent.Plant -> "Đã gieo cây!\nĐã trồng " + (intent.name.ifBlank { "mầm mới" }) + " tại ô " + (intent.plotId?.toString()?.padStart(2, '0') ?: garden.plots.firstOrNull { it.treeId == garden.trees.maxByOrNull { tree -> tree.id }?.id }?.id ?: "")
                            changed -> null
                            intent is GardenIntent.UnlockPlot -> "Không thể mở ô trồng. Hãy kiểm tra số xu hiện có."
                            else -> "Chưa thể gieo cây. Hãy kiểm tra hạt trong túi và ô trồng còn trống."
                        })
                        if (changed) effectChannel.send(GardenEffect.DataChanged)
                    } finally {
                        _uiState.value = _uiState.value.copy(isLoading = false)
                    }
                }
            }
        }
    }
}
