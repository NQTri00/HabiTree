package com.mamxanh.app.feature.inventory.presentation.ui.inventory

data class InventoryUiState(
    val seeds: List<InventorySeed> = emptyList(),
    val items: List<InventoryItem> = emptyList(),
    val coins: Int = 0,
    val isLoading: Boolean = false
)
