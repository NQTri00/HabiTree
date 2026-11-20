package com.mamxanh.app.feature.stats.presentation

data class WeeklyReviewDisplay(
    val startDisplay: String,
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
    val dayLabels: List<String>,
)
