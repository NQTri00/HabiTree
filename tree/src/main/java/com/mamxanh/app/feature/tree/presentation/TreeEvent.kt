package com.mamxanh.app.feature.tree.presentation

sealed interface TreeEffect {
    data object Back : TreeEffect
    data object OpenFertilizerShop : TreeEffect
    data class Feedback(val message: String) : TreeEffect
    data object TreeChanged : TreeEffect
}
