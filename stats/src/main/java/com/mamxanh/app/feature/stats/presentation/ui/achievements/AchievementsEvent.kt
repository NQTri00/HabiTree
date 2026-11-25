package com.mamxanh.app.feature.stats.presentation.ui.achievements

import com.mamxanh.app.core.presentation.UiEvent

sealed interface AchievementsEvent : UiEvent {
    data class Feedback(
        val message: String,
    ) : AchievementsEvent
}
