package com.mamxanh.app.feature.garden.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import com.mamxanh.app.core.presentation.navigation.HabiTreeFeatureDestination
import com.mamxanh.app.core.presentation.navigation.HabiTreeFeatureModule
import com.mamxanh.app.core.presentation.navigation.HabiTreeNavKey
import com.mamxanh.app.feature.garden.presentation.ui.garden.BioGardenScreen
import com.mamxanh.app.feature.garden.presentation.ui.garden.GardenAction
import com.mamxanh.app.feature.garden.presentation.ui.garden.GardenDisplayState
import kotlinx.serialization.Serializable

@Serializable
internal data object GardenKey : HabiTreeNavKey {
    override val routeId: String get() = "garden"
}

class GardenRouteContent(
    val state: GardenDisplayState,
    val onAction: (GardenAction) -> Unit,
)

val LocalGardenRouteContent =
    compositionLocalOf<@Composable () -> GardenRouteContent> {
        error("Garden route requires its state and actions")
    }

val gardenFeatureNavigation =
    HabiTreeFeatureModule(
        HabiTreeFeatureDestination("garden", content = {
            val content = LocalGardenRouteContent.current()
            BioGardenScreen(content.state, content.onAction)
        }) { GardenKey.takeIf { _ -> it.isEmpty() } },
    )
