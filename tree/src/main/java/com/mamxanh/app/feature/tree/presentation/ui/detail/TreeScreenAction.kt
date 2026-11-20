package com.mamxanh.app.feature.tree.presentation.ui.detail

sealed interface TreeScreenAction {
    data object Encyclopedia : TreeScreenAction
    data object Water : TreeScreenAction
    data class Fertilize(val id: String) : TreeScreenAction
    data object BuyFertilizer : TreeScreenAction
    data object Unequip : TreeScreenAction
    data object BuyAccessory : TreeScreenAction
    data object Species : TreeScreenAction
    data object Sell : TreeScreenAction
    data object Remove : TreeScreenAction
}
