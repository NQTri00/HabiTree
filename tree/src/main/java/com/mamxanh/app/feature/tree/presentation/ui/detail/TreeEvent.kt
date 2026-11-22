package com.mamxanh.app.feature.tree.presentation.ui.detail

import com.mamxanh.app.core.presentation.UiEvent

sealed interface TreeEvent : UiEvent {
    data object Back : TreeEvent

    data object OpenFertilizerShop : TreeEvent

    data class Feedback(
        val message: String,
    ) : TreeEvent

    data object TreeChanged : TreeEvent
}
