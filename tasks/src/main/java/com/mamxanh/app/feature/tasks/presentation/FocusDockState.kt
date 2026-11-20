package com.mamxanh.app.feature.tasks.presentation

data class FocusDockState(
    val hasSession: Boolean,
    val running: Boolean,
    val canStart: Boolean,
    val busy: Boolean,
)
