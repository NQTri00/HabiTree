package com.mamxanh.app.feature.tasks.domain.model

data class Task(
    val id: Long,
    val title: String,
    val kind: String,
    val category: String,
    val reward: Int,
    val frequency: String,
    val due: String,
    val completed: String,
    val streak: Int,
    val lastDone: String,
    val config: TaskConfig = TaskConfig(title, kind, category, frequency = frequency, due = due),
    val lifecycle: String = "ACTIVE",
    val scheduledToday: Boolean = true,
    val available: Boolean = true,
    val availability: String = "",
    val history: List<TaskHistoryEntry> = emptyList(),
    val doneToday: Boolean = false,
) {
    val finished: Boolean get() = kind == "TODO" && completed.isNotBlank()
}

data class TaskHistoryEntry(val date: String, val nominal: Int, val granted: Int)
