package com.mamxanh.app.feature.tasks.presentation.ui.rest

class RestControls(
    val restDate: String,
    val protectionDate: String,
    val busy: Boolean,
    val onRestDate: (String) -> Unit,
    val onProtectionDate: (String) -> Unit,
)
