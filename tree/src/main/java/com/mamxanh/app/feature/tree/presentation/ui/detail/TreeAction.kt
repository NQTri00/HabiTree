package com.mamxanh.app.feature.tree.presentation.ui.detail

import com.mamxanh.app.core.presentation.UiAction

sealed interface TreeAction : UiAction {
    data class Browse(
        val currentId: Long,
        val forward: Boolean,
    ) : TreeAction

    data object Back : TreeAction

    data class Load(
        val id: Long,
    ) : TreeAction

    data object OpenAccessoryShop : TreeAction

    data class OpenAccessory(
        val sku: String,
    ) : TreeAction

    data object OpenFertilizerShop : TreeAction

    data class SelectTab(
        val value: String,
    ) : TreeAction

    data class Unequip(
        val id: Long,
    ) : TreeAction

    data class Dispose(
        val id: Long,
        val sell: Boolean,
    ) : TreeAction

    data class SetPanelExpanded(
        val expanded: Boolean,
    ) : TreeAction

    data class Water(
        val id: Long,
    ) : TreeAction

    data class Fertilize(
        val id: Long,
        val tier: String = "AUTO",
    ) : TreeAction

    data class Decorate(
        val id: Long,
        val decoration: String,
    ) : TreeAction

    data class AskDisposition(
        val id: Long,
        val sell: Boolean,
        val openDialog: Boolean = true,
    ) : TreeAction

    data object DismissDisposition : TreeAction

    data object ConfirmDisposition : TreeAction
}
