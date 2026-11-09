package com.mamxanh.shared.time

/** Local calendar date, matching the existing persisted yyyy-MM-dd keys. */
expect fun currentDateKey(): String

/** Locale's week boundary, in SQLite order: Sunday=0 through Saturday=6. */
expect fun firstDayOfWeekIndex(): Int
