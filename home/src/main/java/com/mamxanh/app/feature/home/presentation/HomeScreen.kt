package com.mamxanh.app.feature.home.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mamxanh.app.feature.home.domain.model.*
import com.mamxanh.app.presentation.ui.*
import com.mamxanh.app.presentation.ui.components.*

@Composable
fun HomeScreen(
    state: HomeUiState, onIntent: (HomeIntent) -> Unit, fertilizerCount: Int = 0,
    modifier: Modifier = Modifier, sharedTreeModifier: Modifier = Modifier,
    taskTitle: String = "Mỗi việc nhỏ, một bước lớn lên",
    onTaskAction: (Long,Boolean)->Unit = {_,_->onIntent(HomeIntent.OpenTasks)},
    canClaimBonus: Boolean = false, onClaimBonus: () -> Unit = { onIntent(HomeIntent.OpenTasks) }
) {
    val d = state.dashboard
    val tree = d.trees.firstOrNull()
    val scroll = rememberScrollState()
    MintBackdrop(modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize()) {
            MintCompactHeader(d.coins)
            BoxWithConstraints(Modifier.weight(1f)) {
                val heroHeight = minOf(mintTreeHeroHeight(tree?.speciesId.orEmpty(), maxWidth), maxHeight * .64f)
                Column(Modifier.fillMaxSize().padding(horizontal = 10.dp)) {
                    if(tree != null) {
                        GardenBotanicalFrame(Modifier.fillMaxWidth().clickable(role = Role.Button) {
                            onIntent(HomeIntent.OpenTree(tree.id))
                        }, contentPadding = PaddingValues(horizontal = 10.dp, vertical = 5.dp), cornerSize = 18.dp) {
                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(tree.name,Modifier.weight(1f),color=Deep,fontWeight=FontWeight.Bold,fontSize=13.sp)
                                    Text("${tree.status.percentage}%",color=Green,fontSize=12.sp,fontWeight=FontWeight.Bold)
                                }
                                MintProgress(tree.status.stageProgress,Modifier.fillMaxWidth().height(5.dp))
                            }
                        }
                        Box(Modifier.fillMaxWidth().height(heroHeight),contentAlignment=Alignment.Center) {
                            GardenPlantPortrait(tree.speciesId,tree.decoration,
                                sharedTreeModifier.fillMaxWidth().padding(horizontal=36.dp).height(heroHeight)
                                    .clickable(role=Role.Button){onIntent(HomeIntent.OpenTree(tree.id))}
                                    .semantics {contentDescription="Chăm sóc "+tree.name},
                                growth=tree.growth,fitVisibleArtwork=true,artworkAlignment=Alignment.Center,withered=!tree.status.alive)
                            Column(Modifier.align(Alignment.CenterEnd).width(48.dp)) {
                                MintCareIcon(GardenArt.WATERING_CAN,tree.status.waterLabel,tree.status.canWater,
                                    {onIntent(HomeIntent.WaterTree(tree.id))},checked=tree.status.waterActive)
                                MintCareIcon(GardenArt.FERTILIZER,"Bón · $fertilizerCount túi",
                                    fertilizerCount>0 && tree.status.canFertilize,{onIntent(HomeIntent.FertilizeTree(tree.id))})
                            }
                        }
                    }
                    Column(Modifier.weight(1f).mintScrollEdges(scroll.canScrollBackward,scroll.canScrollForward)
                        .verticalScroll(scroll).padding(bottom=6.dp),
                        horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(4.dp)) {
                        if(tree == null) {
                            MintEmptySprout(Modifier.size(110.dp))
                            Text("Chưa có cây trong vườn",fontWeight=FontWeight.Bold,fontSize=17.sp,color=Deep)
                            GardenPaintedButton("Gieo mầm đầu tiên",{onIntent(HomeIntent.OpenGarden)},Modifier.fillMaxWidth())
                        }
                        GardenBotanicalFrame(Modifier.fillMaxWidth(),contentPadding=PaddingValues(10.dp),cornerSize=18.dp) {
                            Column(verticalArrangement=Arrangement.spacedBy(8.dp)) {
                                if(tree!=null)MintTreeStatus("${tree.status.stageIndex+1}/${tree.status.stageCount}",
                                    tree.status.stateLabel,tree.status.waterLabel)
                                MintMetricStrip(listOf("Hôm nay" to "${d.phase.earnedToday}/${com.mamxanh.shared.model.Balance.DAILY_CAP}",
                                    "Kế hoạch" to "${d.completedTasks}/${d.totalTasks}","Chuỗi ngày" to d.phase.streak.toString()))
                                if(tree?.status?.fertilizerLabel?.isNotBlank()==true)MintStatusPill(tree.status.fertilizerLabel)
                            }
                        }
                        val claimed=d.dailyRewardDate.isNotBlank()
                        if(canClaimBonus && !claimed)HomeLink(GardenArt.COIN,"Nhận thưởng hôm nay","+${d.dailyRewardCoins} xu",onClaimBonus)
                        if(d.nextTasks.isEmpty())HomeLink(GardenArt.CHECKLIST,"Thêm thói quen","Chọn việc nhỏ cho ngày mới",{onIntent(HomeIntent.OpenTasks)})
                        d.nextTasks.forEach { task ->
                            HomeLink(GardenArt.CHECKLIST,task.title,task.availability.ifBlank{"+${task.reward} xu"},
                                {onTaskAction(task.id,task.config.verification=="TIMER")},enabled=task.available)
                        }
                        if(d.nextGoal.isNotEmpty())HomeLink(GardenArt.COIN,"Mục tiêu tiếp theo",d.nextGoal,{onIntent(HomeIntent.OpenShop)})
                        if(d.seedCount==0)HomeLink(GardenArt.SEED,"Chọn hạt giống","Ghé Tiệm hạt",{onIntent(HomeIntent.OpenShop)})
                    }
                }
            }
        }
        if(state.isDailyClearVisible)MintFeedback("Đã nhận +${d.dailyRewardCoins} xu",{onIntent(HomeIntent.DismissDailyClear)})
    }
}

@Composable
private fun HomeLink(art: GardenArt, title: String, subtitle: String, onClick: () -> Unit, enabled: Boolean = true) {
    GardenBotanicalFrame(Modifier.fillMaxWidth().heightIn(min = 48.dp)
        .clickable(enabled = enabled, role = Role.Button, onClick = onClick),
        contentPadding = PaddingValues(6.dp), cornerSize = 14.dp) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            GardenArtwork(art, Modifier.size(24.dp))
            Column(Modifier.weight(1f)) {
                Text(title, color = Deep, fontWeight = FontWeight.Bold, fontSize = 12.sp, lineHeight = 15.sp)
                if (subtitle.isNotEmpty()) Text(subtitle, color = Deep, fontSize = 11.sp, lineHeight = 14.sp)
            }
            if (enabled) Text("›", color = Green, fontSize = 22.sp)
        }
    }
}

private fun previewHomeState(treeName: String = "Đào hồng xuân") = HomeUiState(HomeDashboard(
    trees = listOf(HomeTree(1L, "cherry", treeName, 120, "FLOWERS", false)),
    completedTasks = 2, totalTasks = 4, coins = 120, equippedCan = "can_basic"), false)

@Preview(widthDp = 360, heightDp = 640, device = "spec:width=360dp,height=640dp,dpi=160")
@Composable
fun HomeScreenPreview() { MamXanhPreviewTheme { HomeScreen(previewHomeState(), {}, 2) } }
@Preview(widthDp = 360, heightDp = 640, device = "spec:width=360dp,height=640dp,dpi=160")
@Composable
fun HomeScreenEmptyPreview() { MamXanhPreviewTheme { HomeScreen(HomeUiState(HomeDashboard(coins = 60), false), {}) } }
@Preview(widthDp = 320, heightDp = 1000, fontScale = 1.3f, device = "spec:width=320dp,height=1000dp,dpi=160")
@Composable
fun HomeLongNamePreview() { MamXanhPreviewTheme { HomeScreen(previewHomeState("Cây đào bình yên bên cửa sổ của gia đình"), {}, 2) } }
