package com.mamxanh.app.feature.garden.presentation.ui.garden

import com.mamxanh.app.core.presentation.UiEvent

sealed interface PlantingEvent : UiEvent {
    data class Planted(
        val treeId: Long?,
    ) : PlantingEvent

    data class Feedback(
        val message: String,
    ) : PlantingEvent
}
