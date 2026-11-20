package com.mamxanh.app.feature.catalog.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import com.mamxanh.app.core.presentation.navigation.HabiTreeFeatureDestination
import com.mamxanh.app.core.presentation.navigation.HabiTreeFeatureModule
import com.mamxanh.app.core.presentation.navigation.HabiTreeNavKey
import com.mamxanh.app.feature.catalog.presentation.ui.encyclopedia.CollectionGoalDisplay
import com.mamxanh.app.feature.catalog.presentation.ui.encyclopedia.EncyclopediaScreen
import com.mamxanh.app.feature.catalog.presentation.model.SpeciesDisplay
import com.mamxanh.app.feature.catalog.presentation.ui.species.SpeciesScreen
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("com.mamxanh.app.feature.garden.presentation.navigation.EncyclopediaKey")
internal data object EncyclopediaKey : HabiTreeNavKey {
    override val routeId: String get() = "encyclopedia"
}

@Serializable
@SerialName("com.mamxanh.app.feature.garden.presentation.navigation.SpeciesKey")
internal data class SpeciesKey(override val argument: String) : HabiTreeNavKey {
    override val routeId: String get() = "species"
}

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

val LocalEncyclopediaRouteContent = compositionLocalOf<() -> EncyclopediaRouteContent> {
    error("Encyclopedia route requires its state and item content")
}

val LocalSpeciesRouteContent = compositionLocalOf<(String) -> SpeciesRouteContent> {
    error("Species route requires its state and actions")
}

val catalogFeatureNavigation = HabiTreeFeatureModule(
    HabiTreeFeatureDestination("encyclopedia", content = {
        val content = LocalEncyclopediaRouteContent.current()
        EncyclopediaScreen(content.discovered, content.speciesCount, content.goals, content.item)
    }) { EncyclopediaKey.takeIf { _ -> it.isEmpty() } },
    HabiTreeFeatureDestination("species", content = {
        val content = LocalSpeciesRouteContent.current(it.argument)
        SpeciesScreen(content.state, content.onTree, content.onShop)
    }) { SpeciesKey(it) },
)
