package com.mamxanh.app.feature.garden.presentation.ui.garden

import com.mamxanh.app.core.presentation.UiAction

sealed interface PlantingAction : UiAction {
    data class Open(
        val seed: String,
        val preferredPlot: Int?,
    ) : PlantingAction

    data class SelectPlot(
        val plotId: Int,
    ) : PlantingAction

    data class SetName(
        val name: String,
    ) : PlantingAction

    data object Plant : PlantingAction

    data object Close : PlantingAction
}
