package com.mamxanh.app.feature.garden.presentation.ui.livinggarden

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.mamxanh.app.core.components.*
import com.mamxanh.app.core.components.components.*
import com.mamxanh.app.core.components.design.AppColors
import com.mamxanh.app.core.components.design.AppDimensions
import com.mamxanh.app.core.components.design.AppOpacity
import com.mamxanh.app.core.components.design.AppThemeEffects
import com.mamxanh.app.core.components.design.AppTypography
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey
import com.mamxanh.shared.model.*

@Composable internal fun Encyclopedia(
    state: LivingGardenUiState,
    onOpen: (String, String) -> Unit,
) {
    val records = state.snapshot.species
    val discovered = records.count { it.discoveredAt > 0 }
    GardenScreenIntro(AppText.text(TextKey.CopyMoiLoaiMotCauChuyen3baf0d))
    GardenPanel {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            GardenHeading(AppText.text(TextKey.CopyBoSuuTam539bd6), Modifier.weight(1f))
            Text(
                "$discovered/${catalog.size} · ${discovered * 100 / catalog.size}%",
                color = Green,
                fontSize = AppTypography.Sp11,
                fontWeight = FontWeight.Bold,
            )
        }
        PlotProgress(discovered.toFloat() / catalog.size)
        GardenCaption(AppText.text(TextKey.Copy0LoaiTrongDanhMucGiuLichSuKhiDonHoacBanCAc2aaf, catalog.size))
    }
    BoxWithConstraints(Modifier.fillMaxWidth()) {
        val columns =
            if (maxWidth < AppDimensions.Dp340 ||
                LocalDensity.current.fontScale > AppThemeEffects.COLLECTION_LARGE_FONT_SCALE
            ) {
                2
            } else {
                3
            }
        Column(verticalArrangement = Arrangement.spacedBy(AppDimensions.Dp12)) {
            catalog.chunked(columns).forEach { row ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(AppDimensions.Dp12)) {
                    row.forEach { s ->
                        val r = records.firstOrNull { it.id == s.id } ?: SpeciesRecord(s.id)
                        Box(Modifier.weight(1f)) {
                            SmallCatalogTile(s.name, r.status, { onOpen("SPECIES", s.id) }) {
                                if (r.discoveredAt > 0) {
                                    GardenPlantPortrait(s.id, "NONE", it)
                                } else {
                                    Icon(
                                        Icons.Rounded.Spa,
                                        null,
                                        it.padding(AppDimensions.Dp5),
                                        tint = Green.copy(alpha = AppOpacity.VALUE_35),
                                    )
                                }
                            }
                        }
                    }
                    repeat(columns - row.size) { Spacer(Modifier.weight(1f)) }
                }
            }
        }
    }
    GardenPanel {
        GardenHeading(AppText.text(TextKey.CopyDauMocSuuTam1b494b))
        state.snapshot.goals.forEachIndexed { index, goal ->
            if (index > 0)HorizontalDivider(color = Green.copy(alpha = AppOpacity.VALUE_08))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(AppDimensions.Dp16)) {
                val reward = gardenCosmetics.firstOrNull { it.id == goal.reward }
                if (reward != null) {
                    CosmeticArtwork(reward, Modifier.size(AppDimensions.Dp22))
                } else {
                    Icon(Icons.Rounded.WorkspacePremium, null, Modifier.size(AppDimensions.Dp22), tint = Green)
                }
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(AppDimensions.Dp3)) {
                    Text(
                        goal.title,
                        color = Deep,
                        fontSize = AppTypography.Sp11,
                        lineHeight = AppTypography.Sp14,
                        fontWeight = FontWeight.SemiBold,
                    )
                    GardenCaption(reward?.name ?: AppText.text(TextKey.CopyHuyHieuBoSuuTam521407))
                    PlotProgress(goal.progress.toFloat() / goal.target)
                }
                if (goal.unlockedAt > 0) {
                    Icon(Icons.Rounded.CheckCircle, null, Modifier.size(AppDimensions.Dp15), tint = Green)
                } else {
                    Text("${goal.progress.coerceAtMost(goal.target)}/${goal.target}", color = Muted, fontSize = AppTypography.Sp10)
                }
            }
        }
    }
}

@Composable internal fun SpeciesDetail(
    state: LivingGardenUiState,
    onTree: (Long) -> Unit,
    onShop: () -> Unit,
) {
    val s =
        catalog.firstOrNull { it.id == state.selected } ?: run {
            GardenCaption(AppText.text(TextKey.CopyLoaiChuaDuocHoTro9107c4))
            return
        }
    val r = state.snapshot.species.firstOrNull { it.id == s.id } ?: SpeciesRecord(s.id)
    GardenBotanicalFrame(
        Modifier.fillMaxWidth(),
        skin = GardenPanelSkin.HINT,
        contentPadding = PaddingValues(AppDimensions.Dp18),
        cornerSize = AppDimensions.Dp28,
    ) {
        Column(
            Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(AppDimensions.Dp8),
        ) {
            MintStatusPill("${s.tier} · ${r.status}")
            if (r.discoveredAt > 0) {
                GardenPlantPortrait(s.id, "NONE", Modifier.fillMaxWidth().height(AppDimensions.Dp220))
            } else {
                Box(Modifier.fillMaxWidth().height(AppDimensions.Dp220), contentAlignment = Alignment.Center) {
                    Icon(Icons.Rounded.Spa, null, Modifier.size(AppDimensions.Dp72), tint = Green)
                }
            }
            Text(s.name, color = Deep, fontSize = AppTypography.Sp26, lineHeight = AppTypography.Sp34, fontWeight = FontWeight.Bold)
            GardenCaption(s.description)
        }
    }
    Column(verticalArrangement = Arrangement.spacedBy(AppDimensions.Dp12)) {
        Row(Modifier.fillMaxWidth()) {
            GardenHeading(AppText.text(TextKey.CopyCacGiaiDoanD4547f), Modifier.weight(1f))
            GardenCaption("${(r.highestStage + 1).coerceAtLeast(0)}/${s.formCount}")
        }
        val columns = 3
        s.profile.stageThresholds.indices.toList().chunked(columns).forEach { row ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(AppDimensions.Dp5)) {
                row.forEach { index ->
                    Column(
                        Modifier
                            .weight(1f)
                            .background(
                                if (index <=
                                    r.highestStage
                                ) {
                                    AppColors.SwatchFFEDF5E9
                                } else {
                                    AppColors.SwatchFFF0F4EF
                                },
                                RoundedCornerShape(AppDimensions.Dp9),
                            ).padding(vertical = AppDimensions.Dp4),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(AppDimensions.Dp2),
                    ) {
                        if (index <= r.highestStage) {
                            GardenPlantPortrait(s.id, "NONE", Modifier.size(AppDimensions.Dp64))
                        } else {
                            Box(Modifier.size(AppDimensions.Dp64), contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Rounded.Lock,
                                    null,
                                    Modifier.size(AppDimensions.Dp14),
                                    tint = Muted.copy(alpha = AppOpacity.VALUE_55),
                                )
                            }
                        }
                        Text((index + 1).toString(), color = Muted, fontSize = AppTypography.Sp9, lineHeight = AppTypography.Sp12)
                    }
                }
                repeat(columns - row.size) { Spacer(Modifier.weight(1f)) }
            }
        }
    }
    GardenPanel {
        listOf(
            AppText.text(TextKey.CopyDaTrong42fa2b) to r.planted.toString(),
            AppText.text(TextKey.CopyTrongTui905deb) to r.ownedSeeds.toString(),
            AppText.text(TextKey.CopyDangSongE3573d) to r.living.toString(),
            AppText.text(TextKey.CopyTrongDauTien2e1aed) to dateLabel(r.firstPlanted),
            AppText.text(TextKey.CopyTruongThanhDauDd677a) to dateLabel(r.firstMatured),
            AppText.text(TextKey.CopyCayCaoNhatDangSong7d778f) to
                if (r.tallestStage >=
                    0
                ) {
                    "${r.tallestStage + 1}/${s.formCount}"
                } else {
                    AppText.text(TextKey.CopyChuaGhiNhanF7654d)
                },
        ).forEachIndexed { index, (label, value) ->
            if (index > 0)HorizontalDivider(color = AppColors.SwatchFFE4E7DC)
            Row(
                Modifier.fillMaxWidth().padding(vertical = AppDimensions.Dp8),
                horizontalArrangement = Arrangement.spacedBy(AppDimensions.Dp12),
            ) {
                Text(label, Modifier.weight(1f), color = Muted, fontSize = AppTypography.Sp12)
                Text(value, color = Deep, fontSize = AppTypography.Sp12)
            }
        }
    }
    GardenExpandableSection(AppText.text(TextKey.CopyChamSocLoaiF520bc)) {
        GardenCaption(AppText.text(TextKey.CopyBinhTuoi8121824GioPhanBonDungKhiCayDangDe84c6))
    }
    state.garden.trees
        .filter { it.speciesId == s.id }
        .forEach { tree -> GardenNavigationCard(tree.name, { onTree(tree.id) }) }
    GardenPaintedButton(AppText.text(TextKey.CopyTimHatA56320), onShop, Modifier.fillMaxWidth())
}

private fun webComparisonGardenState() =
    LivingGardenUiState(
        loading = false,
        route = "STUDIO",
        selected = "accent_lantern",
        snapshot =
            LivingGardenSnapshot(
                coins = 620,
                owned = mapOf("accent_lantern" to 1),
                species =
                    listOf(
                        SpeciesRecord(
                            "cherry",
                            discoveredAt = 1,
                            highestStage = 3,
                            planted = 1,
                            firstPlanted = 1793494800000,
                            ownedSeeds = 2,
                            living = 1,
                            tallestStage = 3,
                        ),
                        SpeciesRecord("pine", discoveredAt = 1, highestStage = 2, planted = 1, living = 1),
                        SpeciesRecord("succulent", discoveredAt = 1, highestStage = 5, planted = 1, firstMatured = 1),
                    ),
                sections =
                    (1..4).map { GardenSection(it, ((it - 1) * 6 + 1..it * 6).toList(), it == 1, 5400) },
            ),
        garden =
            Garden(
                trees =
                    listOf(
                        Tree(
                            1,
                            "cherry",
                            AppText.text(TextKey.CopyDaoHongXuanC2fd99),
                            45,
                            "2026-11-01",
                            status = TreeStatus(stageIndex = 3, stageCount = 9, stageProgress = .46f),
                        ),
                        Tree(2, "pine", AppText.text(TextKey.CopyThongNho84a4db), 24, "2026-11-04"),
                    ),
                plots =
                    (1..24).map { GardenPlot(it, it <= 5, if (it <= 2)it.toLong()else null) },
            ),
    )

@Preview(widthDp = 414, heightDp = 1000, device = "spec:width=414dp,height=1000dp,dpi=160")
@Composable
fun StudioWebDesignPreview() {
    MamXanhPreviewTheme {
        LivingGardenScreen(webComparisonGardenState(), { _, _ -> }, {}, {}, { _, _ -> }, {}, {}, {}, {}, {}, {}, {}, false, {}, true)
    }
}

@Preview(widthDp = 414, heightDp = 1000, device = "spec:width=414dp,height=1000dp,dpi=160")
@Composable
fun CosmeticWebDesignPreview() {
    MamXanhPreviewTheme {
        MintBackdrop {
            Column(Modifier.padding(AppDimensions.Dp24), verticalArrangement = Arrangement.spacedBy(AppDimensions.Dp18)) {
                CosmeticDetail(webComparisonGardenState(), { _, _ -> }, {})
            }
        }
    }
}

@Preview(widthDp = 414, heightDp = 1400, device = "spec:width=414dp,height=1400dp,dpi=160")
@Composable
fun DecorShopWebDesignPreview() {
    MamXanhPreviewTheme {
        LivingGardenScreen(webComparisonGardenState().copy(route = "SHOP", selected = ""), { _, _ -> }, {
        }, {}, { _, _ -> }, {}, {}, {}, {}, {}, {}, {}, false, {}, true)
    }
}

@Preview(widthDp = 414, heightDp = 1400, device = "spec:width=414dp,height=1400dp,dpi=160")
@Composable
fun DecorInventoryWebDesignPreview() {
    MamXanhPreviewTheme {
        LivingGardenScreen(webComparisonGardenState().copy(route = "INVENTORY", selected = ""), { _, _ -> }, {
        }, {}, { _, _ -> }, {}, {}, {}, {}, {}, {}, {}, false, {}, true)
    }
}

@Preview(widthDp = 414, heightDp = 1400, device = "spec:width=414dp,height=1400dp,dpi=160")
@Composable
fun EncyclopediaWebDesignPreview() {
    MamXanhPreviewTheme {
        LivingGardenScreen(webComparisonGardenState().copy(route = "ENCYCLOPEDIA", selected = ""), { _, _ -> }, {
        }, {}, { _, _ -> }, {}, {}, {}, {}, {}, {}, {}, false, {}, true)
    }
}

@Preview(widthDp = 414, heightDp = 1400, device = "spec:width=414dp,height=1400dp,dpi=160")
@Composable
fun SpeciesWebDesignPreview() {
    MamXanhPreviewTheme {
        LivingGardenScreen(webComparisonGardenState().copy(route = "SPECIES", selected = "cherry"), { _, _ -> }, {
        }, {}, { _, _ -> }, {}, {}, {}, {}, {}, {}, {}, false, {}, true)
    }
}

@Preview(widthDp = 414, heightDp = 1400, device = "spec:width=414dp,height=1400dp,dpi=160")
@Composable
fun AppearanceWebDesignPreview() {
    MamXanhPreviewTheme {
        LivingGardenScreen(webComparisonGardenState().copy(route = "APPEARANCE", selected = ""), { _, _ -> }, {
        }, {}, { _, _ -> }, {}, {}, {}, {}, {}, {}, {}, false, {}, true)
    }
}

@Preview(widthDp = 414, heightDp = 1400, device = "spec:width=414dp,height=1400dp,dpi=160")
@Composable
fun ExpansionWebDesignPreview() {
    MamXanhPreviewTheme {
        LivingGardenScreen(webComparisonGardenState().copy(route = "EXPANSION", selected = "2"), { _, _ -> }, {
        }, {}, { _, _ -> }, {}, {}, {}, {}, {}, {}, {}, false, {}, true)
    }
}
