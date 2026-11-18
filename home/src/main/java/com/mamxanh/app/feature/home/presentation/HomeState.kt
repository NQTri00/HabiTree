package com.mamxanh.app.feature.home.presentation

import com.mamxanh.app.core.presentation.UiState
import com.mamxanh.app.feature.home.domain.model.HomeDashboard

data class HomeUiState(val dashboard: HomeDashboard = HomeDashboard(), val isLoading: Boolean = true,
    val isDailyClearVisible: Boolean = false) : UiState
