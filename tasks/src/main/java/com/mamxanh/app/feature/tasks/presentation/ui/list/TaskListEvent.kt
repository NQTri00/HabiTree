package com.mamxanh.app.feature.tasks.presentation.ui.list

import com.mamxanh.app.core.presentation.UiEvent

sealed interface TaskListEvent : UiEvent {
    data object Add : TaskListEvent

    data object Templates : TaskListEvent

    data object Insights : TaskListEvent

    data class Focus(
        val taskId: String = "",
    ) : TaskListEvent

    data class Detail(
        val taskId: String,
    ) : TaskListEvent

    data class Feedback(
        val message: String,
    ) : TaskListEvent
}
