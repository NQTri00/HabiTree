package com.mamxanh.app.feature.home.domain.model

data class HomeTree(
    val id: Long,
    val speciesId: String,
    val name: String,
    val growth: Int,
    val decoration: String,
    val wateredToday: Boolean,
    val status: HomeTreeCare = HomeTreeCare(),
)

data class HomeTreeCare(
    val alive: Boolean = true,
    val waterActive: Boolean = false,
    val canWater: Boolean = true,
    val canFertilize: Boolean = false,
    val percentage: Int = 0,
    val stageProgress: Float = 0f,
    val stageIndex: Int = 0,
    val stageCount: Int = 6,
    val stageLabel: String = "",
    val stateLabel: String = "",
    val waterLabel: String = "",
    val fertilizerLabel: String = "",
    val mature: Boolean = false,
)

data class HomeTaskSummary(
    val id: Long,
    val title: String,
    val category: String,
    val reward: Int,
    val timer: Boolean = false,
    val canComplete: Boolean = true,
    val minutes: Int = 0,
)

data class HomeDashboard(
    val trees: List<HomeTree> = emptyList(),
    val completedTasks: Int = 0,
    val totalTasks: Int = 0,
    val seedCount: Int = 0,
    val coins: Int = 0,
    val equippedCan: String = "can_basic",
    val dailyRewardDate: String = "",
    val dailyRewardCoins: Int = 8,
    val earnedToday: Int = 0,
    val streak: Int = 0,
    val nextTasks: List<HomeTaskSummary> = emptyList(),
    val nextGoal: String = "",
    val canClaimReward: Boolean = false,
)
