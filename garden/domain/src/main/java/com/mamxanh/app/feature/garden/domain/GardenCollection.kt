package com.mamxanh.app.feature.garden.domain

data class GardenCollection(
    val species: List<CollectedSpecies>,
    val milestones: List<CollectionMilestone>,
    val decorationsByTree: Map<Long, String>,
    val ownedAccessories: Map<String, Int>,
)

data class CollectedSpecies(
    val id: String,
    val ownedSeeds: Int,
    val planted: Int,
    val discovered: Boolean,
    val highestStage: Int,
    val firstPlanted: Long,
    val firstMatured: Long,
    val flourished: Boolean,
    val status: String,
)

data class CollectionMilestone(
    val id: String,
    val title: String,
    val progress: Int,
    val target: Int,
    val reward: String,
    val unlocked: Boolean,
)

fun interface GardenCollectionRepository {
    fun load(): GardenCollection
}
