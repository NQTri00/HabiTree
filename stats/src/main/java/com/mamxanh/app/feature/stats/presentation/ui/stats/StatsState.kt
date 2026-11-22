package com.mamxanh.app.feature.stats.presentation.ui.stats

import com.mamxanh.app.core.presentation.UiState
import com.mamxanh.app.feature.stats.domain.model.StatisticsSummary

data class StatsState(
    val statistics: StatisticsSummary = StatisticsSummary(),
    val isLoading: Boolean = true,
) : UiState
