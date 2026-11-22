package com.mamxanh.app.feature.home.presentation.ui.home

import com.mamxanh.app.core.presentation.UiAction

sealed interface HomeAction : UiAction {
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
