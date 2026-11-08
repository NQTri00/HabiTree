package com.mamxanh.app.feature.tree.data

import com.mamxanh.app.data.local.model.TreeWateringResult
import com.mamxanh.app.data.repository.GameStorage
import com.mamxanh.app.feature.tree.domain.TreeRepository

class OfflineTreeRepository(private val game: GameStorage) : TreeRepository {
    override fun water(treeId: Long): TreeWateringResult = game.water(treeId)
    override fun fertilize(treeId: Long) = game.fertilize(treeId)
    override fun decorate(treeId: Long, decoration: String) = game.decorate(treeId, decoration)
}
