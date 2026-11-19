package com.mamxanh.app.feature.shop.data

import com.mamxanh.app.core.domain.RepositoryResult
import com.mamxanh.app.feature.shop.domain.ShopRepository
import com.mamxanh.app.feature.shop.domain.model.ShopWallet
import com.mamxanh.shared.data.GameStorage
import com.mamxanh.shared.model.ActionResult

class OfflineShopRepository(private val storage: GameStorage) : ShopRepository {
    override fun equipCan(id: String): RepositoryResult<String> = storage.equipCan(id).toResult()

    override fun purchase(id: String, quantity: Int, key: String): RepositoryResult<String> =
        storage.purchase(id, quantity, key).toResult()

    override fun dailyEarnings(): List<Int> = storage.phaseSnapshot().days.map { it.earned }

    override fun loadWallet(): ShopWallet {
        val garden = storage.garden()
        return ShopWallet(storage.statistics().coins, garden.inventory, garden.equippedCan)
    }

    private fun ActionResult.toResult(): RepositoryResult<String> =
        if (success) RepositoryResult.Success(message) else RepositoryResult.Error(message)
}
