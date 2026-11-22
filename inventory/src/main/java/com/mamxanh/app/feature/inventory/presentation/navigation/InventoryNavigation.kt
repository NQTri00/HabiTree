package com.mamxanh.app.feature.inventory.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import com.mamxanh.app.core.presentation.navigation.HabiTreeFeatureDestination
import com.mamxanh.app.core.presentation.navigation.HabiTreeFeatureModule
import com.mamxanh.app.core.presentation.navigation.HabiTreeNavKey
import com.mamxanh.app.feature.inventory.presentation.ui.inventory.BioInventoryScreen
import kotlinx.serialization.Serializable

@Serializable
internal data class InventoryKey(
    override val argument: String,
) : HabiTreeNavKey {
    override val routeId: String get() = "inventory"
}

class InventoryRouteContent(
    val tab: String,
    val itemCount: Int,
    val onTab: (String) -> Unit,
    val onShop: () -> Unit,
    val onBuy: () -> Unit,
    val item: @Composable (Int, Modifier) -> Unit,
)

val LocalInventoryRouteContent =
    compositionLocalOf<() -> InventoryRouteContent> {
        error("Inventory route requires its state and actions")
    }

val inventoryFeatureNavigation =
    HabiTreeFeatureModule(
        HabiTreeFeatureDestination("inventory", content = {
            val content = LocalInventoryRouteContent.current()
            BioInventoryScreen(
                tab = content.tab,
                itemCount = content.itemCount,
                onTab = content.onTab,
                onShop = content.onShop,
                onBuy = content.onBuy,
                item = content.item,
            )
        }) { InventoryKey(it) },
    )
