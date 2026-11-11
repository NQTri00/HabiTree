package com.mamxanh.shared.feature.tree.data

import com.mamxanh.shared.model.TreeWateringResult
import com.mamxanh.shared.data.GameStorage
import com.mamxanh.shared.feature.tree.domain.TreeRepository

class OfflineTreeRepository(private val game: GameStorage) : TreeRepository {
    override fun applyFertilizer(id: Long,tier: String) = game.applyFertilizer(id,tier)
    override fun saleQuote(id: Long) = game.treeSaleQuote(id)
    override fun dispose(id: Long,sell: Boolean,key: String) = game.disposeTree(id,sell,key)
    override fun water(treeId: Long): TreeWateringResult = game.water(treeId)
    override fun fertilize(treeId: Long) = game.fertilize(treeId)
    override fun decorate(treeId: Long, decoration: String) = game.decorate(treeId, decoration)
}
