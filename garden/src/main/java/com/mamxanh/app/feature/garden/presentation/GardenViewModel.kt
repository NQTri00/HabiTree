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
                _uiState.value = _uiState.value.copy(isLoading = true)
                val garden = withContext(Dispatchers.IO) { LoadGarden(repository)() }
                _uiState.value = GardenUiState(garden, false)
            }
            GardenIntent.OpenShop -> viewModelScope.launch { effectChannel.send(GardenEffect.OpenShop) }
            is GardenIntent.OpenTree -> viewModelScope.launch { effectChannel.send(GardenEffect.OpenTree(intent.id)) }
            is GardenIntent.Plant -> viewModelScope.launch(Dispatchers.IO) {
                PlantSeed(repository)(intent.speciesId, intent.name)
                dispatch(GardenIntent.Refresh)
                effectChannel.send(GardenEffect.DataChanged)
            }
        }
    }
}
