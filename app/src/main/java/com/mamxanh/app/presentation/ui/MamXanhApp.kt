package com.mamxanh.app.presentation.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Forest
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Insights
import androidx.compose.material.icons.rounded.Store
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mamxanh.app.feature.garden.presentation.GardenScreen
import com.mamxanh.app.feature.garden.presentation.GardenViewModel
import com.mamxanh.app.feature.home.presentation.HomeScreen
import com.mamxanh.app.feature.home.presentation.HomeViewModel
import com.mamxanh.app.feature.shop.presentation.ShopScreen
import com.mamxanh.app.feature.shop.presentation.ShopViewModel
import com.mamxanh.app.feature.stats.presentation.StatsScreen
import com.mamxanh.app.feature.stats.presentation.StatsViewModel
import com.mamxanh.app.feature.tasks.presentation.TasksScreen
import com.mamxanh.app.feature.tasks.presentation.TasksViewModel
import com.mamxanh.app.feature.tasks.presentation.dialog.AddTaskDialog
import com.mamxanh.app.feature.tree.domain.model.Tree as FeatureTree
import com.mamxanh.app.feature.tree.presentation.TreeInteractionScreen
import com.mamxanh.app.feature.tree.presentation.TreeViewModel

private enum class AppDestination(val label: String) {
    HOME("Trang chủ"), TASKS("Nhiệm vụ"), GARDEN("Khu vườn"), SHOP("Cửa hàng"), STATS("Thống kê")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MamXanhApp(
    tasksVm: TasksViewModel,
    gardenVm: GardenViewModel,
    shopVm: ShopViewModel,
    treeVm: TreeViewModel,
    statsVm: StatsViewModel,
    homeVm: HomeViewModel
) {
    val tasks by tasksVm.tasks.collectAsState()
    val garden by gardenVm.garden.collectAsState()
    val wallet by shopVm.wallet.collectAsState()
    val statistics by statsVm.statistics.collectAsState()
    val dashboard by homeVm.dashboard.collectAsState()
    val taskMessage by tasksVm.message.collectAsState()
    val shopMessage by shopVm.message.collectAsState()
    val treeMessage by treeVm.message.collectAsState()

    var destination by remember { mutableStateOf(AppDestination.HOME) }
    var selectedTreeId by remember { mutableStateOf<Long?>(null) }
    var adding by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }

    LaunchedEffect(tasks, garden, wallet, statistics) {
        homeVm.refresh()
    }
    LaunchedEffect(tasks, garden) {
        statsVm.refresh()
        shopVm.refresh()
    }
    LaunchedEffect(taskMessage) { if (taskMessage.isNotBlank()) message = taskMessage }
    LaunchedEffect(shopMessage) { if (shopMessage.isNotBlank()) message = shopMessage }
    LaunchedEffect(treeMessage) { if (treeMessage.isNotBlank()) message = treeMessage }

    val selectedTree = garden.trees.firstOrNull { it.id == selectedTreeId }
    MaterialTheme(colorScheme = lightColorScheme(primary = Green, secondary = Color(0xFF9A6A31), background = Cream, surface = Color.White)) {
        Scaffold(
            containerColor = Cream,
            topBar = {
                TopAppBar(
                    navigationIcon = {
                        if (selectedTree != null) {
                            IconButton(onClick = { selectedTreeId = null }) {
                                Icon(Icons.Rounded.Home, contentDescription = "Quay lại")
                            }
                        }
                    },
                    title = {
                        androidx.compose.foundation.layout.Column {
                            Text(selectedTree?.name ?: "Mầm Xanh", fontWeight = FontWeight.ExtraBold, color = Deep)
                            Text(if (selectedTree != null) "Chăm sóc cây" else "Nuôi dưỡng thói quen, lớn lên cùng cây",
                                style = MaterialTheme.typography.labelSmall, color = Green)
                        }
                    },
                    actions = {
                        Surface(color = Gold, shape = androidx.compose.foundation.shape.RoundedCornerShape(50), modifier = Modifier.padding(end = 14.dp)) {
                            Text("🪙 " + wallet.coins, modifier = Modifier.padding(horizontal = 13.dp, vertical = 8.dp),
                                color = Deep, fontWeight = FontWeight.Bold)
                        }
                    }
                )
            },
            floatingActionButton = {
                if (selectedTree == null && destination == AppDestination.TASKS) {
                    FloatingActionButton(onClick = { adding = true }, containerColor = Green, contentColor = Color.White) {
                        Icon(Icons.Rounded.Check, contentDescription = "Thêm nhiệm vụ")
                    }
                }
            },
            bottomBar = {
                if (selectedTree == null) {
                    NavigationBar(containerColor = Color.White) {
                        AppDestination.entries.forEach { item ->
                            val icon = when (item) {
                                AppDestination.HOME -> Icons.Rounded.Home
                                AppDestination.TASKS -> Icons.Rounded.Check
                                AppDestination.GARDEN -> Icons.Rounded.Forest
                                AppDestination.SHOP -> Icons.Rounded.Store
                                AppDestination.STATS -> Icons.Rounded.Insights
                            }
                            NavigationBarItem(
                                selected = destination == item,
                                onClick = { destination = item },
                                icon = { Icon(icon, contentDescription = item.label) },
                                label = { Text(item.label) }
                            )
                        }
                    }
                }
            }
        ) { padding ->
            when {
                selectedTree != null -> TreeInteractionScreen(
                    tree = FeatureTree(selectedTree.id, selectedTree.speciesId, selectedTree.name, selectedTree.growth,
                        selectedTree.planted, selectedTree.decoration, selectedTree.wateredToday),
                    fertilizerCount = wallet.inventory["fertilizer"] ?: 0,
                    onWater = { treeVm.water(selectedTree.id) },
                    onFertilize = { treeVm.fertilize(selectedTree.id) },
                    onDecorate = { treeVm.decorate(selectedTree.id, it) },
                    modifier = Modifier.padding(padding)
                )
                else -> when (destination) {
                    AppDestination.HOME -> HomeScreen(
                        dashboard = dashboard,
                        onOpenTree = { selectedTreeId = it },
                        onOpenTasks = { destination = AppDestination.TASKS },
                        onOpenGarden = { destination = AppDestination.GARDEN },
                        modifier = Modifier.padding(padding)
                    )
                    AppDestination.TASKS -> TasksScreen(
                        tasks = tasks,
                        onComplete = tasksVm::complete,
                        onDelete = tasksVm::delete,
                        onBonus = tasksVm::bonus,
                        modifier = Modifier.padding(padding)
                    )
                    AppDestination.GARDEN -> GardenScreen(
                        garden = garden,
                        onPlant = gardenVm::plant,
                        onOpenTree = { selectedTreeId = it },
                        modifier = Modifier.padding(padding)
                    )
                    AppDestination.SHOP -> ShopScreen(
                        wallet = wallet,
                        onBuySeed = shopVm::buySeed,
                        onBuyFertilizer = shopVm::buyFertilizer,
                        modifier = Modifier.padding(padding)
                    )
                    AppDestination.STATS -> StatsScreen(statistics, Modifier.padding(padding))
                }
            }
        }
        if (adding) AddTaskDialog(
            onDismiss = { adding = false },
            onSave = { title, kind, category, reward, frequency, due ->
                tasksVm.add(title, kind, category, reward, frequency, due)
                adding = false
            }
        )
        if (message.isNotBlank()) AlertDialog(
            onDismissRequest = { message = "" },
            confirmButton = { TextButton(onClick = { message = "" }) { Text("Đóng") } },
            text = { Text(message) }
        )
    }
}
