package com.mamxanh.app.feature.garden.presentation.ui.garden

import com.mamxanh.app.core.presentation.UiAction

sealed interface GardenIntent : UiAction {
    data object Refresh : GardenIntent

    data class WaterAll(
        val key: String,
    ) : GardenIntent

    data object OpenShop : GardenIntent

    data class Plant(
        val speciesId: String,
        val name: String,
        val plotId: Int? = null,
    ) : GardenIntent

    data class UnlockPlot(
        val plotId: Int,
    ) : GardenIntent

    data object DismissMessage : GardenIntent

    data class OpenTree(
        val id: Long,
    ) : GardenIntent
}
