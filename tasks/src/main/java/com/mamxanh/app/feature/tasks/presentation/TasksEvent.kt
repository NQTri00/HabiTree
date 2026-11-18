package com.mamxanh.app.feature.tasks.presentation

import com.mamxanh.app.core.presentation.UiEvent

sealed interface TasksEffect : UiEvent {
    data class Feedback(val message: String) : TasksEffect
    data object DataChanged : TasksEffect
}
