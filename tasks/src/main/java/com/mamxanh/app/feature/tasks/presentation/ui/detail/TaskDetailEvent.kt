package com.mamxanh.app.feature.tasks.presentation.ui.detail

import com.mamxanh.app.core.presentation.UiEvent

sealed interface TaskDetailEvent : UiEvent {
    data object OpenList : TaskDetailEvent

    data object Edit : TaskDetailEvent

    data object Insights : TaskDetailEvent

    data object Focus : TaskDetailEvent

    data object ConfirmArchive : TaskDetailEvent

    data class Feedback(
        val message: String,
        val archived: Boolean = false,
    ) : TaskDetailEvent
}
