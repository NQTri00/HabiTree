package com.mamxanh.app.feature.garden.presentation

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

@Stable
class PlantingSelectionState {
    var plotKey: String by mutableStateOf("")
        private set

    fun select(plotKey: String) {
        this.plotKey = plotKey
    }

    fun clear() = select("")

    fun initialDialogPlot(openPlots: List<Int>): String =
        plotKey.ifBlank { openPlots.firstOrNull()?.toString().orEmpty() }
}
