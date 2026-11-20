package com.mamxanh.app.feature.tasks.domain.model

enum class TaskConfigError { Title, Description, Kind, Verification, Duration, Frequency, Days, Date, TimePair, TimeRange, ZeroDuration }

data class TaskConfig(
    val title: String = "", val kind: String = "DAILY", val category: String = "",
    val description: String = "", val frequency: String = "EVERY_DAY", val days: String = "",
    val due: String = "", val verification: String = "CLICK", val minutes: Int = 5,
    val startMinute: Int = -1, val endMinute: Int = -1, val reminderMinute: Int = -1
) {
    fun validationError(): TaskConfigError? = when {
        title.trim().length !in 1..100 -> TaskConfigError.Title
        description.length > 500 -> TaskConfigError.Description
        kind !in setOf("HABIT", "DAILY", "TODO") -> TaskConfigError.Kind
        verification !in setOf("CLICK", "TIMER") -> TaskConfigError.Verification
        verification == "TIMER" && minutes !in 5..240 -> TaskConfigError.Duration
        frequency !in setOf("EVERY_DAY","WEEKDAYS","WEEKENDS","CUSTOM","ONCE") -> TaskConfigError.Frequency
        frequency == "CUSTOM" && (days.isEmpty() || days.any { it !in '0'..'6' }) -> TaskConfigError.Days
        kind == "TODO" && due.isNotBlank() && !validDate(due) -> TaskConfigError.Date
        (startMinute == -1) != (endMinute == -1) -> TaskConfigError.TimePair
        startMinute !in -1..1439 || endMinute !in -1..1439 || reminderMinute !in -1..1439 -> TaskConfigError.TimeRange
        startMinute >= 0 && startMinute == endMinute -> TaskConfigError.ZeroDuration
        else -> null
    }
}
private fun validDate(value: String): Boolean {
    if (!Regex("[0-9]{4}-[0-9]{2}-[0-9]{2}").matches(value)) return false
    val parts = value.split('-').map { it.toIntOrNull() ?: return false }
    if (parts.size != 3 || value.length != 10 || parts[0] !in 1970..9999 || parts[1] !in 1..12) return false
    val leap = parts[0] % 4 == 0 && (parts[0] % 100 != 0 || parts[0] % 400 == 0)
    return parts[2] in 1..listOf(31, if (leap) 29 else 28,31,30,31,30,31,31,30,31,30,31)[parts[1]-1]
}
