package com.mamxanh.shared.feature.stats.domain.model

data class GardenStatistics(
    val coins: Int = 0,
    val total: Int = 0,
    val today: Int = 0,
    val week: Int = 0,
    val streak: Int = 0,
    val treeCount: Int = 0,
    val recentDailyCompletions: List<Int> = emptyList()
)
