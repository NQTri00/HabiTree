package com.mamxanh.app.feature.tasks.presentation

data class TaskDetailDisplay(
    val category: String,
    val title: String,
    val kind: String,
    val frequency: String,
    val days: String,
    val description: String,
    val start: String,
    val end: String,
    val due: String,
    val lastDone: String,
    val lifecycle: String,
    val minutes: Int,
    val streak: Int,
    val reward: Int,
    val done: Boolean,
    val timer: Boolean,
    val history: List<TaskCompletionDisplay>,
)
