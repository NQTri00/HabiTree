package com.mamxanh.app.feature.shop.presentation.ui.shop

import com.mamxanh.app.core.presentation.UiEvent

sealed interface ShopEvent : UiEvent {
    data class Feedback(
        val message: String,
    ) : ShopEvent

    data class PurchaseFinished(
        val id: String,
        val success: Boolean,
        val message: String,
        val openTree: Boolean = false,
        val treeId: Long? = null,
    ) : ShopEvent

    data object InventoryChanged : ShopEvent

    data object OpenGarden : ShopEvent
}
