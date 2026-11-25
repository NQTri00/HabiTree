package com.mamxanh.app.feature.tasks.presentation.ui.templates

import com.mamxanh.app.core.presentation.UiAction

sealed interface TemplatesAction : UiAction {
    data class Toggle(
        val id: String,
    ) : TemplatesAction

    data object Apply : TemplatesAction
}
