package com.mamxanh.app.feature.home.presentation.ui.home

import com.mamxanh.app.core.presentation.UiAction

sealed interface HomeIntent : UiAction {
    data object Refresh : HomeIntent
    data object DismissDailyClear : HomeIntent
    data class OpenTree(val id: Long) : HomeIntent
    data object OpenTasks : HomeIntent
    data object OpenGarden : HomeIntent
    data object OpenShop : HomeIntent
    data class WaterTree(val id: Long) : HomeIntent
    data class FertilizeTree(val id: Long) : HomeIntent
}
