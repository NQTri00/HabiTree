package com.mamxanh.app.feature.home.domain.model

data class HomeTree(
    val id: Long, val speciesId: String, val name: String, val growth: Int,
    val decoration: String
)
data class HomeDashboard(
    val trees: List<HomeTree> = emptyList(),
    val completedTasks: Int = 0,
    val totalTasks: Int = 0,
    val seedCount: Int = 0,
    val coins: Int = 0
)
