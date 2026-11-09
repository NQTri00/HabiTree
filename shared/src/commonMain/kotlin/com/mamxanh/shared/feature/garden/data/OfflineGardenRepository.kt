package com.mamxanh.shared.feature.garden.data

import com.mamxanh.shared.data.GameStorage
import com.mamxanh.shared.feature.garden.domain.GardenRepository
import com.mamxanh.shared.feature.garden.domain.model.Garden
import com.mamxanh.shared.feature.garden.domain.model.Tree

class OfflineGardenRepository(private val game: GameStorage) : GardenRepository {
    override fun loadGarden(): Garden {
        val source = game.garden()
        return Garden(source.trees.map {
            Tree(it.id, it.speciesId, it.name, it.growth, it.planted, it.decoration, it.wateredToday)
        }, source.inventory, source.plots, source.coins)
    }
    override fun plant(speciesId: String, name: String, plotId: Int?) = game.plant(speciesId, name, plotId)
    override fun unlockPlot(plotId: Int) = game.unlockPlot(plotId)
}
