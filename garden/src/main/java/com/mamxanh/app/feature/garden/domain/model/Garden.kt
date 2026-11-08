package com.mamxanh.app.feature.garden.domain.model

data class Species(val id: String, val name: String, val emoji: String, val description: String, val price: Int)
data class Tree(val id: Long, val speciesId: String, val name: String, val growth: Int, val planted: String, val decoration: String = "NONE", val wateredToday: Boolean = false)
data class Garden(val trees: List<Tree> = emptyList(), val inventory: Map<String, Int> = emptyMap())
val seedCatalog = listOf(
    Species("succulent", "Sen đá ngọc", "🪴", "Mọng nước, cánh lá xếp tầng xinh xắn", 25),
    Species("monstera", "Trầu bà lá xẻ", "🌿", "Lá xanh mướt mang không khí trong lành", 25),
    Species("ficus", "Cây bàng búp", "🌳", "Tán lá tròn trịa đón ánh mặt trời", 25),
    Species("snake", "Lưỡi hổ xanh", "🎋", "Vươn thẳng mạnh mẽ và kiên cường", 25),
    Species("cactus", "Xương rồng hoa", "🌵", "Nụ hoa hồng bé xíu nở trên đỉnh", 25),
    Species("aloe", "Nha đam mọng", "🌱", "Mềm mại, tươi mát suốt bốn mùa", 25),
    Species("lavender", "Oải hương tím", "🪻", "Hương thơm dịu dàng giúp thư giãn", 25),
    Species("pilea", "Cỏ đồng tiền", "🪙", "Lá tròn như đồng xu may mắn", 25),
    Species("cherry", "Đào hồng xuân", "🌸", "Cánh hoa đào rực rỡ ngày xuân", 25),
    Species("pine", "Thông xanh biếc", "🌲", "Bền bỉ, xanh ngát quanh năm", 25)
)
