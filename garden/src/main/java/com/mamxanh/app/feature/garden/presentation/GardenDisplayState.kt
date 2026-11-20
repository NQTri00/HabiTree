package com.mamxanh.app.feature.garden.presentation

data class GardenDisplayState(
    val filter: String,
    val treeCount: Int,
    val emptyPlots: Int,
    val speciesCount: Int,
    val canWater: Boolean,
    val plots: List<GardenDisplayPlot>,
)
