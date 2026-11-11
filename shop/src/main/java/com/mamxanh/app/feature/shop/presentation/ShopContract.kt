package com.mamxanh.app.feature.shop.presentation

import com.mamxanh.app.feature.shop.domain.model.ShopWallet

enum class ShopFilter { ITEMS, SEEDS }
data class PurchaseOutcome(val success: Boolean, val name: String, val beforeCoins: Int, val afterCoins: Int,
    val beforeOwned: Int, val afterOwned: Int, val message: String = "")
data class ShopUiState(val wallet: ShopWallet = ShopWallet(0, emptyMap()), val isLoading: Boolean = true,
    val averageEarnings: Int = 40,
    val filter: ShopFilter = ShopFilter.SEEDS, val isPurchasing: Boolean = false, val outcome: PurchaseOutcome? = null)
sealed interface ShopIntent {
    data object Refresh : ShopIntent
    data class Equip(val id: String) : ShopIntent
    data class SetFilter(val filter: ShopFilter) : ShopIntent
    data object DismissOutcome : ShopIntent
    data class BuySeed(val id: String) : ShopIntent
    data class Purchase(val id: String,val quantity: Int,val key: String) : ShopIntent
    data object BuyFertilizer : ShopIntent
    data object OpenGarden : ShopIntent
}
sealed interface ShopEffect {
    data class Feedback(val message: String) : ShopEffect
    data object InventoryChanged : ShopEffect
    data object OpenGarden : ShopEffect
}
