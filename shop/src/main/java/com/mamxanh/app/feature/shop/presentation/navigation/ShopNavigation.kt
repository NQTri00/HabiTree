package com.mamxanh.app.feature.shop.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import com.mamxanh.app.core.presentation.navigation.HabiTreeFeatureDestination
import com.mamxanh.app.core.presentation.navigation.HabiTreeFeatureModule
import com.mamxanh.app.core.presentation.navigation.HabiTreeNavKey
import com.mamxanh.app.feature.shop.presentation.ui.shop.BioShopScreen
import com.mamxanh.app.feature.shop.presentation.ui.shop.ShopTreeContext
import kotlinx.serialization.Serializable

@Serializable
internal data class ShopKey(override val argument: String) : HabiTreeNavKey {
    override val routeId: String get() = "shop"
}

class ShopRouteContent(
    val tab: String,
    val shoppingTree: ShopTreeContext?,
    val itemCount: Int,
    val onTab: (String) -> Unit,
    val onInventory: () -> Unit,
    val onTree: (String) -> Unit,
    val onShop: () -> Unit,
    val item: @Composable (Int, Modifier) -> Unit,
)

val LocalShopRouteContent = compositionLocalOf<(String) -> ShopRouteContent> {
    error("Shop route requires its state and actions")
}

val shopFeatureNavigation = HabiTreeFeatureModule(
    HabiTreeFeatureDestination("shop", content = {
        val content = LocalShopRouteContent.current(it.argument)
        BioShopScreen(
            tab = content.tab,
            shoppingTree = content.shoppingTree,
            itemCount = content.itemCount,
            onTab = content.onTab,
            onInventory = content.onInventory,
            onTree = content.onTree,
            onShop = content.onShop,
            item = content.item,
        )
    }) { ShopKey(it) },
)
