package com.mamxanh.app.feature.garden.data.di

import com.mamxanh.app.feature.garden.domain.usecase.GardenActionResult
import com.mamxanh.app.feature.garden.domain.usecase.GardenRepository
import com.mamxanh.app.feature.garden.domain.model.Garden
import com.mamxanh.app.feature.garden.domain.model.GardenPlot
import com.mamxanh.app.feature.garden.domain.model.GardenTreeCare
import com.mamxanh.app.feature.garden.domain.model.GardenTreeGrowth
import com.mamxanh.app.feature.garden.domain.model.GardenWatering
import com.mamxanh.app.feature.garden.domain.model.Tree
import com.mamxanh.shared.feature.garden.domain.GardenRepository as SharedGardenRepository
import com.mamxanh.shared.model.TreeGrowthState
import org.koin.dsl.module

private class AndroidGardenRepository(private val source: SharedGardenRepository) : GardenRepository {
    override fun loadGarden(): Garden {
        val snapshot = source.loadGarden()
        return Garden(
            trees = snapshot.trees.map { tree ->
                val care = tree.status
                Tree(
                    tree.id, tree.speciesId, tree.name, tree.growth, tree.planted, tree.decoration, tree.wateredToday,
                    GardenTreeCare(
                        state = when (care.state) {
                            TreeGrowthState.GROWING -> GardenTreeGrowth.Growing
                            TreeGrowthState.MATURE -> GardenTreeGrowth.Mature
                            TreeGrowthState.DEAD -> GardenTreeGrowth.Dead
                        },
                        stageIndex = care.stageIndex,
                        stageProgress = care.stageProgress,
                        percentage = care.percentage,
                        waterActive = care.waterActive,
                        canWater = care.canWater,
                        canFertilize = care.canFertilize,
                        fertilizerId = care.fertilizerId,
                        stateLabel = care.stateLabel,
                        waterLabel = care.waterLabel,
                        fertilizerLabel = care.fertilizerLabel,
                    ),
                )
            },
            inventory = snapshot.inventory,
            plots = snapshot.plots.map { GardenPlot(it.id, it.isUnlocked, it.treeId) },
            coins = snapshot.coins,
            equippedCan = snapshot.equippedCan,
            waterAll = snapshot.waterAll.let { GardenWatering(it.eligible, it.active, it.dead) },
        )
    }

    override fun waterAll(key: String): GardenActionResult =
        source.waterAll(key).let { GardenActionResult(it.success, it.message) }

    override fun plant(speciesId: String, name: String, plotId: Int?): Boolean =
        source.plant(speciesId, name, plotId)

    override fun unlockPlot(plotId: Int): Boolean = source.unlockPlot(plotId)
}

val gardenDataModule = module {
    single<GardenRepository> { AndroidGardenRepository(get<SharedGardenRepository>()) }
}
