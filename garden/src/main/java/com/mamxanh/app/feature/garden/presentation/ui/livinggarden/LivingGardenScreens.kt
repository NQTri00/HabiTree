package com.mamxanh.app.feature.garden.presentation.ui.livinggarden

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Undo
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.mamxanh.app.core.components.*
import com.mamxanh.app.core.components.components.*
import com.mamxanh.app.core.components.design.AppColors
import com.mamxanh.app.core.components.design.AppDimensions
import com.mamxanh.app.core.components.design.AppOpacity
import com.mamxanh.app.core.components.design.AppTypography
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey
import com.mamxanh.shared.model.*
import kotlin.math.sin

internal fun newKey() =
    java.util.UUID
        .randomUUID()
        .toString()

internal fun dateLabel(at: Long) =
    if (at <=
        0
    ) {
        AppText.text(TextKey.CopyChuaGhiNhanF7654d)
    } else {
        java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault()).format(java.util.Date(at))
    }

@Composable
fun LivingGardenScreen(
    state: LivingGardenUiState,
    onOpen: (String, String) -> Unit,
    onAct: (LivingGardenAction) -> Unit,
    onSelect: (String) -> Unit,
    onPlace: (Int, String?) -> Unit,
    onRemove: (String) -> Unit,
    onUndo: () -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit,
    onTree: (Long) -> Unit,
    onSeedShop: () -> Unit,
    onMemories: () -> Unit,
    reducedMotion: Boolean,
    onReducedMotion: (Boolean) -> Unit,
    active: Boolean,
    modifier: Modifier = Modifier,
    onPlant: (Int) -> Unit = {},
    onBack: () -> Unit = { onOpen("", "") },
) {
    val arranging = false
    val title =
        when (state.route) {
            "SHOP" -> AppText.text(TextKey.CopyTiemTrangTriFbd50e)
            "INVENTORY" -> AppText.text(TextKey.CopyTrangTriCuaBanDa2761)
            "ARRANGE" -> AppText.text(TextKey.CopyGocVuonF4c049)
            "ENCYCLOPEDIA" -> AppText.text(TextKey.CopyBachThaoF2ed64)
            "SPECIES" -> AppText.text(TextKey.CopyHoSoLoaiCfff22)
            "APPEARANCE" -> AppText.text(TextKey.CopyDienMao14bd0c)
            "DETAIL" -> AppText.text(TextKey.CopyTrangTriD48231)
            "EXPANSION" -> AppText.text(TextKey.CopyMoKhuVuon35e0bd)
            else -> state.snapshot.appearance.nickname
        }
    MintBackdrop(modifier.fillMaxSize()) {
        ProvideTextStyle(
            MaterialTheme.typography.bodyMedium.copy(fontSize = AppTypography.Sp13, lineHeight = AppTypography.Sp19, color = Deep),
        ) {
            Column(Modifier.fillMaxSize()) {
                Row(Modifier.fillMaxWidth().padding(horizontal = AppDimensions.Dp4), verticalAlignment = Alignment.CenterVertically) {
                    IconButton({ if (arranging)onCancel() else onBack() }) {
                        Icon(
                            Icons.AutoMirrored.Rounded.ArrowBack,
                            if (arranging)AppText.text(TextKey.CopyHuyBoTriEa6ca2) else AppText.text(TextKey.CopyQuayLai6d7935),
                            Modifier.size(AppDimensions.Dp18),
                            tint = Green,
                        )
                    }
                    Text(
                        title,
                        Modifier.weight(1f),
                        color = Deep,
                        fontSize = AppTypography.Sp13,
                        lineHeight = AppTypography.Sp17,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    if (arranging) {
                        IconButton(onUndo, enabled = state.undoAvailable && !state.busy) {
                            Icon(
                                Icons.AutoMirrored.Rounded.Undo,
                                AppText.text(TextKey.CopyHoanTacE72ff6),
                                Modifier.size(AppDimensions.Dp18),
                                tint = if (state.undoAvailable)Green else Muted,
                            )
                        }
                        GardenCompactAction(
                            AppText.text(TextKey.CopyLuuE91f26),
                            onSave,
                            primary = true,
                            enabled = !state.busy && !state.loading,
                            icon = Icons.Rounded.Check,
                        )
                    } else {
                        MintCoinBadge(state.snapshot.coins)
                        Spacer(Modifier.width(AppDimensions.Dp6))
                    }
                }
                if (state.loading || state.busy)LinearProgressIndicator(Modifier.fillMaxWidth().height(AppDimensions.Dp2))
                if (state.message.isNotBlank()) {
                    Text(
                        state.message,
                        Modifier.fillMaxWidth().padding(horizontal = AppDimensions.Dp12, vertical = AppDimensions.Dp3),
                        color = Green,
                        fontSize = AppTypography.Sp12,
                        lineHeight = AppTypography.Sp17,
                    )
                }
                when (state.route) {
                    "STUDIO", "ARRANGE" -> GardenStudio(state, onOpen, onSelect, onPlace, onRemove, onTree, onPlant, reducedMotion, active)
                    "SHOP", "INVENTORY" -> CosmeticCatalog(state, onOpen)
                    else ->
                        key(state.route, state.selected) {
                            val scroll = rememberScrollState()
                            Column(
                                Modifier
                                    .weight(1f)
                                    .fillMaxWidth()
                                    .mintScrollEdges(scroll.canScrollBackward, scroll.canScrollForward)
                                    .verticalScroll(scroll)
                                    .padding(horizontal = AppDimensions.Dp24, vertical = AppDimensions.Dp12),
                                verticalArrangement = Arrangement.spacedBy(AppDimensions.Dp12),
                            ) {
                                when (state.route) {
                                    "EXPANSION" -> ExpansionContent(state, onAct)
                                    "DETAIL" -> CosmeticDetail(state, onOpen, onAct)
                                    "ENCYCLOPEDIA" -> Encyclopedia(state, onOpen)
                                    "SPECIES" -> SpeciesDetail(state, onTree, onSeedShop)
                                    "APPEARANCE" -> AppearanceContent(state, reducedMotion, onReducedMotion, onAct, onMemories)
                                }
                            }
                        }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ColumnScope.GardenStudio(
    state: LivingGardenUiState,
    onOpen: (String, String) -> Unit,
    onSelect: (String) -> Unit,
    onPlace: (Int, String?) -> Unit,
    onRemove: (String) -> Unit,
    onTree: (Long) -> Unit,
    onPlant: (Int) -> Unit,
    reducedMotion: Boolean,
    active: Boolean,
) {
    var section by rememberSaveable { mutableIntStateOf(1) }
    val current = state.snapshot.sections.firstOrNull { it.id == section }
    Column(
        Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = AppDimensions.Dp24, vertical = AppDimensions.Dp12),
        verticalArrangement = Arrangement.spacedBy(AppDimensions.Dp18),
    ) {
        GardenScreenIntro(state.snapshot.appearance.nickname, AppText.text(TextKey.CopyKhongGianCuaBanD45f67))
        GardenNavigationCard(AppText.text(TextKey.CopyBachThaoF2ed64), { onOpen("ENCYCLOPEDIA", "") }, art = GardenArt.BOOK)
        Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(AppDimensions.Dp8)) {
            state.snapshot.sections.forEach { s ->
                GardenPill("Khu ${s.id}", section == s.id, { section = s.id }, icon = if (s.unlocked)null else Icons.Rounded.Lock)
            }
        }
        if (current != null && !current.unlocked) {
            GardenPanel {
                GardenHeading(AppText.text(TextKey.CopyKhu0DangKhoaBdcfda, section))
                GardenCaption(AppText.text(TextKey.Copy0XuThem6OBd7dbb, current.price))
                GardenPaintedButton(
                    AppText.text(TextKey.CopyXemMoKhu28c09c),
                    { onOpen("EXPANSION", section.toString()) },
                    Modifier.fillMaxWidth(),
                )
            }
        } else {
            state.garden.plots.filter { it.id in (current?.plots ?: emptyList()) }.chunked(2).forEach { row ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(AppDimensions.Dp14)) {
                    row.forEach { plot ->
                        val tree = state.garden.trees.firstOrNull { it.id == plot.treeId }
                        val placed = state.snapshot.placements.filter { it.plotId == plot.id }
                        GardenBotanicalFrame(
                            Modifier.weight(1f).clickable {
                                if (tree !=
                                    null
                                ) {
                                    onTree(tree.id)
                                } else if (plot.isUnlocked) {
                                    onPlant(plot.id)
                                }
                            },
                            contentPadding = PaddingValues(AppDimensions.Dp12),
                            cornerSize = AppDimensions.Dp20,
                        ) {
                            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(AppDimensions.Dp8)) {
                                GardenCaption(plot.id.toString().padStart(2, '0'))
                                Box(
                                    Modifier
                                        .fillMaxWidth()
                                        .height(
                                            AppDimensions.Dp84,
                                        ).background(Pale, RoundedCornerShape(AppDimensions.Dp14)),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    if (tree !=
                                        null
                                    ) {
                                        GardenPlantPortrait(
                                            tree.speciesId,
                                            tree.decoration,
                                            Modifier.fillMaxSize(),
                                            withered = !tree.status.alive,
                                        )
                                    } else {
                                        Icon(if (plot.isUnlocked)Icons.Rounded.Add else Icons.Rounded.Lock, null, tint = Green)
                                    }
                                }
                                Text(
                                    tree?.name ?: if (plot.isUnlocked) {
                                        AppText.text(
                                            TextKey.CopyGieoCay31adde,
                                        )
                                    } else {
                                        AppText.text(TextKey.CopyChuaMoB744b3)
                                    },
                                    color = Deep,
                                    fontSize = AppTypography.Sp13,
                                    lineHeight = AppTypography.Sp18,
                                    fontWeight = FontWeight.SemiBold,
                                )
                                if (tree !=
                                    null
                                ) {
                                    MintProgress(tree.status.stageProgress, Modifier.fillMaxWidth())
                                    GardenCaption("${tree.status.percentage}%")
                                    GardenCaption(tree.status.waterLabel)
                                } else {
                                    GardenCaption(
                                        if (plot.isUnlocked) {
                                            AppText.text(
                                                TextKey.CopyMotKhoiDauMoi280995,
                                            )
                                        } else {
                                            AppText.text(TextKey.CopyODangKhoa0b475c)
                                        },
                                    )
                                }
                                if (placed.isNotEmpty())GardenCaption(AppText.text(TextKey.Copy0TrangTriA468bd, placed.size))
                            }
                        }
                    }
                    if (row.size == 1)Spacer(Modifier.weight(1f))
                }
            }
        }
        GardenNavigationCard(AppText.text(TextKey.CopyTiemTrangTriFbd50e), { onOpen("SHOP", "") }, art = GardenArt.SHOP)
        GardenNavigationCard(AppText.text(TextKey.CopyTrangTriCuaBanDa2761), { onOpen("INVENTORY", "") }, art = GardenArt.SEED)
        GardenNavigationCard(AppText.text(TextKey.CopyDienMao14bd0c), { onOpen("APPEARANCE", "") }, art = GardenArt.LANTERN)
    }
}

@Composable private fun GardenParticles(low: Boolean) {
    val transition = rememberInfiniteTransition(label = "garden-ambience")
    val fraction by transition.animateFloat(0f, 1f, infiniteRepeatable(tween(10000, easing = LinearEasing)), label = "petals")
    Canvas(Modifier.fillMaxSize()) {
        repeat(if (low)4 else 8) { i ->
            drawCircle(
                AppColors.SwatchFFE6E6AC.copy(alpha = AppOpacity.VALUE_45),
                AppDimensions.Dp3.toPx(),
                Offset(
                    ((i * .173f + .12f) % 1f) * size.width + sin((fraction + i) * 6.28f) * AppDimensions.Dp12.toPx(),
                    ((fraction + i * .137f) % 1f) * size.height,
                ),
            )
        }
    }
}

@Preview(name = "Garden appearance", widthDp = 375, heightDp = 812)
@Composable
private fun LivingGardenScreenPreview() {
    MamXanhPreviewTheme {
        LivingGardenScreen(
            state = LivingGardenUiState(route = "APPEARANCE", loading = false),
            onOpen = { _, _ -> },
            onAct = {},
            onSelect = {},
            onPlace = { _, _ -> },
            onRemove = {},
            onUndo = {},
            onSave = {},
            onCancel = {},
            onTree = {},
            onSeedShop = {},
            onMemories = {},
            reducedMotion = true,
            onReducedMotion = {},
            active = true,
        )
    }
}
