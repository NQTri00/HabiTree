package com.mamxanh.app.feature.garden.presentation

import com.mamxanh.app.core.presentation.UiEvent

sealed interface GardenEffect : UiEvent {
    data object DataChanged : GardenEffect
    data class OpenTree(val id: Long) : GardenEffect
    data object OpenShop : GardenEffect
}
