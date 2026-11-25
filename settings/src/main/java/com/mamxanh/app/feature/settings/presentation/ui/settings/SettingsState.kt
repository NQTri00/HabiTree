package com.mamxanh.app.feature.settings.presentation.ui.settings

import com.mamxanh.app.core.presentation.UiState
import com.mamxanh.app.feature.settings.domain.model.ReminderSettings
import com.mamxanh.shared.model.clockLabel

data class SettingsState(
    val overrides: Map<String, Boolean> = emptyMap(),
    val quietStart: String? = null,
    val quietEnd: String? = null,
    val busy: Boolean = false,
    val persisted: ReminderSettings? = null,
) : UiState {
    fun display() =
        SettingsDisplay(
            options =
                mapOf(
                    "paused" to (persisted?.paused ?: false),
                    "treeReminders" to (persisted?.treeReminders ?: false),
                    "breakPrompts" to (persisted?.breakPrompts ?: false),
                    "reducedMotion" to (persisted?.reducedMotion ?: false),
                ) + overrides,
            quietStart = quietStart ?: persisted?.quietStartMinute?.let(::clockLabel) ?: "22:00",
            quietEnd = quietEnd ?: persisted?.quietEndMinute?.let(::clockLabel) ?: "07:00",
        )
}
