package com.mamxanh.app.feature.garden.domain

import com.mamxanh.app.feature.garden.domain.model.Garden

/** Use the requested plot when possible, then fall back to the first open plot. */
fun Garden.eligiblePlantingPlot(preferredId: Int?): Int? {
    val openPlots = plots.filter { it.isUnlocked && it.treeId == null }
    return (openPlots.firstOrNull { it.id == preferredId } ?: openPlots.firstOrNull())?.id
}

fun Garden.plantedTreeId(plotId: Int?): Long? {
    val treeId = plots.firstOrNull { it.id == plotId }?.treeId
    return trees.firstOrNull { it.id == treeId }?.id
}
