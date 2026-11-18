package com.mamxanh.app.feature.tasks.presentation

import com.mamxanh.app.core.presentation.UiState
import com.mamxanh.app.feature.tasks.domain.model.Task

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
) : UiState
