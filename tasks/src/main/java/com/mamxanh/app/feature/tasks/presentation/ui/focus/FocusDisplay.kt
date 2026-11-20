package com.mamxanh.app.feature.tasks.presentation.ui.focus

data class FocusDisplay(
    val title: String,
    val present: Boolean,
    val running: Boolean,
    val onBreak: Boolean,
    val remaining: Int,
    val date: String,
    val minutes: String,
    val task: String,
    val eligibleTasks: List<FocusTaskChoice>,
    val history: List<String>,
)
