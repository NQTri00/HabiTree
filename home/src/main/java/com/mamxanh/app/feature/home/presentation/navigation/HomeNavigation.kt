package com.mamxanh.app.feature.home.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import com.mamxanh.app.core.presentation.navigation.HabiTreeFeatureDestination
import com.mamxanh.app.core.presentation.navigation.HabiTreeFeatureModule
import com.mamxanh.app.core.presentation.navigation.HabiTreeNavKey
import com.mamxanh.app.feature.home.presentation.ui.home.BioHomeScreen
import com.mamxanh.app.feature.home.presentation.ui.home.HomeScreenAction
import com.mamxanh.app.feature.home.presentation.ui.home.HomeScreenState
import com.mamxanh.app.feature.home.presentation.ui.onboarding.OnboardingAction
import com.mamxanh.app.feature.home.presentation.ui.onboarding.OnboardingDisplay
import com.mamxanh.app.feature.home.presentation.ui.onboarding.OnboardingScreen
import kotlinx.serialization.Serializable

@Serializable
internal data object HomeKey : HabiTreeNavKey {
    override val routeId: String get() = "home"
}

@Serializable
internal data object OnboardingKey : HabiTreeNavKey {
    override val routeId: String get() = "onboard"
}

class HomeRouteContent(
    val state: HomeScreenState,
    val onAction: (HomeScreenAction) -> Unit,
    val tree: (@Composable () -> Unit)?,
    val nextTask: (@Composable () -> Unit)?,
    val reward: (@Composable () -> Unit)?,
)

class OnboardingRouteContent(
    val state: OnboardingDisplay,
    val gutter: Int,
    val onAction: (OnboardingAction) -> Unit,
    val templateRow: @Composable (Int) -> Unit,
)

val LocalHomeRouteContent =
    compositionLocalOf<@Composable () -> HomeRouteContent> {
        error("Home route requires its state and actions")
    }

val LocalOnboardingRouteContent =
    compositionLocalOf<@Composable () -> OnboardingRouteContent> {
        error("Onboarding route requires its state and actions")
    }

val homeFeatureNavigation =
    HabiTreeFeatureModule(
        HabiTreeFeatureDestination("onboard", content = {
            val content = LocalOnboardingRouteContent.current()
            OnboardingScreen(content.state, content.gutter, content.onAction, content.templateRow)
        }) { argument -> OnboardingKey.takeIf { argument.isEmpty() } },
        HabiTreeFeatureDestination("home", content = {
            val content = LocalHomeRouteContent.current()
            BioHomeScreen(content.state, content.onAction, content.tree, content.nextTask, content.reward)
        }) { HomeKey.takeIf { _ -> it.isEmpty() } },
    )
