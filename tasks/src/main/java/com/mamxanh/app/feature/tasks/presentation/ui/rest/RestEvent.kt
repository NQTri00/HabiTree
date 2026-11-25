package com.mamxanh.app.feature.tasks.presentation.ui.rest

import com.mamxanh.app.core.presentation.UiEvent

sealed interface RestEvent : UiEvent {
    data object ConfirmTicket : RestEvent

    data class Feedback(
        val message: String,
        val success: Boolean,
        val ticket: Boolean,
    ) : RestEvent
}
