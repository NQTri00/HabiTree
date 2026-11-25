package com.mamxanh.app.feature.shop.presentation.ui.shop

import com.mamxanh.app.core.presentation.UiAction

sealed interface ShopAction : UiAction {
    data object Refresh : ShopAction

    data class SelectTab(
        val tab: String,
    ) : ShopAction

    data class OpenPurchase(
        val id: String,
    ) : ShopAction

    data class SetQuantity(
        val quantity: Int,
    ) : ShopAction

    data class Equip(
        val id: String,
    ) : ShopAction

    data class SetFilter(
        val filter: ShopFilter,
    ) : ShopAction

    data object DismissOutcome : ShopAction

    data class BuySeed(
        val id: String,
    ) : ShopAction

    data class Purchase(
        val id: String,
        val quantity: Int,
        val key: String,
    ) : ShopAction

    data class BuyAndEquip(
        val id: String,
        val quantity: Int,
    ) : ShopAction

    data class BuyAndPlant(
        val id: String,
        val quantity: Int,
        val preferredPlot: Int?,
    ) : ShopAction

    data object BuyFertilizer : ShopAction

    data object OpenGarden : ShopAction
}
