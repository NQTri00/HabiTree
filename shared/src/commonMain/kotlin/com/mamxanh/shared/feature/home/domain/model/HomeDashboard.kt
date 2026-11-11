package com.mamxanh.shared.feature.home.domain.model

data class HomeTree(
    val id: Long, val speciesId: String, val name: String, val growth: Int,
    val decoration: String, val wateredToday: Boolean,
    val status: com.mamxanh.shared.model.TreeStatus = com.mamxanh.shared.model.TreeStatus()
)
data class HomeDashboard(
    val trees: List<HomeTree> = emptyList(),
    val completedTasks: Int = 0,
    val totalTasks: Int = 0,
    val seedCount: Int = 0,
    val coins: Int = 0,
    val equippedCan: String = "can_basic",
    val dailyRewardDate: String = "",
    val dailyRewardCoins: Int = com.mamxanh.shared.model.Balance.DAILY_CLEAR,
    val phase: com.mamxanh.shared.model.PhaseSnapshot = com.mamxanh.shared.model.PhaseSnapshot(),
    val nextTasks: List<com.mamxanh.shared.model.Task> = emptyList(),
    val nextGoal: String = ""
)
