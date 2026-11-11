package com.mamxanh.shared.feature.shop.data

import com.mamxanh.shared.data.GameStorage
import com.mamxanh.shared.feature.shop.domain.ShopRepository
import com.mamxanh.shared.feature.shop.domain.model.ShopWallet

class OfflineShopRepository(private val game: GameStorage) : ShopRepository {
    override fun purchase(id: String,quantity: Int,key: String) = game.purchase(id,quantity,key)
    override fun averageEarnings(): Int {
        val days=game.phaseSnapshot().days
        return if(days.size<7)40 else (days.sumOf{it.earned}/7).coerceAtLeast(1)
    }
    override fun loadWallet(): ShopWallet {
        val garden = game.garden()
        return ShopWallet(game.statistics().coins, garden.inventory)
    }
    override fun buySeed(id: String) = game.buySeed(id)
    override fun buyFertilizer() = game.buyFertilizer()
}
