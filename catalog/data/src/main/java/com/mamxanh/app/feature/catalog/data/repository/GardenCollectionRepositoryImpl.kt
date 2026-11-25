package com.mamxanh.app.feature.catalog.data.repository

import com.mamxanh.app.feature.catalog.domain.repository.CollectedSpecies
import com.mamxanh.app.feature.catalog.domain.repository.CollectedTree
import com.mamxanh.app.feature.catalog.domain.repository.CollectionMilestone
import com.mamxanh.app.feature.catalog.domain.repository.GardenCollection
import com.mamxanh.app.feature.catalog.domain.repository.GardenCollectionRepository
import com.mamxanh.shared.data.GameStorage
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.model.TreeGrowthState
import com.mamxanh.shared.model.gardenCosmetics
import org.koin.dsl.module

class GardenCollectionRepositoryImpl(
    private val storage: GameStorage,
) : GardenCollectionRepository {
    override fun loadTrees(speciesId: String): List<CollectedTree> =
        storage.garden().trees.filter { it.speciesId == speciesId }.map { tree ->
            CollectedTree(
                id = tree.id,
                name = AppText.plantName(tree.speciesId, tree.name),
                living = tree.status.state != TreeGrowthState.DEAD,
                stage = tree.status.stageIndex + 1,
            )
        }

    override fun load(): GardenCollection {
        val snapshot = storage.livingGarden()
        val treeAccessories = gardenCosmetics.filter { it.slot == "TREE" }.map { it.id }.toSet()
        val decorations =
            buildMap {
                for (placement in snapshot.placements) {
                    val treeId = placement.treeId
                    if (placement.sku in treeAccessories && treeId !in this) put(treeId, placement.sku)
                }
            }
        return GardenCollection(
            species =
                snapshot.species.map {
                    CollectedSpecies(
                        it.id,
                        it.ownedSeeds,
                        it.planted,
                        it.discoveredAt > 0,
                        (it.highestStage + 1).coerceAtLeast(0),
                        it.firstPlanted,
                        it.firstMatured,
                        it.flourished,
                        it.status,
                    )
                },
            milestones =
                snapshot.goals.map {
                    CollectionMilestone(it.id, it.title, it.progress, it.target, it.reward, it.unlockedAt > 0)
                },
            decorationsByTree = decorations,
            ownedAccessories = snapshot.owned,
        )
    }
}

val catalogDataModule =
    module {
        single<GardenCollectionRepository> { GardenCollectionRepositoryImpl(get()) }
    }
