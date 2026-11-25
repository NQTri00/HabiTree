package com.mamxanh.app.feature.stats.presentation.ui.achievements

import com.mamxanh.app.core.presentation.UiAction

sealed interface AchievementsAction : UiAction {
    data object Refresh : AchievementsAction
}
