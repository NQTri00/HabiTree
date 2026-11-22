package com.mamxanh.app.feature.tasks.presentation.ui.list

import com.mamxanh.app.core.presentation.UiAction

sealed interface TasksAction : UiAction {
    data object Refresh : TasksAction

    data class Edit(
        val id: Long,
    ) : TasksAction

    data class Detail(
        val id: Long?,
    ) : TasksAction

    data class Save(
        val config: com.mamxanh.app.feature.tasks.domain.model.TaskConfig,
    ) : TasksAction

    data class Lifecycle(
        val id: Long,
        val state: String,
    ) : TasksAction

    data class Timer(
        val id: Long,
        val action: String,
    ) : TasksAction

    data class Foreground(
        val active: Boolean,
    ) : TasksAction

    data object ShowAddDialog : TasksAction

    data object DismissAddDialog : TasksAction

    data class UpdateDraft(
        val values: List<String>,
    ) : TasksAction

    data class SetFilter(
        val filter: TaskFilter,
    ) : TasksAction

    data class Add(
        val title: String,
        val kind: String,
        val category: String,
        val reward: Int,
        val frequency: String,
        val due: String,
    ) : TasksAction

    data class Delete(
        val id: Long,
    ) : TasksAction

    data class Complete(
        val id: Long,
    ) : TasksAction

    data object ClaimBonus : TasksAction
}
