package com.mamxanh.app.feature.tasks.presentation.ui.rest

import com.mamxanh.app.core.presentation.UiState
import com.mamxanh.app.feature.tasks.domain.repository.RestSchedule

data class RestState(
    val restDate: String? = null,
    val protectionDate: String = "",
    val busy: Boolean = false,
    val schedule: RestSchedule? = null,
) : UiState
