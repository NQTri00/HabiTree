package com.mamxanh.shared.feature.shop.domain.model

data class ShopWallet(val coins: Int, val inventory: Map<String, Int>,val equippedCan: String = "can_basic")
data class ShopSeed(val id: String, val name: String, val emoji: String, val description: String, val price: Int)
