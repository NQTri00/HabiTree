package com.mamxanh.app.feature.tree.presentation.ui.detail

import com.mamxanh.app.core.presentation.UiEvent

sealed interface TreeEvent : UiEvent {
    data class BrowseTo(
        val fromId: Long,
        val treeId: Long,
    ) : TreeEvent

    data object Back : TreeEvent

    data object OpenFertilizerShop : TreeEvent

    data class Feedback(
        val message: String,
    ) : TreeEvent

    data class ConfirmDisposition(
        val id: Long,
        val sell: Boolean,
    ) : TreeEvent

    data class Disposed(
        val message: String,
    ) : TreeEvent
}
