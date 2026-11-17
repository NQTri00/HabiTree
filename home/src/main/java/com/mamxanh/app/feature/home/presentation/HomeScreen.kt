package com.mamxanh.app.feature.home.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
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
    canClaimBonus: Boolean = false, onClaimBonus: () -> Unit = { onIntent(HomeIntent.OpenTasks) },
    onWeeklyReview: ()->Unit = {}, onJourney: ()->Unit = {}
) {
    val d = state.dashboard
    val tree = d.trees.firstOrNull()
    MintBackdrop(modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize()) {
            MintCompactHeader(d.coins,title="mầm xanh",actions={IconButton(onJourney){GardenArtwork(GardenArt.CATEGORY,Modifier.size(22.dp))}})
            Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal=24.dp,vertical=12.dp),
                verticalArrangement=Arrangement.spacedBy(20.dp)) {
                Column(verticalArrangement=Arrangement.spacedBy(6.dp)) {
                    Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.SpaceBetween) {
                        Text("MỘT NGÀY MỚI",color=Muted,fontSize=10.sp,letterSpacing=2.sp)
                        MintStatusPill(java.text.SimpleDateFormat("EEE, dd/MM",java.util.Locale.forLanguageTag("vi-VN")).format(java.util.Date()))
                    }
                    Text("Chào ngày xanh.",color=Deep,fontSize=27.sp,lineHeight=34.sp,fontWeight=FontWeight.Bold)
                    Text("Mỗi việc nhỏ, một mầm lớn.",color=Muted,fontSize=12.sp)
                }
                if(tree!=null)GardenBotanicalFrame(Modifier.fillMaxWidth(),contentPadding=PaddingValues(18.dp),cornerSize=28.dp,skin=GardenPanelSkin.HINT) {
                    Column(Modifier.fillMaxWidth(),verticalArrangement=Arrangement.spacedBy(8.dp)) {
                        Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween,verticalAlignment=Alignment.CenterVertically) {
                            MintStatusPill(tree.status.stateLabel)
                            TextButton({onIntent(HomeIntent.OpenTree(tree.id))}){Text("›",color=Green,fontSize=24.sp)}
                        }
                        Box(Modifier.fillMaxWidth().height(220.dp),contentAlignment=Alignment.Center) {
                            Box(Modifier.size(220.dp).background(androidx.compose.ui.graphics.Color(0xFFE1EACF),androidx.compose.foundation.shape.CircleShape))
                            GardenPlantPortrait(tree.speciesId,tree.decoration,sharedTreeModifier.fillMaxWidth().height(220.dp)
                            .clickable(role=Role.Button){onIntent(HomeIntent.OpenTree(tree.id))}
                            .semantics{contentDescription="Chăm sóc "+tree.name},growth=tree.growth,fitVisibleArtwork=true,
                            artworkAlignment=Alignment.Center,withered=!tree.status.alive)
                        }
                        Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text(tree.name,color=Deep,fontSize=19.sp,lineHeight=25.sp,fontWeight=FontWeight.Bold)
                                Text(tree.status.stageLabel,color=Muted,fontSize=12.sp)
                            }
                            MintStatusPill(tree.status.waterLabel)
                        }
                        Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween) {
                            Text("Tiến độ giai đoạn",color=Muted,fontSize=11.sp)
                            Text("${tree.status.percentage}%",color=Green,fontSize=11.sp)
                        }
                        MintProgress(tree.status.stageProgress,Modifier.fillMaxWidth())
                        if(tree.status.fertilizerLabel.isNotBlank())MintStatusPill(tree.status.fertilizerLabel)
                        Row(horizontalArrangement=Arrangement.spacedBy(10.dp)) {
                            GardenPaintedButton(if(tree.status.waterActive)"Đã tưới" else "Tưới cây",{onIntent(HomeIntent.WaterTree(tree.id))},Modifier.weight(1f),enabled=tree.status.canWater)
                            GardenPaintedButton("Bón phân · $fertilizerCount",{onIntent(HomeIntent.FertilizeTree(tree.id))},Modifier.weight(1f),enabled=fertilizerCount>0&&tree.status.canFertilize,primary=false)
                        }
                    }
                } else GardenBotanicalFrame(Modifier.fillMaxWidth(),skin=GardenPanelSkin.HINT) {
                    Column(verticalArrangement=Arrangement.spacedBy(12.dp)) {
                        Text("Gieo một khởi đầu",color=Deep,fontSize=22.sp,fontWeight=FontWeight.Bold)
                        GardenPaintedButton("Chọn hạt",{onIntent(HomeIntent.OpenGarden)},Modifier.fillMaxWidth())
                    }
                }
                MintMetricStrip(listOf("Xu hôm nay" to "${d.phase.earnedToday}/${com.mamxanh.shared.model.Balance.DAILY_CAP}","Việc đã xong" to "${d.completedTasks}/${d.totalTasks}","Ngày đều đặn" to "${d.phase.streak}"))
                Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically) {
                    Text("Việc tiếp theo",Modifier.weight(1f),color=Deep,fontSize=18.sp,fontWeight=FontWeight.Bold)
                    TextButton({onIntent(HomeIntent.OpenTasks)}){Text("Tất cả ›",color=Green,fontSize=12.sp)}
                }
                if(d.nextTasks.isEmpty())HomeLink(GardenArt.CHECKLIST,"Thêm thói quen","Một việc vừa sức",{onIntent(HomeIntent.OpenTasks)})
                d.nextTasks.take(1).forEach{task->HomeNextTask(task,{onIntent(HomeIntent.OpenTasks)},{onTaskAction(task.id,task.config.verification=="TIMER")})}
                if(canClaimBonus&&d.dailyRewardDate.isBlank())HomeLink(GardenArt.COIN,"Thưởng hôm nay","+${d.dailyRewardCoins} xu",onClaimBonus)
                Text("Dành cho bạn",color=Deep,fontSize=18.sp,fontWeight=FontWeight.Bold)
                if(d.nextGoal.isNotEmpty())HomeLink(GardenArt.COIN,"Mục tiêu tiếp theo",d.nextGoal,{onIntent(HomeIntent.OpenShop)})
                HomeLink(GardenArt.CALENDAR,"Nhìn lại tuần","Một tuần, nhiều điều nhỏ",onWeeklyReview)
                if(d.seedCount==0)HomeLink(GardenArt.SEED,"Chọn hạt giống","Ghé tiệm nhỏ",{onIntent(HomeIntent.OpenShop)})
            }
        }
        if(state.isDailyClearVisible)MintFeedback("Đã nhận +${d.dailyRewardCoins} xu",{onIntent(HomeIntent.DismissDailyClear)})
    }
}

@Composable
private fun HomeNextTask(task:com.mamxanh.shared.model.Task,onOpen:()->Unit,onAction:()->Unit) {
    GardenBotanicalFrame(Modifier.fillMaxWidth().clickable(role=Role.Button,onClick=onOpen),contentPadding=PaddingValues(14.dp),cornerSize=18.dp) {
        Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(12.dp)) {
            GardenArtwork(GardenArt.BOOK,Modifier.size(28.dp))
            Column(Modifier.weight(1f)) {
                Text(task.title,color=Deep,fontSize=14.sp,lineHeight=20.sp,fontWeight=FontWeight.SemiBold)
                Text(task.category,color=Muted,fontSize=11.sp)
            }
            Text("+${task.reward}",color=Green,fontSize=12.sp)
            IconButton(onAction,enabled=task.available&&!task.doneToday&&!task.finished&&task.lifecycle=="ACTIVE",
                modifier=Modifier.size(48.dp).semantics{contentDescription=if(task.config.verification=="TIMER")"Bắt đầu tập trung" else "Hoàn thành việc"}) {
                Surface(Modifier.size(44.dp),shape=androidx.compose.foundation.shape.CircleShape,color=androidx.compose.ui.graphics.Color.Transparent,border=androidx.compose.foundation.BorderStroke(1.dp,Muted.copy(alpha=.6f))) {
                    Box(contentAlignment=Alignment.Center){if(task.config.verification=="TIMER")Text("▷",color=Green,fontSize=20.sp)}
                }
            }
        }
    }
}

@Composable
private fun HomeLink(art: GardenArt, title: String, subtitle: String, onClick: () -> Unit, enabled: Boolean = true) {
    GardenBotanicalFrame(Modifier.fillMaxWidth().heightIn(min = 48.dp)
        .clickable(enabled = enabled, role = Role.Button, onClick = onClick),
        contentPadding = PaddingValues(14.dp), cornerSize = 18.dp) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            GardenArtwork(art, Modifier.size(28.dp))
            Column(Modifier.weight(1f)) {
                Text(title, color = Deep, fontWeight = FontWeight.Bold, fontSize = 14.sp, lineHeight = 20.sp)
                if (subtitle.isNotEmpty()) Text(subtitle, color = Deep, fontSize = 11.sp, lineHeight = 14.sp)
            }
            if (enabled) Text("›", color = Green, fontSize = 16.sp)
        }
    }
}

private fun previewHomeState(treeName: String = "Đào hồng xuân") = HomeUiState(HomeDashboard(
    trees = listOf(HomeTree(1L, "cherry", treeName, 120, "FLOWERS", false)),
    completedTasks = 2, totalTasks = 4, coins = 120, equippedCan = "can_basic"), false)

@Preview(widthDp = 360, heightDp = 640, device = "spec:width=360dp,height=640dp,dpi=160")
@Composable
fun HomeScreenPreview() { MamXanhPreviewTheme { HomeScreen(previewHomeState(), {}, 2) } }

@Preview(name="Web design comparison",widthDp=414,heightDp=1200,device="spec:width=414dp,height=1200dp,dpi=160")
@Composable
fun HomeWebDesignPreview() { MamXanhPreviewTheme {
    val base=previewHomeState()
    HomeScreen(base.copy(dashboard=base.dashboard.copy(coins=620,completedTasks=1,seedCount=2,nextGoal="Chọn hạt cho góc vườn",
        trees=base.dashboard.trees.map{it.copy(status=com.mamxanh.shared.model.TreeStatus(stageIndex=3,stageCount=9,stageProgress=.46f))},
        nextTasks=listOf(com.mamxanh.shared.model.Task(1,"Đọc 10 trang sách","HABIT","Học tập",8,"EVERY_DAY","","",4,"")),
        phase=com.mamxanh.shared.model.PhaseSnapshot(earnedToday=24,streak=5))), {}, 3)
} }
@Preview(widthDp = 360, heightDp = 640, device = "spec:width=360dp,height=640dp,dpi=160")
@Composable
fun HomeScreenEmptyPreview() { MamXanhPreviewTheme { HomeScreen(HomeUiState(HomeDashboard(coins = 60), false), {}) } }
@Preview(widthDp = 320, heightDp = 1000, fontScale = 1.3f, device = "spec:width=320dp,height=1000dp,dpi=160")
@Composable
fun HomeLongNamePreview() { MamXanhPreviewTheme { HomeScreen(previewHomeState("Cây đào bình yên bên cửa sổ của gia đình"), {}, 2) } }

// Each preview renders the actual composable in isolation, with home-screen sample data.
@Composable
private fun HomeComponentPreviewSurface(content: @Composable () -> Unit) {
    MamXanhPreviewTheme {
        MintBackdrop(Modifier.fillMaxSize()) {
            Box(Modifier.fillMaxSize().padding(10.dp), contentAlignment = Alignment.Center) { content() }
        }
    }
}

@Preview(widthDp = 360, heightDp = 180)
@Composable
fun HomeBackdropComponentPreview() { MamXanhPreviewTheme { MintBackdrop(Modifier.fillMaxSize()) {} } }

@Preview(widthDp = 360, heightDp = 60)
@Composable
fun HomeHeaderComponentPreview() { HomeComponentPreviewSurface { MintCompactHeader(120) } }

@Preview(widthDp = 100, heightDp = 60)
@Composable
fun HomeCoinBadgeComponentPreview() { HomeComponentPreviewSurface { MintCoinBadge(120) } }

@Preview(widthDp = 360, heightDp = 90)
@Composable
fun HomeFrameComponentPreview() { HomeComponentPreviewSurface {
    GardenBotanicalFrame(Modifier.fillMaxWidth(), contentPadding = PaddingValues(10.dp), cornerSize = 18.dp) {
        Text("Đào hồng xuân", color = Deep)
    }
} }

@Preview(widthDp = 360, heightDp = 35)
@Composable
fun HomeProgressComponentPreview() { HomeComponentPreviewSurface {
    MintProgress(previewHomeState().dashboard.trees.first().status.stageProgress, Modifier.fillMaxWidth().height(5.dp))
} }

@Preview(widthDp = 288, heightDp = 320)
@Composable
fun HomePlantComponentPreview() { HomeComponentPreviewSurface {
    val tree = previewHomeState().dashboard.trees.first()
    GardenPlantPortrait(tree.speciesId, tree.decoration, Modifier.fillMaxSize(), growth = tree.growth,
        fitVisibleArtwork = true, artworkAlignment = Alignment.Center, withered = !tree.status.alive)
} }

@Preview(widthDp = 80, heightDp = 80)
@Composable
fun HomeWaterComponentPreview() { HomeComponentPreviewSurface {
    val tree = previewHomeState().dashboard.trees.first()
    MintCareIcon(GardenArt.WATERING_CAN, tree.status.waterLabel, tree.status.canWater, {}, checked = tree.status.waterActive)
} }

@Preview(widthDp = 80, heightDp = 80)
@Composable
fun HomeFertilizerComponentPreview() { HomeComponentPreviewSurface {
    MintCareIcon(GardenArt.FERTILIZER, "Bón · 2 túi", previewHomeState().dashboard.trees.first().status.canFertilize, {})
} }

@Preview(widthDp = 340, heightDp = 60)
@Composable
fun HomeTreeStatusComponentPreview() { HomeComponentPreviewSurface {
    val status = previewHomeState().dashboard.trees.first().status
    MintTreeStatus("${status.stageIndex + 1}/${status.stageCount}", status.stateLabel, status.waterLabel)
} }

@Preview(widthDp = 340, heightDp = 70)
@Composable
fun HomeMetricsComponentPreview() { HomeComponentPreviewSurface {
    val d = previewHomeState().dashboard
    MintMetricStrip(listOf("Hôm nay" to "${d.phase.earnedToday}/${com.mamxanh.shared.model.Balance.DAILY_CAP}",
        "Kế hoạch" to "${d.completedTasks}/${d.totalTasks}", "Chuỗi ngày" to d.phase.streak.toString()))
} }

@Preview(widthDp = 160, heightDp = 60)
@Composable
fun HomeStatusPillComponentPreview() { HomeComponentPreviewSurface { MintStatusPill("Đang lớn") } }

@Preview(widthDp = 360, heightDp = 80)
@Composable
fun HomeWeeklyReviewComponentPreview() { HomeComponentPreviewSurface {
    HomeLink(GardenArt.CHECKLIST, "Nhìn lại tuần", "Một khoảng để điều chỉnh nhẹ nhàng", {})
} }

@Preview(widthDp = 360, heightDp = 80)
@Composable
fun HomeAddHabitComponentPreview() { HomeComponentPreviewSurface {
    HomeLink(GardenArt.CHECKLIST, "Thêm thói quen", "Chọn việc nhỏ cho ngày mới", {})
} }

@Preview(widthDp = 360, heightDp = 80)
@Composable
fun HomeSeedShopComponentPreview() { HomeComponentPreviewSurface {
    HomeLink(GardenArt.SEED, "Chọn hạt giống", "Ghé Tiệm hạt", {})
} }

@Preview(widthDp = 360, heightDp = 80)
@Composable
fun HomeRewardComponentPreview() { HomeComponentPreviewSurface {
    HomeLink(GardenArt.COIN, "Nhận thưởng hôm nay", "+${previewHomeState().dashboard.dailyRewardCoins} xu", {})
} }

@Preview(widthDp = 360, heightDp = 80)
@Composable
fun HomeTaskComponentPreview() { HomeComponentPreviewSurface {
    HomeLink(GardenArt.CHECKLIST, "Đọc 10 trang sách", "+10 xu", {})
} }

@Preview(widthDp = 360, heightDp = 80)
@Composable
fun HomeGoalComponentPreview() { HomeComponentPreviewSurface {
    HomeLink(GardenArt.COIN, "Mục tiêu tiếp theo", "Mua hạt giống mới", {})
} }

@Preview(widthDp = 80, heightDp = 80)
@Composable
fun HomeEmptySproutComponentPreview() { HomeComponentPreviewSurface { MintEmptySprout(Modifier.size(55.dp)) } }

@Preview(widthDp = 360, heightDp = 80)
@Composable
fun HomePlantButtonComponentPreview() { HomeComponentPreviewSurface {
    GardenPaintedButton("Gieo mầm đầu tiên", {}, Modifier.fillMaxWidth())
} }

@Preview(widthDp = 360, heightDp = 100)
@Composable
fun HomeFeedbackComponentPreview() { HomeComponentPreviewSurface {
    MintFeedback("Đã nhận +${previewHomeState().dashboard.dailyRewardCoins} xu", {})
} }

@Preview(widthDp = 48, heightDp = 48)
@Composable
fun HomeArtworkComponentPreview() { HomeComponentPreviewSurface { GardenArtwork(GardenArt.CHECKLIST, Modifier.size(16.dp)) } }
