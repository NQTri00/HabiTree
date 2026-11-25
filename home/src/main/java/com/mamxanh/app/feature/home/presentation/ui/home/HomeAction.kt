package com.mamxanh.app.feature.home.presentation.ui.home

import com.mamxanh.app.core.presentation.UiAction

sealed interface HomeAction : UiAction {
    data class Shortcut(
        val action: HomeScreenAction,
    ) : HomeAction

    data object ClaimReward : HomeAction

    data object ChooseTree : HomeAction

    data class SelectTree(
        val id: String,
    ) : HomeAction

    data class OpenTask(
        val id: String,
        val startTimer: Boolean = false,
    ) : HomeAction

    data class CompleteTask(
        val id: String,
    ) : HomeAction

    data class DecorateTree(
        val id: String,
    ) : HomeAction

    data object Refresh : HomeAction

    data object DismissDailyClear : HomeAction

    data class OpenTree(
        val id: Long,
    ) : HomeAction

    data object OpenTasks : HomeAction

    data object OpenGarden : HomeAction

    data object OpenShop : HomeAction

    data class WaterTree(
        val id: Long,
    ) : HomeAction

    data class FertilizeTree(
        val id: Long,
    ) : HomeAction
}
