package com.mamxanh.app.feature.tasks.presentation.ui.focus

import com.mamxanh.app.core.presentation.UiEvent

sealed interface FocusEvent : UiEvent {
    data object ConfirmEnd : FocusEvent

    data class Feedback(
        val message: String,
        val success: Boolean,
    ) : FocusEvent
}
