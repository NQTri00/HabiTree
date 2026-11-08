package com.mamxanh.app.feature.shop.presentation

import com.mamxanh.app.feature.shop.domain.model.ShopWallet

data class ShopUiState(val wallet: ShopWallet = ShopWallet(0, emptyMap()), val isLoading: Boolean = true)

sealed interface ShopIntent {
    data object Refresh : ShopIntent
    data class BuySeed(val id: String) : ShopIntent
    data object BuyFertilizer : ShopIntent
    data object OpenGarden : ShopIntent
}

sealed interface ShopEffect {
    data class Feedback(val message: String) : ShopEffect
    data object InventoryChanged : ShopEffect
    data object OpenGarden : ShopEffect
}
