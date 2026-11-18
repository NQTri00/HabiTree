package com.mamxanh.app.feature.shop.presentation

sealed interface ShopEffect {
    data class Feedback(val message: String) : ShopEffect
    data object InventoryChanged : ShopEffect
    data object OpenGarden : ShopEffect
}
