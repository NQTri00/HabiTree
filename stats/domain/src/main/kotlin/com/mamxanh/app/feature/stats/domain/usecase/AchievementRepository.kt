package com.mamxanh.app.feature.stats.domain.usecase

data class AchievementProgress(
    val title: String,
    val criterion: String,
    val progress: Int,
    val target: Int,
    val unlocked: Boolean,
)

fun interface AchievementRepository {
    fun load(): List<AchievementProgress>
}
