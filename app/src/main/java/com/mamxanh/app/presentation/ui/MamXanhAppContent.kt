package com.mamxanh.app.presentation.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.mamxanh.app.feature.garden.presentation.*
import com.mamxanh.app.feature.inventory.presentation.*
import com.mamxanh.app.feature.home.presentation.*
import com.mamxanh.app.feature.home.domain.model.HomeDashboard
import com.mamxanh.app.feature.home.domain.model.HomeTree
import com.mamxanh.app.feature.shop.presentation.*
import com.mamxanh.app.feature.shop.domain.model.ShopWallet
import com.mamxanh.app.feature.stats.presentation.*
import com.mamxanh.app.feature.tasks.presentation.*
import com.mamxanh.app.feature.tree.presentation.*
import com.mamxanh.app.feature.tree.domain.model.Tree as FeatureTree

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun MamXanhAppContent(
    state: AppUiState,
    dashboard: HomeUiState,
    tasks: TasksUiState,
    garden: GardenUiState,
    shop: ShopUiState,
    stats: StatsUiState,
    treeState: TreeUiState = TreeUiState(),
    modifier: Modifier = Modifier,
    onAppIntent: (AppIntent) -> Unit = {},
    onHomeIntent: (HomeIntent) -> Unit = {},
    onTasksIntent: (TasksIntent) -> Unit = {},
    onGardenIntent: (GardenIntent) -> Unit = {},
    onShopIntent: (ShopIntent) -> Unit = {},
    onStatsIntent: (StatsIntent) -> Unit = {},
    onTreeIntent: (TreeIntent) -> Unit = {}
) {
    val routeStates = rememberSaveableStateHolder()
    val app = state
    val gardenState = garden
    val walletState = shop
    val statistics = stats
    val wallet = shop.wallet
    MaterialTheme(colorScheme = GardenColorScheme, typography = GardenTypography) {
        Scaffold(
            modifier = modifier,
            containerColor = Color(0xFFE9F7F2),
            bottomBar = {
                GardenTrailBar(app.destination, {
                    onTreeIntent(TreeIntent.SetPanelExpanded(false))
                    onAppIntent(AppIntent.Navigate(it))
                })
            }
        ) { padding ->
            if (app.inventoryOpen) {
                InventoryScreen(
                    state = InventoryUiState(
                        seeds = com.mamxanh.app.feature.garden.domain.model.seedCatalog.map {
                            InventorySeed(it.id, it.name, wallet.inventory[it.id] ?: 0)
                        },
                        fertilizerCount = wallet.inventory["fertilizer"] ?: 0,
                        wateringCanMl = dashboard.dashboard.wateringCanMl,
                        coins = wallet.coins,
                        isLoading = shop.isLoading || garden.isLoading
                    ),
                    onBack = { onAppIntent(AppIntent.Back) },
                    onSelectSeed = { onAppIntent(AppIntent.SelectInventorySeed(it)) },
                    onOpenShop = { onAppIntent(AppIntent.Navigate(AppDestination.SHOP)) },
                    targetPlotId = app.inventoryPlotId,
                    modifier = Modifier.padding(padding)
                )
            } else SharedTransitionLayout(Modifier.fillMaxSize().padding(padding)) {
                val selectedTreeId = app.selectedTreeId
                val homeTreeId = dashboard.dashboard.trees.firstOrNull()?.id
                AnimatedContent(
                    targetState = app.destination to selectedTreeId,
                    transitionSpec = {
                        val (fromDestination, fromTreeId) = initialState
                        val (toDestination, toTreeId) = targetState
                        if (fromTreeId == null && toTreeId != null) {
                            (fadeIn(tween(450)) + scaleIn(initialScale = 0.82f, animationSpec = tween(450))) togetherWith
                                (fadeOut(tween(300)) + scaleOut(targetScale = 1.04f, animationSpec = tween(450)))
                        } else if (fromTreeId != null && toTreeId == null) {
                            (fadeIn(tween(450)) + scaleIn(initialScale = 1.04f, animationSpec = tween(450))) togetherWith
                                (fadeOut(tween(300)) + scaleOut(targetScale = 0.82f, animationSpec = tween(450)))
                        } else {
                            val direction = if (AppDestination.entries.indexOf(toDestination) >= AppDestination.entries.indexOf(fromDestination)) 1 else -1
                            (slideInHorizontally(tween(260)) { direction * it / 12 } + fadeIn(tween(220))) togetherWith
                                (slideOutHorizontally(tween(260)) { -direction * it / 12 } + fadeOut(tween(180)))
                        }
                    },
                    label = "garden-route"
                ) { (destination, routeTreeId) ->
                    val routeAnimationScope = this
                    val routeKey = routeTreeId?.let { "tree-$it" } ?: "destination-${destination.name}"
                    routeStates.SaveableStateProvider(routeKey) {
                    val routeTree = garden.garden.trees.firstOrNull { it.id == routeTreeId }
                    val treeSharedModifier = when {
                        routeTree != null -> Modifier.sharedElement(
                            rememberSharedContentState(key = "tree-${routeTree.id}"),
                            animatedVisibilityScope = this
                        )
                        homeTreeId != null -> Modifier.sharedElement(
                            rememberSharedContentState(key = "tree-$homeTreeId"),
                            animatedVisibilityScope = this
                        )
                        else -> Modifier
                    }
                    when {
                        routeTree != null -> TreeInteractionScreen(
                            tree = FeatureTree(routeTree.id, routeTree.speciesId, routeTree.name, routeTree.growth, routeTree.planted, routeTree.decoration, routeTree.wateredToday),
                            state = treeState,
                            fertilizerCount = wallet.inventory["fertilizer"] ?: 0,
                            wateringCanMl = dashboard.dashboard.wateringCanMl,
                            onIntent = { onTreeIntent(it) },
                            modifier = Modifier,
                            sharedTreeModifier = treeSharedModifier,
                            coins = wallet.coins,
                            onOpenGarden = {
                                onTreeIntent(TreeIntent.SetPanelExpanded(false))
                                onAppIntent(AppIntent.Navigate(AppDestination.GARDEN))
                            },
                            onPrevious = garden.garden.trees.takeIf { it.size > 1 }?.let { trees -> {
                                val index = trees.indexOfFirst { it.id == routeTree.id }
                                onAppIntent(AppIntent.OpenTree(trees[(index - 1 + trees.size) % trees.size].id))
                            } },
                            onNext = garden.garden.trees.takeIf { it.size > 1 }?.let { trees -> {
                                val index = trees.indexOfFirst { it.id == routeTree.id }
                                onAppIntent(AppIntent.OpenTree(trees[(index + 1) % trees.size].id))
                            } }
                        )
                        else -> when (destination) {
                            AppDestination.HOME -> HomeScreen(state = dashboard, onIntent = onHomeIntent,
                                fertilizerCount = wallet.inventory["fertilizer"] ?: 0, modifier = Modifier,
                                sharedTreeModifier = treeSharedModifier,
                                taskTitle = tasks.tasks.firstOrNull { !it.doneToday && !it.finished }?.title ?: "Mọi việc đã xong hôm nay",
                                canClaimBonus = tasks.canClaimDailyBonus && !tasks.isDailyBonusClaimed,
                                onClaimBonus = { onTasksIntent(TasksIntent.ClaimBonus) })
                            AppDestination.TASKS -> TasksScreen(tasks, onTasksIntent, Modifier, coins = wallet.coins)
                            AppDestination.GARDEN -> GardenScreen(gardenState, onGardenIntent, Modifier,
                                onOpenInventory = { onAppIntent(AppIntent.OpenInventory(it)) },
                                plantingSeedId = app.plantingSeedId,
                                plantingPlotId = app.inventoryPlotId,
                                onDismissPlanting = { onAppIntent(AppIntent.DismissPlanting) },
                                treeTransitionModifier = { id ->
                                    Modifier.sharedElement(
                                        rememberSharedContentState(key = "tree-$id"),
                                        animatedVisibilityScope = routeAnimationScope
                                    )
                                })
                            AppDestination.SHOP -> ShopScreen(walletState, onShopIntent, Modifier)
                            AppDestination.STATS -> StatsScreen(statistics, onStatsIntent, Modifier)
                        }
                    }
                    }
                }
            }
        }
        if (app.message.isNotBlank()) {
            GardenMessageDialog(
                message = app.message,
                onDismiss = { onAppIntent(AppIntent.DismissMessage) }
            )
        }
    }
}

@Preview(name = "App shell · home", showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun MamXanhAppContentHomePreview() {
    var route by remember { mutableStateOf(AppUiState()) }
    fun navigate(intent: AppIntent) {
        route = when (intent) {
            is AppIntent.Navigate -> if (intent.destination == route.destination && route.selectedTreeId == null) route else route.copy(
                destination = intent.destination, selectedTreeId = null,
                backStack = if (intent.destination == route.destination) route.backStack else route.backStack + route.destination
            )
            is AppIntent.OpenTree -> route.copy(selectedTreeId = intent.id)
            AppIntent.Back -> if (route.selectedTreeId != null) route.copy(selectedTreeId = null)
                else if (route.backStack.isNotEmpty()) route.copy(
                    destination = route.backStack.last(), backStack = route.backStack.dropLast(1)
                ) else route
            is AppIntent.ShowMessage -> route.copy(message = intent.message)
            AppIntent.DismissMessage -> route.copy(message = "")
            is AppIntent.OpenInventory -> route.copy(inventoryOpen = true, inventoryPlotId = intent.plotId)
            is AppIntent.SelectInventorySeed -> route.copy(inventoryOpen = false, destination = AppDestination.GARDEN, plantingSeedId = intent.seedId)
            AppIntent.DismissPlanting -> route.copy(plantingSeedId = null, inventoryPlotId = null)
        }
    }
    MamXanhPreviewTheme {
        MamXanhAppContent(
            state = route,
            dashboard = HomeUiState(HomeDashboard(listOf(HomeTree(1L, "cherry", "Đào hồng xuân", 68, "FLOWERS", false)), 2, 4, coins = 120, wateringCanMl = 500), false),
            tasks = TasksUiState(isLoading = false),
            garden = GardenUiState(
                com.mamxanh.app.feature.garden.domain.model.Garden(
                    trees = listOf(com.mamxanh.app.feature.garden.domain.model.Tree(
                        1L, "cherry", "Đào hồng xuân", 68, "2026-11-01", "FLOWERS"
                    ))
                ), false
            ),
            shop = ShopUiState(ShopWallet(120, mapOf("fertilizer" to 2)), false),
            stats = StatsUiState(isLoading = false),
            onAppIntent = ::navigate,
            onHomeIntent = {
                when (it) {
                    is HomeIntent.OpenTree -> navigate(AppIntent.OpenTree(it.id))
                    HomeIntent.OpenTasks -> navigate(AppIntent.Navigate(AppDestination.TASKS))
                    HomeIntent.OpenGarden -> navigate(AppIntent.Navigate(AppDestination.GARDEN))
                    HomeIntent.OpenShop -> navigate(AppIntent.Navigate(AppDestination.SHOP))
                    else -> Unit
                }
            },
            onGardenIntent = {
                when (it) {
                    is GardenIntent.OpenTree -> navigate(AppIntent.OpenTree(it.id))
                    GardenIntent.OpenShop -> navigate(AppIntent.Navigate(AppDestination.SHOP))
                    else -> Unit
                }
            },
            onTreeIntent = { if (it == TreeIntent.Back) navigate(AppIntent.Back) }
        )
    }
}

@Preview(name = "App shell · garden tab", showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun MamXanhAppContentGardenPreview() {
    MamXanhPreviewTheme {
        MamXanhAppContent(
            state = AppUiState(destination = AppDestination.GARDEN),
            dashboard = HomeUiState(isLoading = false),
            tasks = TasksUiState(isLoading = false),
            garden = GardenUiState(isLoading = false),
            shop = ShopUiState(ShopWallet(0, emptyMap()), false),
            stats = StatsUiState(isLoading = false)
        )
    }
}
