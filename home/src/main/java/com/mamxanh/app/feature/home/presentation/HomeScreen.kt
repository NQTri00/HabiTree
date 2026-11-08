package com.mamxanh.app.feature.home.presentation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mamxanh.app.feature.home.domain.model.HomeDashboard
import com.mamxanh.app.feature.home.domain.model.HomeTree
import com.mamxanh.app.feature.home.presentation.components.*
import com.mamxanh.app.feature.tree.presentation.components.KawaiiPlantIllustration
import com.mamxanh.app.presentation.ui.*
import com.mamxanh.app.presentation.ui.components.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import androidx.activity.compose.BackHandler

@Composable
fun HomeScreen(
    state: HomeUiState,
    onIntent: (HomeIntent) -> Unit,
    fertilizerCount: Int = 0,
    modifier: Modifier = Modifier,
    sharedTreeModifier: Modifier = Modifier,
    onOpenPlace: (String) -> Unit = {},
    previewApproach: Float = 0f
) {
    val dashboard = state.dashboard
    val tree = dashboard.trees.firstOrNull()
    val camera = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    var selectedPlace by remember { mutableStateOf("SHOP") }
    var companion by remember { mutableStateOf(false) }
    var entering by remember { mutableStateOf(false) }
    var handingOff by remember { mutableStateOf(false) }
    var travelJob by remember { mutableStateOf<Job?>(null) }
    BackHandler(enabled = entering && !handingOff) {
        travelJob?.cancel()
        travelJob = scope.launch {
            camera.animateTo(0f, tween(280))
            entering = false
        }
    }
    fun enter(place: String) {
        if (entering) return
        selectedPlace = place
        entering = true
        travelJob = scope.launch {
            camera.animateTo(1f, tween(620))
            handingOff = true
            // Keep the close view until AnimatedContent disposes this route.
            onOpenPlace(place)
        }
    }
    val approach = if (previewApproach > 0f) previewApproach else camera.value
    val origin = when(selectedPlace) {
        "SHOP" -> TransformOrigin(.80f,.36f)
        "TASKS" -> TransformOrigin(.20f,.36f)
        "GARDEN" -> TransformOrigin(.20f,.74f)
        else -> TransformOrigin(.80f,.74f)
    }
    Box(modifier.fillMaxSize()) {
        Box(Modifier.fillMaxSize().graphicsLayer {
            scaleX = 1f + approach * 2.3f
            scaleY = 1f + approach * 2.3f
            transformOrigin = origin
            translationX = (.5f - origin.pivotFractionX) * size.width * approach
            translationY = (.5f - origin.pivotFractionY) * size.height * approach
        }) {
            FarmPastoralBackdrop(Modifier.fillMaxSize())
            Canvas(Modifier.fillMaxSize()) {
                val path = Path().apply {
                    moveTo(size.width*.5f,size.height)
                    cubicTo(size.width*.65f,size.height*.72f,size.width*.27f,size.height*.57f,size.width*.5f,size.height*.49f)
                }
                drawPath(path,Color(0xFFE8CDA4).copy(alpha=.8f),style=Stroke(size.width*.13f))
                val cross = Path().apply {
                    moveTo(size.width*.19f,size.height*.39f)
                    quadraticTo(size.width*.5f,size.height*.65f,size.width*.81f,size.height*.39f)
                }
                drawPath(cross,Color(0xFFE8CDA4).copy(alpha=.8f),style=Stroke(size.width*.065f))
            }
            BoxWithConstraints(Modifier.fillMaxSize()) {
                GardenPlace("TASKS","Bảng việc", { enter("TASKS") },
                    Modifier.offset(maxWidth*.03f,maxHeight*.23f).width(maxWidth*.32f).height(maxHeight*.23f))
                GardenPlace("SHOP","Tiệm hạt", { enter("SHOP") },
                    Modifier.offset(maxWidth*.65f,maxHeight*.23f).width(maxWidth*.32f).height(maxHeight*.23f))
                GardenPlace("GARDEN","Vườn ươm", { enter("GARDEN") },
                    Modifier.offset(maxWidth*.03f,maxHeight*.65f).width(maxWidth*.30f).height(maxHeight*.21f))
                GardenPlace("STATS","Nhật ký", { enter("STATS") },
                    Modifier.offset(maxWidth*.67f,maxHeight*.65f).width(maxWidth*.30f).height(maxHeight*.21f))
                Column(
                    Modifier.offset(maxWidth*.24f,maxHeight*.35f).width(maxWidth*.52f).height(maxHeight*.32f),
                    horizontalAlignment=Alignment.CenterHorizontally
                ) {
                    if(tree!=null) {
                        KawaiiPlantIllustration(tree.growth,tree.speciesId,tree.decoration,
                            sharedTreeModifier.weight(1f).fillMaxWidth().clickable(enabled=!entering) { onIntent(HomeIntent.OpenTree(tree.id)) })
                        Text(tree.name,color=Deep,fontWeight=FontWeight.Bold,style=MaterialTheme.typography.labelMedium)
                    } else {
                        GardenLandmark("GARDEN",Modifier.weight(1f).fillMaxWidth().clickable { enter("GARDEN") })
                        Text("Gieo mầm đầu tiên",color=Deep,fontWeight=FontWeight.Bold)
                    }
                }
            }
        }
        Column(Modifier.align(Alignment.TopCenter).padding(12.dp).graphicsLayer { alpha=1f-approach }) {
            HomeTopBar(
                nutrientPoints=dashboard.trees.sumOf { it.growth },
                completedTasks=dashboard.completedTasks,totalTasks=dashboard.totalTasks,
                coins=dashboard.coins,onTasksClick={enter("TASKS")}
            )
            Text("Khu vườn nhỏ của bạn",color=Deep,fontWeight=FontWeight.ExtraBold,
                modifier=Modifier.align(Alignment.CenterHorizontally).padding(top=8.dp))
            Text("Chạm một góc vườn để ghé thăm",color=Green,style=MaterialTheme.typography.labelSmall,
                modifier=Modifier.align(Alignment.CenterHorizontally))
        }
        GardenBotanicalFrame(Modifier.align(Alignment.BottomCenter).padding(12.dp).graphicsLayer { alpha=1f-approach }) {
            Column(Modifier.fillMaxWidth()) {
                Text(
                    "Nước ${dashboard.wateringCanMl} / 1000 ml · Phân bón $fertilizerCount",
                    color = Green,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceEvenly,verticalAlignment=Alignment.CenterVertically) {
                TextButton(modifier=Modifier.weight(1f),enabled=!entering,onClick={onIntent(HomeIntent.RefillWater)}) { Text("Bơm",maxLines=1) }
                TextButton(modifier=Modifier.weight(1f),onClick={tree?.let { onIntent(HomeIntent.WaterTree(it.id)) }},
                    enabled=!entering && tree!=null && !tree.wateredToday && dashboard.wateringCanMl>=50) {
                    Text("Tưới",maxLines=1)
                }
                TextButton(modifier=Modifier.weight(1f),onClick={tree?.let { onIntent(HomeIntent.FertilizeTree(it.id)) }},
                    enabled=!entering && tree!=null && fertilizerCount>0) { Text("Bón",maxLines=1) }
                TextButton(modifier=Modifier.weight(1f),enabled=!entering,onClick={companion=true}) { Text("Bạn",maxLines=1) }
            }
            }
        }
        if(companion) MascotCompanionDialog(onDismiss={companion=false})
    }
}

@Composable
private fun GardenPlace(kind: String, label: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Column(modifier.clickable(onClick=onClick),horizontalAlignment=Alignment.CenterHorizontally) {
        GardenLandmark(kind,Modifier.weight(1f).fillMaxWidth())
        Surface(color=Color(0xFFFFEAC5),shape=androidx.compose.foundation.shape.RoundedCornerShape(5.dp),shadowElevation=2.dp) {
            Text(label,color=Color(0xFF64452E),fontWeight=FontWeight.Bold,
                style=MaterialTheme.typography.labelMedium,modifier=Modifier.padding(horizontal=12.dp,vertical=4.dp))
        }
    }
}

private fun previewHomeState() = HomeUiState(HomeDashboard(
    trees=listOf(HomeTree(1L,"cherry","Đào hồng xuân",90,"FLOWERS",false)),
    completedTasks=2,totalTasks=4,coins=120,wateringCanMl=500
),false)

@Preview(name="Garden hub",widthDp=360,heightDp=800,showBackground=true)
@Composable
fun HomeScreenPreview() { MamXanhPreviewTheme { HomeScreen(previewHomeState(),{},2) } }

@Preview(name="Garden hub · empty",widthDp=360,heightDp=800,showBackground=true)
@Composable
fun HomeScreenEmptyPreview() { MamXanhPreviewTheme { HomeScreen(HomeUiState(isLoading=false),{}) } }

@Preview(name="Camera approaching seed stall",widthDp=360,heightDp=800,showBackground=true)
@Composable
fun HomeShopApproachPreview() { MamXanhPreviewTheme { HomeScreen(previewHomeState(),{},2,previewApproach=.65f) } }

@Preview(name="Painted stall destination",widthDp=180,heightDp=200,showBackground=true)
@Composable
fun GardenPlacePreview() { MamXanhPreviewTheme { GardenPlace("SHOP","Tiệm hạt",{},Modifier.fillMaxSize()) } }
