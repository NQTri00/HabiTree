package com.mamxanh.app.presentation.bio

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.*
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.mamxanh.app.core.components.design.*
import com.mamxanh.app.core.components.design.AppDimensions
import com.mamxanh.app.core.presentation.navigation.HabiTreeRouteRegistry

@Composable
internal fun BioRouteDisplay(
    world: BioWorld,
    ui: BioController,
    routes: HabiTreeRouteRegistry,
    holders: SaveableStateHolder,
    gutter: Dp,
    topInset: Dp,
    modifier: Modifier = Modifier,
) {
    NavDisplay(
        backStack = ui.backStack.map(routes::keyFor),
        modifier = modifier,
        onBack = ui::back,
        transitionSpec = { EnterTransition.None togetherWith ExitTransition.None },
        popTransitionSpec = { EnterTransition.None togetherWith ExitTransition.None },
        predictivePopTransitionSpec = { EnterTransition.None togetherWith ExitTransition.None },
        entryProvider = { destination ->
            NavEntry(destination) {
                holders.SaveableStateProvider(destination.routeId + ":" + destination.argument) {
                    Column(Modifier.fillMaxSize()) {
                        val registeredContent = routes.contentFor(destination)
                        if (destination.routeId == BioRoute.ONBOARD.id) {
                            registeredContent?.invoke(this)
                        } else {
                            Column(
                                Modifier
                                    .fillMaxSize()
                                    .verticalScroll(
                                        rememberScrollState(
                                            if (!world.native && ui.preview.scrollBottom) {
                                                Int.MAX_VALUE
                                            } else {
                                                0
                                            },
                                        ),
                                    ).padding(
                                        start = gutter,
                                        end = gutter,
                                        top = topInset,
                                        bottom = AppDimensions.Dp24,
                                    ),
                                verticalArrangement = Arrangement.spacedBy(AppDimensions.Dp18),
                            ) {
                                if (world.loading) {
                                    LinearProgressIndicator(
                                        Modifier.fillMaxWidth(),
                                        color = bp.deep,
                                        trackColor = bp.soft,
                                    )
                                }
                                if (registeredContent != null) {
                                    registeredContent(this)
                                } else if (destination.routeId == BioRoute.MORE.id) {
                                    BioMore(ui)
                                }
                            }
                        }
                    }
                }
            }
        },
    )
}
