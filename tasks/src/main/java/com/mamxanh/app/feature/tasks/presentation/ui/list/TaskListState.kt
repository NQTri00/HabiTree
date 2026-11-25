package com.mamxanh.app.feature.tasks.presentation.ui.list

import com.mamxanh.app.core.presentation.UiState

data class TaskListState(
    val filter: String,
    val items: List<TaskListItem>,
    val completed: Int,
    val required: Int,
    val earned: Int,
    val busy: Boolean,
    val rewardClaimed: Boolean = false,
    val canClaimReward: Boolean = false,
    val loading: Boolean = true,
) : UiState
