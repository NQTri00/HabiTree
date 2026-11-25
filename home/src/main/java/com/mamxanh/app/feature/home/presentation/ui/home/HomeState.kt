package com.mamxanh.app.feature.home.presentation.ui.home

import com.mamxanh.app.core.presentation.UiState
import com.mamxanh.app.feature.home.domain.model.HomeDashboard

data class HomeState(
    val dashboard: HomeDashboard = HomeDashboard(),
    val isLoading: Boolean = true,
    val isDailyClearVisible: Boolean = false,
    val selectedTreeId: String = "0",
) : UiState
