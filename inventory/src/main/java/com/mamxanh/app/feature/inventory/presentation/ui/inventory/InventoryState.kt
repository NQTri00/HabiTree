package com.mamxanh.app.feature.inventory.presentation.ui.inventory

import com.mamxanh.app.core.presentation.UiState

data class InventoryState(
    val tab: String = "seeds",
    val quantities: Map<String, Int> = emptyMap(),
    val accessories: Map<String, Int> = emptyMap(),
    val loading: Boolean = true,
    val busy: Boolean = false,
    val equippedCan: String = "",
) : UiState {
    fun count(id: String): Int = (if (tab == "accessories") accessories[id] else quantities[id]) ?: 0

    fun contains(id: String): Boolean = count(id) > 0
}
