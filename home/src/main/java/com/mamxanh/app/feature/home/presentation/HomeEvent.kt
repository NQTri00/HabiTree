package com.mamxanh.app.feature.home.presentation

sealed interface HomeEffect {
    data class OpenTree(val id: Long) : HomeEffect
    data object OpenTasks : HomeEffect
    data object OpenGarden : HomeEffect
    data object OpenShop : HomeEffect
    data class Feedback(val message: String) : HomeEffect
    data class WaterTree(val id: Long) : HomeEffect
    data class FertilizeTree(val id: Long) : HomeEffect
}
