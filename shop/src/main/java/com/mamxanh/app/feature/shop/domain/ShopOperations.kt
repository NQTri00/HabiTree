package com.mamxanh.app.feature.shop.domain

import com.mamxanh.app.feature.shop.domain.model.ShopWallet

interface ShopRepository {
    fun loadWallet(): ShopWallet
    fun buySeed(id: String): Boolean
    fun buyFertilizer(): Boolean
}
class LoadShopWallet(private val repository: ShopRepository) { operator fun invoke() = repository.loadWallet() }
class BuySeed(private val repository: ShopRepository) { operator fun invoke(id: String) = repository.buySeed(id) }
class BuyFertilizer(private val repository: ShopRepository) { operator fun invoke() = repository.buyFertilizer() }
