package com.mamxanh.app.presentation.ui

import com.mamxanh.app.core.presentation.UiAction
import com.mamxanh.app.core.presentation.UiState
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey

enum class AppDestination(
    private val labelKey: TextKey,
) {
    HOME(TextKey.CopyTrangChu8be437),
    TASKS(TextKey.CopyNhiemVu0f2beb),
    GARDEN(TextKey.CopyKhuVuonC22009),
    SHOP(TextKey.CopyCuaHangCde3b6),
    STATS(TextKey.CopyThongKe9befe4),
    ;

    val label: String get() = AppText.text(labelKey)
}

data class AppUiState(
    val destination: AppDestination = AppDestination.HOME,
    val backStack: List<AppDestination> = emptyList(),
    val selectedTreeId: Long? = null,
    val message: String = "",
    val inventoryOpen: Boolean = false,
    val inventoryPlotId: Int? = null,
    val plantingSeedId: String? = null,
) : UiState

sealed interface AppIntent : UiAction {
    data class Navigate(
        val destination: AppDestination,
    ) : AppIntent

    data class OpenTree(
        val id: Long,
    ) : AppIntent

    data object Back : AppIntent

    data class OpenInventory(
        val plotId: Int? = null,
    ) : AppIntent

    data class SelectInventorySeed(
        val seedId: String,
    ) : AppIntent

    data object DismissPlanting : AppIntent

    data object DismissMessage : AppIntent

    data class ShowMessage(
        val message: String,
    ) : AppIntent
}
