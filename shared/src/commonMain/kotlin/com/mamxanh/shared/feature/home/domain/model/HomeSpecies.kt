package com.mamxanh.shared.feature.home.domain.model

data class HomeSpecies(val id: String, val name: String, val emoji: String)
val homeSpeciesCatalog = listOf(
    HomeSpecies("succulent", "Sen đá ngọc", "🪴"),
    HomeSpecies("monstera", "Trầu bà lá xẻ", "🌿"),
    HomeSpecies("ficus", "Cây bàng búp", "🌳"),
    HomeSpecies("snake", "Lưỡi hổ xanh", "🎋"),
    HomeSpecies("cactus", "Xương rồng hoa", "🌵"),
    HomeSpecies("aloe", "Nha đam mọng", "🌱"),
    HomeSpecies("lavender", "Oải hương tím", "🪻"),
    HomeSpecies("pilea", "Cỏ đồng tiền", "🪙"),
    HomeSpecies("cherry", "Đào hồng xuân", "🌸"),
    HomeSpecies("pine", "Thông xanh biếc", "🌲")
)
