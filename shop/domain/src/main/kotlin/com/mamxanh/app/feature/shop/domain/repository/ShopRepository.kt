package com.mamxanh.app.feature.shop.domain.repository

import com.mamxanh.app.core.domain.RepositoryResult
import com.mamxanh.app.feature.shop.domain.model.ShopWallet

data class PlantCheckoutReceipt(
    val message: String,
    val treeId: Long? = null,
)

interface ShopRepository {
    fun equipCan(id: String): RepositoryResult<String>

    fun purchase(
        id: String,
        quantity: Int,
        key: String,
    ): RepositoryResult<String>

    fun purchaseAndEquip(
        id: String,
        quantity: Int,
        key: String,
    ): RepositoryResult<String>

    fun purchaseAndPlant(
        id: String,
        quantity: Int,
        preferredPlot: Int?,
        key: String,
    ): RepositoryResult<PlantCheckoutReceipt>

    fun loadWallet(): ShopWallet

    fun dailyEarnings(): List<Int>
}

fun averageShopEarnings(earnings: List<Int>): Int = if (earnings.size < 7) 40 else (earnings.sum() / 7).coerceAtLeast(1)
