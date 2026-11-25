package com.mamxanh.app.feature.tasks.domain.repository

fun interface TaskReminderWriter {
    fun save(
        taskId: Long,
        mode: String,
        minute: Int,
        followUp: Boolean,
    ): TaskOperationResult
}
