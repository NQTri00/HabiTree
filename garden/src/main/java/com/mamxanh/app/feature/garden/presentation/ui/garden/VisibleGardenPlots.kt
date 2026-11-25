package com.mamxanh.app.feature.garden.presentation.ui.garden

import com.mamxanh.app.feature.garden.domain.model.Garden
import com.mamxanh.app.feature.garden.domain.model.GardenPlot
import com.mamxanh.app.feature.garden.domain.model.GardenTreeGrowth

internal fun visibleGardenPlots(
    garden: Garden,
    plots: List<GardenPlot>,
    filter: Int,
): List<GardenPlot> =
    plots.filter { plot ->
        val status = garden.trees.firstOrNull { it.id == plot.treeId }?.status
        when (filter) {
            1 -> status?.canWater == true
            2 -> status?.state == GardenTreeGrowth.Growing
            3 -> status?.state == GardenTreeGrowth.Mature
            4 -> status?.alive == false
            else -> true
        }
    }
