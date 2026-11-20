package com.mamxanh.app.feature.inventory.data.repository

import com.mamxanh.app.feature.inventory.domain.repository.InventoryActionResult
import com.mamxanh.app.feature.inventory.domain.repository.InventoryRepository
import com.mamxanh.shared.data.GameStorage

class InventoryRepositoryImpl(private val storage: GameStorage) : InventoryRepository {
    override fun quantities(): Map<String, Int> = storage.inventoryQuantities()

    override fun equipWateringCan(id: String): InventoryActionResult {
        val result = storage.equipCan(id)
        return InventoryActionResult(result.success, result.message)
    }

    override fun plantSeed(id: String, name: String, plotId: Int?): Boolean =
        storage.plant(id, name, plotId)
}
