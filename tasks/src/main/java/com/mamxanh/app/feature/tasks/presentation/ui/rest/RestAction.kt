package com.mamxanh.app.feature.tasks.presentation.ui.rest

import com.mamxanh.app.core.presentation.UiAction

sealed interface RestAction : UiAction {
    data object Refresh : RestAction

    data class SelectRestDate(
        val date: String,
    ) : RestAction

    data class SelectProtectionDate(
        val date: String,
    ) : RestAction

    data class Schedule(
        val date: String,
    ) : RestAction

    data class Protect(
        val date: String,
    ) : RestAction

    data object RequestTicket : RestAction

    data object BuyTicket : RestAction
}
