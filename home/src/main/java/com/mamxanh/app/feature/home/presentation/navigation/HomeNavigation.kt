package com.mamxanh.app.feature.home.presentation.navigation

import com.mamxanh.app.core.presentation.navigation.HabiTreeFeatureDestination
import com.mamxanh.app.core.presentation.navigation.HabiTreeFeatureModule
import com.mamxanh.app.core.presentation.navigation.HabiTreeNavKey
import kotlinx.serialization.Serializable

@Serializable
internal data object HomeKey : HabiTreeNavKey {
    override val routeId: String get() = "home"
}

val homeFeatureNavigation = HabiTreeFeatureModule(
    HabiTreeFeatureDestination("home") { HomeKey.takeIf { _ -> it.isEmpty() } },
)
