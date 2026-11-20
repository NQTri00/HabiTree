package com.mamxanh.app.feature.tasks.presentation.ui.list
import com.mamxanh.app.feature.tasks.presentation.ui.form.message

import com.mamxanh.app.core.presentation.UiEvent

sealed interface TasksEffect : UiEvent {
    data class Feedback(val message: String) : TasksEffect
    data object DataChanged : TasksEffect
}
