package com.mamxanh.shared.feature.shop.domain

import com.mamxanh.shared.feature.shop.domain.model.ShopWallet

interface ShopRepository {
    fun purchase(id: String,quantity: Int,key: String): com.mamxanh.shared.model.ActionResult
    fun averageEarnings(): Int
    fun loadWallet(): ShopWallet
    fun buySeed(id: String): Boolean
    fun buyFertilizer(): Boolean
}
class LoadShopWallet(private val repository: ShopRepository) { operator fun invoke() = repository.loadWallet() }
class BuySeed(private val repository: ShopRepository) { operator fun invoke(id: String) = repository.buySeed(id) }
class BuyFertilizer(private val repository: ShopRepository) { operator fun invoke() = repository.buyFertilizer() }
