package com.mamxanh.app.feature.tasks.presentation.ui.templates

import com.mamxanh.app.core.presentation.UiState

data class TemplatesState(
    val selected: Set<String> = emptySet(),
    val busy: Boolean = false,
) : UiState
