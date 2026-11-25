package com.mamxanh.app.feature.tasks.presentation.ui.insights

import com.mamxanh.app.core.presentation.UiState
import com.mamxanh.app.feature.tasks.domain.repository.InsightsSnapshot

data class InsightsState(
    val period: String = "7",
    val busy: Boolean = false,
    val snapshot: InsightsSnapshot = InsightsSnapshot(),
    val reminders: Map<String, InsightsReminderDraft> = emptyMap(),
) : UiState
