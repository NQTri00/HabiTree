package com.mamxanh.app.presentation.bio

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mamxanh.app.core.components.design.*
import com.mamxanh.app.core.components.design.AppDimensions
import com.mamxanh.app.core.presentation.navigation.HabiTreeRouteRegistry
import com.mamxanh.app.di.previewRouteRegistry
import com.mamxanh.app.feature.catalog.presentation.navigation.LocalEncyclopediaRouteContent
import com.mamxanh.app.feature.catalog.presentation.navigation.LocalSpeciesRouteContent
import com.mamxanh.app.feature.garden.presentation.navigation.LocalGardenRouteContent
import com.mamxanh.app.feature.home.presentation.navigation.LocalHomeRouteContent
import com.mamxanh.app.feature.home.presentation.navigation.LocalOnboardingRouteContent
import com.mamxanh.app.feature.home.presentation.ui.home.HomeAction
import com.mamxanh.app.feature.inventory.presentation.navigation.LocalInventoryRouteContent
import com.mamxanh.app.feature.settings.presentation.navigation.LocalBackupRouteContent
import com.mamxanh.app.feature.settings.presentation.navigation.LocalSettingsRouteContent
import com.mamxanh.app.feature.shop.presentation.navigation.LocalShopRouteContent
import com.mamxanh.app.feature.stats.presentation.navigation.LocalAchievementsRouteContent
import com.mamxanh.app.feature.stats.presentation.navigation.LocalStatisticsRouteContent
import com.mamxanh.app.feature.stats.presentation.navigation.LocalTimelineRouteContent
import com.mamxanh.app.feature.stats.presentation.navigation.LocalWeeklyReviewRouteContent
import com.mamxanh.app.feature.tasks.presentation.navigation.LocalFocusRouteContent
import com.mamxanh.app.feature.tasks.presentation.navigation.LocalInsightsRouteContent
import com.mamxanh.app.feature.tasks.presentation.navigation.LocalRestRouteContent
import com.mamxanh.app.feature.tasks.presentation.navigation.LocalTaskDetailRouteContent
import com.mamxanh.app.feature.tasks.presentation.navigation.LocalTaskFormRouteContent
import com.mamxanh.app.feature.tasks.presentation.navigation.LocalTaskListRouteContent
import com.mamxanh.app.feature.tasks.presentation.navigation.LocalTemplatesRouteContent
import com.mamxanh.app.feature.tree.presentation.navigation.LocalTreeRouteContent
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey

@Composable internal fun BioApplication(
    world: BioWorld,
    ui: BioController =
        remember {
            BioController()
        },
    modifier: Modifier = Modifier,
    routes: HabiTreeRouteRegistry = previewRouteRegistry,
    refreshWorld: suspend () -> Unit = {},
) {
    ui.world = world
    val livePlanting = if (world.native) bindLivePlanting(world, ui) else null
    val treeHomeSelection = if (world.native && ui.route == "tree") bindTreeHomeSelection(world, ui) else null
    val accessoryCatalog =
        if (world.native && ui.route in setOf("home", "shop", "inventory", "tree")) {
            bindLiveAccessoryCatalog(world, ui.modal)
        } else {
            null
        }
    val liveCatalog = if (world.native) bindLiveCatalog(world, ui) else null
    val liveShop = if (world.native) bindLiveShop(world, ui) else null
    val liveForm = if (world.native && ui.route == "task-new") bindLiveTaskForm(ui) else null
    val liveTemplates = if (world.native && ui.route == "templates") bindLiveTemplates(ui) else null
    val liveFocus = if (world.native && ui.route == "focus") bindLiveFocus(ui) else null
    val holders = rememberSaveableStateHolder()
    val primary = ui.route in listOf("home", "tasks", "garden", "shop", "stats")
    val inspection = LocalInspectionMode.current
    LaunchedEffect(ui.message) {
        if (ui.message.isNotBlank()) {
            kotlinx.coroutines.delay(4000)
            ui.message = ""
        }
    }
    BioTheme(world, ui.homeTreeState.selectedId) {
        BoxWithConstraints(modifier.fillMaxSize().background(bp.bg)) {
            val gutter =
                if (maxWidth < AppDimensions.Dp359) {
                    AppDimensions.Dp16
                } else if (maxWidth >= AppDimensions.Dp400) {
                    AppDimensions.Dp22
                } else {
                    AppDimensions.Dp20
                }
            val compactScreen = maxHeight < AppDimensions.Dp600
            val shortScreen = maxHeight < AppDimensions.Dp700
            val topInset =
                if (maxHeight < AppDimensions.Dp600) {
                    AppDimensions.Dp14
                } else if (maxWidth < AppDimensions.Dp359) {
                    AppDimensions.Dp16
                } else {
                    AppDimensions.Dp20
                }
            val actionDockVisible = ui.route == "templates" || (compactScreen && ui.route in listOf("task-new", "focus"))
            val navigationVisible = ui.route !in listOf("onboard", "data") && !actionDockVisible
            CompositionLocalProvider(
                LocalBioExpanded provides (ui.tabs["expanded"] == "true"),
                LocalStatisticsRouteContent provides {
                    if (world.native) liveStatisticsRouteContent(world, ui) else statisticsRouteContent(world, ui)
                },
                LocalWeeklyReviewRouteContent provides {
                    if (world.native) {
                        weeklyReviewRouteContent(world, ui, bindLiveJournal(ui, world, loadTimeline = false))
                    } else {
                        weeklyReviewRouteContent(world, ui)
                    }
                },
                LocalAchievementsRouteContent provides {
                    if (world.native) liveAchievements(world, ui) else previewAchievements()
                },
                LocalTimelineRouteContent provides {
                    timelineRouteContent(world, ui, if (world.native) bindLiveJournal(ui, world, loadTimeline = true) else null)
                },
                LocalGardenRouteContent provides {
                    if (world.native) liveGardenRouteContent(world, ui) else gardenRouteContent(world, ui)
                },
                LocalEncyclopediaRouteContent provides { encyclopediaRouteContent(world, ui, liveCatalog) },
                LocalSpeciesRouteContent provides { argument -> speciesRouteContent(world, ui, argument, liveCatalog) },
                LocalSettingsRouteContent provides {
                    if (world.native) liveSettingsRouteContent(world, ui) else settingsRouteContent(world, ui)
                },
                LocalBackupRouteContent provides {
                    val model = if (world.native) backupViewModel() else null
                    val backup = model?.state?.collectAsStateWithLifecycle()?.value
                    backupRouteContent(world, ui, model, backup?.display)
                },
                LocalRestRouteContent provides {
                    restRouteContent(world, ui, if (world.native) bindLiveRest(world, ui) else null)
                },
                LocalInsightsRouteContent provides { argument ->
                    insightsRouteContent(world, ui, argument, if (world.native) bindLiveInsights(world, ui, argument) else null)
                },
                LocalTemplatesRouteContent provides {
                    templatesRouteContent(world, ui, selection = liveTemplates)
                },
                LocalTaskListRouteContent provides {
                    if (world.native) liveTaskListRouteContent(ui) else taskListRouteContent(world, ui)
                },
                LocalTaskDetailRouteContent provides { argument ->
                    if (world.native) liveTaskDetailRouteContent(ui, argument) else taskDetailRouteContent(world, ui, argument)
                },
                LocalTaskFormRouteContent provides { argument ->
                    if (liveForm != null && ui.param == argument) {
                        liveForm.content(compactScreen)
                    } else {
                        taskFormRouteContent(
                            world,
                            ui,
                            argument,
                            if (compactScreen) {
                                null
                            } else {
                                { BioTaskFormActions(world, ui) }
                            },
                        )
                    }
                },
                LocalFocusRouteContent provides { argument ->
                    focusRouteContent(
                        world,
                        ui,
                        argument,
                        if (compactScreen) {
                            null
                        } else {
                            { if (liveFocus == null) BioFocusActions(world, ui) else liveFocus.Actions(world) }
                        },
                        selection = liveFocus,
                    )
                },
                LocalInventoryRouteContent provides {
                    if (world.native) liveInventoryRouteContent(world, ui, accessoryCatalog) else inventoryRouteContent(world, ui)
                },
                LocalShopRouteContent provides { argument ->
                    if (liveShop ==
                        null
                    ) {
                        shopRouteContent(world, ui, argument)
                    } else {
                        shopRouteContent(ui, argument, liveShop, accessoryCatalog)
                    }
                },
                LocalHomeRouteContent provides {
                    if (world.native) {
                        liveHomeRouteContent(
                            world,
                            ui,
                            shortScreen,
                            accessoryCatalog,
                        )
                    } else {
                        homeRouteContent(world, ui, shortScreen)
                    }
                },
                LocalOnboardingRouteContent provides {
                    if (world.native) {
                        liveOnboardingRouteContent(
                            world,
                            ui,
                            gutter.value.toInt(),
                            refreshWorld,
                        )
                    } else {
                        onboardingRouteContent(world, ui, gutter.value.toInt())
                    }
                },
                LocalTreeRouteContent provides { argument ->
                    if (world.native) {
                        liveTreeRouteContent(world, ui, argument, compactScreen, accessoryCatalog) { id ->
                            treeHomeSelection?.dispatch(HomeAction.SelectTree(id))
                        }
                    } else {
                        treeRouteContent(world, ui, argument, compactScreen)
                    }
                },
            ) {
                Column(Modifier.fillMaxSize()) {
                    val title =
                        if (ui.route ==
                            "task-new"
                        ) {
                            if (ui.param.isBlank()) {
                                AppText.text(TextKey.CopyThemViecC371c2)
                            } else {
                                AppText.text(TextKey.CopySuaViec55ba70)
                            }
                        } else {
                            bioRoutes[ui.route]
                                ?: AppText.text(TextKey.CopyMamXanh7d3324)
                        }
                    BioHeader(title, world.coins, primary, { ui.back() }, { ui.go("more") }, showWallet = ui.route != "onboard")
                    if (ui.busy)LinearProgressIndicator(Modifier.fillMaxWidth(), color = bp.deep, trackColor = bp.soft)
                    BioRouteDisplay(world, ui, routes, holders, gutter, topInset, Modifier.weight(1f).fillMaxWidth())
                    if (actionDockVisible) {
                        BioActionDock {
                            when (ui.route) {
                                "task-new" -> if (liveForm == null) BioTaskFormActions(world, ui) else liveForm.Actions()
                                "templates" -> if (liveTemplates == null) BioTemplateActions(ui) else liveTemplates.Actions()
                                "focus" -> if (liveFocus == null) BioFocusActions(world, ui) else liveFocus.Actions(world)
                                else -> Unit
                            }
                        }
                    }
                    if (navigationVisible) {
                        BioNav(ui.route) {
                            if (it == "shop") {
                                ui.resetShopContext()
                            }
                            ui.go(it, root = true)
                        }
                    }
                }
                if (ui.message.isNotBlank() && ui.modal.isBlank()) {
                    val bottom = if (actionDockVisible || navigationVisible) AppDimensions.Dp90 else AppDimensions.Dp18
                    BioToast(
                        ui.message,
                        { ui.message = "" },
                        Modifier
                            .align(Alignment.BottomCenter)
                            .padding(start = AppDimensions.Dp15, end = AppDimensions.Dp15, bottom = bottom),
                    )
                }
                if (ui.modal.isNotBlank()) {
                    CompositionLocalProvider(LocalBioButtonSize provides AppTypography.SIZE_13) {
                        BioModal(world, ui, livePlanting, liveShop, treeHomeSelection, activeFocus = liveFocus)
                    }
                }
            }
        }
    }
    if (!inspection)BackHandler(enabled = ui.modal.isNotBlank() || ui.route != "home") { ui.back() }
}

@Preview(name = "Application shell", widthDp = 375, heightDp = 812)
@Preview(name = "Application shell landscape", widthDp = 844, heightDp = 390)
@Composable
private fun BioApplicationShellPreview() {
    BioReferenceSurface("home")
}
