package com.mamxanh.app.di

import com.mamxanh.app.feature.garden.domain.model.Garden
import com.mamxanh.app.feature.garden.domain.model.GardenPlot
import com.mamxanh.app.feature.garden.domain.model.Tree
import com.mamxanh.app.feature.garden.domain.usecase.availablePlantingPlots
import com.mamxanh.app.feature.garden.domain.usecase.eligiblePlantingPlot
import com.mamxanh.app.feature.garden.domain.usecase.plantedTreeId
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class GardenPlantingPlotsTest {
    @Test
    fun choosesTheRequestedEligiblePlotOrTheFirstAvailablePlot() {
        val garden =
            Garden(
                plots =
                    listOf(
                        GardenPlot(1, isUnlocked = false),
                        GardenPlot(2, isUnlocked = true, treeId = 50),
                        GardenPlot(3, isUnlocked = true),
                        GardenPlot(4, isUnlocked = true),
                    ),
            )

        assertEquals(listOf(3, 4), garden.availablePlantingPlots())
        assertEquals(4, garden.eligiblePlantingPlot(4))
        for (preference in listOf(null, 1, 2, 99)) {
            assertEquals(3, garden.eligiblePlantingPlot(preference))
        }
        assertNull(garden.copy(plots = garden.plots.take(2)).eligiblePlantingPlot(1))
    }

    @Test
    fun resolvesThePlantedTreeOnlyWhenThePlotAndTreeStillExist() {
        val garden =
            Garden(
                trees = listOf(Tree(50, "succulent", "My tree", 0, "2026-11-19")),
                plots = listOf(GardenPlot(2, isUnlocked = true, treeId = 50)),
            )

        assertEquals(50L, garden.plantedTreeId(2))
        assertNull(garden.plantedTreeId(null))
        assertNull(garden.plantedTreeId(3))
        assertNull(garden.copy(trees = emptyList()).plantedTreeId(2))
    }
}
