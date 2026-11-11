package com.mamxanh.shared.time
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
actual fun treeDateAt(epochMillis: Long,zone: String): String =
    SimpleDateFormat("yyyy-MM-dd",Locale.US).apply{timeZone=TimeZone.getTimeZone(zone)}.format(java.util.Date(epochMillis))
actual fun treeMidnightAfter(epochMillis: Long,zone: String): Long =
    Calendar.getInstance(TimeZone.getTimeZone(zone)).apply {
        timeInMillis=epochMillis
        add(Calendar.DAY_OF_MONTH,1)
        set(Calendar.HOUR_OF_DAY,0);set(Calendar.MINUTE,0);set(Calendar.SECOND,0);set(Calendar.MILLISECOND,0)
    }.timeInMillis
