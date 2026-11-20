package com.mamxanh.app.feature.tasks.domain

data class TaskProgress(
    val earnedToday: Int = 0,
    val required: Int = 0,
    val completed: Int = 0,
    val streak: Int = 0,
    val sessions: List<TaskTimerProgress> = emptyList(),
)

data class TaskTimerProgress(
    val id: String,
    val taskId: Long,
    val activeSeconds: Long,
    val requiredSeconds: Long,
    val running: Boolean,
)
