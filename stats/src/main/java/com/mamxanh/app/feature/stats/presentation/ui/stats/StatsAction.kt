package com.mamxanh.app.feature.stats.presentation.ui.stats

import com.mamxanh.app.core.presentation.UiAction

sealed interface StatsAction : UiAction {
    data object Refresh : StatsAction
}
