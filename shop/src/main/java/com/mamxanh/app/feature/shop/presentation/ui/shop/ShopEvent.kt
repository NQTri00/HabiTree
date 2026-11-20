package com.mamxanh.app.feature.shop.presentation.ui.shop

import com.mamxanh.app.core.presentation.UiEvent

sealed interface ShopEffect : UiEvent {
    data class Feedback(val message: String) : ShopEffect
    data object InventoryChanged : ShopEffect
    data object OpenGarden : ShopEffect
}
