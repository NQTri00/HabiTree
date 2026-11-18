package com.mamxanh.app.feature.shop.presentation.navigation

import com.mamxanh.app.core.presentation.navigation.HabiTreeFeatureDestination
import com.mamxanh.app.core.presentation.navigation.HabiTreeFeatureModule
import com.mamxanh.app.core.presentation.navigation.HabiTreeNavKey
import kotlinx.serialization.Serializable

@Serializable
internal data class ShopKey(override val argument: String) : HabiTreeNavKey {
    override val routeId: String get() = "shop"
}

val shopFeatureNavigation = HabiTreeFeatureModule(
    HabiTreeFeatureDestination("shop") { ShopKey(it) },
)
