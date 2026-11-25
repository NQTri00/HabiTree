package com.mamxanh.app.feature.tasks.presentation.ui.form

import com.mamxanh.app.core.presentation.UiEvent

sealed interface TaskFormEvent : UiEvent {
    data class Feedback(
        val message: String,
    ) : TaskFormEvent
}
