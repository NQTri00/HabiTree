package com.mamxanh.app.feature.shop.domain.model

data class ShopWallet(val coins: Int, val inventory: Map<String, Int>)
data class ShopSeed(val id: String, val name: String, val emoji: String, val description: String, val price: Int)
