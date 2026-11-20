package com.mamxanh.app.di

import com.mamxanh.app.feature.shop.presentation.ShopSelectionState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ShopSelectionStateTest {
    @Test
    fun equipsOwnedAccessoryWithEmptyWalletButRequiresAnAvailableTree() {
        val purchase = ShopSelectionState().purchaseAvailability(
            coins = 0, unitPrice = 100, owned = true, permanent = false, busy = false,
        )

        assertEquals(0, purchase.total)
        assertFalse(purchase.canBuyAccessory)
        assertTrue(purchase.canEquipAccessory(hasTarget = true, equipped = false))
        assertFalse(purchase.canEquipAccessory(hasTarget = false, equipped = false))
        assertFalse(purchase.canEquipAccessory(hasTarget = true, equipped = true))
    }

    @Test
    fun fallsBackToHomeTreeAndThenFirstLivingTree() {
        val selection = ShopSelectionState().apply { targetTree = "removed" }
        assertEquals("home", selection.accessoryTarget(listOf("first", "home"), "home"))
        assertEquals("first", selection.accessoryTarget(listOf("first"), "home"))
    }
}
