package com.mamxanh.app.feature.tasks.presentation.ui.templates

import com.mamxanh.app.core.presentation.UiEvent

sealed interface TemplatesEvent : UiEvent {
    data class Feedback(
        val message: String,
    ) : TemplatesEvent
}
