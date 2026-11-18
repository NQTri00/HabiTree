package com.mamxanh.app.feature.stats.presentation

sealed interface StatsIntent { data object Refresh : StatsIntent }
