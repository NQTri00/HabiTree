package com.mamxanh.app.presentation.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Forest
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Insights
import androidx.compose.material.icons.rounded.Store
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mamxanh.app.feature.garden.presentation.*
import com.mamxanh.app.feature.home.presentation.*
import com.mamxanh.app.feature.shop.presentation.*
import com.mamxanh.app.feature.stats.presentation.*
import com.mamxanh.app.feature.tasks.presentation.*
import com.mamxanh.app.feature.tree.domain.model.Tree as FeatureTree
import com.mamxanh.app.feature.tree.presentation.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun MamXanhApp(
    tasksVm: TasksViewModel,
    gardenVm: GardenViewModel,
    shopVm: ShopViewModel,
    treeVm: TreeViewModel,
    statsVm: StatsViewModel,
    homeVm: HomeViewModel,
    appVm: AppViewModel
) {
    val tasks by tasksVm.uiState.collectAsStateWithLifecycle()
    val gardenState by gardenVm.uiState.collectAsStateWithLifecycle()
    val walletState by shopVm.uiState.collectAsStateWithLifecycle()
    val statistics by statsVm.uiState.collectAsStateWithLifecycle()
    val dashboard by homeVm.uiState.collectAsStateWithLifecycle()
    val treeState by treeVm.uiState.collectAsStateWithLifecycle()
    val app by appVm.uiState.collectAsStateWithLifecycle()
    val garden = gardenState.garden
    val wallet = walletState.wallet
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(tasksVm, lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            tasksVm.effects.collect { effect ->
                when (effect) {
                    is TasksEffect.Feedback -> appVm.dispatch(AppIntent.ShowMessage(effect.message))
                    TasksEffect.DataChanged -> {
                        gardenVm.dispatch(GardenIntent.Refresh)
                        statsVm.dispatch(StatsIntent.Refresh)
                        shopVm.dispatch(ShopIntent.Refresh)
                        homeVm.dispatch(HomeIntent.Refresh)
                    }
                }
            }
        }
    }
    LaunchedEffect(homeVm, lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            homeVm.effects.collect { effect ->
                when (effect) {
                    is HomeEffect.OpenTree -> appVm.dispatch(AppIntent.OpenTree(effect.id))
                    is HomeEffect.Feedback -> appVm.dispatch(AppIntent.ShowMessage(effect.message))
                    HomeEffect.OpenTasks -> appVm.dispatch(AppIntent.Navigate(AppDestination.TASKS))
                    HomeEffect.OpenGarden -> appVm.dispatch(AppIntent.Navigate(AppDestination.GARDEN))
                    is HomeEffect.WaterTree -> treeVm.dispatch(TreeIntent.Water(effect.id))
                    is HomeEffect.FertilizeTree -> treeVm.dispatch(TreeIntent.Fertilize(effect.id))
                }
            }
        }
    }
    LaunchedEffect(gardenVm, lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            gardenVm.effects.collect { effect ->
                when (effect) {
                    GardenEffect.DataChanged -> {
                        shopVm.dispatch(ShopIntent.Refresh)
                        statsVm.dispatch(StatsIntent.Refresh)
                        homeVm.dispatch(HomeIntent.Refresh)
                    }
                    is GardenEffect.OpenTree -> appVm.dispatch(AppIntent.OpenTree(effect.id))
                }
            }
        }
    }
    LaunchedEffect(shopVm, lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            shopVm.effects.collect { effect ->
                when (effect) {
                    is ShopEffect.Feedback -> appVm.dispatch(AppIntent.ShowMessage(effect.message))
                    ShopEffect.InventoryChanged -> {
                        gardenVm.dispatch(GardenIntent.Refresh)
                        homeVm.dispatch(HomeIntent.Refresh)
                        statsVm.dispatch(StatsIntent.Refresh)
                    }
                    ShopEffect.OpenGarden -> appVm.dispatch(AppIntent.Navigate(AppDestination.GARDEN))
                }
            }
        }
    }
    LaunchedEffect(treeVm, lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            treeVm.effects.collect { effect ->
                when (effect) {
                    is TreeEffect.Feedback -> appVm.dispatch(AppIntent.ShowMessage(effect.message))
                    TreeEffect.TreeChanged -> {
                        gardenVm.dispatch(GardenIntent.Refresh)
                        homeVm.dispatch(HomeIntent.Refresh)
                        statsVm.dispatch(StatsIntent.Refresh)
                        shopVm.dispatch(ShopIntent.Refresh)
                    }
                }
            }
        }
    }

    val selectedTree = garden.trees.firstOrNull { it.id == app.selectedTreeId }
    val canGoBack = app.selectedTreeId != null || app.backStack.isNotEmpty()
    BackHandler(enabled = canGoBack) { appVm.dispatch(AppIntent.Back) }

    MaterialTheme(colorScheme = lightColorScheme(primary = Green, secondary = Color(0xFFFFA01A), background = Cream, surface = Color.White)) {
        Scaffold(
            containerColor = Cream,
            topBar = {
                if (selectedTree != null || app.destination != AppDestination.HOME) {
                    TopAppBar(
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = if (app.destination == AppDestination.SHOP) PinkTileBg else Cream),
                        navigationIcon = { if (canGoBack) IconButton(onClick = { appVm.dispatch(AppIntent.Back) }) { Icon(Icons.Rounded.ArrowBack, contentDescription = "Quay lại") } },
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
                if (selectedTree == null) Surface(color = Color.White.copy(alpha = .96f), shadowElevation = 8.dp, shape = androidx.compose.foundation.shape.RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)) {
                    NavigationBar(containerColor = Color.Transparent, tonalElevation = 0.dp) {
                        AppDestination.entries.forEach { item ->
                            val icon = when (item) {
                                AppDestination.HOME -> Icons.Rounded.Home
                                AppDestination.TASKS -> Icons.Rounded.Check
                                AppDestination.GARDEN -> Icons.Rounded.Forest
                                AppDestination.SHOP -> Icons.Rounded.Store
                                AppDestination.STATS -> Icons.Rounded.Insights
                            }
                            NavigationBarItem(selected = app.destination == item, onClick = { appVm.dispatch(AppIntent.Navigate(item)) },
                                icon = { Icon(icon, contentDescription = item.label) }, label = { Text(item.label, fontWeight = if (app.destination == item) FontWeight.Bold else FontWeight.Normal) },
                                colors = NavigationBarItemDefaults.colors(selectedIconColor = Green, selectedTextColor = Green, indicatorColor = Color(0xFFD6EADC)))
                        }
                    }
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
                ) { (destination, _) ->
                    val treeSharedModifier = when {
                        selectedTree != null -> Modifier.sharedElement(
                            rememberSharedContentState(key = "tree-${selectedTree.id}"),
                            animatedVisibilityScope = this
                        )
                        homeTreeId != null -> Modifier.sharedElement(
                            rememberSharedContentState(key = "tree-$homeTreeId"),
                            animatedVisibilityScope = this
                        )
                        else -> Modifier
                    }
                    when {
                        selectedTree != null -> TreeInteractionScreen(
                            tree = FeatureTree(selectedTree.id, selectedTree.speciesId, selectedTree.name, selectedTree.growth, selectedTree.planted, selectedTree.decoration, selectedTree.wateredToday),
                            state = treeState,
                            fertilizerCount = wallet.inventory["fertilizer"] ?: 0,
                            wateringCanMl = dashboard.dashboard.wateringCanMl,
                            onIntent = { treeVm.dispatch(it) },
                            modifier = Modifier.padding(padding),
                            sharedTreeModifier = treeSharedModifier
                        )
                        else -> when (destination) {
                            AppDestination.HOME -> HomeScreen(state = dashboard, onIntent = homeVm::dispatch,
                                fertilizerCount = wallet.inventory["fertilizer"] ?: 0, modifier = Modifier.padding(padding),
                                sharedTreeModifier = treeSharedModifier)
                            AppDestination.TASKS -> TasksScreen(tasks, tasksVm::dispatch, Modifier.padding(padding))
                            AppDestination.GARDEN -> GardenScreen(gardenState, gardenVm::dispatch, Modifier.padding(padding))
                            AppDestination.SHOP -> ShopScreen(walletState, shopVm::dispatch, Modifier.padding(padding))
                            AppDestination.STATS -> StatsScreen(statistics, statsVm::dispatch, Modifier.padding(padding))
                        }
                    }
                }
            }
        }
        if (app.message.isNotBlank()) {
            GardenMessageDialog(
                message = app.message,
                onDismiss = { appVm.dispatch(AppIntent.DismissMessage) }
            )
        }
    }
}
