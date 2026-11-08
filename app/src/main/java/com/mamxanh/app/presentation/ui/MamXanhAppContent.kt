package com.mamxanh.app.presentation.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Forest
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Insights
import androidx.compose.material.icons.rounded.Store
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mamxanh.app.feature.garden.presentation.GardenScreen
import com.mamxanh.app.feature.garden.presentation.GardenUiState
import com.mamxanh.app.feature.home.domain.model.HomeDashboard
import com.mamxanh.app.feature.home.domain.model.HomeTree
import com.mamxanh.app.feature.home.presentation.HomeScreen
import com.mamxanh.app.feature.home.presentation.HomeUiState
import com.mamxanh.app.feature.shop.domain.model.ShopWallet
import com.mamxanh.app.feature.shop.presentation.ShopScreen
import com.mamxanh.app.feature.shop.presentation.ShopUiState
import com.mamxanh.app.feature.stats.presentation.StatsScreen
import com.mamxanh.app.feature.stats.presentation.StatsUiState
import com.mamxanh.app.feature.tasks.presentation.TasksScreen
import com.mamxanh.app.feature.tasks.presentation.TasksUiState
import com.mamxanh.app.feature.tree.domain.model.Tree
import com.mamxanh.app.feature.tree.presentation.TreeInteractionScreen
import com.mamxanh.app.feature.tree.presentation.TreeUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MamXanhAppContent(
    state: AppUiState,
    dashboard: HomeUiState,
    tasks: TasksUiState,
    garden: GardenUiState,
    shop: ShopUiState,
    stats: StatsUiState,
    tree: Tree? = null,
    treeState: TreeUiState = TreeUiState(),
    modifier: Modifier = Modifier
) {
    val hasSelectedTree = tree != null
    MaterialTheme(
        colorScheme = androidx.compose.material3.lightColorScheme(
            primary = Green,
            secondary = Color(0xFFFFA01A),
            background = Cream,
            surface = Color.White
        )
    ) {
        Scaffold(
            modifier = modifier,
            containerColor = Cream,
            topBar = {
                if (hasSelectedTree || state.destination != AppDestination.HOME) {
                    TopAppBar(
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = if (state.destination == AppDestination.SHOP) PinkTileBg else Cream
                        ),
                        navigationIcon = {},
                        title = {
                            androidx.compose.foundation.layout.Column {
                                Text(state.destination.label, fontWeight = FontWeight.ExtraBold, color = Deep)
                                Text("Nuôi dưỡng thói quen, lớn lên cùng cây", style = MaterialTheme.typography.labelSmall, color = Green)
                            }
                        },
                        actions = {
                            Surface(
                                color = Gold,
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(50),
                                modifier = Modifier.padding(end = 14.dp)
                            ) {
                                Text("🪙 ${shop.wallet.coins}", modifier = Modifier.padding(horizontal = 13.dp, vertical = 7.dp), color = Deep, fontWeight = FontWeight.ExtraBold)
                            }
                        }
                    )
                }
            },
            bottomBar = {
                if (!hasSelectedTree) {
                    Surface(
                        color = Color.White.copy(alpha = .96f),
                        shadowElevation = 8.dp,
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                    ) {
                        NavigationBar(containerColor = Color.Transparent, tonalElevation = 0.dp) {
                            AppDestination.entries.forEach { destination ->
                                val icon = when (destination) {
                                    AppDestination.HOME -> Icons.Rounded.Home
                                    AppDestination.TASKS -> Icons.Rounded.Check
                                    AppDestination.GARDEN -> Icons.Rounded.Forest
                                    AppDestination.SHOP -> Icons.Rounded.Store
                                    AppDestination.STATS -> Icons.Rounded.Insights
                                }
                                NavigationBarItem(
                                    selected = state.destination == destination,
                                    onClick = {},
                                    icon = { Icon(icon, contentDescription = destination.label) },
                                    label = { Text(destination.label) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = Green,
                                        selectedTextColor = Green,
                                        indicatorColor = Color(0xFFD6EADC)
                                    )
                                )
                            }
                        }
                    }
                }
            }
        ) { padding ->
            when {
                tree != null -> TreeInteractionScreen(tree, treeState, shop.wallet.inventory["fertilizer"] ?: 0, dashboard.dashboard.wateringCanMl, {}, Modifier.padding(padding))
                else -> when (state.destination) {
                    AppDestination.HOME -> HomeScreen(dashboard, {}, shop.wallet.inventory["fertilizer"] ?: 0, Modifier.padding(padding))
                    AppDestination.TASKS -> TasksScreen(tasks, {}, Modifier.padding(padding))
                    AppDestination.GARDEN -> GardenScreen(garden, {}, Modifier.padding(padding))
                    AppDestination.SHOP -> ShopScreen(shop, {}, Modifier.padding(padding))
                    AppDestination.STATS -> StatsScreen(stats, {}, Modifier.padding(padding))
                }
            }
        }
    }
}

@Preview(name = "App shell · home", showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun MamXanhAppContentHomePreview() {
    MamXanhPreviewTheme {
        MamXanhAppContent(
            state = AppUiState(),
            dashboard = HomeUiState(HomeDashboard(listOf(HomeTree(1L, "cherry", "Đào hồng xuân", 68, "FLOWERS", false)), 2, 4, coins = 120, wateringCanMl = 500), false),
            tasks = TasksUiState(isLoading = false),
            garden = GardenUiState(isLoading = false),
            shop = ShopUiState(ShopWallet(120, emptyMap()), false),
            stats = StatsUiState(isLoading = false)
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
