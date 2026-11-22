package com.mamxanh.app.feature.home.presentation.ui.home

import com.mamxanh.app.core.presentation.UiEvent

sealed interface HomeEvent : UiEvent {
    data class OpenTree(
        val id: Long,
    ) : HomeEvent

    data object OpenTasks : HomeEvent

    data object OpenGarden : HomeEvent

    data object OpenShop : HomeEvent

    data class Feedback(
        val message: String,
    ) : HomeEvent

    data class WaterTree(
        val id: Long,
    ) : HomeEvent

    data class FertilizeTree(
        val id: Long,
    ) : HomeEvent
}
