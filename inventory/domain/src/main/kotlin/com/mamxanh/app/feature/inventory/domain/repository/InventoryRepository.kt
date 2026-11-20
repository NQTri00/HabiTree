package com.mamxanh.app.feature.inventory.domain.repository

data class InventoryActionResult(val success: Boolean, val message: String)

interface InventoryRepository {
    fun quantities(): Map<String, Int>
    fun equipWateringCan(id: String): InventoryActionResult
    fun plantSeed(id: String, name: String, plotId: Int?): Boolean
}
