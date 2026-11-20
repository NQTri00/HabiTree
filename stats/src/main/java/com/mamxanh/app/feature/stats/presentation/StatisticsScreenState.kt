package com.mamxanh.app.feature.stats.presentation

data class StatisticsScreenState(
    val total: Int,
    val treeCount: Int,
    val week: Int,
    val today: Int,
    val streak: Int,
    val dailyCompletions: List<Int>,
    val dayLabels: List<String>,
)
