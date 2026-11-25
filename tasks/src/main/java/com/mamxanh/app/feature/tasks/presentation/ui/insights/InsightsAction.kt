package com.mamxanh.app.feature.tasks.presentation.ui.insights

import com.mamxanh.app.core.presentation.UiAction

sealed interface InsightsAction : UiAction {
    data class Load(
        val id: String,
    ) : InsightsAction

    data class SetPeriod(
        val value: String,
    ) : InsightsAction

    data class OpenTask(
        val id: String,
    ) : InsightsAction

    data class RequestAdjustment(
        val id: String,
    ) : InsightsAction

    data class ApplyAdjustment(
        val id: String,
    ) : InsightsAction

    data class EditReminder(
        val id: String,
        val draft: InsightsReminderDraft,
    ) : InsightsAction

    data class SaveReminder(
        val id: String,
        val draft: InsightsReminderDraft,
    ) : InsightsAction

    data object OpenSettings : InsightsAction

    data object AddTask : InsightsAction
}
