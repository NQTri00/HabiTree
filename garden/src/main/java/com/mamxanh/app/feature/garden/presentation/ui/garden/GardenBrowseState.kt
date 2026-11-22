package com.mamxanh.app.feature.garden.presentation.ui.garden

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

@Stable
class GardenBrowseState {
    var filter by mutableStateOf("all")

    fun includes(
        growth: String?,
        watered: Boolean,
    ): Boolean =
        filter == "all" ||
            growth != null &&
            (filter == growth || filter == "thirsty" && isThirsty(growth, watered))

    companion object {
        const val SAVED_FILTER_KEY = "gardenFilter"

        fun isThirsty(
            growth: String,
            watered: Boolean,
        ): Boolean = !watered && growth != "dead"
    }
}
