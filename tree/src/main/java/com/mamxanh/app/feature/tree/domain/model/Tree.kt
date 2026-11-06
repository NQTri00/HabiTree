package com.mamxanh.app.feature.tree.domain.model

data class Tree(
    val id: Long, val speciesId: String, val name: String, val growth: Int,
    val planted: String, val decoration: String, val wateredToday: Boolean
)
data class TreeSpecies(val id: String, val name: String)
val treeSpeciesCatalog = listOf(
    TreeSpecies("cherry", "Đào hồng"),
    TreeSpecies("orange", "Quýt ngọt"),
    TreeSpecies("pine", "Thông xanh"),
    TreeSpecies("maple", "Phong đỏ"),
    TreeSpecies("bamboo", "Tre trúc")
)
