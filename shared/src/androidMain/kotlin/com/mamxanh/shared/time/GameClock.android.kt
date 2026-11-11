package com.mamxanh.shared.time
import java.util.Calendar
import java.util.TimeZone
actual class SystemGameClock actual constructor() : GameClock {
    actual override fun now() = System.currentTimeMillis()
    actual override fun elapsed() = android.os.SystemClock.elapsedRealtime()
    actual override fun date() = currentDateKey()
    actual override fun zone() = TimeZone.getDefault().id
    actual override fun localMinute(date: String, minute: Int): Long {
        val p = date.split("-").map(String::toInt)
        return Calendar.getInstance().apply {
            clear(); set(p[0], p[1]-1, p[2], minute / 60, minute % 60)
        }.timeInMillis
    }
}
