package com.mamxanh.app.feature.garden.presentation.ui.garden

import com.mamxanh.app.core.presentation.UiState
import com.mamxanh.app.feature.garden.domain.model.Garden

data class GardenUiState(val garden: Garden = Garden(), val isLoading: Boolean = true, val message: String? = null) : UiState
