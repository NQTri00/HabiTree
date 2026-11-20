package com.mamxanh.app.feature.stats.presentation.ui.achievements

data class AchievementDisplay(
    val title: String,
    val criterion: String,
    val progress: Int,
    val target: Int,
    val unlocked: Boolean = false,
)
