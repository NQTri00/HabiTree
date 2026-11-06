package com.mamxanh.app.domain.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
enum class Page(val label: String) { TASKS("Nhiệm vụ"), GARDEN("Khu vườn"), SHOP("Cửa hàng"), STATS("Thống kê") }
data class Task(val id: Long, val title: String, val kind: String, val category: String, val reward: Int, val frequency: String, val due: String, val completed: String, val streak: Int, val lastDone: String) {
    val doneToday get() = completed == today()
    val finished get() = kind == "TODO" && completed.isNotBlank()
}
data class Species(val id: String, val name: String, val emoji: String, val description: String, val price: Int)
val catalog = listOf(
    Species("cherry", "Đào hồng", "🌸", "Nở hoa rực rỡ khi trưởng thành", 35),
    Species("orange", "Quýt ngọt", "🍊", "Tán lá xanh và quả vàng", 45),
    Species("pine", "Thông xanh", "🌲", "Bền bỉ, xanh quanh năm", 55),
    Species("maple", "Phong đỏ", "🍁", "Lá chuyển sắc khi cây lớn", 70),
    Species("bamboo", "Tre trúc", "🎋", "Vươn cao qua từng cột mốc", 40)
)
data class Tree(val id: Long, val speciesId: String, val name: String, val growth: Int, val planted: String, val decoration: String = "NONE", val wateredToday: Boolean = false)
data class Garden(val trees: List<Tree> = emptyList(), val inventory: Map<String, Int> = emptyMap())
data class Statistics(val coins: Int = 0, val total: Int = 0, val today: Int = 0, val week: Int = 0, val streak: Int = 0, val treeCount: Int = 0)
data class State(val tasks: List<Task> = emptyList(), val garden: Garden = Garden(), val stats: Statistics = Statistics(), val loading: Boolean = true, val message: String = "")


fun today()=SimpleDateFormat("yyyy-MM-dd",Locale.US).format(Date())
fun key(date:Date)=SimpleDateFormat("yyyy-MM-dd",Locale.US).format(date)
