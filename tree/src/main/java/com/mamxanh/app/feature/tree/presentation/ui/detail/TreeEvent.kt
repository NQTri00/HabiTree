package com.mamxanh.app.feature.tree.presentation.ui.detail

import com.mamxanh.app.core.presentation.UiEvent

sealed interface TreeEffect : UiEvent {
    data object Back : TreeEffect
    data object OpenFertilizerShop : TreeEffect
    data class Feedback(val message: String) : TreeEffect
    data object TreeChanged : TreeEffect
}
