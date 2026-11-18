package com.mamxanh.app.feature.garden.presentation.navigation

import com.mamxanh.app.core.presentation.navigation.HabiTreeFeatureDestination
import com.mamxanh.app.core.presentation.navigation.HabiTreeFeatureModule
import com.mamxanh.app.core.presentation.navigation.HabiTreeNavKey
import kotlinx.serialization.Serializable

@Serializable
internal data object GardenKey : HabiTreeNavKey {
    override val routeId: String get() = "garden"
}

@Serializable
internal data object EncyclopediaKey : HabiTreeNavKey {
    override val routeId: String get() = "encyclopedia"
}

@Serializable
internal data class SpeciesKey(override val argument: String) : HabiTreeNavKey {
    override val routeId: String get() = "species"
}

val gardenFeatureNavigation = HabiTreeFeatureModule(
    HabiTreeFeatureDestination("garden") { GardenKey.takeIf { _ -> it.isEmpty() } },
    HabiTreeFeatureDestination("encyclopedia") { EncyclopediaKey.takeIf { _ -> it.isEmpty() } },
    HabiTreeFeatureDestination("species") { SpeciesKey(it) },
)
