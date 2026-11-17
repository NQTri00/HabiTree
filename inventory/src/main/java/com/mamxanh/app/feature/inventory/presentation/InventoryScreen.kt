package com.mamxanh.app.feature.inventory.presentation

import com.mamxanh.app.presentation.ui.design.AppDimensions
import com.mamxanh.app.presentation.ui.design.AppTypography

import com.mamxanh.shared.localization.TextKey

import com.mamxanh.shared.localization.AppText

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Inventory2
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mamxanh.app.presentation.ui.*
import com.mamxanh.app.presentation.ui.components.*

data class InventorySeed(val id: String, val name: String, val count: Int)
data class InventoryItem(val id: String, val name: String, val count: Int, val isCan: Boolean, val equipped: Boolean = false)
data class InventoryUiState(
    val seeds: List<InventorySeed> = emptyList(),
    val items: List<InventoryItem> = emptyList(),
    val coins: Int = 0,
    val isLoading: Boolean = false
)
enum class InventoryTab { ITEMS, SEEDS }

/** Reads existing inventory; only the host owns planting and purchase mutations. */
@Composable
fun InventoryScreen(
    state: InventoryUiState,
    onBack: () -> Unit,
    onSelectSeed: (String) -> Unit,
    onOpenShop: () -> Unit,
    modifier: Modifier = Modifier,
    targetPlotId: Int? = null,
    onEquip: (String) -> Unit = {},
    onLiving: (String,String)->Unit = {_,_->}
) {
    var tab by rememberSaveable(targetPlotId) {
        mutableStateOf(if (targetPlotId != null) InventoryTab.SEEDS else InventoryTab.ITEMS)
    }
    val itemsGrid = rememberLazyGridState()
    val seedsGrid = rememberLazyGridState()
    val activeGrid = if (tab == InventoryTab.ITEMS) itemsGrid else seedsGrid
    val ownedSeeds = state.seeds.filter { it.count > 0 }
    MintBackdrop(modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize()) {
            Row(Modifier.fillMaxWidth().padding(horizontal = AppDimensions.Dp8, vertical = AppDimensions.Dp4),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AppDimensions.Dp14)) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Rounded.ArrowBack, AppText.text(TextKey.CopyQuayLai6d7935), tint = Deep)
                }
                Icon(Icons.Rounded.Inventory2, null, tint = Green, modifier = Modifier.size(AppDimensions.Dp16))
                Text(AppText.text(TextKey.CopyTuiDo6a8e55), Modifier.weight(1f), color = Deep,
                    fontSize = AppTypography.Sp17, fontWeight = FontWeight.SemiBold)
                IconButton({onLiving("INVENTORY","")}){Icon(Icons.Rounded.Palette,AppText.text(TextKey.CopyTrangTriDaSoHuuA10e7a),Modifier.size(AppDimensions.Dp18),tint=Green)}
                GardenBotanicalFrame(contentPadding = PaddingValues(AppDimensions.Dp5)) {
                    GardenRichText("🪙 ${state.coins}", color = Deep, fontSize = AppTypography.Sp13)
                }
            }
            MintCatalogTabs(if (tab == InventoryTab.ITEMS) 0 else 1,
                { tab = if (it == 0) InventoryTab.ITEMS else InventoryTab.SEEDS },
                Modifier.padding(horizontal = AppDimensions.Dp24, vertical = AppDimensions.Dp8))
            if (targetPlotId != null && tab == InventoryTab.SEEDS) Text(
                AppText.text(TextKey.CopyChonHatDeGieoTaiO183f3d) + targetPlotId.toString().padStart(2, '0'),
                Modifier.padding(horizontal = AppDimensions.Dp10, vertical = AppDimensions.Dp3),
                color = Deep, fontSize = AppTypography.Sp13)
            if (state.isLoading) LinearProgressIndicator(Modifier.fillMaxWidth())
            BoxWithConstraints(Modifier.weight(1f)) {
            val fontScale=androidx.compose.ui.platform.LocalDensity.current.fontScale
            val columnCount=if(maxWidth>=AppDimensions.Dp600)3 else 2
            key(tab) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(columnCount),
                state = activeGrid,
                modifier = Modifier.fillMaxSize().mintScrollEdges(activeGrid.canScrollBackward, activeGrid.canScrollForward),
                contentPadding = PaddingValues(horizontal=AppDimensions.Dp24,vertical=AppDimensions.Dp16),
                horizontalArrangement = Arrangement.spacedBy(AppDimensions.Dp14),
                verticalArrangement = Arrangement.spacedBy(AppDimensions.Dp14)
            ) {
                if (tab == InventoryTab.ITEMS) {
                    items(state.items.filter { it.count > 0 }, key = { it.id }) { product ->
                        InventoryCard(product.name, if(product.equipped) AppText.text(TextKey.CopyDangDung275ca2) else if(product.isCan) AppText.text(TextKey.CopyChamDeDungEd92ea) else "×${product.count}",
                            onClick = if(product.isCan && !product.equipped && !state.isLoading) ({ onEquip(product.id) }) else null) {
                            GardenArtwork(if(product.isCan) GardenArt.WATERING_CAN else GardenArt.FERTILIZER, it)
                        }
                    }
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Text(AppText.text(TextKey.CopyChamBinhTuoiDeTrangBiDungPhanBonTaiManHinD63945),
                            Modifier.padding(top = AppDimensions.Dp8), color = Muted, fontSize = AppTypography.Sp11, lineHeight = AppTypography.Sp15)
                    }
                } else {
                    items(ownedSeeds, key = { it.id }) { seed ->
                        InventoryCard(seed.name, "×${seed.count}", onClick = {
                            if (!state.isLoading) onSelectSeed(seed.id)
                        }) { PaintedGardenPlant(seed.id, it) }
                    }
                    if (ownedSeeds.isEmpty() && !state.isLoading) item(span = { GridItemSpan(maxLineSpan) }) {
                        Column(Modifier.fillMaxWidth().padding(vertical = AppDimensions.Dp16),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(AppDimensions.Dp14)) {
                            GardenArtwork(GardenArt.SEED, Modifier.size(AppDimensions.Dp48))
                            Text(AppText.text(TextKey.CopyChuaCoHatTrongTuiA6854d), color = Deep, fontWeight = FontWeight.Bold)
                            Text(AppText.text(TextKey.CopyGheTiemDeChonHatDauTien1bc7be), color = Muted)
                        }
                    }
                }
                item(span = { GridItemSpan(maxLineSpan) }) {
                    GardenNavigationCard(AppText.text(TextKey.CopyTrangTriCuaBanDa2761),{onLiving("INVENTORY","")},art=GardenArt.RIBBON)
                }
                item(span = { GridItemSpan(maxLineSpan) }) {
                    GardenPaintedButton(AppText.text(TextKey.CopyGheTiemCb74e9), onOpenShop,
                        Modifier.fillMaxWidth().padding(top = AppDimensions.Dp12), primary = false)
                }
            }
            }
            }
        }
    }
}

@Composable
private fun InventoryCard(
    name: String, quantity: String, onClick: (() -> Unit)? = null,
    artwork: @Composable (Modifier) -> Unit
) {
    MintCatalogCard(name,quantity,onClick,artwork=artwork)
}

@Preview(widthDp = 360, heightDp = 640)
@Composable
fun InventoryScreenPreview() {
    MamXanhPreviewTheme {
        InventoryScreen(InventoryUiState(
            seeds = listOf(InventorySeed("cherry", AppText.text(TextKey.CopyDaoHongXuanC2fd99), 3),
                InventorySeed("pine", AppText.text(TextKey.CopyThongXanhF9e157), 2), InventorySeed("succulent", AppText.text(TextKey.CopySenDaNgocAba46f), 1)),
            items = listOf(InventoryItem("can_basic",AppText.text(TextKey.CopyBinhCoBan323b7f),1,true,true)), coins = 120),
            {}, {}, {}, targetPlotId = 5)
    }
}
