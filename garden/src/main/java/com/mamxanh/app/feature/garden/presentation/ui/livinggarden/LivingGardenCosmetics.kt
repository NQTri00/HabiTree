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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.mamxanh.app.core.components.*
import com.mamxanh.app.core.components.components.*
import com.mamxanh.app.core.components.design.AppColors
import com.mamxanh.app.core.components.design.AppDimensions
import com.mamxanh.app.core.components.design.AppTypography
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey
import com.mamxanh.shared.model.*
import kotlin.math.ceil

private fun categoryName(s: String) =
    when (s) {
        "GROUND" -> AppText.text(TextKey.CopyMatDat9d263a)
        "BORDER" -> AppText.text(TextKey.CopyVien5051f2)
        "ACCENT" -> AppText.text(TextKey.CopyDiemNhan0400b9)
        "AMBIENT" -> AppText.text(TextKey.CopyHieuUng40f9c1)
        "TREE" -> AppText.text(TextKey.CopyPhuKien0852d7)
        "THEME" -> AppText.text(TextKey.CopyChuDe59500d)
        else -> s
    }

@Composable internal fun ColumnScope.CosmeticCatalog(
    state: LivingGardenUiState,
    onOpen: (String, String) -> Unit,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(
                horizontal = AppDimensions.Dp24,
                vertical = AppDimensions.Dp12,
            ).background(Pale, RoundedCornerShape(AppDimensions.Dp15))
            .padding(AppDimensions.Dp4),
    ) {
        listOf(
            "SHOP" to AppText.text(TextKey.CopyTiemTrangTriFbd50e),
            "INVENTORY" to AppText.text(TextKey.CopyDaSoHuu822625),
        ).forEach { (route, label) ->
            Box(
                Modifier
                    .weight(1f)
                    .heightIn(min = AppDimensions.Dp48)
                    .background(
                        if (state.route ==
                            route
                        ) {
                            AppColors.White
                        } else {
                            Pale
                        },
                        RoundedCornerShape(AppDimensions.Dp12),
                    ).clickable { onOpen(route, "") },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    label,
                    color =
                        if (state.route ==
                            route
                        ) {
                            Green
                        } else {
                            Muted
                        },
                    fontSize = AppTypography.Sp12,
                )
            }
        }
    }
    var filter by rememberSaveable { mutableStateOf("ALL") }
    Row(
        Modifier.fillMaxWidth().padding(horizontal = AppDimensions.Dp8).horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(AppDimensions.Dp5),
    ) {
        (listOf("ALL") + gardenCosmetics.map { it.category }.distinct()).forEach { category ->
            GardenPill(
                if (category ==
                    "ALL"
                ) {
                    AppText.text(TextKey.CopyTatCa49c73a)
                } else {
                    categoryName(category)
                },
                filter == category,
                { filter = category },
            )
        }
    }
    val grid = rememberLazyGridState()
    BoxWithConstraints(Modifier.weight(1f)) {
        val columns = if (maxWidth >= AppDimensions.Dp600)3 else 2
        LazyVerticalGrid(
            GridCells.Fixed(columns),
            Modifier.fillMaxSize().mintScrollEdges(grid.canScrollBackward, grid.canScrollForward),
            state = grid,
            contentPadding = PaddingValues(horizontal = AppDimensions.Dp24, vertical = AppDimensions.Dp16),
            horizontalArrangement = Arrangement.spacedBy(AppDimensions.Dp12),
            verticalArrangement = Arrangement.spacedBy(AppDimensions.Dp12),
        ) {
            val items =
                gardenCosmetics.filter {
                    (filter == "ALL" || it.category == filter) &&
                        (state.route != "INVENTORY" || (state.snapshot.owned[it.id] ?: 0) > 0)
                }
            items(items, key = { it.id }) { item ->
                SmallCatalogTile(
                    item.name,
                    if ((state.snapshot.owned[item.id] ?: 0) >
                        0
                    ) {
                        AppText.text(TextKey.CopySoHuu0A67ee0, state.snapshot.owned[item.id])
                    } else if (item.tier ==
                        "Reward"
                    ) {
                        AppText.text(TextKey.CopyQuaSuuTam218de5)
                    } else {
                        AppText.text(TextKey.Copy0Xu251e68, item.price)
                    },
                    { onOpen("DETAIL", item.id) },
                ) { CosmeticArtwork(item, it) }
            }
            if (items.isEmpty()) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    GardenPanel {
                        GardenHeading(AppText.text(TextKey.CopyChuaCoTrangTriC94f39))
                        GardenCompactAction(
                            AppText.text(TextKey.CopyGheTiemCb74e9),
                            { onOpen("SHOP", "") },
                            icon = Icons.Rounded.Storefront,
                        )
                    }
                }
            }
        }
    }
}

@Composable internal fun SmallCatalogTile(
    name: String,
    value: String,
    onClick: () -> Unit,
    art: @Composable (Modifier) -> Unit,
) {
    GardenBotanicalFrame(
        Modifier.fillMaxWidth().clickable(onClick = onClick),
        contentPadding = PaddingValues(AppDimensions.Dp12),
        cornerSize = AppDimensions.Dp20,
    ) {
        Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(AppDimensions.Dp10)) {
            Box(
                Modifier.fillMaxWidth().height(AppDimensions.Dp100).background(Pale, RoundedCornerShape(AppDimensions.Dp14)),
                contentAlignment = Alignment.Center,
            ) {
                art(Modifier.size(AppDimensions.Dp44))
            }
            Text(
                name,
                Modifier.heightIn(min = AppDimensions.Dp32),
                color = Deep,
                fontSize = AppTypography.Sp12,
                lineHeight = AppTypography.Sp16,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Text(value, color = Green, fontSize = AppTypography.Sp12, lineHeight = AppTypography.Sp18)
        }
    }
}

@Composable internal fun CosmeticDetail(
    state: LivingGardenUiState,
    onOpen: (String, String) -> Unit,
    onAct: (LivingGardenAction) -> Unit,
) {
    val item =
        gardenCosmetics.firstOrNull { it.id == state.selected } ?: run {
            GardenCaption(AppText.text(TextKey.CopyVatPhamChuaDuocHoTro5099e1))
            return
        }
    val owned = state.snapshot.owned[item.id] ?: 0
    GardenBotanicalFrame(
        Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(AppDimensions.Dp28),
        cornerSize = AppDimensions.Dp28,
        skin = GardenPanelSkin.HINT,
    ) {
        Column(
            Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(AppDimensions.Dp16),
        ) {
            MintStatusPill(categoryName(item.category))
            CosmeticArtwork(item, Modifier.size(AppDimensions.Dp86))
            Text(
                item.name,
                color = Deep,
                fontSize = AppTypography.Sp26,
                lineHeight = AppTypography.Sp34,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
            GardenCaption(
                if (item.tier ==
                    "Reward"
                ) {
                    AppText.text(TextKey.CopyQuaSuuTam218de5)
                } else {
                    AppText.text(TextKey.Copy0Xu251e68, item.price)
                },
            )
        }
    }
    MintMetricStrip(
        listOf(
            AppText.text(TextKey.CopyDaSoHuu822625) to owned.toString(),
            AppText.text(TextKey.CopyDangDatAe223b) to
                state.snapshot.placements
                    .count { it.sku == item.id }
                    .toString(),
        ),
    )
    GardenExpandableSection(AppText.text(TextKey.CopyViTriCachDung82d89c)) {
        GardenCaption("${categoryName(item.category)} · ${item.footprint} · ${item.tier}")
        GardenCaption(AppText.text(TextKey.CopyChiTrangTriKhongDoiTocDoCayMuaDeCatVaoTu58883c))
    }
    if (item.tier == "Reward") {
        GardenNavigationCard(AppText.text(TextKey.CopyMoKhoaQuaSuuTam12f128), { onOpen("ENCYCLOPEDIA", "") })
    } else if (!item.permanent || owned == 0) {
        GardenPaintedButton(
            AppText.text(TextKey.CopyMua0Xu39faa9, item.price),
            { onAct(LivingGardenAction.Buy(item.id, newKey())) },
            Modifier.fillMaxWidth(),
            enabled =
                !state.busy && state.snapshot.coins >= item.price,
        )
        val days = ceil((item.price - state.snapshot.coins).coerceAtLeast(0) / state.snapshot.earningRate.coerceAtLeast(1.0)).toInt()
        GardenCaption(
            AppText.text(
                TextKey.CopyUocTinh0Ngay1B69012,
                days,
                if (state.snapshot.estimatedRate) {
                    AppText.text(
                        TextKey.CopyThamKhao40XuNgayC8596f,
                    )
                } else {
                    AppText.text(TextKey.CopyTheoThuNhap7NgayF72e2b)
                },
            ),
        )
    }
    if (owned > 0 &&
        item.slot == "THEME"
    ) {
        GardenPaintedButton(
            AppText.text(TextKey.CopyChonChuDeC1f89a),
            { onOpen("APPEARANCE", item.id) },
            Modifier.fillMaxWidth(),
            primary = false,
        )
    }
}
