package com.mamxanh.app.feature.home.presentation

import com.mamxanh.app.presentation.ui.design.AppColors
import com.mamxanh.app.presentation.ui.design.AppDimensions
import com.mamxanh.app.presentation.ui.design.AppOpacity
import com.mamxanh.app.presentation.ui.design.AppTypography

import com.mamxanh.shared.localization.TextKey

import com.mamxanh.shared.localization.AppText

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
    taskTitle: String = AppText.text(TextKey.CopyMoiViecNhoMotBuocLonLen80104a),
    onTaskAction: (Long,Boolean)->Unit = {_,_->onIntent(HomeIntent.OpenTasks)},
    canClaimBonus: Boolean = false, onClaimBonus: () -> Unit = { onIntent(HomeIntent.OpenTasks) },
    onWeeklyReview: ()->Unit = {}, onJourney: ()->Unit = {}
) {
    val d = state.dashboard
    val tree = d.trees.firstOrNull()
    MintBackdrop(modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize()) {
            MintCompactHeader(d.coins,title=AppText.text(TextKey.CopyMamXanh692a0a),actions={IconButton(onJourney){GardenArtwork(GardenArt.CATEGORY,Modifier.size(AppDimensions.Dp22))}})
            Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal=AppDimensions.Dp24,vertical=AppDimensions.Dp12),
                verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp20)) {
                Column(verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp6)) {
                    Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.SpaceBetween) {
                        Text(AppText.text(TextKey.CopyMotNgayMoiCc8267),color=Muted,fontSize=AppTypography.Sp10,letterSpacing=AppTypography.Sp2)
                        MintStatusPill(java.text.SimpleDateFormat("EEE, dd/MM",java.util.Locale.forLanguageTag("vi-VN")).format(java.util.Date()))
                    }
                    Text(AppText.text(TextKey.CopyChaoNgayXanh71726f),color=Deep,fontSize=AppTypography.Sp27,lineHeight=AppTypography.Sp34,fontWeight=FontWeight.Bold)
                    Text(AppText.text(TextKey.CopyMoiViecNhoMotMamLonF6a672),color=Muted,fontSize=AppTypography.Sp12)
                }
                if(tree!=null)GardenBotanicalFrame(Modifier.fillMaxWidth(),contentPadding=PaddingValues(AppDimensions.Dp18),cornerSize=AppDimensions.Dp28,skin=GardenPanelSkin.HINT) {
                    Column(Modifier.fillMaxWidth(),verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp8)) {
                        Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween,verticalAlignment=Alignment.CenterVertically) {
                            MintStatusPill(tree.status.stateLabel)
                            TextButton({onIntent(HomeIntent.OpenTree(tree.id))}){Text("›",color=Green,fontSize=AppTypography.Sp24)}
                        }
                        Box(Modifier.fillMaxWidth().height(AppDimensions.Dp220),contentAlignment=Alignment.Center) {
                            Box(Modifier.size(AppDimensions.Dp220).background(AppColors.SwatchFFE1EACF,androidx.compose.foundation.shape.CircleShape))
                            GardenPlantPortrait(tree.speciesId,tree.decoration,sharedTreeModifier.fillMaxWidth().height(AppDimensions.Dp220)
                            .clickable(role=Role.Button){onIntent(HomeIntent.OpenTree(tree.id))}
                            .semantics{contentDescription=AppText.text(TextKey.CopyChamSoc5b1872)+tree.name},growth=tree.growth,fitVisibleArtwork=true,
                            artworkAlignment=Alignment.Center,withered=!tree.status.alive)
                        }
                        Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text(tree.name,color=Deep,fontSize=AppTypography.Sp19,lineHeight=AppTypography.Sp25,fontWeight=FontWeight.Bold)
                                Text(tree.status.stageLabel,color=Muted,fontSize=AppTypography.Sp12)
                            }
                            MintStatusPill(tree.status.waterLabel)
                        }
                        Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween) {
                            Text(AppText.text(TextKey.CopyTienDoGiaiDoanC1f3ab),color=Muted,fontSize=AppTypography.Sp11)
                            Text("${tree.status.percentage}%",color=Green,fontSize=AppTypography.Sp11)
                        }
                        MintProgress(tree.status.stageProgress,Modifier.fillMaxWidth())
                        if(tree.status.fertilizerLabel.isNotBlank())MintStatusPill(tree.status.fertilizerLabel)
                        Row(horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp10)) {
                            GardenPaintedButton(if(tree.status.waterActive)AppText.text(TextKey.CopyDaTuoiDe2042) else AppText.text(TextKey.CopyTuoiCay0990f6),{onIntent(HomeIntent.WaterTree(tree.id))},Modifier.weight(1f),enabled=tree.status.canWater)
                            GardenPaintedButton(AppText.text(TextKey.CopyBonPhan095a3dd,fertilizerCount),{onIntent(HomeIntent.FertilizeTree(tree.id))},Modifier.weight(1f),enabled=fertilizerCount>0&&tree.status.canFertilize,primary=false)
                        }
                    }
                } else GardenBotanicalFrame(Modifier.fillMaxWidth(),skin=GardenPanelSkin.HINT) {
                    Column(verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp12)) {
                        Text(AppText.text(TextKey.CopyGieoMotKhoiDau82f23a),color=Deep,fontSize=AppTypography.Sp22,fontWeight=FontWeight.Bold)
                        GardenPaintedButton(AppText.text(TextKey.CopyChonHat3d6476),{onIntent(HomeIntent.OpenGarden)},Modifier.fillMaxWidth())
                    }
                }
                MintMetricStrip(listOf(AppText.text(TextKey.CopyXuHomNay0c44ed) to "${d.phase.earnedToday}/${com.mamxanh.shared.model.Balance.DAILY_CAP}",AppText.text(TextKey.CopyViecDaXong4cbf90) to "${d.completedTasks}/${d.totalTasks}",AppText.text(TextKey.CopyNgayDeuDan31b8f2) to "${d.phase.streak}"))
                Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically) {
                    Text(AppText.text(TextKey.CopyViecTiepTheoEa34c3),Modifier.weight(1f),color=Deep,fontSize=AppTypography.Sp18,fontWeight=FontWeight.Bold)
                    TextButton({onIntent(HomeIntent.OpenTasks)}){Text(AppText.text(TextKey.CopyTatCa2553fa),color=Green,fontSize=AppTypography.Sp12)}
                }
                if(d.nextTasks.isEmpty())HomeLink(GardenArt.CHECKLIST,AppText.text(TextKey.CopyThemThoiQuen4d2eb4),AppText.text(TextKey.CopyMotViecVuaSuc2272cf),{onIntent(HomeIntent.OpenTasks)})
                d.nextTasks.take(1).forEach{task->HomeNextTask(task,{onIntent(HomeIntent.OpenTasks)},{onTaskAction(task.id,task.config.verification=="TIMER")})}
                if(canClaimBonus&&d.dailyRewardDate.isBlank())HomeLink(GardenArt.COIN,AppText.text(TextKey.CopyThuongHomNayC015c6),AppText.text(TextKey.Copy0Xu7c2391,d.dailyRewardCoins),onClaimBonus)
                Text(AppText.text(TextKey.CopyDanhChoBan785034),color=Deep,fontSize=AppTypography.Sp18,fontWeight=FontWeight.Bold)
                if(d.nextGoal.isNotEmpty())HomeLink(GardenArt.COIN,AppText.text(TextKey.CopyMucTieuTiepTheo9dbfa7),d.nextGoal,{onIntent(HomeIntent.OpenShop)})
                HomeLink(GardenArt.CALENDAR,AppText.text(TextKey.CopyNhinLaiTuanFf3f0c),AppText.text(TextKey.CopyMotTuanNhieuDieuNho4ca68f),onWeeklyReview)
                if(d.seedCount==0)HomeLink(GardenArt.SEED,AppText.text(TextKey.CopyChonHatGiongEebad6),AppText.text(TextKey.CopyGheTiemNho8395ee),{onIntent(HomeIntent.OpenShop)})
            }
        }
        if(state.isDailyClearVisible)MintFeedback(AppText.text(TextKey.CopyDaNhan0Xu9e4321,d.dailyRewardCoins),{onIntent(HomeIntent.DismissDailyClear)})
    }
}

@Composable
private fun HomeNextTask(task:com.mamxanh.shared.model.Task,onOpen:()->Unit,onAction:()->Unit) {
    GardenBotanicalFrame(Modifier.fillMaxWidth().clickable(role=Role.Button,onClick=onOpen),contentPadding=PaddingValues(AppDimensions.Dp14),cornerSize=AppDimensions.Dp18) {
        Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp12)) {
            GardenArtwork(GardenArt.BOOK,Modifier.size(AppDimensions.Dp28))
            Column(Modifier.weight(1f)) {
                Text(task.title,color=Deep,fontSize=AppTypography.Sp14,lineHeight=AppTypography.Sp20,fontWeight=FontWeight.SemiBold)
                Text(AppText.category(task.category),color=Muted,fontSize=AppTypography.Sp11)
            }
            Text("+${task.reward}",color=Green,fontSize=AppTypography.Sp12)
            IconButton(onAction,enabled=task.available&&!task.doneToday&&!task.finished&&task.lifecycle=="ACTIVE",
                modifier=Modifier.size(AppDimensions.Dp48).semantics{contentDescription=if(task.config.verification=="TIMER")AppText.text(TextKey.CopyBatDauTapTrungD198d9) else AppText.text(TextKey.CopyHoanThanhViecA2a386)}) {
                Surface(Modifier.size(AppDimensions.Dp44),shape=androidx.compose.foundation.shape.CircleShape,color=androidx.compose.ui.graphics.Color.Transparent,border=androidx.compose.foundation.BorderStroke(AppDimensions.Dp1,Muted.copy(alpha=AppOpacity.Value_6))) {
                    Box(contentAlignment=Alignment.Center){if(task.config.verification=="TIMER")Text("▷",color=Green,fontSize=AppTypography.Sp20)}
                }
            }
        }
    }
}

@Composable
private fun HomeLink(art: GardenArt, title: String, subtitle: String, onClick: () -> Unit, enabled: Boolean = true) {
    GardenBotanicalFrame(Modifier.fillMaxWidth().heightIn(min = AppDimensions.Dp48)
        .clickable(enabled = enabled, role = Role.Button, onClick = onClick),
        contentPadding = PaddingValues(AppDimensions.Dp14), cornerSize = AppDimensions.Dp18) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(AppDimensions.Dp12)) {
            GardenArtwork(art, Modifier.size(AppDimensions.Dp28))
            Column(Modifier.weight(1f)) {
                Text(title, color = Deep, fontWeight = FontWeight.Bold, fontSize = AppTypography.Sp14, lineHeight = AppTypography.Sp20)
                if (subtitle.isNotEmpty()) Text(subtitle, color = Deep, fontSize = AppTypography.Sp11, lineHeight = AppTypography.Sp14)
            }
            if (enabled) Text("›", color = Green, fontSize = AppTypography.Sp16)
        }
    }
}

private fun previewHomeState(treeName: String = AppText.text(TextKey.CopyDaoHongXuanC2fd99)) = HomeUiState(HomeDashboard(
    trees = listOf(HomeTree(1L, "cherry", treeName, 120, "FLOWERS", false)),
    completedTasks = 2, totalTasks = 4, coins = 120, equippedCan = "can_basic"), false)

@Preview(widthDp = 360, heightDp = 640, device = "spec:width=360dp,height=640dp,dpi=160")
@Composable
fun HomeScreenPreview() { MamXanhPreviewTheme { HomeScreen(previewHomeState(), {}, 2) } }

@Preview(name="Web design comparison",widthDp=414,heightDp=1200,device="spec:width=414dp,height=1200dp,dpi=160")
@Composable
fun HomeWebDesignPreview() { MamXanhPreviewTheme {
    val base=previewHomeState()
    HomeScreen(base.copy(dashboard=base.dashboard.copy(coins=620,completedTasks=1,seedCount=2,nextGoal=AppText.text(TextKey.CopyChonHatChoGocVuon3c30f3),
        trees=base.dashboard.trees.map{it.copy(status=com.mamxanh.shared.model.TreeStatus(stageIndex=3,stageCount=9,stageProgress=.46f))},
        nextTasks=listOf(com.mamxanh.shared.model.Task(1,AppText.text(TextKey.CopyDoc10TrangSachA3c873),"HABIT",AppText.canonical(TextKey.CopyHocTapA51516),8,"EVERY_DAY","","",4,"")),
        phase=com.mamxanh.shared.model.PhaseSnapshot(earnedToday=24,streak=5))), {}, 3)
} }
@Preview(widthDp = 360, heightDp = 640, device = "spec:width=360dp,height=640dp,dpi=160")
@Composable
fun HomeScreenEmptyPreview() { MamXanhPreviewTheme { HomeScreen(HomeUiState(HomeDashboard(coins = 60), false), {}) } }
@Preview(widthDp = 320, heightDp = 1000, fontScale = 1.3f, device = "spec:width=320dp,height=1000dp,dpi=160")
@Composable
fun HomeLongNamePreview() { MamXanhPreviewTheme { HomeScreen(previewHomeState(AppText.text(TextKey.CopyCayDaoBinhYenBenCuaSoCuaGiaDinh3c7da8)), {}, 2) } }

// Each preview renders the actual composable in isolation, with home-screen sample data.
@Composable
private fun HomeComponentPreviewSurface(content: @Composable () -> Unit) {
    MamXanhPreviewTheme {
        MintBackdrop(Modifier.fillMaxSize()) {
            Box(Modifier.fillMaxSize().padding(AppDimensions.Dp10), contentAlignment = Alignment.Center) { content() }
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
    GardenBotanicalFrame(Modifier.fillMaxWidth(), contentPadding = PaddingValues(AppDimensions.Dp10), cornerSize = AppDimensions.Dp18) {
        Text(AppText.text(TextKey.CopyDaoHongXuanC2fd99), color = Deep)
    }
} }

@Preview(widthDp = 360, heightDp = 35)
@Composable
fun HomeProgressComponentPreview() { HomeComponentPreviewSurface {
    MintProgress(previewHomeState().dashboard.trees.first().status.stageProgress, Modifier.fillMaxWidth().height(AppDimensions.Dp5))
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
    MintCareIcon(GardenArt.FERTILIZER, AppText.text(TextKey.CopyBon2Tui23af29), previewHomeState().dashboard.trees.first().status.canFertilize, {})
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
    MintMetricStrip(listOf(AppText.text(TextKey.CopyHomNayE6747a) to "${d.phase.earnedToday}/${com.mamxanh.shared.model.Balance.DAILY_CAP}",
        AppText.text(TextKey.CopyKeHoach4caeaa) to "${d.completedTasks}/${d.totalTasks}", AppText.text(TextKey.CopyChuoiNgay5ad2e6) to d.phase.streak.toString()))
} }

@Preview(widthDp = 160, heightDp = 60)
@Composable
fun HomeStatusPillComponentPreview() { HomeComponentPreviewSurface { MintStatusPill(AppText.text(TextKey.CopyDangLon40daab)) } }

@Preview(widthDp = 360, heightDp = 80)
@Composable
fun HomeWeeklyReviewComponentPreview() { HomeComponentPreviewSurface {
    HomeLink(GardenArt.CHECKLIST, AppText.text(TextKey.CopyNhinLaiTuanFf3f0c), AppText.text(TextKey.CopyMotKhoangDeDieuChinhNheNhang52ad01), {})
} }

@Preview(widthDp = 360, heightDp = 80)
@Composable
fun HomeAddHabitComponentPreview() { HomeComponentPreviewSurface {
    HomeLink(GardenArt.CHECKLIST, AppText.text(TextKey.CopyThemThoiQuen4d2eb4), AppText.text(TextKey.CopyChonViecNhoChoNgayMoi2e19a4), {})
} }

@Preview(widthDp = 360, heightDp = 80)
@Composable
fun HomeSeedShopComponentPreview() { HomeComponentPreviewSurface {
    HomeLink(GardenArt.SEED, AppText.text(TextKey.CopyChonHatGiongEebad6), AppText.text(TextKey.CopyGheTiemHatB6db1f), {})
} }

@Preview(widthDp = 360, heightDp = 80)
@Composable
fun HomeRewardComponentPreview() { HomeComponentPreviewSurface {
    HomeLink(GardenArt.COIN, AppText.text(TextKey.CopyNhanThuongHomNay7b23cc), AppText.text(TextKey.Copy0Xu7c2391,previewHomeState().dashboard.dailyRewardCoins), {})
} }

@Preview(widthDp = 360, heightDp = 80)
@Composable
fun HomeTaskComponentPreview() { HomeComponentPreviewSurface {
    HomeLink(GardenArt.CHECKLIST, AppText.text(TextKey.CopyDoc10TrangSachA3c873), AppText.text(TextKey.Copy10Xu8c4bbf), {})
} }

@Preview(widthDp = 360, heightDp = 80)
@Composable
fun HomeGoalComponentPreview() { HomeComponentPreviewSurface {
    HomeLink(GardenArt.COIN, AppText.text(TextKey.CopyMucTieuTiepTheo9dbfa7), AppText.text(TextKey.CopyMuaHatGiongMoi35919a), {})
} }

@Preview(widthDp = 80, heightDp = 80)
@Composable
fun HomeEmptySproutComponentPreview() { HomeComponentPreviewSurface { MintEmptySprout(Modifier.size(AppDimensions.Dp55)) } }

@Preview(widthDp = 360, heightDp = 80)
@Composable
fun HomePlantButtonComponentPreview() { HomeComponentPreviewSurface {
    GardenPaintedButton(AppText.text(TextKey.CopyGieoMamDauTien85032c), {}, Modifier.fillMaxWidth())
} }

@Preview(widthDp = 360, heightDp = 100)
@Composable
fun HomeFeedbackComponentPreview() { HomeComponentPreviewSurface {
    MintFeedback(AppText.text(TextKey.CopyDaNhan0Xu9e4321,previewHomeState().dashboard.dailyRewardCoins), {})
} }

@Preview(widthDp = 48, heightDp = 48)
@Composable
fun HomeArtworkComponentPreview() { HomeComponentPreviewSurface { GardenArtwork(GardenArt.CHECKLIST, Modifier.size(AppDimensions.Dp16)) } }
