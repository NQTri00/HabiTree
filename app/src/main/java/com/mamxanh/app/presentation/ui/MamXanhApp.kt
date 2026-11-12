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
    appVm: AppViewModel,
    engagementVm: EngagementViewModel,
    incomingEntity: Long? = null,
    onIncomingHandled: ()->Unit = {}
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
    val engagement by engagementVm.state.collectAsStateWithLifecycle()
    var permissionExplanation by remember { mutableStateOf(false) }
    val requestPrefs=remember(context){context.getSharedPreferences("engagement_permissions",android.content.Context.MODE_PRIVATE)}
    val permission=androidx.activity.compose.rememberLauncherForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.RequestPermission()) { granted ->
        appVm.dispatch(AppIntent.ShowMessage(if(granted)"Đã bật quyền thông báo" else "Thông báo đang tắt. Việc và bộ đếm vẫn hoạt động."))
    }
    LaunchedEffect(tasks.tasks,engagement.snapshot.settings,engagement.snapshot.reminders,
        gardenState.garden.trees.map{Triple(it.id,it.status.wateredUntil,it.status.alive)}) {
        kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
            com.mamxanh.app.data.TaskReminders.schedule(context,tasks.tasks)
        }
    }
    LaunchedEffect(incomingEntity,tasks.isLoading) {
        val id=incomingEntity
        if(id!=null && !tasks.isLoading) {
            if(id<0)appVm.dispatch(AppIntent.OpenTree(-id))
            else {
                val task=tasks.tasks.firstOrNull{it.id==id}
                if(task!=null)engagementVm.open(if(task.config.verification=="TIMER")"FOCUS" else "INSIGHTS",id)
            }
            onIncomingHandled()
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
    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            while(true) {
                gardenVm.dispatch(GardenIntent.Refresh)
                homeVm.dispatch(HomeIntent.Refresh)
                kotlinx.coroutines.delay(15_000)
            }
        }
    }
    LaunchedEffect(tasks.phase.days.firstOrNull()?.date) {
        homeVm.dispatch(HomeIntent.Refresh)
        statsVm.dispatch(StatsIntent.Refresh)
    }
    DisposableEffect(lifecycleOwner) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _,event ->
            if(event==Lifecycle.Event.ON_START){tasksVm.dispatch(TasksIntent.Foreground(true));engagementVm.foreground(true)}
            if(event==Lifecycle.Event.ON_STOP){tasksVm.dispatch(TasksIntent.Foreground(false));engagementVm.foreground(false)}
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        engagementVm.foreground(lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED))
        tasksVm.dispatch(TasksIntent.Foreground(lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)))
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            engagementVm.foreground(false)
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
                    TreeEffect.OpenFertilizerShop -> {
                        shopVm.dispatch(ShopIntent.SetFilter(ShopFilter.ITEMS))
                        appVm.dispatch(AppIntent.Back)
                        appVm.dispatch(AppIntent.Navigate(AppDestination.SHOP))
                    }
                    TreeEffect.TreeChanged -> Unit
                }
            }
        }
    }

    val canGoBack = engagement.route.isNotBlank() || app.inventoryOpen || app.selectedTreeId != null || app.backStack.isNotEmpty()
    BackHandler(enabled = canGoBack) {
        if(engagement.route.isNotBlank())engagementVm.open("")
        else if (app.inventoryOpen) appVm.dispatch(AppIntent.Back)
        else if (app.selectedTreeId != null) treeVm.dispatch(TreeIntent.Back) else appVm.dispatch(AppIntent.Back)
    }
    if(permissionExplanation) {
        val activity=context as? android.app.Activity
        val granted=android.os.Build.VERSION.SDK_INT<33 ||
            context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)==android.content.pm.PackageManager.PERMISSION_GRANTED
        val rationale=android.os.Build.VERSION.SDK_INT>=33 &&
            activity?.shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS)==true
        val settings=granted || requestPrefs.getBoolean("requested",false) && !rationale
        com.mamxanh.app.presentation.ui.components.MintDialog("Nhắc việc tùy chọn",
            {permissionExplanation=false},if(settings)"Mở cài đặt" else "Tiếp tục",{
                permissionExplanation=false
                if(settings)context.startActivity(android.content.Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    android.net.Uri.fromParts("package",context.packageName,null)))
                else {
                    requestPrefs.edit().putBoolean("requested",true).apply()
                    if(android.os.Build.VERSION.SDK_INT>=33)permission.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                }
            }) {
            Text("Thông báo chỉ nhắc những việc bạn đã chọn, tôn trọng giờ yên lặng và có thể tắt bất cứ lúc nào. Từ chối vẫn dùng được mọi việc và phiên tập trung.")
        }
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
        onTreeIntent = treeVm::dispatch,
        onEngagement = engagementVm::open,
        hideNavigation=engagement.route=="ONBOARD",
        reducedMotion=engagement.snapshot.settings.reducedMotion,
        engagementContent=if(engagement.route.isBlank())null else { contentModifier ->
            EngagementScreen(engagement,engagementVm::open,engagementVm::act,engagementVm::adjust,
                onPermission={permissionExplanation=true},
                onEntity={event->
                    if(event.type.startsWith("TREE") && gardenState.garden.trees.any{it.id==event.entity}) {
                        engagementVm.open("");appVm.dispatch(AppIntent.OpenTree(event.entity))
                    } else if(!event.type.startsWith("TREE") && tasks.tasks.any{it.id==event.entity})engagementVm.open("INSIGHTS",event.entity)
                    else appVm.dispatch(AppIntent.ShowMessage("Mục này đã được lưu trong lịch sử."))
                },onDismissMessage=engagementVm::dismissMessage,modifier=contentModifier)
        }
    )
}
