package com.mamxanh.app.feature.tasks.presentation.ui.form

import com.mamxanh.app.core.presentation.UiAction

sealed interface TaskFormAction : UiAction {
    data class Change(
        val field: TaskFormField,
        val value: String,
    ) : TaskFormAction

    data object Load : TaskFormAction

    data object Save : TaskFormAction

    data object Discard : TaskFormAction
}
