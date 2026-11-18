package com.mamxanh.app.feature.tree.presentation

import com.mamxanh.app.core.presentation.UiAction

sealed interface TreeIntent : UiAction {
    data object Back : TreeIntent
    data object OpenFertilizerShop : TreeIntent
    data class SetPanelExpanded(val expanded: Boolean) : TreeIntent
    data class Water(val id: Long) : TreeIntent
    data class Fertilize(val id: Long,val tier: String = "AUTO") : TreeIntent
    data class Decorate(val id: Long,val decoration: String) : TreeIntent
    data class AskDisposition(val id: Long,val sell: Boolean) : TreeIntent
    data object DismissDisposition : TreeIntent
    data object ConfirmDisposition : TreeIntent
}
