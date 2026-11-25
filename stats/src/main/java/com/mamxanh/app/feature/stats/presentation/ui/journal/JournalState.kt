package com.mamxanh.app.feature.stats.presentation.ui.journal

import com.mamxanh.app.core.presentation.UiState
import com.mamxanh.app.feature.stats.domain.usecase.JournalMemory
import com.mamxanh.app.feature.stats.domain.usecase.WeeklyJournalReview

data class JournalState(
    val drafts: Map<String, String> = emptyMap(),
    val busy: Boolean = false,
    val memories: List<JournalMemory> = emptyList(),
    val weeklyReview: WeeklyJournalReview? = null,
) : UiState {
    fun reflection(
        week: String,
        saved: String,
    ): String = drafts["week:" + week] ?: saved

    fun memory(
        id: String,
        saved: String,
    ): String = drafts["memory:" + id] ?: saved
}
