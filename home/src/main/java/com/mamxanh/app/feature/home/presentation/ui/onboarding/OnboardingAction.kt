package com.mamxanh.app.feature.home.presentation.ui.onboarding

sealed interface OnboardingAction {
    data object Back : OnboardingAction
    data object Next : OnboardingAction
    data object Skip : OnboardingAction
    data class Choose(val step: Int, val id: String) : OnboardingAction
    data class Remind(val enabled: Boolean) : OnboardingAction
}
