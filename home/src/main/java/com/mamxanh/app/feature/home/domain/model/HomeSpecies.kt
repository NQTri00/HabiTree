package com.mamxanh.app.feature.home.domain.model

data class HomeSpecies(val id: String, val name: String, val emoji: String)
val homeSpeciesCatalog = listOf(
    HomeSpecies("cherry", "Đào hồng", "🌸"),
    HomeSpecies("orange", "Quýt ngọt", "🍊"),
    HomeSpecies("pine", "Thông xanh", "🌲"),
    HomeSpecies("maple", "Phong đỏ", "🍁"),
    HomeSpecies("bamboo", "Tre trúc", "🎋")
)
