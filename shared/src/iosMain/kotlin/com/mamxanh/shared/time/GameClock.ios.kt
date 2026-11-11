package com.mamxanh.shared.time
import platform.Foundation.*
actual class SystemGameClock actual constructor() : GameClock {
    actual override fun now() = (NSDate().timeIntervalSince1970 * 1000).toLong()
    actual override fun elapsed() = (NSProcessInfo.processInfo.systemUptime * 1000).toLong()
    actual override fun date() = currentDateKey()
    actual override fun zone() = NSTimeZone.localTimeZone.name
    actual override fun localMinute(date: String, minute: Int): Long {
        val formatter = NSDateFormatter().apply {
            locale = NSLocale(localeIdentifier = "en_US_POSIX")
            timeZone = NSTimeZone.localTimeZone
            dateFormat = "yyyy-MM-dd HH:mm"
            lenient = true
        }
        val time = "${(minute / 60).toString().padStart(2,'0')}:${(minute % 60).toString().padStart(2,'0')}"
        return ((formatter.dateFromString("$date $time")?.timeIntervalSince1970 ?: 0.0) * 1000).toLong()
    }
}
