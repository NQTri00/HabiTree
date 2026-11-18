package com.mamxanh.app.feature.stats.presentation

import com.mamxanh.app.core.presentation.UiAction

sealed interface StatsIntent : UiAction { data object Refresh : StatsIntent }
