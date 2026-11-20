package com.mamxanh.app.feature.garden.domain.usecase

import com.mamxanh.app.feature.garden.domain.model.Garden

fun Garden.availablePlantingPlots(): List<Int> =
    plots.filter { it.isUnlocked && it.treeId == null }.map { it.id }

/** Use the requested plot when possible, then fall back to the first open plot. */
fun Garden.eligiblePlantingPlot(preferredId: Int?): Int? {
    val openPlots = availablePlantingPlots()
    return openPlots.firstOrNull { it == preferredId } ?: openPlots.firstOrNull()
}

fun Garden.plantedTreeId(plotId: Int?): Long? {
    val treeId = plots.firstOrNull { it.id == plotId }?.treeId
    return trees.firstOrNull { it.id == treeId }?.id
}
