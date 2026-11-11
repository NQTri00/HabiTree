package com.mamxanh.app.feature.garden.presentation

import com.mamxanh.app.feature.garden.domain.model.Garden

data class GardenUiState(val garden: Garden = Garden(), val isLoading: Boolean = true, val message: String? = null)
sealed interface GardenEffect {
    data object DataChanged : GardenEffect
    data class OpenTree(val id: Long) : GardenEffect
    data object OpenShop : GardenEffect
}

sealed interface GardenIntent {
    data object Refresh : GardenIntent
    data class WaterAll(val key: String) : GardenIntent
    data object OpenShop : GardenIntent
    data class Plant(val speciesId: String, val name: String, val plotId: Int? = null) : GardenIntent
    data class UnlockPlot(val plotId: Int) : GardenIntent
    data object DismissMessage : GardenIntent
    data class OpenTree(val id: Long) : GardenIntent
}
