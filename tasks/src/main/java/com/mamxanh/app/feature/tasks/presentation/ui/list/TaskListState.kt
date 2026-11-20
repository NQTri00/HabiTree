package com.mamxanh.app.feature.tasks.presentation.ui.list

data class TaskListState(
    val filter: String,
    val items: List<TaskListItem>,
    val completed: Int,
    val required: Int,
    val earned: Int,
    val busy: Boolean,
)
