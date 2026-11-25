package com.mamxanh.app.feature.inventory.presentation.ui.inventory

import com.mamxanh.app.core.presentation.UiEvent

sealed interface InventoryEvent : UiEvent {
    data class OpenShop(
        val tab: String,
    ) : InventoryEvent

    data object FertilizerHelp : InventoryEvent

    data class Plant(
        val seed: String,
    ) : InventoryEvent

    data class Accessory(
        val sku: String,
        val target: String,
    ) : InventoryEvent

    data class Feedback(
        val message: String,
        val success: Boolean,
    ) : InventoryEvent
}
