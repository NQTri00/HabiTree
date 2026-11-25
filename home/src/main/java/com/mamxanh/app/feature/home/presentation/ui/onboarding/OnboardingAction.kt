package com.mamxanh.app.feature.home.presentation.ui.onboarding

import com.mamxanh.app.core.presentation.UiAction

sealed interface OnboardingAction : UiAction {
    data class ToggleTemplate(
        val id: String,
    ) : OnboardingAction

    data object Back : OnboardingAction

    data object Next : OnboardingAction

    data object ContinueCompleted : OnboardingAction

    data object Skip : OnboardingAction

    data class Choose(
        val step: Int,
        val id: String,
    ) : OnboardingAction

    data class Remind(
        val enabled: Boolean,
    ) : OnboardingAction
}
