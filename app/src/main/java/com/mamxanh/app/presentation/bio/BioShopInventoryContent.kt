package com.mamxanh.app.presentation.bio

import com.mamxanh.app.feature.inventory.presentation.navigation.InventoryRouteContent
import com.mamxanh.app.feature.shop.presentation.navigation.ShopRouteContent
import com.mamxanh.app.feature.shop.presentation.ui.shop.ShopTreeContext
import com.mamxanh.app.feature.tree.domain.repository.AccessorySnapshot
import com.mamxanh.shared.model.careProducts
import com.mamxanh.shared.model.catalog
import com.mamxanh.shared.model.gardenCosmetics
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

private fun liveProducts(tab: String): List<BioProductDisplay> =
    when (tab) {
        "care" -> careProducts.map { BioProductDisplay(it.id, it.name, it.price, permanent = it.permanent) }
        "accessories" ->
            gardenCosmetics.filter { it.category == "TREE" }.map {
                BioProductDisplay(it.id, it.name, it.price, permanent = it.permanent)
            }
        else -> catalog.map { BioProductDisplay(it.id, it.name, it.price, seed = true, tier = it.tier) }
    }

internal fun inventoryRouteContent(
    world: BioWorld,
    ui: BioController,
): InventoryRouteContent {
    val tab = ui.preview.shopSelection.inventoryTab
    val items = catalogProducts(world, tab).filter { world.inventoryCount(it.s("id"), accessory = tab == "accessories") > 0 }
    val openShop = {
        ui.preview.shopSelection.shopTab = tab
        ui.go("shop")
    }
    return InventoryRouteContent(
        tab = tab,
        itemCount = items.size,
        onTab = { ui.preview.shopSelection.inventoryTab = it },
        onShop = openShop,
        onBuy = openShop,
        item = { index, modifier ->
            val target =
                ui.preview.shopSelection.targetTree
                    .ifBlank { ui.homeTreeState.selectedId }
            BioProduct(world, ui, items[index], modifier, inventory = true, target = target)
        },
    )
}

internal fun inventoryRouteContent(
    ui: BioController,
    live: LiveInventory,
    accessoryCatalog: AccessorySnapshot?,
): InventoryRouteContent {
    val state = live.state
    val items = liveProducts(state.tab).filter { state.contains(it.id) }
    val target = ui.accessoryTargetArgument.ifBlank { ui.homeTreeState.selectedId }
    return InventoryRouteContent(
        tab = state.tab,
        itemCount = items.size,
        onTab = live::selectTab,
        onShop = live::openShop,
        onBuy = live::openShop,
        item = { index, modifier ->
            val item = items[index]
            val use = { live.use(item.id, target, item.permanent) }
            if (state.tab == "accessories") {
                BioAccessoryCard(item.id, item.name, item.price, true, accessoryCatalog?.equipped(item.id) == true, modifier, use)
            } else {
                BioProductCard(item, modifier, state.count(item.id), state.equippedCan, inventory = true, onClick = use)
            }
        },
    )
}

internal fun shopRouteContent(
    world: BioWorld,
    ui: BioController,
    argument: String,
): ShopRouteContent {
    val tab = ui.preview.shopSelection.shopTab
    val items = catalogProducts(world, tab)
    val target =
        ui.preview.shopSelection.targetTree
            .ifBlank { ui.homeTreeState.selectedId }
    return ShopRouteContent(
        tab = tab,
        shoppingTree = world.tree(argument)?.let { ShopTreeContext(it.s("id"), it.s("name"), it.s("species")) },
        itemCount = items.size,
        onTab = { ui.preview.shopSelection.shopTab = it },
        onInventory = { ui.go("inventory") },
        onTree = { ui.go("tree", it) },
        onShop = { ui.go("shop") },
        item = { index, modifier -> BioProduct(world, ui, items[index], modifier, target = target) },
    )
}

internal fun shopRouteContent(
    ui: BioController,
    argument: String,
    live: LiveShop,
    accessoryCatalog: AccessorySnapshot?,
): ShopRouteContent {
    val state = live.state
    val items = liveProducts(state.tab)
    val target = argument.ifBlank { ui.accessoryTargetArgument }.ifBlank { ui.homeTreeState.selectedId }
    val selected = accessoryCatalog?.trees?.firstOrNull { it.id == argument }
    return ShopRouteContent(
        tab = state.tab,
        shoppingTree = selected?.let { ShopTreeContext(it.id, it.name, it.speciesId) },
        itemCount = items.size,
        onTab = live::selectTab,
        onInventory = { ui.go("inventory") },
        onTree = { ui.go("tree", it) },
        onShop = { ui.go("shop") },
        item = { index, modifier ->
            val item = items[index]
            if (state.tab == "accessories") {
                BioAccessoryCard(
                    item.id,
                    item.name,
                    item.price,
                    accessoryCatalog?.owns(item.id) == true,
                    accessoryCatalog?.equipped(item.id) == true,
                    modifier,
                ) { live.selectProduct(item.id, target) }
            } else {
                BioProductCard(item, modifier, state.wallet.inventory[item.id] ?: 0, state.wallet.equippedCan) {
                    live.selectProduct(item.id, null)
                }
            }
        },
    )
}
