package com.mamxanh.app.feature.garden.domain

import com.mamxanh.app.feature.garden.domain.model.Garden

data class GardenActionResult(val success: Boolean, val message: String)

interface GardenRepository {
    fun waterAll(key: String): GardenActionResult
    fun loadGarden(): Garden
    fun plant(speciesId: String, name: String, plotId: Int? = null): Boolean
    fun unlockPlot(plotId: Int): Boolean
}

class LoadGarden(private val repository: GardenRepository) {
    operator fun invoke(): Garden = repository.loadGarden()
}

class PlantSeed(private val repository: GardenRepository) {
    operator fun invoke(speciesId: String, name: String, plotId: Int? = null): Boolean =
        repository.plant(speciesId, name, plotId)
}
