package com.mamxanh.app.feature.home.presentation

data class OnboardingDisplay(
    val step: Int,
    val seed: String,
    val category: String,
    val selectedCount: Int,
    val selectedTitles: List<String>,
    val seedNames: Map<String, String>,
    val templateCount: Int,
    val remind: Boolean,
)
