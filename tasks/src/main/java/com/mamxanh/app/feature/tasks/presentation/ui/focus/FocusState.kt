package com.mamxanh.app.feature.tasks.presentation.ui.focus

import com.mamxanh.app.core.presentation.UiState
import com.mamxanh.app.feature.tasks.presentation.ui.engagement.EngagementUiState
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey

data class FocusState(
    val taskId: String,
    val minutes: String,
    val busy: Boolean = false,
) : UiState {
    fun display(
        engagement: EngagementUiState,
        date: String,
    ): FocusDisplay {
        val current = engagement.snapshot.focus.firstOrNull { it.state in activeStates }
        val task = engagement.tasks.firstOrNull { it.id.toString() == taskId }
        return FocusDisplay(
            title =
                current?.let { focusSessionTitle(it.taskId, it.title) }
                    ?: AppText.text(TextKey.CopyMotKhoangRiengMinh9ead0d),
            present = current != null,
            running = current?.state == "RUNNING",
            onBreak = current?.state == "BREAK",
            remaining = current?.remaining?.toInt() ?: 1500,
            date = date,
            minutes = task?.config?.minutes?.toString() ?: minutes,
            task = taskId,
            eligibleTasks =
                engagement.tasks
                    .filter {
                        it.config.verification == "TIMER" && !it.doneToday && it.available && it.lifecycle == "ACTIVE"
                    }.map { FocusTaskChoice(it.id.toString(), it.title, it.config.minutes) },
            history =
                engagement.snapshot.focus
                    .filter { it.state == "COMPLETE" }
                    .map { focusSessionTitle(it.taskId, it.title) },
        )
    }

    private companion object {
        val activeStates = setOf("RUNNING", "PAUSED", "BREAK")
    }
}
