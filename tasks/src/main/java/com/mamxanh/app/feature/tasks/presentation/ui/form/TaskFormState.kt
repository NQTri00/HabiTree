package com.mamxanh.app.feature.tasks.presentation.ui.form

import com.mamxanh.app.core.presentation.UiState

data class TaskFormState(
    val draft: Map<TaskFormField, String> = emptyMap(),
    val error: String = "",
    val errorAttempt: Int = 0,
    val busy: Boolean = false,
    val initial: Map<TaskFormField, String>? = null,
    val savedMessage: String? = null,
) : UiState {
    val dirty: Boolean get() = draft.isNotEmpty()

    fun display(): TaskFormDisplay {
        val values = initial.orEmpty() + draft
        return TaskFormDisplay(
            values,
            error,
            errorAttempt.takeIf { it > 0 }?.toString(),
            taskFormReward(values[TaskFormField.Verification].orEmpty(), values[TaskFormField.Minutes].orEmpty()),
        )
    }
}
