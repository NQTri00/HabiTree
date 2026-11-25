package com.mamxanh.app.feature.home.presentation.ui.onboarding

import com.mamxanh.app.core.presentation.UiEvent

sealed interface OnboardingEvent : UiEvent {
    data class Finished(
        val message: String,
    ) : OnboardingEvent

    data class Feedback(
        val message: String,
    ) : OnboardingEvent
}
