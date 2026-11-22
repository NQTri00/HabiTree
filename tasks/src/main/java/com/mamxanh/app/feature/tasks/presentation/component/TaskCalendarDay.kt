package com.mamxanh.app.feature.tasks.presentation.component

data class TaskCalendarDay(
    val date: String,
    val status: TaskCalendarStatus,
)

enum class TaskCalendarStatus {
    Completed,
    Rest,
    Protected,
    Missed,
    Unrecorded,
}
