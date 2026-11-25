package com.mamxanh.app.feature.tasks.presentation.ui.focus

import com.mamxanh.app.core.presentation.UiAction

sealed interface FocusAction : UiAction {
    data class SelectTask(
        val id: String,
    ) : FocusAction

    data class SetMinutes(
        val value: String,
    ) : FocusAction

    data class Start(
        val taskId: Long,
        val minutes: Int,
    ) : FocusAction

    data object Toggle : FocusAction

    data object RequestEnd : FocusAction

    data object End : FocusAction

    data object Leave : FocusAction
}
