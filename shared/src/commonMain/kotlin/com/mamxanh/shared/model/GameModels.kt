package com.mamxanh.shared.model

import com.mamxanh.shared.time.currentDateKey

data class Task(
    val id: Long, val title: String, val kind: String, val category: String,
    val reward: Int, val frequency: String, val due: String, val completed: String,
    val streak: Int, val lastDone: String,
    val config: TaskConfig = TaskConfig(title, kind, category, frequency = frequency, due = due),
    val lifecycle: String = "ACTIVE", val scheduledToday: Boolean = true,
    val available: Boolean = true, val availability: String = "",
    val history: List<TaskOccurrence> = emptyList(),
    val requiredToday: Boolean = false, val occurrenceDone: Boolean = false
) {
    val doneToday: Boolean get() = completed == currentDateKey() || occurrenceDone
    val finished: Boolean get() = kind == "TODO" && completed.isNotBlank()
}
data class Species(val id: String, val name: String, val emoji: String, val description: String, private val legacyPrice: Int) {
    val profile get() = lifecycleProfile(id)
    val price get() = profile.seedPrice
    val tier get() = profile.tier
    val formCount get() = profile.stageCount
    val unlockType get() = "COINS"
    val growthBalanceVersion get() = profile.balanceVersion
}
data class Tree(
    val id: Long, val speciesId: String, val name: String, val growth: Int,
    val planted: String, val decoration: String = "NONE", val wateredToday: Boolean = false,
    val status: TreeStatus = TreeStatus()
)
data class GardenPlot(val id: Int, val isUnlocked: Boolean, val treeId: Long? = null)
object GardenPlotPolicy {
    const val INITIAL_PLOTS = 5
}
data class Garden(
    val trees: List<Tree> = emptyList(), val inventory: Map<String, Int> = emptyMap(),
    val plots: List<GardenPlot> = emptyList(), val coins: Int = 0,
    val equippedCan: String = WateringCanTier.BASIC.itemId, val waterAll: WaterAllSummary = WaterAllSummary()
)
data class Statistics(
    val coins: Int = 0, val total: Int = 0, val today: Int = 0,
    val week: Int = 0, val streak: Int = 0, val treeCount: Int = 0,
    val recentDailyCompletions: List<Int> = emptyList()
)

enum class TreeWateringResult { WATERED, ALREADY_WATERED, DEAD, CLOCK_CHANGED, TREE_NOT_FOUND }
val catalog = listOf(
    Species("succulent", "Sen đá ngọc", "🪴", "Mọng nước, cánh lá xếp tầng xinh xắn", 150),
    Species("monstera", "Trầu bà lá xẻ", "🌿", "Lá xanh mướt mang không khí trong lành", 200),
    Species("ficus", "Cây bàng búp", "🌳", "Tán lá tròn trịa đón ánh mặt trời", 500),
    Species("snake", "Lưỡi hổ xanh", "🎋", "Vươn thẳng mạnh mẽ và kiên cường", 200),
    Species("cactus", "Xương rồng hoa", "🌵", "Nụ hoa hồng bé xíu nở trên đỉnh", 200),
    Species("aloe", "Nha đam mọng", "🌱", "Mềm mại, tươi mát suốt bốn mùa", 200),
    Species("lavender", "Oải hương tím", "🪻", "Hương thơm dịu dàng giúp thư giãn", 200),
    Species("pilea", "Cỏ đồng tiền", "🪙", "Lá tròn như đồng xu may mắn", 500),
    Species("cherry", "Đào hồng xuân", "🌸", "Cánh hoa đào rực rỡ ngày xuân", 1200),
    Species("pine", "Thông xanh biếc", "🌲", "Bền bỉ, xanh ngát quanh năm", 4500),
    Species("mandarin", "Quýt nắng", "🍊", "Trái ngọt nhỏ mang nắng ấm đến khu vườn", 500)
)
