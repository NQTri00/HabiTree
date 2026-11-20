package com.mamxanh.app.presentation.bio

import com.mamxanh.app.presentation.ui.design.*

import com.mamxanh.app.presentation.ui.design.AppDimensions
import com.mamxanh.app.presentation.ui.design.AppRadii
import com.mamxanh.app.presentation.ui.design.AppSpacing
import com.mamxanh.app.presentation.ui.design.AppTypography

import com.mamxanh.shared.localization.TextKey

import com.mamxanh.shared.localization.AppText

import androidx.activity.compose.BackHandler
import com.mamxanh.app.core.presentation.navigation.HabiTreeRouteRegistry
import com.mamxanh.app.di.previewRouteRegistry
import com.mamxanh.app.feature.stats.presentation.StatisticsLink
import com.mamxanh.app.feature.stats.presentation.navigation.LocalWeeklyReviewRouteContent
import com.mamxanh.app.feature.catalog.presentation.navigation.LocalEncyclopediaRouteContent
import com.mamxanh.app.feature.garden.presentation.navigation.LocalGardenRouteContent
import com.mamxanh.app.feature.catalog.presentation.navigation.LocalSpeciesRouteContent
import com.mamxanh.app.feature.settings.presentation.navigation.LocalBackupRouteContent
import com.mamxanh.app.feature.settings.presentation.navigation.LocalSettingsRouteContent
import com.mamxanh.app.feature.tasks.presentation.navigation.LocalRestRouteContent
import com.mamxanh.app.feature.tasks.presentation.navigation.LocalInsightsRouteContent
import com.mamxanh.app.feature.tasks.presentation.navigation.LocalTemplatesRouteContent
import com.mamxanh.app.feature.tasks.presentation.navigation.LocalTaskListRouteContent
import com.mamxanh.app.feature.tasks.presentation.navigation.LocalTaskDetailRouteContent
import com.mamxanh.app.feature.tasks.presentation.navigation.LocalTaskFormRouteContent
import com.mamxanh.app.feature.tasks.presentation.navigation.LocalFocusRouteContent
import com.mamxanh.app.feature.inventory.presentation.navigation.LocalInventoryRouteContent
import com.mamxanh.app.feature.shop.presentation.navigation.LocalShopRouteContent
import com.mamxanh.app.feature.home.presentation.navigation.LocalHomeRouteContent
import com.mamxanh.app.feature.home.presentation.navigation.LocalOnboardingRouteContent
import com.mamxanh.app.feature.tree.presentation.navigation.LocalTreeRouteContent
import com.mamxanh.app.feature.stats.presentation.navigation.LocalAchievementsRouteContent
import com.mamxanh.app.feature.stats.presentation.navigation.LocalTimelineRouteContent
import com.mamxanh.app.feature.stats.presentation.navigation.LocalStatisticsRouteContent
import com.mamxanh.app.feature.stats.presentation.navigation.StatisticsRouteContent
import com.mamxanh.app.feature.stats.presentation.StatisticsScreenState
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.togetherWith
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.*

@Composable internal fun BioApplication(world:BioWorld,ui:BioController=remember{BioController()},modifier:Modifier=Modifier,routes:HabiTreeRouteRegistry=previewRouteRegistry) {
    ui.world=world
    val holders=rememberSaveableStateHolder()
    val primary=ui.route in listOf("home","tasks","garden","shop","stats")
    val inspection=LocalInspectionMode.current
    LaunchedEffect(ui.message){if(ui.message.isNotBlank()){kotlinx.coroutines.delay(4000);ui.message=""}}
    BioTheme(world,ui.homeTreeState.selectedId){
        BoxWithConstraints(modifier.fillMaxSize().background(bp.bg)){
            val gutter=if(maxWidth<AppDimensions.Dp359)16 else if(maxWidth>=AppDimensions.Dp400)22 else 20
            val compactScreen=maxHeight<AppDimensions.Dp600
            val shortScreen=maxHeight<AppDimensions.Dp700
            val topInset=if(maxHeight<AppDimensions.Dp600)14 else if(maxWidth<AppDimensions.Dp359)16 else 20
            val actionDockVisible = compactScreen && ui.route in listOf("task-new", "templates", "focus")
            val navigationVisible = ui.route !in listOf("onboard", "data") && !actionDockVisible
        CompositionLocalProvider(
            LocalBioExpanded provides (ui.tabs["expanded"] == "true"),
            LocalStatisticsRouteContent provides { statisticsRouteContent(world, ui) },
            LocalWeeklyReviewRouteContent provides { weeklyReviewRouteContent(world, ui) },
            LocalAchievementsRouteContent provides { achievementEntries(world) },
            LocalTimelineRouteContent provides { timelineRouteContent(world, ui) },
            LocalGardenRouteContent provides { gardenRouteContent(world, ui) },
            LocalEncyclopediaRouteContent provides { encyclopediaRouteContent(world, ui) },
            LocalSpeciesRouteContent provides { argument -> speciesRouteContent(world, ui, argument) },
            LocalSettingsRouteContent provides { settingsRouteContent(world, ui) },
            LocalBackupRouteContent provides { backupRouteContent(world, ui) },
            LocalRestRouteContent provides { restRouteContent(world, ui) },
            LocalInsightsRouteContent provides { argument -> insightsRouteContent(world, ui, argument) },
            LocalTemplatesRouteContent provides {
                templatesRouteContent(world, ui, if (compactScreen) null else { { BioTemplateActions(ui) } })
            },
            LocalTaskListRouteContent provides { taskListRouteContent(world, ui) },
            LocalTaskDetailRouteContent provides { argument -> taskDetailRouteContent(world, ui, argument) },
            LocalTaskFormRouteContent provides { argument ->
                taskFormRouteContent(world, ui, argument, if (compactScreen) null else { { BioTaskFormActions(world, ui) } })
            },
            LocalFocusRouteContent provides { argument ->
                focusRouteContent(world, ui, argument, if (compactScreen) null else { { BioFocusActions(world, ui) } })
            },
            LocalInventoryRouteContent provides { inventoryRouteContent(world, ui) },
            LocalShopRouteContent provides { argument -> shopRouteContent(world, ui, argument) },
            LocalHomeRouteContent provides { homeRouteContent(world, ui, shortScreen) },
            LocalOnboardingRouteContent provides { onboardingRouteContent(world, ui, gutter) },
            LocalTreeRouteContent provides { argument -> treeRouteContent(world, ui, argument, compactScreen) },
        ) {
            Column(Modifier.fillMaxSize()){
                val title=if(ui.route=="task-new")if(ui.param.isBlank())AppText.text(TextKey.CopyThemViecC371c2)else AppText.text(TextKey.CopySuaViec55ba70)else bioRoutes[ui.route]?:AppText.text(TextKey.CopyMamXanh7d3324)
                BioHeader(title,world.coins,primary,{ui.back()},{ui.go("more")},showWallet=ui.route!="onboard")
                if(ui.busy)LinearProgressIndicator(Modifier.fillMaxWidth(),color=bp.deep,trackColor=bp.soft)
                NavDisplay(
                    backStack = ui.backStack.map(routes::keyFor),
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    onBack = ui::back,
                    transitionSpec = { EnterTransition.None togetherWith ExitTransition.None },
                    popTransitionSpec = { EnterTransition.None togetherWith ExitTransition.None },
                    predictivePopTransitionSpec = { EnterTransition.None togetherWith ExitTransition.None },
                    entryProvider = { destination ->
                        NavEntry(destination) {
                            holders.SaveableStateProvider(destination.routeId + ":" + destination.argument) {
                                Column(Modifier.fillMaxSize()){
                                    val registeredContent = routes.contentFor(destination)
                                    if (destination.routeId == BioRoute.ONBOARD.id) {
                                        registeredContent?.invoke(this)
                                    } else Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState(if(ui.tabs["scrollBottom"]=="true")Int.MAX_VALUE else 0)).padding(start=gutter.dp,end=gutter.dp,top=topInset.dp,bottom=AppDimensions.Dp24),verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp18)){
                                        if(world.loading)LinearProgressIndicator(Modifier.fillMaxWidth(),color=bp.deep,trackColor=bp.soft)
                                        if (registeredContent != null) registeredContent(this)
                                        else if (destination.routeId == BioRoute.MORE.id) BioMore(ui)
                                    }
                                }
                            }
                        }
                    },
                )
                if (actionDockVisible) {
                    BioActionDock {
                        when (ui.route) {
                            "task-new" -> BioTaskFormActions(world, ui)
                            "templates" -> BioTemplateActions(ui)
                            "focus" -> BioFocusActions(world, ui)
                            else -> Unit
                        }
                    }
                }
                if (navigationVisible) {
                    BioNav(ui.route) {
                        if (it == "shop") {
                            ui.shopSelection.targetTree = ""
                            ui.plantingSelection.clear()
                        }
                        ui.go(it, root = true)
                    }
                }
            }
            if (ui.message.isNotBlank() && ui.modal.isBlank()) {
                val bottom = if (actionDockVisible || navigationVisible) AppDimensions.Dp90 else AppDimensions.Dp18
                BioToast(ui.message, { ui.message = "" }, Modifier.align(Alignment.BottomCenter)
                    .padding(start = AppDimensions.Dp15, end = AppDimensions.Dp15, bottom = bottom))
            }
            if(ui.modal.isNotBlank())CompositionLocalProvider(LocalBioButtonSize provides 13){BioModal(world,ui)}
        }
    }
    }
    if(!inspection)BackHandler(enabled=ui.modal.isNotBlank()||ui.route!="home"){ui.back()}
}

@Composable internal fun BioToast(message:String,onDismiss:()->Unit,modifier:Modifier=Modifier){BioCard(modifier.fillMaxWidth(),padding=AppSpacing.Value14,radius=AppRadii.Value14){Row(verticalAlignment=Alignment.CenterVertically){Bt(message,Modifier.weight(1f),size=AppTypography.Size12);BioIconButton("close",onDismiss)}}}


private fun statisticsRouteContent(world: BioWorld, ui: BioController): StatisticsRouteContent {
    val statistics = world.data.o("statistics")
    return StatisticsRouteContent(
        state = StatisticsScreenState(
            total = statistics.i("total"),
            treeCount = world.trees.size,
            week = statistics.i("week"),
            today = statistics.i("today"),
            streak = statistics.i("streak"),
            dailyCompletions = world.statisticsDailyCompletions(),
            dayLabels = world.statisticsDayLabels(),
        ),
        onNavigate = { link ->
            val route = when (link) {
                StatisticsLink.Review -> BioRoute.REVIEW
                StatisticsLink.Achievements -> BioRoute.ACHIEVEMENTS
                StatisticsLink.Timeline -> BioRoute.TIMELINE
                StatisticsLink.Focus -> BioRoute.FOCUS
                StatisticsLink.Settings -> BioRoute.SETTINGS
                StatisticsLink.More -> BioRoute.MORE
            }
            ui.go(route.id)
        },
        onRefresh = { ui.send("refresh") },
    )
}

internal fun BioWorld.statisticsDayLabels(): List<String> = bioDates(data.s("date"), 7).map { date ->
        val calendar = java.util.Calendar.getInstance().apply {
            time = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).parse(date)!!
        }
        bioWeekday(calendar.get(java.util.Calendar.DAY_OF_WEEK))
    }

internal fun BioWorld.statisticsDailyCompletions(): List<Int> {
    val daily = data.o("statistics").a("recentDailyCompletions")
    return List(7) { daily.optInt(it).coerceAtLeast(0) }
}
