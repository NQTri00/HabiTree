package com.mamxanh.app

import com.mamxanh.app.feature.inventory.presentation.ui.inventory.InventoryAction
import com.mamxanh.app.feature.inventory.presentation.ui.inventory.InventoryState
import com.mamxanh.app.presentation.bio.BioController
import com.mamxanh.app.presentation.bio.LiveInventory
import com.mamxanh.app.presentation.bio.inventoryRouteContent
import com.mamxanh.shared.model.gardenCosmetics
import org.junit.Assert.assertEquals
import org.junit.Test

class InventoryRouteContentTest {
    @Test
    fun inventoryShortcutsOpenTheMatchingShopCategory() {
        val controller = BioController("inventory")

        for (tab in listOf("seeds", "care", "accessories")) {
            for (useBrowseShortcut in listOf(true, false)) {
                val actions = mutableListOf<InventoryAction>()
                val live = LiveInventory(InventoryState(tab = tab), actions::add)
                val content = inventoryRouteContent(controller, live, null)
                if (useBrowseShortcut) content.onShop() else content.onBuy()

                assertEquals(tab, content.tab)
                assertEquals(listOf(InventoryAction.OpenShop), actions)
            }
        }
    }

    @Test
    fun anEmptyCategoryUsesTheLiveCountsAndOwnedAccessoriesStayVisible() {
        val controller = BioController("inventory")

        for (tab in listOf("seeds", "care", "accessories")) {
            val live = LiveInventory(InventoryState(tab = tab), {})
            assertEquals(tab, 0, inventoryRouteContent(controller, live, null).itemCount)
        }

        val accessory = gardenCosmetics.first { it.category == "TREE" }.id
        val state = InventoryState(tab = "accessories", accessories = mapOf(accessory to 2))
        assertEquals(2, state.count(accessory))
        assertEquals(1, inventoryRouteContent(controller, LiveInventory(state, {}), null).itemCount)
    }
}
