package com.mamxanh.app.feature.home.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import com.mamxanh.app.presentation.ui.components.GardenRichText as Text
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.semantics.Role
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mamxanh.app.feature.home.domain.model.HomeDashboard
import com.mamxanh.app.feature.home.domain.model.HomeTree
import com.mamxanh.app.feature.home.presentation.components.*
import com.mamxanh.app.presentation.ui.*
import com.mamxanh.app.presentation.ui.components.*
import com.mamxanh.core.ui.R
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

private enum class HomePlace { SHOP, TASKS, GARDEN }

@Composable
fun HomeScreen(
    state: HomeUiState,
    onIntent: (HomeIntent) -> Unit,
    fertilizerCount: Int = 0,
    modifier: Modifier = Modifier,
    sharedTreeModifier: Modifier = Modifier,
    previewApproach: Float = 0f
) {
    val dashboard = state.dashboard
    val tree = dashboard.trees.firstOrNull()
    val camera = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    var companion by remember { mutableStateOf(false) }
    var entering by remember { mutableStateOf(false) }
    var handingOff by remember { mutableStateOf(false) }
    var travelJob by remember { mutableStateOf<Job?>(null) }
    BackHandler(enabled = entering && !handingOff) {
        travelJob?.cancel()
        travelJob = scope.launch { camera.animateTo(0f, tween(280)); entering = false }
    }
    fun enter(place: HomePlace) {
        if (entering) return
        entering = true
        travelJob = scope.launch {
            camera.animateTo(1f, tween(180))
            handingOff = true
            onIntent(when (place) {
                HomePlace.SHOP -> HomeIntent.OpenShop
                HomePlace.TASKS -> HomeIntent.OpenTasks
                HomePlace.GARDEN -> HomeIntent.OpenGarden
            })
        }
    }
    val approach = if (previewApproach > 0f) previewApproach else camera.value
    val fontScale = LocalDensity.current.fontScale.coerceAtLeast(1f)
    BoxWithConstraints(modifier.fillMaxSize()) {
        val sceneHeight = maxOf(maxHeight, 680.dp * fontScale)
        Box(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Box(Modifier.fillMaxWidth().height(sceneHeight)) {
        Box(Modifier.fillMaxSize().graphicsLayer {
            alpha = 1f - approach
        }) {
            FarmPastoralBackdrop(Modifier.fillMaxSize())
            BoxWithConstraints(Modifier.fillMaxSize()) {
                GardenHangingLantern(Modifier.offset(14.dp, 6.dp).size(44.dp, 86.dp))
                GardenCottage(Modifier.offset(maxWidth * .07f, maxHeight * .245f).size(54.dp, 52.dp))
                GardenPlace("TASKS", "Việc nhỏ", { enter(HomePlace.TASKS) },
                    Modifier.offset(maxWidth * .04f, maxHeight * .70f).width(maxWidth * .46f).height(maxHeight * .12f))
                GardenPlace("SHOP", "Tiệm hạt", { enter(HomePlace.SHOP) },
                    Modifier.offset(maxWidth * .60f, maxHeight * .66f).width(maxWidth * .36f).height(maxHeight * .16f))
                Box(
                    Modifier.offset(maxWidth * .10f, maxHeight * .27f).width(maxWidth * .80f).height(maxHeight * .28f),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    if (tree != null) {
                        GardenPlantPortrait(tree.speciesId, tree.decoration,
                            sharedTreeModifier.fillMaxSize().clickable(enabled = !entering) { onIntent(HomeIntent.OpenTree(tree.id)) }, growth = tree.growth)
                    } else {
                        GardenPlace("GARDEN", "Gieo mầm đầu tiên", { enter(HomePlace.GARDEN) }, Modifier.fillMaxSize())
                    }
                }
            }
        }
        Column(Modifier.align(Alignment.TopCenter).fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp).graphicsLayer { alpha = 1f - approach },
            horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
            GardenWoodSign("Mầm Xanh", Modifier.width(220.dp)
                .clickable(enabled = !entering, role = Role.Button, onClickLabel = "Người bạn khu vườn") { companion = true }, fontSize = 28.sp)
            GardenBotanicalFrame(contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp), cornerSize = 20.dp) {
                Text("🪙 ${dashboard.coins} xu", color = Deep, fontSize = 17.sp, lineHeight = 19.sp, fontWeight = FontWeight.Bold)
            }
            GardenBotanicalFrame(contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp), cornerSize = 24.dp) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    GardenArtwork(GardenArt.SPROUT, Modifier.size(22.dp))
                    Text("Khu vườn của bạn", Modifier.weight(1f), color = Deep, fontSize = 21.sp,
                        lineHeight = 24.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                    GardenArtwork(GardenArt.SPROUT, Modifier.size(22.dp))
                }
            }
        }
        if (tree != null) BoxWithConstraints(Modifier.fillMaxSize().graphicsLayer { alpha = 1f - approach }) {
            GardenBotanicalFrame(Modifier.align(Alignment.TopCenter).offset(y = maxHeight * .54f)
                .width(if (fontScale > 1.15f) maxWidth * .80f else 220.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 7.dp), cornerSize = 24.dp) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text(tree.name, Modifier.fillMaxWidth(), color = Deep, fontWeight = FontWeight.Bold,
                        fontSize = 16.sp, lineHeight = 20.sp, textAlign = TextAlign.Center)
                    LinearProgressIndicator(progress = { (tree.growth / 120f).coerceIn(0f,1f) },
                        Modifier.fillMaxWidth().height(6.dp), color = Green, trackColor = Color(0xFFD6B78D), drawStopIndicator = {})
                    Text("${dashboard.completedTasks} / ${dashboard.totalTasks} việc hôm nay",
                        color = Deep, fontWeight = FontWeight.Bold, fontSize = 14.sp, lineHeight = 17.sp)
                }
            }
        }
        Column(
            Modifier.align(Alignment.BottomCenter).fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp).graphicsLayer { alpha = 1f - approach },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            GardenWoodenTray(Modifier.fillMaxWidth()) {
                Row(horizontalArrangement = Arrangement.spacedBy(5.dp), verticalAlignment = Alignment.Bottom) {
                    GardenCareAction(GardenArt.WATERING_CAN, if (tree?.wateredToday == true) "Đã tưới" else "Tưới",
                        { tree?.let { onIntent(HomeIntent.WaterTree(it.id)) } }, Modifier.weight(1f),
                        enabled = !entering && tree != null && !tree.wateredToday && dashboard.wateringCanMl >= 50,
                        completed = tree?.wateredToday == true)
                    GardenCareAction(GardenArt.FERTILIZER, "Bón",
                        { tree?.let { onIntent(HomeIntent.FertilizeTree(it.id)) } }, Modifier.weight(1f),
                        enabled = !entering && tree != null && fertilizerCount > 0)
                    GardenCareAction(GardenArt.PUMP, "Bơm", { onIntent(HomeIntent.RefillWater) }, Modifier.weight(1f), enabled = !entering)
                    GardenBotanicalFrame(Modifier.weight(1.05f), contentPadding = PaddingValues(horizontal = 4.dp, vertical = 7.dp), cornerSize = 16.dp) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            GardenArtwork(GardenArt.GLASS, Modifier.size(24.dp))
                            Text("Nước\n${dashboard.wateringCanMl} ml", Modifier.fillMaxWidth(), color = Deep,
                                fontSize = 12.sp, lineHeight = 14.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                        }
                    }
                }
            }
        }
        }
        }
        if (state.isDailyClearVisible) {
            androidx.compose.ui.window.Dialog(
                onDismissRequest = { onIntent(HomeIntent.DismissDailyClear) },
                properties = androidx.compose.ui.window.DialogProperties(
                    usePlatformDefaultWidth = false, decorFitsSystemWindows = false)
            ) {
                Box(Modifier.fillMaxSize().safeDrawingPadding().padding(20.dp), contentAlignment = Alignment.Center) {
                    GardenBotanicalFrame(Modifier.widthIn(max = 360.dp).fillMaxWidth().heightIn(max = 520.dp),
                        contentPadding = PaddingValues(20.dp), cornerSize = 28.dp, skin = GardenPanelSkin.DIALOG) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                Text("✓", Modifier.weight(1f), color = Green, fontSize = 38.sp, textAlign = TextAlign.Center)
                                IconButton(onClick = { onIntent(HomeIntent.DismissDailyClear) }) {
                                    Text("×", color = Deep, fontSize = 24.sp)
                                }
                            }
                            Column(Modifier.weight(1f, fill = false).verticalScroll(rememberScrollState()),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text("Đã xong hôm nay", color = Deep, fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                                GardenBotanicalFrame(contentPadding = PaddingValues(12.dp), skin = GardenPanelSkin.PRICE) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("Thưởng hôm nay", color = Deep)
                                        Text("+${dashboard.dailyRewardCoins} xu", color = Deep, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                            GardenPaintedButton("Đã nhận thưởng", { onIntent(HomeIntent.DismissDailyClear) })
                        }
                    }
                }
            }
        }
        if (companion) MascotCompanionDialog(onDismiss = { companion = false })
    }
}

@Composable
private fun GardenPlace(kind: String, label: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Column(modifier.clickable(role = Role.Button, onClickLabel = label, onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Bottom) {
        if (kind == "SHOP") GardenArtwork(GardenArt.SHOP,
            Modifier.size(if (androidx.compose.ui.platform.LocalDensity.current.fontScale > 1.15f) 48.dp else 64.dp))
        Box(Modifier.fillMaxWidth().padding(bottom = 10.dp).drawBehind {
            val w = size.width
            val h = size.height
            listOf(.18f, .76f).forEach { x ->
                drawRect(Color(0xFF946537), Offset(w * x, h * .8f), Size(w * .075f, h * .4f))
                drawLine(Color(0xFFD4A366), Offset(w * (x + .02f), h), Offset(w * (x + .02f), h * 1.18f), 2.dp.toPx())
            }
            val board = androidx.compose.ui.graphics.Path().apply {
                moveTo(w * .04f, 0f)
                lineTo(w * .94f, 0f)
                lineTo(w, h * .18f)
                lineTo(w * .98f, h * .88f)
                lineTo(w * .92f, h)
                lineTo(w * .03f, h * .96f)
                lineTo(0f, h * .78f)
                lineTo(0f, h * .14f)
                close()
            }
            drawPath(board, Color(0xFFAA7540))
            drawPath(board, Color(0xFFD9A466),
                style = androidx.compose.ui.graphics.drawscope.Stroke(3.dp.toPx()))
            drawRoundRect(Color(0xFFFFE8BC), Offset(5.dp.toPx(), 5.dp.toPx()),
                Size((w - 10.dp.toPx()).coerceAtLeast(0f), (h - 10.dp.toPx()).coerceAtLeast(0f)),
                CornerRadius(5.dp.toPx()))
        }) {
            Row(Modifier.fillMaxWidth().heightIn(min = 48.dp).padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                if (kind != "SHOP") GardenArtwork(if (kind == "TASKS") GardenArt.CHECKLIST else GardenArt.SPROUT, Modifier.size(34.dp))
                Text("$label ›", Modifier.weight(1f), color = Deep, fontWeight = FontWeight.Bold,
                    fontSize = if (kind == "SHOP") 15.sp else 16.sp, lineHeight = 19.sp,
                    maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
            }
        }
    }
}

private fun previewHomeState(treeName: String = "Đào hồng xuân") = HomeUiState(HomeDashboard(
    trees = listOf(HomeTree(1L, "cherry", treeName, 90, "FLOWERS", false)),
    completedTasks = 2, totalTasks = 4, coins = 120, wateringCanMl = 500
), false)

@Preview(name = "Painted forest garden", widthDp = 360, heightDp = 800, showBackground = true)
@Composable
fun HomeScreenPreview() { MamXanhPreviewTheme { HomeScreen(previewHomeState(), {}, 2) } }

@Preview(name = "Forest garden · empty", widthDp = 360, heightDp = 800, showBackground = true)
@Composable
fun HomeScreenEmptyPreview() { MamXanhPreviewTheme { HomeScreen(HomeUiState(isLoading = false), {}) } }

@Preview(name = "Camera approaching seed stall", widthDp = 360, heightDp = 800, showBackground = true)
@Composable
fun HomeShopApproachPreview() { MamXanhPreviewTheme { HomeScreen(previewHomeState(), {}, 2, previewApproach = .65f) } }

@Preview(name = "Stall destination · constrained, large text", widthDp = 130, heightDp = 120,
    fontScale = 1.3f, device = "spec:width=130dp,height=120dp,dpi=160", showBackground = true)
@Composable
fun GardenPlacePreview() { MamXanhPreviewTheme { GardenPlace("SHOP", "Tiệm hạt", {}, Modifier.fillMaxSize()) } }

@Preview(name = "Home · full document, long name, large text", widthDp = 360, heightDp = 884, fontScale = 1.3f,
    device = "spec:width=360dp,height=884dp,dpi=160")
@Composable
fun HomeLongNamePreview() {
    MamXanhPreviewTheme {
        HomeScreen(previewHomeState("Cây đào bình yên bên cửa sổ của gia đình"), {}, 2)
    }
}
