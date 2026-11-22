package com.mamxanh.app.feature.shop.presentation.ui.shop

data class PurchaseOutcome(
    val success: Boolean,
    val name: String,
    val beforeCoins: Int,
    val afterCoins: Int,
    val beforeOwned: Int,
    val afterOwned: Int,
    val message: String = "",
)
