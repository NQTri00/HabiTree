package com.mamxanh.app.feature.stats.domain.usecase

data class WeeklyJournalReview(
    val start: String,
    val reflection: String,
    val completed: Int,
    val scheduled: Int,
    val focusMinutes: Int,
    val previousRate: Int?,
    val earned: Int,
    val spent: Int,
    val careDays: Int,
    val milestones: Int,
    val consistent: String,
    val improve: String,
    val tickets: Int,
    val dailyCompletions: List<Int>,
)

fun interface WeeklyReviewRepository {
    fun load(): WeeklyJournalReview?
}
