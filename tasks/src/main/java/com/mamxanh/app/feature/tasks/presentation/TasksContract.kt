package com.mamxanh.app.feature.tasks.presentation

import com.mamxanh.app.feature.tasks.domain.model.Task

enum class TaskFilter { ALL, HABIT, DAILY, ONCE, COMPLETED, INACTIVE }

data class TasksUiState(
    val tasks: List<Task> = emptyList(),
    val phase: com.mamxanh.shared.model.PhaseSnapshot = com.mamxanh.shared.model.PhaseSnapshot(),
    val editingId: Long? = null,
    val detailId: Long? = null,
    val busy: Boolean = false,
    val isLoading: Boolean = true,
    val filter: TaskFilter = TaskFilter.ALL,
    val isAddingTask: Boolean = false,
    val draft: List<String> = emptyList(),
    val isDailyBonusClaimed: Boolean = false,
    val canClaimDailyBonus: Boolean = false
)

sealed interface TasksIntent {
    data object Refresh : TasksIntent
    data class Edit(val id: Long) : TasksIntent
    data class Detail(val id: Long?) : TasksIntent
    data class Save(val config: com.mamxanh.shared.model.TaskConfig) : TasksIntent
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

sealed interface TasksEffect {
    data class Feedback(val message: String) : TasksEffect
    data object DataChanged : TasksEffect
}
