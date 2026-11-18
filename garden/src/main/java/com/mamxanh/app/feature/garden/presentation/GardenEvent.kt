package com.mamxanh.app.feature.garden.presentation

sealed interface GardenEffect {
    data object DataChanged : GardenEffect
    data class OpenTree(val id: Long) : GardenEffect
    data object OpenShop : GardenEffect
}
