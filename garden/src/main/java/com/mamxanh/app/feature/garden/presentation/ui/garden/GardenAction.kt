package com.mamxanh.app.feature.garden.presentation.ui.garden
import com.mamxanh.app.feature.garden.presentation.ui.livinggarden.Encyclopedia

sealed interface GardenAction {
    data object Inventory : GardenAction
    data object WaterAll : GardenAction
    data object PickSeed : GardenAction
    data object Encyclopedia : GardenAction
    data class Filter(val value: String) : GardenAction
    data class Plot(val value: GardenDisplayPlot) : GardenAction
}
