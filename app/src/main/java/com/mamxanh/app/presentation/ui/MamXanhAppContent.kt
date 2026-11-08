package com.mamxanh.app.presentation.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mamxanh.app.feature.garden.presentation.*
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
    val app = state
    val gardenState = garden
    val walletState = shop
    val statistics = stats
    val wallet = shop.wallet
    val selectedTree = garden.garden.trees.firstOrNull { it.id == state.selectedTreeId }
    val canGoBack = state.selectedTreeId != null || state.backStack.isNotEmpty()
    MaterialTheme(colorScheme = lightColorScheme(primary = Green, secondary = Color(0xFFFFA01A), background = Cream, surface = Color.White)) {
        Scaffold(
            modifier = modifier,
            containerColor = Cream,
            topBar = {
                if (selectedTree != null || app.destination != AppDestination.HOME) {
                    TopAppBar(
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = if (app.destination == AppDestination.SHOP) PinkTileBg else Cream),
                        navigationIcon = { if (canGoBack) IconButton(onClick = { onAppIntent(AppIntent.Back) }) { Icon(Icons.Rounded.ArrowBack, contentDescription = "Quay lại") } },
                        title = {
                            androidx.compose.foundation.layout.Column {
                                Text(selectedTree?.name ?: app.destination.label, fontWeight = FontWeight.ExtraBold, color = Deep)
                                Text(if (selectedTree != null) "Chăm sóc mầm xanh" else when (app.destination) { AppDestination.HOME -> "Nuôi dưỡng thói quen, lớn lên cùng cây"; AppDestination.TASKS -> "Hoàn thành từng việc nhỏ mỗi ngày"; AppDestination.GARDEN -> "Những người bạn xanh của bạn"; AppDestination.SHOP -> "Chọn thêm mầm xanh mới"; AppDestination.STATS -> "Nhìn lại hành trình của khu vườn" }, style = MaterialTheme.typography.labelSmall, color = Green)
                            }
                        },
                        actions = {
                            Surface(color = Gold, shape = androidx.compose.foundation.shape.RoundedCornerShape(50), modifier = Modifier.padding(end = 14.dp)) {
                                Text("🪙 " + wallet.coins, modifier = Modifier.padding(horizontal = 13.dp, vertical = 7.dp), color = Deep, fontWeight = FontWeight.ExtraBold)
                            }
                        }
                    )
                }
            },
            bottomBar = {
                if (selectedTree == null && app.destination != AppDestination.HOME) {
                    GardenTrailBar(app.destination, { onAppIntent(AppIntent.Navigate(it)) })
                }
            }
        ) { padding ->
            SharedTransitionLayout {
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
                            modifier = Modifier.padding(padding),
                            sharedTreeModifier = treeSharedModifier,
                            developmentControls = true
                        )
                        else -> when (destination) {
                            AppDestination.HOME -> HomeScreen(state = dashboard, onIntent = onHomeIntent,
                                fertilizerCount = wallet.inventory["fertilizer"] ?: 0, modifier = Modifier.padding(padding),
                                sharedTreeModifier = treeSharedModifier,
                                onOpenPlace = { place ->
                                    AppDestination.entries.firstOrNull { it.name == place }?.let {
                                        onAppIntent(AppIntent.Navigate(it))
                                    }
                                })
                            AppDestination.TASKS -> TasksScreen(tasks, onTasksIntent, Modifier.padding(padding))
                            AppDestination.GARDEN -> GardenScreen(gardenState, onGardenIntent, Modifier.padding(padding))
                            AppDestination.SHOP -> ShopScreen(walletState, onShopIntent, Modifier.padding(padding))
                            AppDestination.STATS -> StatsScreen(statistics, onStatsIntent, Modifier.padding(padding))
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
            is AppIntent.Navigate -> if (intent.destination == route.destination) route else route.copy(
                destination = intent.destination, backStack = route.backStack + route.destination
            )
            is AppIntent.OpenTree -> route.copy(selectedTreeId = intent.id)
            AppIntent.Back -> if (route.selectedTreeId != null) route.copy(selectedTreeId = null)
                else if (route.backStack.isNotEmpty()) route.copy(
                    destination = route.backStack.last(), backStack = route.backStack.dropLast(1)
                ) else route
            is AppIntent.ShowMessage -> route.copy(message = intent.message)
            AppIntent.DismissMessage -> route.copy(message = "")
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
                    else -> Unit
                }
            },
            onGardenIntent = {
                if (it is GardenIntent.OpenTree) navigate(AppIntent.OpenTree(it.id))
            }
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
