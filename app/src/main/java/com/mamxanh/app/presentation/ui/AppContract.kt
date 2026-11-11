package com.mamxanh.app.presentation.ui

enum class AppDestination(val label: String) { HOME("Trang chủ"), TASKS("Nhiệm vụ"), GARDEN("Khu vườn"), SHOP("Cửa hàng"), STATS("Thống kê") }

data class AppUiState(
    val destination: AppDestination = AppDestination.HOME,
    val backStack: List<AppDestination> = emptyList(),
    val selectedTreeId: Long? = null,
    val message: String = "",
    val inventoryOpen: Boolean = false,
    val inventoryPlotId: Int? = null,
    val plantingSeedId: String? = null
)

sealed interface AppIntent {
    data class Navigate(val destination: AppDestination) : AppIntent
    data class OpenTree(val id: Long) : AppIntent
    data object Back : AppIntent
    data class OpenInventory(val plotId: Int? = null) : AppIntent
    data class SelectInventorySeed(val seedId: String) : AppIntent
    data object DismissPlanting : AppIntent
    data object DismissMessage : AppIntent
    data class ShowMessage(val message: String) : AppIntent
}
