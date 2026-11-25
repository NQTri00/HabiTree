package com.mamxanh.app.presentation.bio

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.semantics.*
import androidx.compose.ui.unit.*
import com.mamxanh.app.core.components.design.*
import com.mamxanh.app.feature.inventory.presentation.navigation.InventoryRouteContent
import com.mamxanh.app.feature.shop.presentation.navigation.ShopRouteContent
import com.mamxanh.app.feature.shop.presentation.ui.shop.ShopTreeContext
import com.mamxanh.app.feature.tree.domain.repository.AccessorySnapshot
import org.json.JSONObject

private fun catalogProducts(
    world: BioWorld,
    tab: String,
): List<JSONObject> =
    when (tab) {
        "care" -> world.products
        "accessories" -> world.accessories
        else -> world.species
    }

internal fun inventoryRouteContent(
    world: BioWorld,
    ui: BioController,
    live: LiveInventory? = null,
    accessoryCatalog: AccessorySnapshot? = null,
): InventoryRouteContent {
    val tab = live?.state?.tab ?: ui.shopSelection.inventoryTab
    val items =
        catalogProducts(world, tab).filter {
            live?.state?.contains(it.s("id")) ?: (world.inventoryCount(it.s("id"), accessory = tab == "accessories") > 0)
        }
    val openShop = {
        if (live == null) {
            ui.shopSelection.shopTab = tab
            ui.go("shop")
        } else {
            live.openShop()
        }
    }
    return InventoryRouteContent(
        tab = tab,
        itemCount = items.size,
        onTab = { if (live == null) ui.shopSelection.inventoryTab = it else live.selectTab(it) },
        onShop = openShop,
        onBuy = openShop,
        item = { index, modifier ->
            val item = items[index]
            val target =
                (
                    if (live ==
                        null
                    ) {
                        ui.shopSelection.targetTree
                    } else {
                        ui.accessoryTargetArgument
                    }
                ).ifBlank { ui.homeTreeState.selectedId }
            BioProduct(
                world,
                ui,
                item,
                modifier,
                inventory = true,
                target = target,
                ownedQuantity = live?.state?.count(item.s("id")),
                equippedCan = live?.state?.equippedCan ?: world.state.s("equipped"),
                accessoryCatalog = accessoryCatalog,
                onProductClick =
                    live?.let { binding ->
                        { binding.use(item.s("id"), target, item.b("permanent")) }
                    },
            )
        },
    )
}

internal fun shopRouteContent(
    world: BioWorld,
    ui: BioController,
    argument: String,
    live: LiveShop? = null,
    accessoryCatalog: AccessorySnapshot? = null,
): ShopRouteContent {
    val tab = live?.state?.tab ?: ui.shopSelection.shopTab
    val items = catalogProducts(world, tab)
    val selected =
        if (live == null) {
            world.tree(argument)?.let { ShopTreeContext(it.s("id"), it.s("name"), it.s("species")) }
        } else {
            accessoryCatalog
                ?.trees
                ?.firstOrNull { it.id == argument }
                ?.let { ShopTreeContext(it.id, it.name, it.speciesId) }
        }
    return ShopRouteContent(
        tab = tab,
        shoppingTree = selected,
        itemCount = items.size,
        onTab = {
            if (live == null) ui.shopSelection.shopTab = it else live.selectTab(it)
        },
        onInventory = { ui.go("inventory") },
        onTree = { ui.go("tree", it) },
        onShop = { ui.go("shop") },
        item = { index, modifier ->
            val item = items[index]
            val preferredTarget = if (live == null) ui.shopSelection.targetTree else argument.ifBlank { ui.accessoryTargetArgument }
            val target = preferredTarget.ifBlank { ui.homeTreeState.selectedId }
            val accessoryTarget = target.takeIf { item.s("category") == "TREE" }
            val wallet = live?.state?.wallet
            BioProduct(
                world,
                ui,
                item,
                modifier,
                target = target,
                onProductClick = live?.let { binding -> { binding.selectProduct(item.s("id"), accessoryTarget) } },
                ownedQuantity = if (wallet == null || tab == "accessories") null else wallet.inventory[item.s("id")] ?: 0,
                equippedCan = wallet?.equippedCan ?: world.state.s("equipped"),
                accessoryCatalog = accessoryCatalog,
            )
        },
    )
}
