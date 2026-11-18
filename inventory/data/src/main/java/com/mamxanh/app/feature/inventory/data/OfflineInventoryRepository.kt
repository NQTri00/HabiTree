package com.mamxanh.app.feature.inventory.data

import com.mamxanh.app.feature.inventory.domain.InventoryActionResult
import com.mamxanh.app.feature.inventory.domain.InventoryRepository
import com.mamxanh.shared.data.GameStorage

class OfflineInventoryRepository(private val storage: GameStorage) : InventoryRepository {
    override fun equipWateringCan(id: String): InventoryActionResult {
        val result = storage.equipCan(id)
        return InventoryActionResult(result.success, result.message)
    }

    override fun plantSeed(id: String, name: String, plotId: Int?): Boolean =
        storage.plant(id, name, plotId)
}
