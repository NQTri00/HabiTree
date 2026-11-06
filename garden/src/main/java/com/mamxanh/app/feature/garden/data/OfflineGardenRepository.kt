package com.mamxanh.app.feature.garden.data

import com.mamxanh.app.data.repository.OfflineGameRepository
import com.mamxanh.app.feature.garden.domain.GardenRepository
import com.mamxanh.app.feature.garden.domain.model.Garden
import com.mamxanh.app.feature.garden.domain.model.Tree

class OfflineGardenRepository(private val game: OfflineGameRepository) : GardenRepository {
    override fun loadGarden(): Garden {
        val source = game.garden()
        return Garden(source.trees.map {
            Tree(it.id, it.speciesId, it.name, it.growth, it.planted, it.decoration, it.wateredToday)
        }, source.inventory)
    }
    override fun plant(speciesId: String, name: String) = game.plant(speciesId, name)
}
