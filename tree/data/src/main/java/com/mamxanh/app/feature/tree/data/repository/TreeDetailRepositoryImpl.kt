package com.mamxanh.app.feature.tree.data.repository

import com.mamxanh.app.feature.tree.domain.repository.TreeDetailRecord
import com.mamxanh.app.feature.tree.domain.repository.TreeDetailRepository
import com.mamxanh.shared.data.GameStorage
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.model.gardenCosmetics

internal class TreeDetailRepositoryImpl(
    private val storage: GameStorage,
) : TreeDetailRepository {
    override fun load(id: Long): TreeDetailRecord? {
        val garden = storage.garden()
        val tree = garden.trees.firstOrNull { it.id == id } ?: return null
        val collection = storage.livingGarden()
        val decoration =
            collection.placements
                .firstOrNull {
                    it.treeId == id && it.sku in treeAccessories
                }?.sku
                .orEmpty()
        return TreeDetailRecord(
            id = id,
            speciesId = tree.speciesId,
            name = AppText.plantName(tree.speciesId, tree.name),
            growth =
                tree.status.state.name
                    .lowercase(),
            stage = tree.status.stageIndex + 1,
            progress = tree.status.percentage,
            watered = tree.status.waterActive,
            canFertilize = tree.status.canFertilize,
            planted = tree.planted,
            decoration = decoration,
            accessoryOwned = (collection.owned[decoration] ?: 0) > 0,
            quantities = garden.inventory,
            treeIds = garden.trees.map { it.id },
        )
    }

    private companion object {
        val treeAccessories = gardenCosmetics.filter { it.slot == "TREE" }.map { it.id }.toSet()
    }
}
