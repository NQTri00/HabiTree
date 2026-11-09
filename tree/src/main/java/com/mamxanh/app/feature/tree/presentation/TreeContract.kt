package com.mamxanh.app.feature.tree.presentation

data class TreeUiState(val isLoading: Boolean = false, val isPanelExpanded: Boolean = false)

sealed interface TreeIntent {
    data object Back : TreeIntent
    data object OpenWaterSupply : TreeIntent
    data object OpenFertilizerShop : TreeIntent
    data class SetPanelExpanded(val expanded: Boolean) : TreeIntent
    data class Water(val id: Long) : TreeIntent
    data class Fertilize(val id: Long) : TreeIntent
    data class Decorate(val id: Long, val decoration: String) : TreeIntent
}
sealed interface TreeEffect {
    data object Back : TreeEffect
    data object OpenWaterSupply : TreeEffect
    data object OpenFertilizerShop : TreeEffect
    data class Feedback(val message: String) : TreeEffect
    data object TreeChanged : TreeEffect
}
