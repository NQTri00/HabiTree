package com.mamxanh.app.feature.catalog.presentation.model

import com.mamxanh.app.feature.catalog.domain.repository.CollectedSpecies

data class SpeciesTreeDisplay(
    val id: String,
    val name: String,
    val living: Boolean = true,
    val stage: Int = 0,
)

data class SpeciesDisplay(
    val id: String,
    val name: String,
    val description: String,
    val tier: String,
    val status: String,
    val discovered: Boolean,
    val highest: Int,
    val stages: Int,
    val planted: Int,
    val owned: Int,
    val living: Int,
    val firstPlanted: String,
    val firstMatured: String,
    val highestTree: Int?,
    val trees: List<SpeciesTreeDisplay>,
)

fun speciesDisplay(
    id: String,
    name: String,
    description: String,
    tier: String,
    stages: Int,
    owned: Int,
    firstPlanted: String,
    firstMatured: String,
    status: String,
    discovered: Boolean,
    highest: Int,
    planted: Int,
    trees: List<SpeciesTreeDisplay>,
    collection: CollectedSpecies?,
): SpeciesDisplay =
    SpeciesDisplay(
        id = id,
        name = name,
        description = description,
        tier = tier,
        status = collection?.status ?: status,
        discovered = collection?.discovered ?: discovered,
        highest = collection?.highestStage ?: highest,
        stages = stages,
        planted = collection?.planted ?: planted,
        owned = owned,
        living = trees.count { it.living },
        firstPlanted = firstPlanted,
        firstMatured = firstMatured,
        highestTree = trees.maxOfOrNull { it.stage },
        trees = trees,
    )
