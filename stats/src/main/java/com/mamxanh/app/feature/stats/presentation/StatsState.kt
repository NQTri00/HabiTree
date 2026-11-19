package com.mamxanh.app.feature.stats.presentation

import com.mamxanh.app.core.presentation.UiState
import com.mamxanh.app.feature.stats.domain.model.StatisticsSummary

data class StatsUiState(val statistics: StatisticsSummary = StatisticsSummary(), val isLoading: Boolean = true) : UiState
