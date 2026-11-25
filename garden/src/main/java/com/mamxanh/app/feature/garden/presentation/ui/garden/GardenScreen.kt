package com.mamxanh.app.feature.garden.presentation.ui.garden

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.rounded.Inventory2
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.mamxanh.app.core.components.*
import com.mamxanh.app.core.components.components.*
import com.mamxanh.app.core.components.design.AppColors
import com.mamxanh.app.core.components.design.AppDimensions
import com.mamxanh.app.core.components.design.AppTypography
import com.mamxanh.app.feature.garden.domain.model.*
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey
import com.mamxanh.shared.model.catalog
import com.mamxanh.app.core.components.components.GardenRichText as Text

@Composable
fun GardenScreen(
    state: GardenState,
    onIntent: (GardenIntent) -> Unit,
    modifier: Modifier = Modifier,
    treeTransitionModifier: @Composable (Long) -> Modifier = { Modifier },
    onOpenInventory: (Int?) -> Unit = {},
    plantingSeedId: String? = null,
    plantingPlotId: Int? = null,
    onDismissPlanting: () -> Unit = {},
    onLiving: (String, String) -> Unit = { _, _ -> },
) {
    val garden = state.garden
    val listState = rememberLazyGridState()
    var selectedPlotId by rememberSaveable(plantingSeedId, plantingPlotId) { mutableStateOf(plantingPlotId) }
    var unlockingPlotId by rememberSaveable { mutableStateOf<Int?>(null) }
    val selectedSeed = catalog.firstOrNull { it.id == plantingSeedId }
    val plots =
        garden.plots.ifEmpty {
            // Preview/legacy callers still get explicit empty slots without fake saved trees.
            val count = maxOf(5, garden.trees.size)
            (1..count).map { GardenPlot(it, true, garden.trees.getOrNull(it - 1)?.id) }
        }
    var filter by rememberSaveable { mutableIntStateOf(0) }
    var waterAllKey by rememberSaveable { mutableStateOf<String?>(null) }
    val visiblePlots = visibleGardenPlots(garden, plots, filter)
    MintBackdrop(modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize()) {
            MintCompactHeader(garden.coins, actions = {
                IconButton({
                    onOpenInventory(null)
                }) { Icon(androidx.compose.material.icons.Icons.Rounded.Inventory2, AppText.text(TextKey.CopyTuiDo6a8e55), tint = Green) }
            })
            GardenScreenIntro(
                AppText.text(
                    TextKey.CopyCayTrongVuon6565bb,
                ),
                AppText.text(TextKey.CopyGocNhoCuaBanBa1417),
                AppText.text(
                    TextKey.Copy0Cay1OMo528d81,
                    garden.trees.size,
                    plots.count {
                        it.isUnlocked
                    },
                ),
                Modifier.padding(horizontal = AppDimensions.Dp24, vertical = AppDimensions.Dp12),
            )
            Row(
                Modifier.fillMaxWidth().padding(horizontal = AppDimensions.Dp24, vertical = AppDimensions.Dp8),
                horizontalArrangement = Arrangement.spacedBy(AppDimensions.Dp12),
            ) {
                GardenPaintedButton(
                    AppText.text(TextKey.CopyTuoiCaVuon20a14a),
                    {
                        waterAllKey =
                            java.util.UUID
                                .randomUUID()
                                .toString()
                    },
                    Modifier.weight(1f),
                    enabled =
                        garden.waterAll.eligible > 0 && !state.isLoading,
                )
                GardenPaintedButton(
                    AppText.text(TextKey.CopyGocVuonF4c049),
                    { onLiving("STUDIO", "") },
                    Modifier.weight(1f),
                    primary = false,
                )
            }
            Column(
                Modifier.fillMaxWidth().padding(horizontal = AppDimensions.Dp24),
                verticalArrangement = Arrangement.spacedBy(AppDimensions.Dp2),
            ) {
                Row(
                    Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(AppDimensions.Dp4),
                ) {
                    listOf(
                        AppText.text(TextKey.CopyTatCa49c73a),
                        AppText.text(TextKey.CopyCanTuoiA0957f),
                        AppText.text(TextKey.CopyDangLon40daab),
                        AppText.text(TextKey.CopyTruongThanh8c14d7),
                        AppText.text(TextKey.CopyKhoHeoBf546a),
                    ).forEachIndexed {
                        index,
                        label,
                        ->
                        FilterChip(
                            selected = filter == index,
                            onClick = { filter = index },
                            label = { Text(label, fontSize = AppTypography.Sp11, lineHeight = AppTypography.Sp14) },
                            shape = RoundedCornerShape(AppDimensions.Dp14),
                            colors =
                                FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Green,
                                    selectedLabelColor = AppColors.White,
                                    labelColor = Deep,
                                ),
                        )
                    }
                }
            }
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.weight(1f).mintScrollEdges(listState.canScrollBackward, listState.canScrollForward),
                state = listState,
                contentPadding = PaddingValues(horizontal = AppDimensions.Dp24, vertical = AppDimensions.Dp16),
                horizontalArrangement = Arrangement.spacedBy(AppDimensions.Dp14),
                verticalArrangement = Arrangement.spacedBy(AppDimensions.Dp14),
            ) {
                if (visiblePlots.isEmpty()) item { Text(AppText.text(TextKey.CopyChuaCoCayPhuHop75e67d), color = Muted) }
                items(visiblePlots, key = { it.id }) { plot ->
                    val tree = garden.trees.firstOrNull { it.id == plot.treeId }
                    GardenBotanicalFrame(
                        Modifier.fillMaxWidth().clickable(enabled = !state.isLoading) {
                            if (tree !=
                                null
                            ) {
                                onIntent(GardenIntent.OpenTree(tree.id))
                            } else if (plot.isUnlocked) {
                                onOpenInventory(plot.id)
                            } else {
                                unlockingPlotId =
                                    plot.id
                            }
                        },
                        contentPadding = PaddingValues(AppDimensions.Dp12),
                        cornerSize = AppDimensions.Dp20,
                    ) {
                        Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(AppDimensions.Dp9)) {
                            Text(plot.id.toString().padStart(2, '0'), color = Muted, fontSize = AppTypography.Sp10)
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .height(
                                        AppDimensions.Dp108,
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
                                        growth = tree.growth,
                                        withered = !tree.status.alive,
                                    )
                                } else {
                                    Text(if (plot.isUnlocked)"+" else "◇", color = Green, fontSize = AppTypography.Sp30)
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
                            if (tree != null) {
                                MintProgress(tree.status.stageProgress, Modifier.fillMaxWidth())
                                Text(
                                    "${tree.status.percentage}% · ${tree.status.stateLabel.ifBlank {
                                        AppText.text(
                                            TextKey.CopyDangLon40daab,
                                        )
                                    }}",
                                    color = Muted,
                                    fontSize = AppTypography.Sp10,
                                    lineHeight = AppTypography.Sp14,
                                )
                                Text(
                                    tree.status.waterLabel.ifBlank { AppText.text(TextKey.CopyCanTuoiA0957f) },
                                    color = Muted,
                                    fontSize = AppTypography.Sp11,
                                )
                                if (tree.status.fertilizerLabel.isNotBlank()) {
                                    Text(
                                        tree.status.fertilizerLabel,
                                        color = Green,
                                        fontSize = AppTypography.Sp10,
                                    )
                                }
                            } else {
                                Text(
                                    if (plot.isUnlocked) {
                                        AppText.text(
                                            TextKey.CopyMotKhoiDauMoi280995,
                                        )
                                    } else {
                                        AppText.text(
                                            TextKey.Copy0Xu251e68,
                                            com.mamxanh.shared.model.Balance.plot(
                                                plots.count {
                                                    it.isUnlocked
                                                },
                                            ),
                                        )
                                    },
                                    color = Muted,
                                    fontSize = AppTypography.Sp11,
                                    lineHeight = AppTypography.Sp16,
                                )
                            }
                        }
                    }
                }
                item(span = {
                    GridItemSpan(maxLineSpan)
                }) { GardenNavigationCard(AppText.text(TextKey.CopyBachThaoF2ed64), { onLiving("ENCYCLOPEDIA", "") }) }
            }
        }
    }
    GardenCareDialogs(
        state = state,
        waterAllKey = waterAllKey,
        unlockingPlotId = unlockingPlotId,
        onDismissWaterAll = { waterAllKey = null },
        onDismissUnlock = { unlockingPlotId = null },
        onIntent = onIntent,
    )
    selectedSeed?.let { seed ->
        val available =
            plots.any {
                it.isUnlocked &&
                    it.treeId == null &&
                    (selectedPlotId == null || it.id == selectedPlotId)
            }
        if (available) {
            PlantingDialog(
                seed,
                garden.inventory[seed.id] ?: 0,
                targetPlotId = selectedPlotId ?: plots.firstOrNull { it.isUnlocked && it.treeId == null }?.id,
                availablePlotIds = plots.filter { it.isUnlocked && it.treeId == null }.map { it.id },
                onSelectPlot = { selectedPlotId = it },
                onDismiss = onDismissPlanting,
                onPlant = { name ->
                    onIntent(GardenIntent.Plant(seed.id, name, selectedPlotId))
                    onDismissPlanting()
                },
            )
        } else {
            MintDialog(AppText.text(TextKey.CopyChuaCoOTrong6abd67), onDismissPlanting, AppText.text(TextKey.CopyXemOKhoa2d0677), {
                onDismissPlanting()
                unlockingPlotId = plots.firstOrNull { !it.isUnlocked }?.id
            }) {
                Text(
                    AppText.text(TextKey.CopyHatVanOTrongTuiMoThemOTrongDeGieoCay54d8b7),
                    color = Muted,
                    fontSize = AppTypography.Sp12,
                    lineHeight = AppTypography.Sp17,
                )
            }
        }
    }
    state.message?.let { message ->
        MintFeedback(message, { onIntent(GardenIntent.DismissMessage) })
    }
}
