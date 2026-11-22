package com.mamxanh.app.core.components.design

import com.mamxanh.app.core.utils.DisplayDates
import com.mamxanh.shared.localization.AppLanguage
import com.mamxanh.shared.localization.AppText
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Locale

/** Presentation locale only; persisted dates and identifiers retain their machine format. */
fun bioLocale(): Locale =
    when (AppText.language) {
        AppLanguage.English -> Locale.ENGLISH
        AppLanguage.Vietnamese -> Locale.forLanguageTag("vi-VN")
    }

fun bioWeekday(calendarDay: Int): String = DisplayDates.weekday(calendarDay, bioLocale())

/** Display and picker fields share one date order; storage keeps ISO keys. */
fun bioDateFormat(): DateFormat = DisplayDates.shortFormat(bioLocale())

fun bioIsoDateFormat(): SimpleDateFormat = DisplayDates.isoFormat()

fun bioDisplayDate(value: String): String = DisplayDates.format(value, bioLocale())
