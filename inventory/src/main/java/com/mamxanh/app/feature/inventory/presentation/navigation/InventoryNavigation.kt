package com.mamxanh.app.feature.inventory.presentation.navigation

import com.mamxanh.app.core.presentation.navigation.HabiTreeFeatureDestination
import com.mamxanh.app.core.presentation.navigation.HabiTreeFeatureModule
import com.mamxanh.app.core.presentation.navigation.HabiTreeNavKey
import kotlinx.serialization.Serializable

@Serializable
internal data class InventoryKey(override val argument: String) : HabiTreeNavKey {
    override val routeId: String get() = "inventory"
}

val inventoryFeatureNavigation = HabiTreeFeatureModule(
    HabiTreeFeatureDestination("inventory") { InventoryKey(it) },
)
