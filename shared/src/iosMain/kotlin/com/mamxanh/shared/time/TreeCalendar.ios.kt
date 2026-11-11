package com.mamxanh.shared.time
import platform.Foundation.*
private fun treeFormatter(zone: String) = NSDateFormatter().apply {
    locale=NSLocale(localeIdentifier="en_US_POSIX")
    timeZone=NSTimeZone.timeZoneWithName(zone) ?: NSTimeZone.localTimeZone
    dateFormat="yyyy-MM-dd HH:mm"
    lenient=true
}
actual fun treeDateAt(epochMillis: Long,zone: String): String =
    treeFormatter(zone).stringFromDate(NSDate(timeIntervalSinceReferenceDate=epochMillis/1000.0-978307200.0)).take(10)
actual fun treeMidnightAfter(epochMillis: Long,zone: String): Long =
    ((treeFormatter(zone).dateFromString("${followingDate(treeDateAt(epochMillis,zone))} 00:00")?.timeIntervalSince1970
        ?: (epochMillis/1000.0+86400)) * 1000).toLong()
