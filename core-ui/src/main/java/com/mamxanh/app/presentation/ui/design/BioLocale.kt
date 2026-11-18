package com.mamxanh.app.presentation.ui.design

import com.mamxanh.shared.localization.AppLanguage
import com.mamxanh.shared.localization.AppText
import java.text.DateFormatSymbols
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Locale

/** Presentation locale only; persisted dates and identifiers retain their machine format. */
fun bioLocale(): Locale = when (AppText.language) {
    AppLanguage.English -> Locale.ENGLISH
    AppLanguage.Vietnamese -> Locale.forLanguageTag("vi-VN")
}

fun bioWeekday(calendarDay: Int): String =
    DateFormatSymbols.getInstance(bioLocale()).shortWeekdays[calendarDay]

/** Display and picker fields share one date order; storage keeps ISO keys. */
fun bioDateFormat(): DateFormat = DateFormat.getDateInstance(DateFormat.SHORT, bioLocale())

fun bioIsoDateFormat(): SimpleDateFormat =
    SimpleDateFormat("yyyy-MM-dd", Locale.ROOT).apply { isLenient = false }

fun bioDisplayDate(value:String):String {
    if(!Regex("\\d{4}-\\d{2}-\\d{2}").matches(value))return value
    return runCatching { bioDateFormat().format(bioIsoDateFormat().parse(value)!!) }.getOrDefault(value)
}
