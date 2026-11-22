package com.mamxanh.app.feature.garden.presentation.ui.garden

import com.mamxanh.app.core.presentation.UiEvent

sealed interface GardenEvent : UiEvent {
    data object DataChanged : GardenEvent

    data class OpenTree(
        val id: Long,
    ) : GardenEvent

    data object OpenShop : GardenEvent
}
