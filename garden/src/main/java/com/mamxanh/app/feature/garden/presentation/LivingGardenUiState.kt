package com.mamxanh.app.feature.garden.presentation

import com.mamxanh.app.core.presentation.UiState
import com.mamxanh.shared.model.Garden
import com.mamxanh.shared.model.GardenPlacement
import com.mamxanh.shared.model.LivingGardenSnapshot

data class LivingGardenUiState(
    val snapshot: LivingGardenSnapshot = LivingGardenSnapshot(),
    val garden: Garden = Garden(),
    val route: String = "",
    val selected: String = "",
    val draft: List<GardenPlacement>? = null,
    val draftVersion: Long = 0,
    val busy: Boolean = false,
    val message: String = "",
    val loading: Boolean = true,
    val undoAvailable: Boolean = false,
) : UiState
