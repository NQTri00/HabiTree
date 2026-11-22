package com.mamxanh.app.presentation.ui
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.mamxanh.app.core.components.Cream
import com.mamxanh.app.core.components.GardenColorScheme
import com.mamxanh.app.core.components.GardenShapes
import com.mamxanh.app.core.components.GardenTypography
import com.mamxanh.app.core.components.MamXanhPreviewTheme
import com.mamxanh.app.feature.garden.presentation.ui.garden.GardenIntent
import com.mamxanh.app.feature.garden.presentation.ui.garden.GardenScreen
import com.mamxanh.app.feature.garden.presentation.ui.garden.GardenState
import com.mamxanh.app.feature.home.domain.model.HomeDashboard
import com.mamxanh.app.feature.home.domain.model.HomeTree
import com.mamxanh.app.feature.home.presentation.ui.home.HomeAction
import com.mamxanh.app.feature.home.presentation.ui.home.HomeScreen
import com.mamxanh.app.feature.home.presentation.ui.home.HomeState
import com.mamxanh.app.feature.inventory.presentation.ui.inventory.*
import com.mamxanh.app.feature.shop.domain.model.ShopWallet
import com.mamxanh.app.feature.shop.presentation.ui.shop.*
import com.mamxanh.app.feature.stats.presentation.ui.stats.StatsAction
import com.mamxanh.app.feature.stats.presentation.ui.stats.StatsScreen
import com.mamxanh.app.feature.stats.presentation.ui.stats.StatsState
import com.mamxanh.app.feature.tasks.presentation.ui.list.TasksAction
import com.mamxanh.app.feature.tasks.presentation.ui.list.TasksScreen
import com.mamxanh.app.feature.tasks.presentation.ui.list.TasksState
import com.mamxanh.app.feature.tree.presentation.ui.detail.TreeAction
import com.mamxanh.app.feature.tree.presentation.ui.detail.TreeInteractionScreen
import com.mamxanh.app.feature.tree.presentation.ui.detail.TreeState
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun MamXanhAppContent(
    state: AppUiState,
    dashboard: HomeState,
    tasks: TasksState,
    garden: GardenState,
    shop: ShopState,
    stats: StatsState,
    treeState: TreeState = TreeState(),
    modifier: Modifier = Modifier,
    onAppIntent: (AppIntent) -> Unit = {},
    onHomeIntent: (HomeAction) -> Unit = {},
    onTasksIntent: (TasksAction) -> Unit = {},
    onGardenIntent: (GardenIntent) -> Unit = {},
    onShopIntent: (ShopAction) -> Unit = {},
    onStatsIntent: (StatsAction) -> Unit = {},
    onTreeIntent: (TreeAction) -> Unit = {},
    onEngagement: (String, Long) -> Unit = { _, _ -> },
    engagementContent: (@Composable (Modifier) -> Unit)? = null,
    hideNavigation: Boolean = false,
    reducedMotion: Boolean = false,
    onLiving: (String, String) -> Unit = { _, _ -> },
    livingContent: (@Composable (Modifier) -> Unit)? = null,
    onDiscoveredSpecies: (List<String>) -> Unit = {},
) {
    val routeStates = rememberSaveableStateHolder()
    val app = state
    val gardenState = garden
    val walletState = shop
    val statistics = stats
    val wallet = shop.wallet
    MaterialTheme(colorScheme = GardenColorScheme, typography = GardenTypography, shapes = GardenShapes) {
        Scaffold(
            modifier = modifier,
            containerColor = Cream,
            bottomBar = {
                if (!hideNavigation) {
                    GardenTrailBar(app.destination, {
                        onEngagement("", 0)
                        onLiving("", "")
                        onTreeIntent(TreeAction.SetPanelExpanded(false))
                        onAppIntent(AppIntent.Navigate(it))
                    })
                }
            },
        ) { padding ->
            if (engagementContent != null) {
                engagementContent(Modifier.padding(padding))
            } else if (livingContent != null) {
                livingContent(Modifier.padding(padding))
            } else if (app.inventoryOpen) {
                InventoryScreen(
                    state =
                        InventoryUiState(
                            seeds =
                                com.mamxanh.shared.model.catalog.map {
                                    InventorySeed(it.id, it.name, wallet.inventory[it.id] ?: 0)
                                },
                            items =
                                com.mamxanh.shared.model.careProducts.map {
                                    InventoryItem(it.id, it.name, wallet.inventory[it.id] ?: 0, it.permanent, it.id == wallet.equippedCan)
                                },
                            coins = wallet.coins,
                            isLoading = shop.isLoading || garden.isLoading,
                        ),
                    onBack = { onAppIntent(AppIntent.Back) },
                    onSelectSeed = { onAppIntent(AppIntent.SelectInventorySeed(it)) },
                    onOpenShop = { onAppIntent(AppIntent.Navigate(AppDestination.SHOP)) },
                    targetPlotId = app.inventoryPlotId,
                    onEquip = { onShopIntent(ShopAction.Equip(it)) },
                    onLiving = onLiving,
                    modifier = Modifier.padding(padding),
                )
            } else {
                SharedTransitionLayout(Modifier.fillMaxSize().padding(padding)) {
                    val selectedTreeId = app.selectedTreeId
                    val homeTreeId =
                        dashboard.dashboard.trees
                            .firstOrNull()
                            ?.id
                    AnimatedContent(
                        targetState = app.destination to selectedTreeId,
                        transitionSpec = {
                            val (fromDestination, fromTreeId) = initialState
                            val (toDestination, toTreeId) = targetState
                            if (reducedMotion) {
                                EnterTransition.None togetherWith ExitTransition.None
                            } else if (fromTreeId == null && toTreeId != null) {
                                EnterTransition.None togetherWith ExitTransition.None
                            } else if (fromTreeId != null && toTreeId == null) {
                                EnterTransition.None togetherWith ExitTransition.None
                            } else {
                                val direction =
                                    if (AppDestination.entries.indexOf(toDestination) >=
                                        AppDestination.entries.indexOf(fromDestination)
                                    ) {
                                        1
                                    } else {
                                        -1
                                    }
                                (slideInHorizontally(tween(260)) { direction * it / 12 } + fadeIn(tween(220))) togetherWith
                                    (slideOutHorizontally(tween(260)) { -direction * it / 12 } + fadeOut(tween(180)))
                            }
                        },
                        label = "garden-route",
                    ) { (destination, routeTreeId) ->
                        val routeAnimationScope = this
                        val routeKey = routeTreeId?.let { "tree-$it" } ?: "destination-${destination.name}"
                        routeStates.SaveableStateProvider(routeKey) {
                            val routeTree = garden.garden.trees.firstOrNull { it.id == routeTreeId }
                            val treeSharedModifier = Modifier
                            when {
                                routeTree != null ->
                                    TreeInteractionScreen(
                                        tree =
                                            com.mamxanh.shared.model.Tree(
                                                id = routeTree.id,
                                                speciesId = routeTree.speciesId,
                                                name = routeTree.name,
                                                growth = routeTree.growth,
                                                planted = routeTree.planted,
                                                decoration = routeTree.decoration,
                                                wateredToday = routeTree.wateredToday,
                                            ),
                                        state = treeState,
                                        fertilizerCount =
                                            com.mamxanh.shared.model.FertilizerTier.entries
                                                .sumOf { wallet.inventory[it.itemId] ?: 0 },
                                        inventory = wallet.inventory,
                                        onIntent = { onTreeIntent(it) },
                                        modifier = Modifier,
                                        sharedTreeModifier = treeSharedModifier,
                                        coins = wallet.coins,
                                        onOpenDecorations = { onLiving("INVENTORY", "") },
                                        onOpenGarden = {
                                            onTreeIntent(TreeAction.SetPanelExpanded(false))
                                            onAppIntent(AppIntent.Navigate(AppDestination.GARDEN))
                                        },
                                        onPrevious =
                                            garden.garden.trees.takeIf { it.size > 1 }?.let { trees ->
                                                {
                                                    val index = trees.indexOfFirst { it.id == routeTree.id }
                                                    onAppIntent(AppIntent.OpenTree(trees[(index - 1 + trees.size) % trees.size].id))
                                                }
                                            },
                                        onNext =
                                            garden.garden.trees.takeIf { it.size > 1 }?.let { trees ->
                                                {
                                                    val index = trees.indexOfFirst { it.id == routeTree.id }
                                                    onAppIntent(AppIntent.OpenTree(trees[(index + 1) % trees.size].id))
                                                }
                                            },
                                    )
                                else ->
                                    when (destination) {
                                        AppDestination.HOME ->
                                            HomeScreen(
                                                state = dashboard,
                                                onIntent = onHomeIntent,
                                                fertilizerCount =
                                                    com.mamxanh.shared.model.FertilizerTier.entries
                                                        .sumOf { wallet.inventory[it.itemId] ?: 0 },
                                                modifier = Modifier,
                                                sharedTreeModifier = treeSharedModifier,
                                                onWeeklyReview = { onEngagement("REVIEW", 0) },
                                                onJourney = { onEngagement("MORE", 0) },
                                                taskTitle =
                                                    tasks.tasks.firstOrNull { !it.doneToday && !it.finished }?.title
                                                        ?: AppText.text(TextKey.CopyMoiViecDaXongHomNay02350c),
                                                canClaimBonus = tasks.canClaimDailyBonus && !tasks.isDailyBonusClaimed,
                                                onTaskAction = { id, timer ->
                                                    if (timer) {
                                                        onAppIntent(AppIntent.Navigate(AppDestination.TASKS))
                                                        onEngagement("FOCUS", id)
                                                    } else {
                                                        onTasksIntent(TasksAction.Complete(id))
                                                    }
                                                },
                                                onClaimBonus = { onTasksIntent(TasksAction.ClaimBonus) },
                                            )
                                        AppDestination.TASKS ->
                                            TasksScreen(
                                                tasks,
                                                onTasksIntent,
                                                Modifier,
                                                coins = wallet.coins,
                                                onEngagement = onEngagement,
                                            )
                                        AppDestination.GARDEN ->
                                            GardenScreen(
                                                gardenState,
                                                onGardenIntent,
                                                Modifier,
                                                onOpenInventory = { onAppIntent(AppIntent.OpenInventory(it)) },
                                                plantingSeedId = app.plantingSeedId,
                                                plantingPlotId = app.inventoryPlotId,
                                                onDismissPlanting = { onAppIntent(AppIntent.DismissPlanting) },
                                                treeTransitionModifier = { Modifier },
                                                onLiving = onLiving,
                                            )
                                        AppDestination.SHOP ->
                                            ShopScreen(
                                                walletState,
                                                onShopIntent,
                                                Modifier,
                                                onLiving = onLiving,
                                                onDiscovered = onDiscoveredSpecies,
                                            )
                                        AppDestination.STATS ->
                                            StatsScreen(
                                                statistics,
                                                onStatsIntent,
                                                Modifier,
                                                onEngagement = { onEngagement(it, 0) },
                                            )
                                    }
                            }
                        }
                    }
                }
            }
        }
        if (app.message.isNotBlank()) {
            GardenMessageDialog(
                message = app.message,
                onDismiss = { onAppIntent(AppIntent.DismissMessage) },
            )
        }
    }
}

@Preview(name = "App shell · home", showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun MamXanhAppContentHomePreview() {
    var route by remember { mutableStateOf(AppUiState()) }

    fun navigate(intent: AppIntent) {
        route =
            when (intent) {
                is AppIntent.Navigate ->
                    if (intent.destination == route.destination && route.selectedTreeId == null) {
                        route
                    } else {
                        route.copy(
                            destination = intent.destination,
                            selectedTreeId = null,
                            backStack =
                                if (intent.destination ==
                                    route.destination
                                ) {
                                    route.backStack
                                } else {
                                    route.backStack + route.destination
                                },
                        )
                    }
                is AppIntent.OpenTree -> route.copy(selectedTreeId = intent.id)
                AppIntent.Back ->
                    if (route.selectedTreeId != null) {
                        route.copy(selectedTreeId = null)
                    } else if (route.backStack.isNotEmpty()) {
                        route.copy(
                            destination = route.backStack.last(),
                            backStack = route.backStack.dropLast(1),
                        )
                    } else {
                        route
                    }
                is AppIntent.ShowMessage -> route.copy(message = intent.message)
                AppIntent.DismissMessage -> route.copy(message = "")
                is AppIntent.OpenInventory -> route.copy(inventoryOpen = true, inventoryPlotId = intent.plotId)
                is AppIntent.SelectInventorySeed ->
                    route.copy(
                        inventoryOpen = false,
                        destination = AppDestination.GARDEN,
                        plantingSeedId = intent.seedId,
                    )
                AppIntent.DismissPlanting -> route.copy(plantingSeedId = null, inventoryPlotId = null)
            }
    }
    MamXanhPreviewTheme {
        MamXanhAppContent(
            state = route,
            dashboard =
                HomeState(
                    HomeDashboard(
                        listOf(HomeTree(1L, "cherry", AppText.text(TextKey.CopyDaoHongXuanC2fd99), 68, "FLOWERS", false)),
                        2,
                        4,
                        coins = 120,
                        equippedCan = "can_basic",
                    ),
                    false,
                ),
            tasks = TasksState(isLoading = false),
            garden =
                GardenState(
                    com.mamxanh.app.feature.garden.domain.model.Garden(
                        trees =
                            listOf(
                                com.mamxanh.app.feature.garden.domain.model.Tree(
                                    1L,
                                    "cherry",
                                    AppText.text(TextKey.CopyDaoHongXuanC2fd99),
                                    68,
                                    "2026-11-01",
                                    "FLOWERS",
                                ),
                            ),
                    ),
                    false,
                ),
            shop = ShopState(ShopWallet(120, mapOf("fertilizer" to 2)), false),
            stats = StatsState(isLoading = false),
            onAppIntent = ::navigate,
            onHomeIntent = {
                when (it) {
                    is HomeAction.OpenTree -> navigate(AppIntent.OpenTree(it.id))
                    HomeAction.OpenTasks -> navigate(AppIntent.Navigate(AppDestination.TASKS))
                    HomeAction.OpenGarden -> navigate(AppIntent.Navigate(AppDestination.GARDEN))
                    HomeAction.OpenShop -> navigate(AppIntent.Navigate(AppDestination.SHOP))
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
            onTreeIntent = { if (it == TreeAction.Back) navigate(AppIntent.Back) },
        )
    }
}

@Preview(name = "App shell · garden tab", showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun MamXanhAppContentGardenPreview() {
    MamXanhPreviewTheme {
        MamXanhAppContent(
            state = AppUiState(destination = AppDestination.GARDEN),
            dashboard = HomeState(isLoading = false),
            tasks = TasksState(isLoading = false),
            garden = GardenState(isLoading = false),
            shop = ShopState(ShopWallet(0, emptyMap()), false),
            stats = StatsState(isLoading = false),
        )
    }
}
