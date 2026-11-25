package com.mamxanh.app.feature.stats.presentation.ui.achievements

import com.mamxanh.app.core.presentation.UiState

data class AchievementsState(
    val entries: List<AchievementDisplay> = emptyList(),
    val loading: Boolean = true,
) : UiState
