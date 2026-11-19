package com.mamxanh.app.feature.tree.data

import com.mamxanh.app.core.domain.RepositoryResult
import com.mamxanh.app.feature.tree.domain.TreeCareReceipt
import com.mamxanh.app.feature.tree.domain.TreeRepository
import com.mamxanh.app.feature.tree.domain.WateringOutcome
import com.mamxanh.shared.data.GameStorage
import com.mamxanh.shared.model.ActionResult
import com.mamxanh.shared.model.TreeWateringResult

class TreeRepositoryImpl(private val storage: GameStorage) : TreeRepository {
    override fun water(treeId: Long): WateringOutcome = when (storage.water(treeId)) {
        TreeWateringResult.WATERED -> WateringOutcome.Watered
        TreeWateringResult.ALREADY_WATERED -> WateringOutcome.AlreadyWatered
        TreeWateringResult.DEAD -> WateringOutcome.Dead
        TreeWateringResult.CLOCK_CHANGED -> WateringOutcome.ClockChanged
        TreeWateringResult.TREE_NOT_FOUND -> WateringOutcome.MissingTree
    }

    override fun applyFertilizer(treeId: Long, tier: String): RepositoryResult<TreeCareReceipt> =
        storage.applyFertilizer(treeId, tier).toReceipt()

    override fun saleQuote(treeId: Long): RepositoryResult<TreeCareReceipt> =
        storage.treeSaleQuote(treeId).toReceipt()

    override fun dispose(treeId: Long, sell: Boolean, key: String): RepositoryResult<TreeCareReceipt> =
        storage.disposeTree(treeId, sell, key).toReceipt()

    override fun decorate(treeId: Long, decoration: String) = storage.decorate(treeId, decoration)

    private fun ActionResult.toReceipt(): RepositoryResult<TreeCareReceipt> =
        if (success) RepositoryResult.Success(TreeCareReceipt(message, nominal, granted)) else RepositoryResult.Error(message)
}
