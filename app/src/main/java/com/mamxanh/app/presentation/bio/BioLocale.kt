package com.mamxanh.app.presentation.bio

import com.mamxanh.shared.localization.AppLanguage
import com.mamxanh.shared.localization.AppText
import java.text.DateFormatSymbols
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Locale

/** Presentation locale only; persisted dates and identifiers retain their machine format. */
internal fun bioLocale(): Locale = when (AppText.language) {
    AppLanguage.English -> Locale.ENGLISH
    AppLanguage.Vietnamese -> Locale.forLanguageTag("vi-VN")
}

internal fun bioWeekday(calendarDay: Int): String =
    DateFormatSymbols.getInstance(bioLocale()).shortWeekdays[calendarDay]

/** Display and picker fields share one date order; storage keeps ISO keys. */
internal fun bioDateFormat(): DateFormat = DateFormat.getDateInstance(DateFormat.SHORT, bioLocale())

internal fun bioIsoDateFormat(): SimpleDateFormat =
    SimpleDateFormat("yyyy-MM-dd", Locale.ROOT).apply { isLenient = false }
