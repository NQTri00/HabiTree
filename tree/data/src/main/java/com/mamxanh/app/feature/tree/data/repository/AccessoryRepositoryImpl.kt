package com.mamxanh.app.feature.tree.data.repository

import com.mamxanh.app.feature.tree.domain.repository.AccessoryRepository
import com.mamxanh.app.feature.tree.domain.repository.AccessorySnapshot
import com.mamxanh.app.feature.tree.domain.repository.AccessoryTree
import com.mamxanh.shared.data.GameStorage
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.model.gardenCosmetics

internal class AccessoryRepositoryImpl(
    private val storage: GameStorage,
) : AccessoryRepository {
    override fun load(): AccessorySnapshot {
        val collection = storage.livingGarden()
        val byTree = collection.placements.filter { it.sku in treeAccessories }.groupBy { it.treeId }
        return AccessorySnapshot(
            quantities = collection.owned,
            trees =
                storage.garden().trees.map {
                    AccessoryTree(
                        it.id.toString(),
                        AppText.plantName(it.speciesId, it.name),
                        byTree[it.id]?.firstOrNull()?.sku.orEmpty(),
                        it.status.alive,
                        speciesId = it.speciesId,
                    )
                },
        )
    }

    private companion object {
        val treeAccessories = gardenCosmetics.filter { it.slot == "TREE" }.map { it.id }.toSet()
    }
}
