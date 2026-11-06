package com.mamxanh.app.feature.garden.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mamxanh.app.feature.garden.domain.GardenRepository
import com.mamxanh.app.feature.garden.domain.LoadGarden
import com.mamxanh.app.feature.garden.domain.PlantSeed
import com.mamxanh.app.feature.garden.domain.model.Garden
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GardenViewModel(private val repository: GardenRepository) : ViewModel() {
    private val _garden = MutableStateFlow(Garden())
    val garden = _garden.asStateFlow()
    init { refresh() }
    fun refresh() = viewModelScope.launch { _garden.value = withContext(Dispatchers.IO) { LoadGarden(repository)() } }
    fun plant(speciesId: String, name: String) = viewModelScope.launch(Dispatchers.IO) {
        PlantSeed(repository)(speciesId, name); refresh()
    }
}
