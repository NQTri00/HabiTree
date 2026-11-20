package com.mamxanh.app.feature.tasks.presentation

data class TaskInsightsDisplay(
    val id: String,
    val title: String,
    val streak: Int,
    val longest: Int,
    val rate: Int?,
    val focusMinutes: Int,
    val commonHour: String,
    val suggestion: String,
    val minute: String,
    val mode: String,
    val followUp: Boolean,
)
