package com.mamxanh.app.feature.tree.domain

interface TreeRepository {
    fun water(treeId: Long): Boolean
    fun fertilize(treeId: Long): Boolean
    fun decorate(treeId: Long, decoration: String)
}
class WaterTree(private val repository: TreeRepository) { operator fun invoke(id: Long) = repository.water(id) }
class FertilizeTree(private val repository: TreeRepository) { operator fun invoke(id: Long) = repository.fertilize(id) }
class DecorateTree(private val repository: TreeRepository) { operator fun invoke(id: Long, decoration: String) = repository.decorate(id, decoration) }
