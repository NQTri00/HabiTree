package com.mamxanh.app

import androidx.test.platform.app.InstrumentationRegistry
import com.mamxanh.app.presentation.bio.BioController
import com.mamxanh.app.presentation.bio.BioWorld
import com.mamxanh.app.presentation.bio.inventoryRouteContent
import com.mamxanh.app.presentation.bio.s
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Test

class InventoryRouteContentTest {
    @Test
    fun anEmptyCategoryUsesTheLiveCountsAndOwnedAccessoriesStayVisible() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val world = BioWorld.reference(context).copy(native = true, inventoryQuantities = emptyMap())
        val controller = BioController("inventory")
        world.state.put("owned", JSONObject())

        for (tab in listOf("seeds", "care", "accessories")) {
            controller.tabs["inventory"] = tab
            assertEquals(tab, 0, inventoryRouteContent(world, controller).itemCount)
        }

        val accessory = world.accessories.first().s("id")
        world.state.put("owned", JSONObject().put(accessory, 2))
        assertEquals(2, world.inventoryCount(accessory, accessory = true))
        assertEquals(1, inventoryRouteContent(world, controller).itemCount)
    }
}
