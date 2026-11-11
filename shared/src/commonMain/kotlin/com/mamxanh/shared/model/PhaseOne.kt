package com.mamxanh.shared.model

object Balance {
    const val VERSION = 2
    const val DAILY_CAP = 50
    const val DAILY_CLEAR = 8
    const val FERTILIZER = 35
    val milestones = mapOf(7 to 10,14 to 15,30 to 20)
    fun reward(mode: String, minutes: Int) = if (mode != "TIMER") 8 else when {
        minutes >= 45 -> 20
        minutes >= 20 -> 15
        else -> 10
    }
    fun plot(unlocked: Int) = 400 + (unlocked - GardenPlotPolicy.INITIAL_PLOTS).coerceAtLeast(0) * 200
}
data class TaskConfig(
    val title: String = "", val kind: String = "DAILY", val category: String = "Học tập",
    val description: String = "", val frequency: String = "EVERY_DAY", val days: String = "",
    val due: String = "", val verification: String = "CLICK", val minutes: Int = 5,
    val startMinute: Int = -1, val endMinute: Int = -1, val reminderMinute: Int = -1
) {
    val reward get() = Balance.reward(verification, minutes)
    fun error(): String? = when {
        title.trim().length !in 1..100 -> "Tên việc cần từ 1 đến 100 ký tự"
        description.length > 500 -> "Mô tả tối đa 500 ký tự"
        kind !in setOf("HABIT", "DAILY", "TODO") -> "Loại việc không hợp lệ"
        verification !in setOf("CLICK", "TIMER") -> "Chọn Click hoặc Timer"
        verification == "TIMER" && minutes !in 5..240 -> "Thời gian từ 5 đến 240 phút"
        frequency !in setOf("EVERY_DAY","WEEKDAYS","WEEKENDS","CUSTOM","ONCE") -> "Lịch không hợp lệ"
        frequency == "CUSTOM" && (days.isEmpty() || days.any { it !in '0'..'6' }) -> "Chọn ít nhất một ngày"
        kind == "TODO" && due.isNotBlank() && !validDate(due) -> "Hạn chót theo YYYY-MM-DD"
        (startMinute == -1) != (endMinute == -1) -> "Nhập cả giờ bắt đầu và kết thúc"
        startMinute !in -1..1439 || endMinute !in -1..1439 || reminderMinute !in -1..1439 -> "Giờ không hợp lệ"
        startMinute >= 0 && startMinute == endMinute -> "Khung giờ phải có độ dài lớn hơn 0"
        else -> null
    }
}
fun validDate(value: String): Boolean {
    if (!Regex("[0-9]{4}-[0-9]{2}-[0-9]{2}").matches(value)) return false
    val parts = value.split('-').map { it.toIntOrNull() ?: return false }
    if (parts.size != 3 || value.length != 10 || parts[0] !in 1970..9999 || parts[1] !in 1..12) return false
    val leap = parts[0] % 4 == 0 && (parts[0] % 100 != 0 || parts[0] % 400 == 0)
    return parts[2] in 1..listOf(31, if (leap) 29 else 28,31,30,31,30,31,31,30,31,30,31)[parts[1]-1]
}
fun clockLabel(minute: Int) = if (minute < 0) "" else "${(minute / 60).toString().padStart(2,'0')}:${(minute % 60).toString().padStart(2,'0')}"
data class TaskOccurrence(val date: String, val title: String, val category: String, val nominal: Int, val granted: Int)
data class TimerSession(val id: String, val taskId: Long, val date: String, val activeSeconds: Long,
    val requiredSeconds: Long, val running: Boolean, val reward: Int)
data class ActionResult(val success: Boolean, val message: String, val nominal: Int = 0, val granted: Int = 0)
data class WalletEntry(val id: String, val date: String, val type: String, val entity: String,
    val nominal: Int, val amount: Int, val balance: Int)
data class DaySummary(val date: String, val eligible: Int, val completed: Int, val cleared: Boolean, val earned: Int)
data class CareEvent(val treeId: Long, val type: String, val timestamp: Long, val resource: Int, val growth: Int)
data class PhaseSnapshot(val earnedToday: Int = 0, val required: Int = 0, val completed: Int = 0,
    val streak: Int = 0, val ledger: List<WalletEntry> = emptyList(), val days: List<DaySummary> = emptyList(),
    val occurrences: List<TaskOccurrence> = emptyList(), val sessions: List<TimerSession> = emptyList(),
    val care: List<CareEvent> = emptyList(), val categories: Map<String, Int> = emptyMap())
