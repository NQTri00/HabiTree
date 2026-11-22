package com.mamxanh.app.feature.home.presentation.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.mamxanh.app.core.components.*
import com.mamxanh.app.core.components.components.*
import com.mamxanh.app.core.components.design.AppDimensions
import com.mamxanh.app.feature.home.domain.model.*
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey

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
fun HomeBackdropComponentPreview() {
    MamXanhPreviewTheme { MintBackdrop(Modifier.fillMaxSize()) {} }
}

@Preview(widthDp = 360, heightDp = 60)
@Composable
fun HomeHeaderComponentPreview() {
    HomeComponentPreviewSurface { MintCompactHeader(120) }
}

@Preview(widthDp = 100, heightDp = 60)
@Composable
fun HomeCoinBadgeComponentPreview() {
    HomeComponentPreviewSurface { MintCoinBadge(120) }
}

@Preview(widthDp = 360, heightDp = 90)
@Composable
fun HomeFrameComponentPreview() {
    HomeComponentPreviewSurface {
        GardenBotanicalFrame(Modifier.fillMaxWidth(), contentPadding = PaddingValues(AppDimensions.Dp10), cornerSize = AppDimensions.Dp18) {
            Text(AppText.text(TextKey.CopyDaoHongXuanC2fd99), color = Deep)
        }
    }
}

@Preview(widthDp = 360, heightDp = 35)
@Composable
fun HomeProgressComponentPreview() {
    HomeComponentPreviewSurface {
        MintProgress(
            previewHomeState()
                .dashboard.trees
                .first()
                .status.stageProgress,
            Modifier.fillMaxWidth().height(AppDimensions.Dp5),
        )
    }
}

@Preview(widthDp = 288, heightDp = 320)
@Composable
fun HomePlantComponentPreview() {
    HomeComponentPreviewSurface {
        val tree = previewHomeState().dashboard.trees.first()
        GardenPlantPortrait(
            tree.speciesId,
            tree.decoration,
            Modifier.fillMaxSize(),
            growth = tree.growth,
            fitVisibleArtwork = true,
            artworkAlignment = Alignment.Center,
            withered = !tree.status.alive,
        )
    }
}

@Preview(widthDp = 80, heightDp = 80)
@Composable
fun HomeWaterComponentPreview() {
    HomeComponentPreviewSurface {
        val tree = previewHomeState().dashboard.trees.first()
        MintCareIcon(
            GardenArt.WATERING_CAN,
            tree.status.waterLabel.ifBlank {
                AppText.text(TextKey.CopyCanTuoiA0957f)
            },
            tree.status.canWater,
            {},
            checked = tree.status.waterActive,
        )
    }
}

@Preview(widthDp = 80, heightDp = 80)
@Composable
fun HomeFertilizerComponentPreview() {
    HomeComponentPreviewSurface {
        MintCareIcon(
            GardenArt.FERTILIZER,
            AppText.text(
                TextKey.CopyBon2Tui23af29,
            ),
            previewHomeState()
                .dashboard.trees
                .first()
                .status.canFertilize,
            {
            },
        )
    }
}

@Preview(widthDp = 340, heightDp = 60)
@Composable
fun HomeTreeStatusComponentPreview() {
    HomeComponentPreviewSurface {
        val status =
            previewHomeState()
                .dashboard.trees
                .first()
                .status
        MintTreeStatus(
            "${status.stageIndex + 1}/${status.stageCount}",
            status.stateLabel.ifBlank { AppText.text(TextKey.CopyDangLon40daab) },
            status.waterLabel.ifBlank { AppText.text(TextKey.CopyCanTuoiA0957f) },
        )
    }
}

@Preview(widthDp = 340, heightDp = 70)
@Composable
fun HomeMetricsComponentPreview() {
    HomeComponentPreviewSurface {
        val d = previewHomeState().dashboard
        MintMetricStrip(
            listOf(
                AppText.text(TextKey.CopyHomNayE6747a) to "${d.earnedToday}/${com.mamxanh.shared.model.Balance.DAILY_CAP}",
                AppText.text(TextKey.CopyKeHoach4caeaa) to "${d.completedTasks}/${d.totalTasks}",
                AppText.text(TextKey.CopyChuoiNgay5ad2e6) to d.streak.toString(),
            ),
        )
    }
}

@Preview(widthDp = 160, heightDp = 60)
@Composable
fun HomeStatusPillComponentPreview() {
    HomeComponentPreviewSurface { MintStatusPill(AppText.text(TextKey.CopyDangLon40daab)) }
}

@Preview(widthDp = 360, heightDp = 80)
@Composable
fun HomeWeeklyReviewComponentPreview() {
    HomeComponentPreviewSurface {
        HomeLink(
            GardenArt.CHECKLIST,
            AppText.text(TextKey.CopyNhinLaiTuanFf3f0c),
            AppText.text(TextKey.CopyMotKhoangDeDieuChinhNheNhang52ad01),
            {},
        )
    }
}

@Preview(widthDp = 360, heightDp = 80)
@Composable
fun HomeAddHabitComponentPreview() {
    HomeComponentPreviewSurface {
        HomeLink(
            GardenArt.CHECKLIST,
            AppText.text(TextKey.CopyThemThoiQuen4d2eb4),
            AppText.text(TextKey.CopyChonViecNhoChoNgayMoi2e19a4),
            {},
        )
    }
}

@Preview(widthDp = 360, heightDp = 80)
@Composable
fun HomeSeedShopComponentPreview() {
    HomeComponentPreviewSurface {
        HomeLink(GardenArt.SEED, AppText.text(TextKey.CopyChonHatGiongEebad6), AppText.text(TextKey.CopyGheTiemHatB6db1f), {})
    }
}

@Preview(widthDp = 360, heightDp = 80)
@Composable
fun HomeRewardComponentPreview() {
    HomeComponentPreviewSurface {
        HomeLink(
            GardenArt.COIN,
            AppText.text(
                TextKey.CopyNhanThuongHomNay7b23cc,
            ),
            AppText.text(TextKey.Copy0Xu7c2391, previewHomeState().dashboard.dailyRewardCoins),
            {
            },
        )
    }
}

@Preview(widthDp = 360, heightDp = 80)
@Composable
fun HomeTaskComponentPreview() {
    HomeComponentPreviewSurface {
        HomeLink(GardenArt.CHECKLIST, AppText.text(TextKey.CopyDoc10TrangSachA3c873), AppText.text(TextKey.Copy10Xu8c4bbf), {})
    }
}

@Preview(widthDp = 360, heightDp = 80)
@Composable
fun HomeGoalComponentPreview() {
    HomeComponentPreviewSurface {
        HomeLink(GardenArt.COIN, AppText.text(TextKey.CopyMucTieuTiepTheo9dbfa7), AppText.text(TextKey.CopyMuaHatGiongMoi35919a), {})
    }
}

@Preview(widthDp = 80, heightDp = 80)
@Composable
fun HomeEmptySproutComponentPreview() {
    HomeComponentPreviewSurface { MintEmptySprout(Modifier.size(AppDimensions.Dp55)) }
}

@Preview(widthDp = 360, heightDp = 80)
@Composable
fun HomePlantButtonComponentPreview() {
    HomeComponentPreviewSurface {
        GardenPaintedButton(AppText.text(TextKey.CopyGieoMamDauTien85032c), {}, Modifier.fillMaxWidth())
    }
}

@Preview(widthDp = 360, heightDp = 100)
@Composable
fun HomeFeedbackComponentPreview() {
    HomeComponentPreviewSurface {
        MintFeedback(AppText.text(TextKey.CopyDaNhan0Xu9e4321, previewHomeState().dashboard.dailyRewardCoins), {})
    }
}

@Preview(widthDp = 48, heightDp = 48)
@Composable
fun HomeArtworkComponentPreview() {
    HomeComponentPreviewSurface { GardenArtwork(GardenArt.CHECKLIST, Modifier.size(AppDimensions.Dp16)) }
}
