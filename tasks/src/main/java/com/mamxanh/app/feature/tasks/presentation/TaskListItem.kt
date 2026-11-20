package com.mamxanh.app.feature.tasks.presentation

data class TaskListItem(
    val id: String,
    val title: String,
    val category: String,
    val minutes: Int,
    val reward: Int,
    val done: Boolean,
    val timer: Boolean,
    val active: Boolean,
)
