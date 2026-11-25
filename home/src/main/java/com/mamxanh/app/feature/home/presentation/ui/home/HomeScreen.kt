package com.mamxanh.app.feature.home.presentation.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.mamxanh.app.core.components.*
import com.mamxanh.app.core.components.components.*
import com.mamxanh.app.core.components.design.AppColors
import com.mamxanh.app.core.components.design.AppDimensions
import com.mamxanh.app.core.components.design.AppTypography
import com.mamxanh.app.feature.home.domain.model.*
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey

@Composable
fun HomeScreen(
    state: HomeState,
    onIntent: (HomeAction) -> Unit,
    fertilizerCount: Int = 0,
    modifier: Modifier = Modifier,
    sharedTreeModifier: Modifier = Modifier,
    taskTitle: String = AppText.text(TextKey.CopyMoiViecNhoMotBuocLonLen80104a),
    onTaskAction: (Long, Boolean) -> Unit = { _, _ -> onIntent(HomeAction.OpenTasks) },
    canClaimBonus: Boolean = false,
    onClaimBonus: () -> Unit = { onIntent(HomeAction.OpenTasks) },
    onWeeklyReview: () -> Unit = {},
    onJourney: () -> Unit = {},
) {
    val d = state.dashboard
    val tree = d.trees.firstOrNull()
    MintBackdrop(modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize()) {
            MintCompactHeader(d.coins, title = AppText.text(TextKey.CopyMamXanh692a0a), actions = {
                IconButton(onJourney) { GardenArtwork(GardenArt.CATEGORY, Modifier.size(AppDimensions.Dp22)) }
            })
            Column(
                Modifier
                    .weight(
                        1f,
                    ).verticalScroll(rememberScrollState())
                    .padding(horizontal = AppDimensions.Dp24, vertical = AppDimensions.Dp12),
                verticalArrangement = Arrangement.spacedBy(AppDimensions.Dp20),
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(AppDimensions.Dp6)) {
                    Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            AppText.text(TextKey.CopyMotNgayMoiCc8267),
                            color = Muted,
                            fontSize = AppTypography.Sp10,
                            letterSpacing = AppTypography.Sp2,
                        )
                        MintStatusPill(
                            java.text.SimpleDateFormat("EEE, dd/MM", java.util.Locale.forLanguageTag("vi-VN")).format(java.util.Date()),
                        )
                    }
                    Text(
                        AppText.text(TextKey.CopyChaoNgayXanh71726f),
                        color = Deep,
                        fontSize = AppTypography.Sp27,
                        lineHeight = AppTypography.Sp34,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(AppText.text(TextKey.CopyMoiViecNhoMotMamLonF6a672), color = Muted, fontSize = AppTypography.Sp12)
                }
                if (tree !=
                    null
                ) {
                    GardenBotanicalFrame(
                        Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(AppDimensions.Dp18),
                        cornerSize = AppDimensions.Dp28,
                        skin = GardenPanelSkin.HINT,
                    ) {
                        Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(AppDimensions.Dp8)) {
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                MintStatusPill(tree.status.stateLabel.ifBlank { AppText.text(TextKey.CopyDangLon40daab) })
                                TextButton(
                                    { onIntent(HomeAction.OpenTree(tree.id)) },
                                ) { Text("›", color = Green, fontSize = AppTypography.Sp24) }
                            }
                            Box(Modifier.fillMaxWidth().height(AppDimensions.Dp220), contentAlignment = Alignment.Center) {
                                Box(
                                    Modifier
                                        .size(
                                            AppDimensions.Dp220,
                                        ).background(AppColors.SwatchFFE1EACF, androidx.compose.foundation.shape.CircleShape),
                                )
                                GardenPlantPortrait(
                                    tree.speciesId,
                                    tree.decoration,
                                    sharedTreeModifier
                                        .fillMaxWidth()
                                        .height(AppDimensions.Dp220)
                                        .clickable(role = Role.Button) { onIntent(HomeAction.OpenTree(tree.id)) }
                                        .semantics { contentDescription = AppText.text(TextKey.CopyChamSoc5b1872) + tree.name },
                                    growth = tree.growth,
                                    fitVisibleArtwork = true,
                                    artworkAlignment = Alignment.Center,
                                    withered = !tree.status.alive,
                                )
                            }
                            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                Column(Modifier.weight(1f)) {
                                    Text(
                                        tree.name,
                                        color = Deep,
                                        fontSize = AppTypography.Sp19,
                                        lineHeight = AppTypography.Sp25,
                                        fontWeight = FontWeight.Bold,
                                    )
                                    Text(
                                        tree.status.stageLabel.ifBlank { AppText.text(TextKey.CopyGiaiDoan01C0a0f8, 1, 6) },
                                        color = Muted,
                                        fontSize = AppTypography.Sp12,
                                    )
                                }
                                MintStatusPill(tree.status.waterLabel.ifBlank { AppText.text(TextKey.CopyCanTuoiA0957f) })
                            }
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(AppText.text(TextKey.CopyTienDoGiaiDoanC1f3ab), color = Muted, fontSize = AppTypography.Sp11)
                                Text("${tree.status.percentage}%", color = Green, fontSize = AppTypography.Sp11)
                            }
                            MintProgress(tree.status.stageProgress, Modifier.fillMaxWidth())
                            if (tree.status.fertilizerLabel.isNotBlank())MintStatusPill(tree.status.fertilizerLabel)
                            Row(horizontalArrangement = Arrangement.spacedBy(AppDimensions.Dp10)) {
                                GardenPaintedButton(
                                    if (tree.status.waterActive) {
                                        AppText.text(
                                            TextKey.CopyDaTuoiDe2042,
                                        )
                                    } else {
                                        AppText.text(TextKey.CopyTuoiCay0990f6)
                                    },
                                    {
                                        onIntent(HomeAction.WaterTree(tree.id))
                                    },
                                    Modifier.weight(1f),
                                    enabled = tree.status.canWater,
                                )
                                GardenPaintedButton(
                                    AppText.text(TextKey.CopyBonPhan095a3dd, fertilizerCount),
                                    { onIntent(HomeAction.FertilizeTree(tree.id)) },
                                    Modifier.weight(1f),
                                    enabled =
                                        fertilizerCount > 0 && tree.status.canFertilize,
                                    primary = false,
                                )
                            }
                        }
                    }
                } else {
                    GardenBotanicalFrame(Modifier.fillMaxWidth(), skin = GardenPanelSkin.HINT) {
                        Column(verticalArrangement = Arrangement.spacedBy(AppDimensions.Dp12)) {
                            Text(
                                AppText.text(TextKey.CopyGieoMotKhoiDau82f23a),
                                color = Deep,
                                fontSize = AppTypography.Sp22,
                                fontWeight = FontWeight.Bold,
                            )
                            GardenPaintedButton(
                                AppText.text(TextKey.CopyChonHat3d6476),
                                { onIntent(HomeAction.OpenGarden) },
                                Modifier.fillMaxWidth(),
                            )
                        }
                    }
                }
                MintMetricStrip(
                    listOf(
                        AppText.text(TextKey.CopyXuHomNay0c44ed) to "${d.earnedToday}/${com.mamxanh.shared.model.Balance.DAILY_CAP}",
                        AppText.text(TextKey.CopyViecDaXong4cbf90) to "${d.completedTasks}/${d.totalTasks}",
                        AppText.text(TextKey.CopyNgayDeuDan31b8f2) to "${d.streak}",
                    ),
                )
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        AppText.text(TextKey.CopyViecTiepTheoEa34c3),
                        Modifier.weight(1f),
                        color = Deep,
                        fontSize = AppTypography.Sp18,
                        fontWeight = FontWeight.Bold,
                    )
                    TextButton(
                        { onIntent(HomeAction.OpenTasks) },
                    ) { Text(AppText.text(TextKey.CopyTatCa2553fa), color = Green, fontSize = AppTypography.Sp12) }
                }
                if (d.nextTasks.isEmpty()) {
                    HomeLink(
                        GardenArt.CHECKLIST,
                        AppText.text(
                            TextKey.CopyThemThoiQuen4d2eb4,
                        ),
                        AppText.text(TextKey.CopyMotViecVuaSuc2272cf),
                        {
                            onIntent(HomeAction.OpenTasks)
                        },
                    )
                }
                d.nextTasks.take(1).forEach { task ->
                    HomeNextTask(task, { onIntent(HomeAction.OpenTasks) }, { onTaskAction(task.id, task.timer) })
                }
                if (canClaimBonus &&
                    d.dailyRewardDate.isBlank()
                ) {
                    HomeLink(
                        GardenArt.COIN,
                        AppText.text(TextKey.CopyThuongHomNayC015c6),
                        AppText.text(TextKey.Copy0Xu7c2391, d.dailyRewardCoins),
                        onClaimBonus,
                    )
                }
                Text(AppText.text(TextKey.CopyDanhChoBan785034), color = Deep, fontSize = AppTypography.Sp18, fontWeight = FontWeight.Bold)
                if (d.nextGoal.isNotEmpty()) {
                    HomeLink(GardenArt.COIN, AppText.text(TextKey.CopyMucTieuTiepTheo9dbfa7), d.nextGoal, {
                        onIntent(HomeAction.OpenShop)
                    })
                }
                HomeLink(
                    GardenArt.CALENDAR,
                    AppText.text(TextKey.CopyNhinLaiTuanFf3f0c),
                    AppText.text(TextKey.CopyMotTuanNhieuDieuNho4ca68f),
                    onWeeklyReview,
                )
                if (d.seedCount ==
                    0
                ) {
                    HomeLink(GardenArt.SEED, AppText.text(TextKey.CopyChonHatGiongEebad6), AppText.text(TextKey.CopyGheTiemNho8395ee), {
                        onIntent(HomeAction.OpenShop)
                    })
                }
            }
        }
        if (state.isDailyClearVisible) {
            MintFeedback(AppText.text(TextKey.CopyDaNhan0Xu9e4321, d.dailyRewardCoins), {
                onIntent(HomeAction.DismissDailyClear)
            })
        }
    }
}

internal fun previewHomeState(treeName: String = AppText.text(TextKey.CopyDaoHongXuanC2fd99)) =
    HomeState(
        HomeDashboard(
            trees = listOf(HomeTree(1L, "cherry", treeName, 120, "FLOWERS", false)),
            completedTasks = 2,
            totalTasks = 4,
            coins = 120,
            equippedCan = "can_basic",
        ),
        false,
    )

@Preview(widthDp = 360, heightDp = 640, device = "spec:width=360dp,height=640dp,dpi=160")
@Composable
fun HomeScreenPreview() {
    MamXanhPreviewTheme { HomeScreen(previewHomeState(), {}, 2) }
}

@Preview(name = "Web design comparison", widthDp = 414, heightDp = 1200, device = "spec:width=414dp,height=1200dp,dpi=160")
@Composable
fun HomeWebDesignPreview() {
    MamXanhPreviewTheme {
        val base = previewHomeState()
        HomeScreen(
            base.copy(
                dashboard =
                    base.dashboard.copy(
                        coins = 620,
                        completedTasks = 1,
                        seedCount = 2,
                        nextGoal = AppText.text(TextKey.CopyChonHatChoGocVuon3c30f3),
                        trees =
                            base.dashboard.trees.map {
                                it.copy(
                                    status =
                                        it.status.copy(
                                            percentage = 46,
                                            stageProgress = .46f,
                                            stageIndex = 3,
                                            stageCount = 9,
                                            stageLabel = AppText.text(TextKey.CopyGiaiDoan01C0a0f8, 4, 9),
                                        ),
                                )
                            },
                        nextTasks =
                            listOf(
                                HomeTaskSummary(
                                    1,
                                    AppText.text(TextKey.CopyDoc10TrangSachA3c873),
                                    AppText.canonical(TextKey.CopyHocTapA51516),
                                    8,
                                ),
                            ),
                        earnedToday = 24,
                        streak = 5,
                    ),
            ),
            {},
            3,
        )
    }
}

@Preview(widthDp = 360, heightDp = 640, device = "spec:width=360dp,height=640dp,dpi=160")
@Composable
fun HomeScreenEmptyPreview() {
    MamXanhPreviewTheme { HomeScreen(HomeState(HomeDashboard(coins = 60), false), {}) }
}

@Preview(widthDp = 320, heightDp = 1000, fontScale = 1.3f, device = "spec:width=320dp,height=1000dp,dpi=160")
@Composable
fun HomeLongNamePreview() {
    MamXanhPreviewTheme { HomeScreen(previewHomeState(AppText.text(TextKey.CopyCayDaoBinhYenBenCuaSoCuaGiaDinh3c7da8)), {}, 2) }
}

// Each preview renders the actual composable in isolation, with home-screen sample data.
