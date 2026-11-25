package com.mamxanh.app.feature.tasks.presentation.ui.insights

import com.mamxanh.app.core.presentation.UiEvent

sealed interface InsightsEvent : UiEvent {
    data class OpenTask(
        val id: String,
    ) : InsightsEvent

    data class ConfirmAdjustment(
        val id: String,
    ) : InsightsEvent

    data class AdjustmentResult(
        val id: String,
        val success: Boolean,
        val message: String,
    ) : InsightsEvent

    data class LoadFailed(
        val id: String,
        val message: String,
    ) : InsightsEvent

    data class ReminderResult(
        val id: String,
        val message: String,
    ) : InsightsEvent

    data object OpenSettings : InsightsEvent

    data object AddTask : InsightsEvent
}
