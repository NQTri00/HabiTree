package com.mamxanh.shared.feature.garden.domain

import com.mamxanh.shared.feature.garden.domain.model.Garden

interface GardenRepository {
    fun waterAll(key: String): com.mamxanh.shared.model.ActionResult
    fun loadGarden(): Garden
    fun plant(speciesId: String, name: String, plotId: Int? = null): Boolean
    fun unlockPlot(plotId: Int): Boolean
}
class LoadGarden(private val repository: GardenRepository) { operator fun invoke() = repository.loadGarden() }
class PlantSeed(private val repository: GardenRepository) {
    operator fun invoke(speciesId: String, name: String, plotId: Int? = null) = repository.plant(speciesId, name, plotId)
}
