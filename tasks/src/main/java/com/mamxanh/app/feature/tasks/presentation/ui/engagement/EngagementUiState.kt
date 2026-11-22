package com.mamxanh.app.feature.tasks.presentation.ui.engagement
import com.mamxanh.app.core.presentation.UiState
import com.mamxanh.shared.model.EngagementSnapshot
import com.mamxanh.shared.model.Task

data class EngagementUiState(
    val snapshot: EngagementSnapshot = EngagementSnapshot(),
    val tasks: List<Task> = emptyList(),
    val route: String = "",
    val selectedTask: Long = 0,
    val busy: Boolean = false,
    val loading: Boolean = true,
    val message: String = "",
) : UiState
