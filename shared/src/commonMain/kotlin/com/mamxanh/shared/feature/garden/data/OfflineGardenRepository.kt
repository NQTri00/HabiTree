package com.mamxanh.shared.feature.garden.data

import com.mamxanh.shared.data.GameStorage
import com.mamxanh.shared.feature.garden.domain.GardenRepository
import com.mamxanh.shared.feature.garden.domain.model.Garden
import com.mamxanh.shared.feature.garden.domain.model.Tree

class OfflineGardenRepository(private val game: GameStorage) : GardenRepository {
    override fun waterAll(key: String) = game.waterAll(key)
    override fun loadGarden(): Garden {
        return game.garden()
    }
    override fun plant(speciesId: String, name: String, plotId: Int?) = game.plant(speciesId, name, plotId)
    override fun unlockPlot(plotId: Int) = game.unlockPlot(plotId)
}
