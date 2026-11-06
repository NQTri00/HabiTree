package com.mamxanh.app.feature.shop.data

import com.mamxanh.app.data.repository.GameStorage
import com.mamxanh.app.feature.shop.domain.ShopRepository
import com.mamxanh.app.feature.shop.domain.model.ShopWallet

class OfflineShopRepository(private val game: GameStorage) : ShopRepository {
    override fun loadWallet(): ShopWallet {
        val garden = game.garden()
        return ShopWallet(game.statistics().coins, garden.inventory)
    }
    override fun buySeed(id: String) = game.buySeed(id)
    override fun buyFertilizer() = game.buyFertilizer()
}
