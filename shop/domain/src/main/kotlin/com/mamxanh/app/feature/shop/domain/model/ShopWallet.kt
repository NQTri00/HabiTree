package com.mamxanh.app.feature.shop.domain.model

data class ShopWallet(
    val coins: Int,
    val inventory: Map<String, Int>,
    val equippedCan: String = "can_basic",
)
