package com.mamxanh.app.feature.shop.domain

import com.mamxanh.app.core.domain.RepositoryResult
import com.mamxanh.app.feature.shop.domain.model.ShopWallet

interface ShopRepository {
    fun equipCan(id: String): RepositoryResult<String>
    fun purchase(id: String, quantity: Int, key: String): RepositoryResult<String>
    fun loadWallet(): ShopWallet
    fun dailyEarnings(): List<Int>
}

fun averageShopEarnings(earnings: List<Int>): Int =
    if (earnings.size < 7) 40 else (earnings.sum() / 7).coerceAtLeast(1)
