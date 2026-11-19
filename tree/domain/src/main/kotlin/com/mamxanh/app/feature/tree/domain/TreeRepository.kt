package com.mamxanh.app.feature.tree.domain

import com.mamxanh.app.core.domain.RepositoryResult

enum class WateringOutcome { Watered, AlreadyWatered, Dead, ClockChanged, MissingTree }

data class TreeCareReceipt(val message: String, val nominal: Int = 0, val granted: Int = 0)

interface TreeRepository {
    fun water(treeId: Long): WateringOutcome
    fun applyFertilizer(treeId: Long, tier: String): RepositoryResult<TreeCareReceipt>
    fun saleQuote(treeId: Long): RepositoryResult<TreeCareReceipt>
    fun dispose(treeId: Long, sell: Boolean, key: String): RepositoryResult<TreeCareReceipt>
    fun decorate(treeId: Long, decoration: String)
    fun buyAccessory(sku: String, purchaseKey: String): RepositoryResult<String>
    fun equipAccessory(sku: String, treeId: Long?, purchaseKey: String): RepositoryResult<String>
    fun unequipAccessory(treeId: Long): RepositoryResult<String>
}
