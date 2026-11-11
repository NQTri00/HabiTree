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
    val lifecycleOwner = LocalLifecycleOwner.current
    val storage = org.koin.compose.koinInject<com.mamxanh.shared.data.GameStorage>()
    val context = androidx.compose.ui.platform.LocalContext.current
    val permission = androidx.activity.compose.rememberLauncherForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.RequestPermission()) {}
    LaunchedEffect(tasks.tasks) {
        kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
            com.mamxanh.app.data.TaskReminders.schedule(context,tasks.tasks)
        }
        if(android.os.Build.VERSION.SDK_INT>=33 && tasks.tasks.any{it.config.reminderMinute>=0}) {
            val prefs=context.getSharedPreferences("reminder_permission",android.content.Context.MODE_PRIVATE)
            if(!prefs.getBoolean("requested",false)) {
                prefs.edit().putBoolean("requested",true).apply()
                permission.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
    LaunchedEffect(storage,lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            storage.changes.collect {
                tasksVm.dispatch(TasksIntent.Refresh)
                gardenVm.dispatch(GardenIntent.Refresh)
                shopVm.dispatch(ShopIntent.Refresh)
                statsVm.dispatch(StatsIntent.Refresh)
                homeVm.dispatch(HomeIntent.Refresh)
            }
        }
    }
    LaunchedEffect(tasks.phase.days.firstOrNull()?.date) {
        homeVm.dispatch(HomeIntent.Refresh)
        statsVm.dispatch(StatsIntent.Refresh)
    }
    DisposableEffect(lifecycleOwner) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _,event ->
            if(event==Lifecycle.Event.ON_START)tasksVm.dispatch(TasksIntent.Foreground(true))
            if(event==Lifecycle.Event.ON_STOP)tasksVm.dispatch(TasksIntent.Foreground(false))
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        tasksVm.dispatch(TasksIntent.Foreground(lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)))
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            tasksVm.dispatch(TasksIntent.Foreground(false))
        }
    }

    LaunchedEffect(tasksVm, lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            tasksVm.effects.collect { effect ->
                when (effect) {
                    is TasksEffect.Feedback -> appVm.dispatch(AppIntent.ShowMessage(effect.message))
                    TasksEffect.DataChanged -> Unit // Shared storage revisions drive every snapshot.
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
                    HomeEffect.OpenShop -> appVm.dispatch(AppIntent.Navigate(AppDestination.SHOP))
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
                    GardenEffect.DataChanged -> Unit
                    is GardenEffect.OpenTree -> appVm.dispatch(AppIntent.OpenTree(effect.id))
                    GardenEffect.OpenShop -> appVm.dispatch(AppIntent.Navigate(AppDestination.SHOP))
                }
            }
        }
    }
    LaunchedEffect(shopVm, lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            shopVm.effects.collect { effect ->
                when (effect) {
                    is ShopEffect.Feedback -> appVm.dispatch(AppIntent.ShowMessage(effect.message))
                    ShopEffect.InventoryChanged -> Unit
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
                    TreeEffect.Back -> appVm.dispatch(AppIntent.Back)
                    TreeEffect.OpenWaterSupply -> {
                        appVm.dispatch(AppIntent.Back)
                        appVm.dispatch(AppIntent.Navigate(AppDestination.HOME))
                    }
                    TreeEffect.OpenFertilizerShop -> {
                        appVm.dispatch(AppIntent.Back)
                        appVm.dispatch(AppIntent.Navigate(AppDestination.SHOP))
                    }
                    TreeEffect.TreeChanged -> Unit
                }
            }
        }
    }

    val canGoBack = app.inventoryOpen || app.selectedTreeId != null || app.backStack.isNotEmpty()
    BackHandler(enabled = canGoBack) {
        if (app.inventoryOpen) appVm.dispatch(AppIntent.Back)
        else if (app.selectedTreeId != null) treeVm.dispatch(TreeIntent.Back) else appVm.dispatch(AppIntent.Back)
    }
    MamXanhAppContent(
        state = app,
        dashboard = dashboard,
        tasks = tasks,
        garden = gardenState,
        shop = walletState,
        stats = statistics,
        treeState = treeState,
        onAppIntent = appVm::dispatch,
        onHomeIntent = homeVm::dispatch,
        onTasksIntent = tasksVm::dispatch,
        onGardenIntent = gardenVm::dispatch,
        onShopIntent = shopVm::dispatch,
        onStatsIntent = statsVm::dispatch,
        onTreeIntent = treeVm::dispatch
    )
}
