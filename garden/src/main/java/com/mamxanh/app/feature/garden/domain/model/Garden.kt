package com.mamxanh.app.feature.garden.domain.model

data class Species(val id: String, val name: String, val emoji: String, val description: String, val price: Int)
data class Tree(val id: Long, val speciesId: String, val name: String, val growth: Int, val planted: String, val decoration: String = "NONE", val wateredToday: Boolean = false)
data class Garden(val trees: List<Tree> = emptyList(), val inventory: Map<String, Int> = emptyMap())
val seedCatalog = listOf(
    Species("cherry", "Đào hồng", "🌸", "Nở hoa rực rỡ khi trưởng thành", 35),
    Species("orange", "Quýt ngọt", "🍊", "Tán lá xanh và quả vàng", 45),
    Species("pine", "Thông xanh", "🌲", "Bền bỉ, xanh quanh năm", 55),
    Species("maple", "Phong đỏ", "🍁", "Lá chuyển sắc khi cây lớn", 70),
    Species("bamboo", "Tre trúc", "🎋", "Vươn cao qua từng cột mốc", 40)
)
