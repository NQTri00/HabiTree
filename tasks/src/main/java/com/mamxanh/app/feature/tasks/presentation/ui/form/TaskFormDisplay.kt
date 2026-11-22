package com.mamxanh.app.feature.tasks.presentation.ui.form

enum class TaskFormField {
    Title,
    Kind,
    Category,
    Due,
    Frequency,
    Days,
    Verification,
    Minutes,
    Description,
    Start,
    End,
    Reminder,
    ;

    val savedKey: String get() = name.lowercase()
}

data class TaskFormDisplay(
    val values: Map<TaskFormField, String>,
    val error: String,
    val errorAttempt: String?,
    val reward: Int,
)

fun taskFormValidationError(value: (TaskFormField) -> String): TaskFormField? =
    when {
        value(TaskFormField.Title).isBlank() -> TaskFormField.Title
        value(TaskFormField.Verification) == "TIMER" &&
            (value(TaskFormField.Minutes).toIntOrNull() ?: 0) !in 5..240 -> TaskFormField.Minutes
        value(TaskFormField.Kind) != "TODO" &&
            value(TaskFormField.Frequency) == "CUSTOM" &&
            value(TaskFormField.Days).isBlank() -> TaskFormField.Days
        else -> null
    }

fun taskFormReward(
    verification: String,
    duration: String,
): Int {
    if (verification != "TIMER") return 8
    val minutes = duration.toIntOrNull() ?: 5
    return when {
        minutes >= 45 -> 20
        minutes >= 20 -> 15
        else -> 10
    }
}
