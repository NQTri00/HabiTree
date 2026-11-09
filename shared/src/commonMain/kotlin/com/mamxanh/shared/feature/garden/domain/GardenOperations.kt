package com.mamxanh.shared.feature.garden.domain

import com.mamxanh.shared.feature.garden.domain.model.Garden

interface GardenRepository {
    fun loadGarden(): Garden
    fun plant(speciesId: String, name: String): Boolean
}
class LoadGarden(private val repository: GardenRepository) { operator fun invoke() = repository.loadGarden() }
class PlantSeed(private val repository: GardenRepository) {
    operator fun invoke(speciesId: String, name: String) = repository.plant(speciesId, name)
}
