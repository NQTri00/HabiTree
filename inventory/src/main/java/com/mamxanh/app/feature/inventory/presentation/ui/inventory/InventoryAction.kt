package com.mamxanh.app.feature.inventory.presentation.ui.inventory

import com.mamxanh.app.core.presentation.UiAction

sealed interface InventoryAction : UiAction {
    data object Refresh : InventoryAction

    data object OpenShop : InventoryAction

    data object FertilizerHelp : InventoryAction

    data class SelectTab(
        val tab: String,
    ) : InventoryAction

    data class Plant(
        val seed: String,
    ) : InventoryAction

    data class Accessory(
        val sku: String,
        val target: String,
    ) : InventoryAction

    data class EquipCan(
        val id: String,
    ) : InventoryAction
}
