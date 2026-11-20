package com.mamxanh.app.feature.stats.presentation.ui.stats

data class StatisticsScreenState(
    val total: Int,
    val treeCount: Int,
    val week: Int,
    val today: Int,
    val streak: Int,
    val dailyCompletions: List<Int>,
    val dayLabels: List<String>,
)
