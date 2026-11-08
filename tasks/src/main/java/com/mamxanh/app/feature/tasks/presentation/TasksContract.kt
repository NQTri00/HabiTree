package com.mamxanh.app.feature.tasks.presentation

import com.mamxanh.app.feature.tasks.domain.model.Task

enum class TaskFilter { ALL, HABIT, DAILY, ONCE }

data class TasksUiState(
    val tasks: List<Task> = emptyList(),
    val isLoading: Boolean = true,
    val filter: TaskFilter = TaskFilter.ALL,
    val isAddingTask: Boolean = false
)

sealed interface TasksIntent {
    data object Refresh : TasksIntent
    data object ShowAddDialog : TasksIntent
    data object DismissAddDialog : TasksIntent
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
