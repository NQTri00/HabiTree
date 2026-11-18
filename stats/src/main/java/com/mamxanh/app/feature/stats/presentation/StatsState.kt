package com.mamxanh.app.feature.stats.presentation

import com.mamxanh.app.core.presentation.UiState
import com.mamxanh.app.feature.stats.domain.model.GardenStatistics

data class StatsUiState(val statistics: GardenStatistics = GardenStatistics(), val isLoading: Boolean = true) : UiState
