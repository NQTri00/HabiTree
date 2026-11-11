package com.mamxanh.app.feature.home.presentation

import com.mamxanh.app.feature.home.domain.model.HomeDashboard

data class HomeUiState(val dashboard: HomeDashboard = HomeDashboard(), val isLoading: Boolean = true,
    val isDailyClearVisible: Boolean = false)
sealed interface HomeIntent {
    data object Refresh : HomeIntent
    data object DismissDailyClear : HomeIntent
    data class OpenTree(val id: Long) : HomeIntent
    data object OpenTasks : HomeIntent
    data object OpenGarden : HomeIntent
    data object OpenShop : HomeIntent
    data class WaterTree(val id: Long) : HomeIntent
    data class FertilizeTree(val id: Long) : HomeIntent
}
sealed interface HomeEffect {
    data class OpenTree(val id: Long) : HomeEffect
    data object OpenTasks : HomeEffect
    data object OpenGarden : HomeEffect
    data object OpenShop : HomeEffect
    data class Feedback(val message: String) : HomeEffect
    data class WaterTree(val id: Long) : HomeEffect
    data class FertilizeTree(val id: Long) : HomeEffect
}
