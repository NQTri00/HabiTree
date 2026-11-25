package com.mamxanh.app.feature.stats.presentation.ui.stats

import com.mamxanh.app.core.presentation.UiEvent

sealed interface StatsEvent : UiEvent {
    data class Open(
        val link: StatisticsLink,
    ) : StatsEvent

    data class Feedback(
        val message: String,
    ) : StatsEvent
}
