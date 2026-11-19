package com.mamxanh.app.core.utils

import java.text.DateFormat
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.Locale

object DisplayDates {
    private val isoDate = Regex("[0-9]{4}-[0-9]{2}-[0-9]{2}")

    fun weekday(calendarDay: Int, locale: Locale): String =
        DateFormatSymbols.getInstance(locale).shortWeekdays[calendarDay]

    fun shortFormat(locale: Locale): DateFormat = DateFormat.getDateInstance(DateFormat.SHORT, locale)

    fun isoFormat(): SimpleDateFormat =
        SimpleDateFormat("yyyy-MM-dd", Locale.ROOT).apply { isLenient = false }

    /** Formats a stored date for display and preserves non-date labels and invalid dates. */
    fun format(value: String, locale: Locale): String {
        if (!isoDate.matches(value)) return value
        val date = runCatching { isoFormat().parse(value) }.getOrNull() ?: return value
        return shortFormat(locale).format(date)
    }
}
