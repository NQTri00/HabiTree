package com.mamxanh.shared.feature.tasks.domain.model

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
    val lastDone: String
) {
    val doneToday: Boolean get() = completed == today()
    val finished: Boolean get() = kind == "TODO" && completed.isNotBlank()
}

fun today(): String = com.mamxanh.shared.time.currentDateKey()
