package com.mamxanh.app

import androidx.test.platform.app.InstrumentationRegistry
import com.mamxanh.app.feature.inventory.presentation.ui.inventory.InventoryAction
import com.mamxanh.app.feature.inventory.presentation.ui.inventory.InventoryState
import com.mamxanh.app.presentation.bio.BioController
import com.mamxanh.app.presentation.bio.BioWorld
import com.mamxanh.app.presentation.bio.LiveInventory
import com.mamxanh.app.presentation.bio.inventoryRouteContent
import com.mamxanh.app.presentation.bio.s
import org.junit.Assert.assertEquals
import org.junit.Test

class InventoryRouteContentTest {
    @Test
    fun inventoryShortcutsOpenTheMatchingShopCategory() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val world = BioWorld.reference(context).copy(native = true)
        val controller = BioController("inventory")

        for (tab in listOf("seeds", "care", "accessories")) {
            for (useBrowseShortcut in listOf(true, false)) {
                val actions = mutableListOf<InventoryAction>()
                val live = LiveInventory(InventoryState(tab = tab), actions::add)
                val content = inventoryRouteContent(world, controller, live)
                if (useBrowseShortcut) content.onShop() else content.onBuy()

                assertEquals(tab, content.tab)
                assertEquals(listOf(InventoryAction.OpenShop), actions)
            }
        }
    }

    @Test
    fun anEmptyCategoryUsesTheLiveCountsAndOwnedAccessoriesStayVisible() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val world = BioWorld.reference(context).copy(native = true)
        val controller = BioController("inventory")

        for (tab in listOf("seeds", "care", "accessories")) {
            val live = LiveInventory(InventoryState(tab = tab), {})
            assertEquals(tab, 0, inventoryRouteContent(world, controller, live).itemCount)
        }

        val accessory = world.accessories.first().s("id")
        val state = InventoryState(tab = "accessories", accessories = mapOf(accessory to 2))
        assertEquals(2, state.count(accessory))
        assertEquals(1, inventoryRouteContent(world, controller, LiveInventory(state, {})).itemCount)
    }
}
