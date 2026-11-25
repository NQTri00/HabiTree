package com.mamxanh.app.feature.garden.presentation.ui.garden

import com.mamxanh.app.core.presentation.UiState

data class PlantingState(
    val seed: String = "",
    val plotId: Int? = null,
    val name: String = "",
    val eligible: List<Int> = emptyList(),
    val stock: Map<String, Int> = emptyMap(),
    val loading: Boolean = true,
    val busy: Boolean = false,
) : UiState
