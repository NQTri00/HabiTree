package com.mamxanh.app.feature.settings.domain

data class TaskReminderSettings(
    val taskId: Long,
    val mode: String,
    val minute: Int,
    val followUp: Boolean,
)
