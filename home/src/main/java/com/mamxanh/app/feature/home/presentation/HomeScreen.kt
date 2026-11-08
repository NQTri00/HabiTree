package com.mamxanh.app.feature.home.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.mamxanh.app.feature.home.domain.model.HomeTree
import com.mamxanh.app.feature.home.presentation.components.*
import com.mamxanh.app.feature.tree.presentation.components.KawaiiPlantIllustration
import com.mamxanh.app.presentation.ui.*

/**
 * Remodeled HomeScreen following Concept Image 1:
 * - Scenic pastoral watercolor farm backdrop (windmill, pink barn, meadow, rolling hills)
 * - Orange circular nutrient / level badge on the left
 * - Vintage teal water pump on brick column with water collection bucket
 * - Center glowing soil island with cute kawaii sprout character
 * - Name capsule pill & gradient progress bar
 * - Glossy "5 lần tưới" button & large blue watering can button with water amount
 * - Bottom cute circular action bar: Nước, Phân bón, Bạn nhỏ
 * - Expandable daily habit task sheet
 */
@Composable
fun HomeScreen(
    state: HomeUiState,
    onIntent: (HomeIntent) -> Unit,
    fertilizerCount: Int = 0,
    modifier: Modifier = Modifier,
    sharedTreeModifier: Modifier = Modifier
) {
    val dashboard = state.dashboard
    val mainTree = dashboard.trees.firstOrNull()
    var showCompanionDialog by remember { mutableStateOf(false) }
    var showTasksSheet by remember { mutableStateOf(false) }
    var showWaterFeedback by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()) {
        // 1. Full pastoral watercolor farm backdrop
        com.mamxanh.app.presentation.ui.components.GardenSceneBackdrop(
            com.mamxanh.app.presentation.ui.components.GardenScene.BEDS,
            Modifier.fillMaxSize()
        )

        // 2. Main Game HUD and Interactive Layer
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            // TOP BAR: Nutrient Badge & Coins & Tasks Shortcut
            HomeTopBar(
                nutrientPoints = dashboard.trees.sumOf { it.growth }.coerceAtLeast(144),
                completedTasks = dashboard.completedTasks,
                totalTasks = dashboard.totalTasks,
                coins = dashboard.coins,
                onTasksClick = { showTasksSheet = !showTasksSheet }
            )

            Spacer(modifier = Modifier.height(6.dp))

            // MIDDLE SECTION: Vintage Water Pump, Center Plant & Watering Can Controls
            Column(
                modifier = Modifier.fillMaxWidth().weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    HomeCenterPlant(
                        tree = mainTree,
                        onOpenTree = { onIntent(HomeIntent.OpenTree(it)) },
                        onOpenGarden = { onIntent(HomeIntent.OpenGarden) },
                        modifier = Modifier.padding(horizontal = 16.dp),
                        sharedTreeModifier = sharedTreeModifier
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth().heightIn(min = 132.dp).padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HomePumpStation(
                        wateringCanMl = dashboard.wateringCanMl,
                        onPumpClick = { onIntent(HomeIntent.RefillWater) }
                    )
                    HomeWaterCareControls(
                        wateringCanMl = dashboard.wateringCanMl,
                        canWater = mainTree != null && !mainTree.wateredToday && dashboard.wateringCanMl >= 50,
                        helperText = when {
                            mainTree == null -> "Chọn cây để tưới"
                            mainTree.wateredToday -> "Đã tưới hôm nay"
                            dashboard.wateringCanMl < 50 -> "Bơm thêm nước"
                            else -> "Chạm bình · 50 ml"
                        },
                        onWater = {
                            if (mainTree != null && !mainTree.wateredToday && dashboard.wateringCanMl >= 50) {
                                onIntent(HomeIntent.WaterTree(mainTree.id))
                                showWaterFeedback = true
                            }
                        }
                    )
                }
            }

            // 3. BOTTOM ACTION BAR: Circular Kawaii Action Buttons
            HomeBottomActionBar(
                isWateredToday = mainTree?.wateredToday ?: false,
                waterAvailable = mainTree != null && dashboard.wateringCanMl >= 50,
                fertilizerCount = fertilizerCount,
                onWaterClick = {
                    if (mainTree != null && !mainTree.wateredToday && dashboard.wateringCanMl >= 50) {
                        onIntent(HomeIntent.WaterTree(mainTree.id))
                        showWaterFeedback = true
                    }
                },
                onFertilizeClick = {
                    if (mainTree != null && fertilizerCount > 0) {
                        onIntent(HomeIntent.FertilizeTree(mainTree.id))
                    } else {
                        showTasksSheet = true
                    }
                },
                onCompanionClick = { showCompanionDialog = true }
            )
        }

        // Water splash feedback overlay
        WaterSplashFeedback(
            visible = showWaterFeedback,
            onDismiss = { showWaterFeedback = false },
            modifier = Modifier.align(Alignment.Center)
        )

        // Companion mascot dialog
        if (showCompanionDialog) {
            MascotCompanionDialog(onDismiss = { showCompanionDialog = false })
        }

        // Bottom Tasks Drawer / Sheet
        if (showTasksSheet) {
            HomeTasksBottomSheet(
                completedTasks = dashboard.completedTasks,
                totalTasks = dashboard.totalTasks,
                treeCount = dashboard.trees.size,
                onDismiss = { showTasksSheet = false },
                onOpenTasks = { onIntent(HomeIntent.OpenTasks) },
                onOpenGarden = { onIntent(HomeIntent.OpenGarden) }
            )
        }
    }
}

/**
 * Center soil mound and plant presentation.
 */
@Composable
private fun HomeCenterPlant(
    tree: HomeTree?,
    onOpenTree: (Long) -> Unit,
    onOpenGarden: () -> Unit,
    modifier: Modifier = Modifier,
    sharedTreeModifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (tree != null) {
            val progress = (tree.growth / 120f).coerceIn(0f, 1f)
            Box(
                Modifier.fillMaxWidth().height(310.dp),
                contentAlignment = Alignment.Center
            ) {
                KawaiiPlantIllustration(
                    growth = tree.growth,
                    speciesId = tree.speciesId,
                    decoration = tree.decoration,
                    modifier = sharedTreeModifier.fillMaxWidth(0.84f).fillMaxHeight().clickable { onOpenTree(tree.id) }
                )
            }
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp).height(7.dp).clip(RoundedCornerShape(50)),
                color = Green,
                trackColor = Color.White.copy(alpha = 0.85f)
            )
        } else {
            Surface(
                onClick = onOpenGarden,
                color = Color.White.copy(alpha = 0.92f),
                shape = RoundedCornerShape(24.dp),
                shadowElevation = 4.dp,
                modifier = Modifier.padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("🌱", fontSize = 42.sp)
                    Text(
                        "Mảnh đất đang chờ bạn",
                        fontWeight = FontWeight.ExtraBold,
                        color = Deep,
                        fontSize = 16.sp
                    )
                    Text(
                        "Chọn hạt giống để gieo mầm nhé",
                        color = Muted,
                        fontSize = 13.sp
                    )
                    Button(
                        onClick = onOpenGarden,
                        colors = ButtonDefaults.buttonColors(containerColor = Green)
                    ) {
                        Text("Gieo cây ngay")
                    }
                }
            }
        }
    }
}

@Preview(name = "Home · thriving garden", showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun HomeScreenPreview() {
    MamXanhPreviewTheme {
        HomeScreen(
            state = HomeUiState(
                dashboard = com.mamxanh.app.feature.home.domain.model.HomeDashboard(
                    trees = listOf(HomeTree(1L, "cherry", "Đào hồng xuân", 68, "FLOWERS", false)),
                    completedTasks = 2,
                    totalTasks = 4,
                    coins = 120,
                    wateringCanMl = 500
                ),
                isLoading = false
            ),
            onIntent = {},
            fertilizerCount = 2
        )
    }
}

@Preview(name = "Home · empty garden", showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun HomeScreenEmptyPreview() {
    MamXanhPreviewTheme {
        HomeScreen(
            state = HomeUiState(
                dashboard = com.mamxanh.app.feature.home.domain.model.HomeDashboard(
                    completedTasks = 1,
                    totalTasks = 3,
                    coins = 40
                ),
                isLoading = false
            ),
            onIntent = {}
        )
    }
}

@Preview(name = "Center plant · cherry", showBackground = true, widthDp = 360, heightDp = 420)
@Composable
fun HomeCenterPlantPreview() {
    MamXanhPreviewTheme {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            FarmPastoralBackdrop(Modifier.fillMaxSize())
            HomeCenterPlant(
                tree = HomeTree(1L, "cherry", "Đào hồng xuân", 68, "FLOWERS", false),
                onOpenTree = {},
                onOpenGarden = {},
                modifier = Modifier.padding(24.dp)
            )
        }
    }
}

@Preview(name = "Center plant · empty", showBackground = true, widthDp = 360, heightDp = 420)
@Composable
fun HomeCenterPlantEmptyPreview() {
    MamXanhPreviewTheme {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            com.mamxanh.app.presentation.ui.components.GardenSceneBackdrop(
                com.mamxanh.app.presentation.ui.components.GardenScene.BEDS,
                Modifier.fillMaxSize()
            )
            HomeCenterPlant(null, {}, {}, Modifier.padding(24.dp))
        }
    }
}
