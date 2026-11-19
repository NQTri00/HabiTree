package com.mamxanh.app.feature.garden.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import com.mamxanh.app.core.presentation.navigation.HabiTreeFeatureDestination
import com.mamxanh.app.core.presentation.navigation.HabiTreeFeatureModule
import com.mamxanh.app.core.presentation.navigation.HabiTreeNavKey
import com.mamxanh.app.feature.garden.presentation.BioGardenScreen
import com.mamxanh.app.feature.garden.presentation.CollectionGoalDisplay
import com.mamxanh.app.feature.garden.presentation.EncyclopediaScreen
import com.mamxanh.app.feature.garden.presentation.GardenAction
import com.mamxanh.app.feature.garden.presentation.GardenDisplayState
import com.mamxanh.app.feature.garden.presentation.SpeciesDisplay
import com.mamxanh.app.feature.garden.presentation.SpeciesScreen
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

class GardenRouteContent(
    val state: GardenDisplayState,
    val onAction: (GardenAction) -> Unit,
)

class EncyclopediaRouteContent(
    val discovered: Int,
    val speciesCount: Int,
    val goals: List<CollectionGoalDisplay>,
    val item: @Composable (Int, Modifier) -> Unit,
)

class SpeciesRouteContent(
    val state: SpeciesDisplay,
    val onTree: (String) -> Unit,
    val onShop: () -> Unit,
)

val LocalGardenRouteContent = compositionLocalOf<() -> GardenRouteContent> {
    error("Garden route requires its state and actions")
}

val LocalEncyclopediaRouteContent = compositionLocalOf<() -> EncyclopediaRouteContent> {
    error("Encyclopedia route requires its state and item content")
}

val LocalSpeciesRouteContent = compositionLocalOf<(String) -> SpeciesRouteContent> {
    error("Species route requires its state and actions")
}

val gardenFeatureNavigation = HabiTreeFeatureModule(
    HabiTreeFeatureDestination("garden", content = {
        val content = LocalGardenRouteContent.current()
        BioGardenScreen(content.state, content.onAction)
    }) { GardenKey.takeIf { _ -> it.isEmpty() } },
    HabiTreeFeatureDestination("encyclopedia", content = {
        val content = LocalEncyclopediaRouteContent.current()
        EncyclopediaScreen(content.discovered, content.speciesCount, content.goals, content.item)
    }) { EncyclopediaKey.takeIf { _ -> it.isEmpty() } },
    HabiTreeFeatureDestination("species", content = {
        val content = LocalSpeciesRouteContent.current(it.argument)
        SpeciesScreen(content.state, content.onTree, content.onShop)
    }) { SpeciesKey(it) },
)
