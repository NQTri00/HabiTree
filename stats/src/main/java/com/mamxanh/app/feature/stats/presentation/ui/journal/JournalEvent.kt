package com.mamxanh.app.feature.stats.presentation.ui.journal

import com.mamxanh.app.core.presentation.UiEvent
import com.mamxanh.app.feature.stats.domain.model.TimelineRelated

sealed interface JournalEvent : UiEvent {
    data class OpenRelated(
        val target: TimelineRelated,
    ) : JournalEvent

    data class Feedback(
        val message: String,
    ) : JournalEvent

    data object OpenTasks : JournalEvent

    data object OpenRest : JournalEvent
}
