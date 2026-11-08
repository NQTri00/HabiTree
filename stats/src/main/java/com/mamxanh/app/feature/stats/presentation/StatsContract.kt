package com.mamxanh.app.feature.stats.presentation

import com.mamxanh.app.feature.stats.domain.model.GardenStatistics

data class StatsUiState(val statistics: GardenStatistics = GardenStatistics(), val isLoading: Boolean = true)
sealed interface StatsIntent { data object Refresh : StatsIntent }
