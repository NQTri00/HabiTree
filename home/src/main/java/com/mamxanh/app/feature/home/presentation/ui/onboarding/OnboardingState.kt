package com.mamxanh.app.feature.home.presentation.ui.onboarding

import com.mamxanh.app.core.presentation.UiState

data class OnboardingState(
    val step: Int = 0,
    val seed: String = OnboardingSelectionState.DEFAULT_SEED,
    val category: String = OnboardingSelectionState.defaultCategory(),
    val templateIds: List<String> = listOf("study25", "water"),
    val reminders: Boolean = false,
    val submitting: Boolean = false,
    val completed: Boolean = false,
    val recoveringSubmission: Boolean = false,
) : UiState
