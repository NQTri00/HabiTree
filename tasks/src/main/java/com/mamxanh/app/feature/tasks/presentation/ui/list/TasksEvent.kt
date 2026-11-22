package com.mamxanh.app.feature.tasks.presentation.ui.list
import com.mamxanh.app.core.presentation.UiEvent

sealed interface TasksEvent : UiEvent {
    data class Feedback(
        val message: String,
    ) : TasksEvent

    data object DataChanged : TasksEvent
}
