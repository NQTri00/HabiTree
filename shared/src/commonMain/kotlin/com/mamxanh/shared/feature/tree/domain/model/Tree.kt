package com.mamxanh.shared.feature.tree.domain.model

data class Tree(
    val id: Long, val speciesId: String, val name: String, val growth: Int,
    val planted: String, val decoration: String, val wateredToday: Boolean
)
data class TreeSpecies(val id: String, val name: String)
val treeSpeciesCatalog = listOf(
    TreeSpecies("succulent", "Sen đá ngọc"),
    TreeSpecies("monstera", "Trầu bà lá xẻ"),
    TreeSpecies("ficus", "Cây bàng búp"),
    TreeSpecies("snake", "Lưỡi hổ xanh"),
    TreeSpecies("cactus", "Xương rồng hoa"),
    TreeSpecies("aloe", "Nha đam mọng"),
    TreeSpecies("lavender", "Oải hương tím"),
    TreeSpecies("pilea", "Cỏ đồng tiền"),
    TreeSpecies("cherry", "Đào hồng xuân"),
    TreeSpecies("pine", "Thông xanh biếc")
)
