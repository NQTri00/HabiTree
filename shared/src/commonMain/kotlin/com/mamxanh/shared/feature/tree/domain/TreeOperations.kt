package com.mamxanh.shared.feature.tree.domain

import com.mamxanh.shared.model.TreeWateringResult

interface TreeRepository {
    fun applyFertilizer(id: Long,tier: String): com.mamxanh.shared.model.ActionResult
    fun saleQuote(id: Long): com.mamxanh.shared.model.ActionResult
    fun dispose(id: Long,sell: Boolean,key: String): com.mamxanh.shared.model.ActionResult
    fun water(treeId: Long): TreeWateringResult
    fun fertilize(treeId: Long): Boolean
    fun decorate(treeId: Long, decoration: String)
}
class WaterTree(private val repository: TreeRepository) {
    operator fun invoke(id: Long) = repository.water(id)
}
class FertilizeTree(private val repository: TreeRepository) {
    operator fun invoke(id: Long) = repository.fertilize(id)
}
class DecorateTree(private val repository: TreeRepository) {
    operator fun invoke(id: Long, decoration: String) = repository.decorate(id, decoration)
}
