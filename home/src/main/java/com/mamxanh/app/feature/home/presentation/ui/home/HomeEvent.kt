package com.mamxanh.app.feature.home.presentation.ui.home

import com.mamxanh.app.core.presentation.UiEvent

sealed interface HomeEvent : UiEvent {
    data class OpenTree(
        val id: Long,
    ) : HomeEvent

    data class Shortcut(
        val action: HomeScreenAction,
    ) : HomeEvent

    data object ClaimReward : HomeEvent

    data object ChooseTree : HomeEvent

    data class TreeSelected(
        val id: String,
    ) : HomeEvent

    data class OpenTask(
        val id: String,
        val startTimer: Boolean,
    ) : HomeEvent

    data class CompleteTask(
        val id: String,
    ) : HomeEvent

    data class DecorateTree(
        val id: String,
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
