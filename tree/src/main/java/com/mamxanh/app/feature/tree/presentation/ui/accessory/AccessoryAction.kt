package com.mamxanh.app.feature.tree.presentation.ui.accessory

import com.mamxanh.app.core.presentation.UiAction

sealed interface AccessoryAction : UiAction {
    data object RefreshCatalog : AccessoryAction

    data class Open(
        val sku: String,
        val preferredTarget: String,
        val homeTarget: String,
    ) : AccessoryAction

    data class Select(
        val target: String,
    ) : AccessoryAction

    data object Buy : AccessoryAction

    data object Equip : AccessoryAction

    data object Close : AccessoryAction
}
