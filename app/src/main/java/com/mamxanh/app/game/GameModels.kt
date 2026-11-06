package com.mamxanh.app.game

enum class TaskKind { HABIT, DAILY, TODO }
enum class TaskCategory { STUDY, HEALTH, LIFE, WORK, PERSONAL }
enum class Difficulty(val xp: Int, val coins: Int, val damage: Int) {
    EASY(5, 3, 1), MEDIUM(10, 5, 2), HARD(20, 10, 4)
}

data class GameTask(
    val id: String,
    val title: String,
    val notes: String = "",
    val kind: TaskKind,
    val category: TaskCategory,
    val difficulty: Difficulty,
    val positive: Boolean = true,
    val completed: Boolean = false,
    val lastCompletedDay: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

data class GardenSeed(val id: String, val name: String, val emoji: String, val price: Int, val description: String)
data class GardenTree(val seed: GardenSeed, val growth: Int, val plantedAt: Long, val waterCount: Int = 0) {
    val stage: Int get() = when { growth >= 120 -> 3; growth >= 50 -> 2; growth >= 10 -> 1; else -> 0 }
    val stageName: String get() = when(stage) { 0 -> "Hạt giống"; 1 -> "Nảy mầm"; 2 -> "Cây non"; else -> "Trưởng thành" }
}

data class Player(
    val name: String = "Người làm vườn",
    val level: Int = 1,
    val xp: Int = 0,
    val coins: Int = 30,
    val health: Int = 50,
    val mana: Int = 0,
    val streak: Int = 0,
    val bestStreak: Int = 0,
    val lastActiveDay: String = "",
    val totalCompleted: Int = 0,
    val totalXp: Int = 0
) {
    val xpToNext: Int get() = level * 100
    val xpProgress: Float get() = (xp.toFloat() / xpToNext).coerceIn(0f, 1f)
}

data class GameState(
    val player: Player = Player(),
    val tasks: List<GameTask> = emptyList(),
    val tree: GardenTree? = null,
    val ownedSeeds: Set<String> = setOf("sprout"),
    val purchasedRewards: List<String> = emptyList(),
    val history: List<String> = emptyList(),
    val lastDailyReset: String = ""
)

object GameCatalog {
    val seeds = listOf(
        GardenSeed("sprout", "Mầm xanh", "🌱", 0, "Một khởi đầu nhỏ cho khu vườn"),
        GardenSeed("cherry", "Anh đào", "🌸", 35, "Cây hoa dịu dàng, nở rộ khi trưởng thành"),
        GardenSeed("citrus", "Cam ngọt", "🍊", 45, "Cây ăn quả mang sắc màu ấm áp"),
        GardenSeed("pine", "Thông bốn mùa", "🌲", 60, "Cây xanh bền bỉ quanh năm"),
        GardenSeed("sunflower", "Hướng dương", "🌻", 50, "Luôn hướng về phía ánh sáng")
    )
    val rewards = listOf(
        Triple("tea", "Một tách trà yêu thích", 15),
        Triple("music", "30 phút nghe nhạc", 20),
        Triple("game", "30 phút chơi game", 35),
        Triple("movie", "Một buổi xem phim", 60),
        Triple("outing", "Một chuyến đi chơi nhỏ", 100)
    )
    val starterTasks = listOf(
        GameTask("habit_read", "Đọc sách", "Đọc vài trang cũng được tính.", TaskKind.HABIT, TaskCategory.STUDY, Difficulty.EASY),
        GameTask("habit_move", "Vận động cơ thể", "Đi bộ, giãn cơ hoặc tập luyện.", TaskKind.HABIT, TaskCategory.HEALTH, Difficulty.EASY),
        GameTask("daily_focus", "Tập trung học/làm việc 25 phút", kind = TaskKind.DAILY, category = TaskCategory.WORK, difficulty = Difficulty.MEDIUM),
        GameTask("daily_water", "Uống đủ nước", kind = TaskKind.DAILY, category = TaskCategory.HEALTH, difficulty = Difficulty.EASY),
        GameTask("daily_tidy", "Dọn dẹp không gian", kind = TaskKind.DAILY, category = TaskCategory.LIFE, difficulty = Difficulty.EASY),
        GameTask("todo_plan", "Lập kế hoạch cho tuần", kind = TaskKind.TODO, category = TaskCategory.PERSONAL, difficulty = Difficulty.MEDIUM)
    )
}
