package com.mamxanh.app.feature.tasks.presentation.ui.list

import com.mamxanh.app.core.presentation.UiState
import com.mamxanh.app.feature.tasks.domain.model.Task
import com.mamxanh.app.feature.tasks.domain.model.TaskProgress

data class TasksState(
    val tasks: List<Task> = emptyList(),
    val phase: TaskProgress = TaskProgress(),
    val editingId: Long? = null,
    val detailId: Long? = null,
    val busy: Boolean = false,
    val isLoading: Boolean = true,
    val filter: TaskFilter = TaskFilter.ALL,
    val isAddingTask: Boolean = false,
    val draft: List<String> = emptyList(),
    val isDailyBonusClaimed: Boolean = false,
    val canClaimDailyBonus: Boolean = false,
) : UiState
