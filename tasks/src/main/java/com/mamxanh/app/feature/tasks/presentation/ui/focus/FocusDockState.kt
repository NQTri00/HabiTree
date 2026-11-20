package com.mamxanh.app.feature.tasks.presentation.ui.focus

data class FocusDockState(
    val hasSession: Boolean,
    val running: Boolean,
    val canStart: Boolean,
    val busy: Boolean,
)
