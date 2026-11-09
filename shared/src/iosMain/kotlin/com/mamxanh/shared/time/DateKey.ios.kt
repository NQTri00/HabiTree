package com.mamxanh.shared.time

import platform.Foundation.*

actual fun currentDateKey(): String = NSDateFormatter().apply {
    locale = NSLocale(localeIdentifier = "en_US_POSIX")
    timeZone = NSTimeZone.localTimeZone
    dateFormat = "yyyy-MM-dd"
}.stringFromDate(NSDate())

actual fun firstDayOfWeekIndex(): Int = platform.Foundation.NSCalendar.currentCalendar.firstWeekday.toInt() - 1
