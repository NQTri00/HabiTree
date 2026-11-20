package com.mamxanh.app.feature.shop.presentation.ui.shop

import com.mamxanh.app.core.presentation.UiAction

sealed interface ShopIntent : UiAction {
    data object Refresh : ShopIntent
    data class Equip(val id: String) : ShopIntent
    data class SetFilter(val filter: ShopFilter) : ShopIntent
    data object DismissOutcome : ShopIntent
    data class BuySeed(val id: String) : ShopIntent
    data class Purchase(val id: String,val quantity: Int,val key: String) : ShopIntent
    data object BuyFertilizer : ShopIntent
    data object OpenGarden : ShopIntent
}
