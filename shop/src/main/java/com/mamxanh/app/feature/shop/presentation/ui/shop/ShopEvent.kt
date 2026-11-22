package com.mamxanh.app.feature.shop.presentation.ui.shop

import com.mamxanh.app.core.presentation.UiEvent

sealed interface ShopEvent : UiEvent {
    data class Feedback(
        val message: String,
    ) : ShopEvent

    data object InventoryChanged : ShopEvent

    data object OpenGarden : ShopEvent
}
