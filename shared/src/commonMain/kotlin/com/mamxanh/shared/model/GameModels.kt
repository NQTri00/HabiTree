package com.mamxanh.shared.model

import com.mamxanh.shared.time.currentDateKey

data class Task(
    val id: Long, val title: String, val kind: String, val category: String,
    val reward: Int, val frequency: String, val due: String, val completed: String,
    val streak: Int, val lastDone: String
) {
    val doneToday: Boolean get() = completed == currentDateKey()
}
data class Species(val id: String, val name: String, val emoji: String, val description: String, val price: Int)
data class Tree(
    val id: Long, val speciesId: String, val name: String, val growth: Int,
    val planted: String, val decoration: String = "NONE", val wateredToday: Boolean = false
)
data class Garden(val trees: List<Tree> = emptyList(), val inventory: Map<String, Int> = emptyMap())
data class Statistics(
    val coins: Int = 0, val total: Int = 0, val today: Int = 0,
    val week: Int = 0, val streak: Int = 0, val treeCount: Int = 0,
    val recentDailyCompletions: List<Int> = emptyList()
)

enum class TreeWateringResult { WATERED, ALREADY_WATERED, NOT_ENOUGH_WATER, TREE_NOT_FOUND }
val catalog = listOf(
    Species("succulent", "Sen đá ngọc", "🪴", "Mọng nước, cánh lá xếp tầng xinh xắn", 25),
    Species("monstera", "Trầu bà lá xẻ", "🌿", "Lá xanh mướt mang không khí trong lành", 25),
    Species("ficus", "Cây bàng búp", "🌳", "Tán lá tròn trịa đón ánh mặt trời", 25),
    Species("snake", "Lưỡi hổ xanh", "🎋", "Vươn thẳng mạnh mẽ và kiên cường", 25),
    Species("cactus", "Xương rồng hoa", "🌵", "Nụ hoa hồng bé xíu nở trên đỉnh", 25),
    Species("aloe", "Nha đam mọng", "🌱", "Mềm mại, tươi mát suốt bốn mùa", 25),
    Species("lavender", "Oải hương tím", "🪻", "Hương thơm dịu dàng giúp thư giãn", 25),
    Species("pilea", "Cỏ đồng tiền", "🪙", "Lá tròn như đồng xu may mắn", 25),
    Species("cherry", "Đào hồng xuân", "🌸", "Cánh hoa đào rực rỡ ngày xuân", 25),
    Species("pine", "Thông xanh biếc", "🌲", "Bền bỉ, xanh ngát quanh năm", 25),
    Species("mandarin", "Quýt nắng", "🍊", "Trái ngọt nhỏ mang nắng ấm đến khu vườn", 60)
)
