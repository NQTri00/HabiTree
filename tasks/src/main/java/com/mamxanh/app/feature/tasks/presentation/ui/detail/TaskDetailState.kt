package com.mamxanh.app.feature.tasks.presentation.ui.detail

import com.mamxanh.app.core.presentation.UiState

data class TaskDetailState(
    val task: TaskDetailDisplay? = null,
    val loading: Boolean = true,
    val busy: Boolean = false,
) : UiState
