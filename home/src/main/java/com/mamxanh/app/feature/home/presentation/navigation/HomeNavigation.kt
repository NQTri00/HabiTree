package com.mamxanh.app.feature.home.presentation.navigation

import com.mamxanh.app.core.presentation.navigation.HabiTreeFeatureDestination
import com.mamxanh.app.core.presentation.navigation.HabiTreeFeatureModule
import com.mamxanh.app.core.presentation.navigation.HabiTreeNavKey
import kotlinx.serialization.Serializable

@Serializable
internal data object HomeKey : HabiTreeNavKey {
    override val routeId: String get() = "home"
}

@Serializable
internal data object OnboardingKey : HabiTreeNavKey {
    override val routeId: String get() = "onboard"
}

val homeFeatureNavigation = HabiTreeFeatureModule(
    HabiTreeFeatureDestination("onboard") { argument -> OnboardingKey.takeIf { argument.isEmpty() } },
    HabiTreeFeatureDestination("home") { HomeKey.takeIf { _ -> it.isEmpty() } },
)
