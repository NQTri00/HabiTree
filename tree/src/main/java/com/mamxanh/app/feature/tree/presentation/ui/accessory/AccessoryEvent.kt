package com.mamxanh.app.feature.tree.presentation.ui.accessory

import com.mamxanh.app.core.presentation.UiEvent

sealed interface AccessoryEvent : UiEvent {
    data class Completed(
        val sku: String,
        val success: Boolean,
        val message: String,
    ) : AccessoryEvent
}
