package com.mamxanh.app.feature.garden.presentation.ui.garden

import com.mamxanh.app.core.presentation.UiState
import com.mamxanh.app.feature.garden.domain.model.Garden
import com.mamxanh.app.feature.garden.domain.model.GardenTreeGrowth
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey
import com.mamxanh.shared.model.Balance

data class GardenState(
    val garden: Garden = Garden(),
    val isLoading: Boolean = true,
    val message: String? = null,
    val filter: String = "all",
) : UiState {
    val nextUnlockCost: Int
        get() = Balance.plot(garden.plots.count { it.isUnlocked })

    fun canUnlock(plotId: Int?): Boolean =
        !isLoading && plotId != null && garden.plots.firstOrNull { !it.isUnlocked }?.id == plotId && garden.coins >= nextUnlockCost

    fun display(speciesCount: Int): GardenDisplayState {
        val trees = garden.trees.associateBy { it.id }
        val unlocked = garden.plots.filter { it.isUnlocked }
        val plots =
            unlocked.mapNotNull { plot ->
                val tree = plot.treeId?.let(trees::get)
                val growth = tree?.status?.state
                val watered = tree?.status?.waterActive == true
                val included =
                    filter == "all" ||
                        (
                            growth != null &&
                                (filter == growth.name.lowercase() || filter == "thirsty" && !watered && growth != GardenTreeGrowth.Dead)
                        )
                if (!included) return@mapNotNull null
                val displayTree =
                    tree?.let {
                        val label =
                            when (it.status.state) {
                                GardenTreeGrowth.Dead -> TextKey.CopyDaKhoHeoA9c1b9
                                GardenTreeGrowth.Mature -> TextKey.CopyTruongThanh8c14d7
                                GardenTreeGrowth.Growing -> TextKey.CopyDangLon40daab
                            }
                        GardenDisplayTree(
                            id = it.id.toString(),
                            species = it.speciesId,
                            name = AppText.plantName(it.speciesId, it.name),
                            progress = it.status.percentage,
                            stageLabel = AppText.text(label),
                            dead = growth == GardenTreeGrowth.Dead,
                            watered = watered,
                        )
                    }
                GardenDisplayPlot(plot.id, displayTree)
            }
        val nextLocked = garden.plots.firstOrNull { !it.isUnlocked }
        val visible =
            if (filter == "all" && nextLocked != null) {
                plots + GardenDisplayPlot(nextLocked.id, price = nextUnlockCost)
            } else {
                plots
            }
        return GardenDisplayState(
            filter,
            trees.size,
            unlocked.count { it.treeId == null },
            speciesCount,
            trees.values.any { it.status.alive && !it.status.waterActive },
            visible,
        )
    }
}
