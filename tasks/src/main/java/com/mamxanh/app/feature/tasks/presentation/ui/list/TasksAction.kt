package com.mamxanh.app.feature.tasks.presentation.ui.list

import com.mamxanh.app.core.presentation.UiAction

sealed interface TasksIntent : UiAction {
    data object Refresh : TasksIntent
    data class Edit(val id: Long) : TasksIntent
    data class Detail(val id: Long?) : TasksIntent
    data class Save(val config: com.mamxanh.app.feature.tasks.domain.model.TaskConfig) : TasksIntent
    data class Lifecycle(val id: Long,val state: String) : TasksIntent
    data class Timer(val id: Long,val action: String) : TasksIntent
    data class Foreground(val active: Boolean) : TasksIntent
    data object ShowAddDialog : TasksIntent
    data object DismissAddDialog : TasksIntent
    data class UpdateDraft(val values: List<String>) : TasksIntent
    data class SetFilter(val filter: TaskFilter) : TasksIntent
    data class Add(val title: String, val kind: String, val category: String, val reward: Int, val frequency: String, val due: String) : TasksIntent
    data class Delete(val id: Long) : TasksIntent
    data class Complete(val id: Long) : TasksIntent
    data object ClaimBonus : TasksIntent
}
