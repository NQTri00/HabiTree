package com.mamxanh.app.feature.stats.presentation.ui.journal

import com.mamxanh.app.core.presentation.UiAction
import com.mamxanh.app.feature.stats.domain.model.TimelineRelated
import com.mamxanh.app.feature.stats.domain.usecase.JournalNote

sealed interface JournalAction : UiAction {
    data object LoadTimeline : JournalAction

    data object LoadWeeklyReview : JournalAction

    data class Edit(
        val note: JournalNote,
    ) : JournalAction

    data class Save(
        val note: JournalNote,
    ) : JournalAction

    data class OpenRelated(
        val target: TimelineRelated,
    ) : JournalAction

    data object OpenTasks : JournalAction

    data object OpenRest : JournalAction
}
