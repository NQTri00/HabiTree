package com.mamxanh.app.feature.tasks.presentation.ui.templates

data class TaskTemplateDisplay(
    val id: String,
    val title: String,
    val category: String,
    val minutes: Int,
    val effort: String,
    val timer: Boolean,
    val weekends: Boolean,
    val reward: Int,
    val timeWindow: String = "",
)
