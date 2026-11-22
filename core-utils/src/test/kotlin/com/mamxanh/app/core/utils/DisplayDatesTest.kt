package com.mamxanh.app.core.utils

import org.junit.Assert.assertEquals
import org.junit.Test
import java.text.DateFormat
import java.text.DateFormatSymbols
import java.util.Calendar
import java.util.Locale

class DisplayDatesTest {
    @Test
    fun appliesTheAppLocaleWithoutChangingTheStoredIsoDate() {
        val stored = "2024-02-29"
        val date = requireNotNull(DisplayDates.isoFormat().parse(stored))
        for (locale in listOf(Locale.ENGLISH, Locale.forLanguageTag("vi-VN"))) {
            assertEquals(DateFormat.getDateInstance(DateFormat.SHORT, locale).format(date), DisplayDates.format(stored, locale))
            assertEquals(
                DateFormatSymbols.getInstance(locale).shortWeekdays[Calendar.THURSDAY],
                DisplayDates.weekday(Calendar.THURSDAY, locale),
            )
        }
        assertEquals(stored, DisplayDates.isoFormat().format(date))
    }

    @Test
    fun retainsInvalidDatesAndLabels() {
        for (value in listOf("2023-02-29", "2026-13-01", "29/02/2024", "No record", "")) {
            assertEquals(value, DisplayDates.format(value, Locale.ENGLISH))
        }
    }
}
