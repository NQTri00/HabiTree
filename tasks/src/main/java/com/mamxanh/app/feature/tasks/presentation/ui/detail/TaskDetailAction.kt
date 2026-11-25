package com.mamxanh.app.feature.tasks.presentation.ui.detail

import com.mamxanh.app.core.presentation.UiAction

sealed interface TaskDetailAction : UiAction {
    data object OpenList : TaskDetailAction

    data object Edit : TaskDetailAction

    data object Insights : TaskDetailAction

    data object Complete : TaskDetailAction

    data object RequestArchive : TaskDetailAction

    data object Archive : TaskDetailAction

    data class ChangeLifecycle(
        val value: String,
    ) : TaskDetailAction
}
