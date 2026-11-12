package com.mamxanh.app.feature.inventory.presentation

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
            Row(Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Rounded.ArrowBack, "Quay lại", tint = Deep)
                }
                Icon(Icons.Rounded.Inventory2, null, tint = Green, modifier = Modifier.size(16.dp))
                Text("Túi đồ", Modifier.weight(1f), color = Deep,
                    fontSize = 14.sp, fontWeight = FontWeight.Bold)
                IconButton({onLiving("INVENTORY","")}){Icon(Icons.Rounded.Palette,"Trang trí đã sở hữu",Modifier.size(18.dp),tint=Green)}
                GardenBotanicalFrame(contentPadding = PaddingValues(5.dp)) {
                    GardenRichText("🪙 ${state.coins}", color = Deep, fontSize = 13.sp)
                }
            }
            MintCatalogTabs(if (tab == InventoryTab.ITEMS) 0 else 1,
                { tab = if (it == 0) InventoryTab.ITEMS else InventoryTab.SEEDS },
                Modifier.padding(horizontal = 10.dp, vertical = 3.dp))
            if (targetPlotId != null && tab == InventoryTab.SEEDS) Text(
                "Chọn hạt để gieo tại ô " + targetPlotId.toString().padStart(2, '0'),
                Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
                color = Deep, fontSize = 13.sp)
            if (state.isLoading) LinearProgressIndicator(Modifier.fillMaxWidth())
            BoxWithConstraints(Modifier.weight(1f)) {
            val fontScale=androidx.compose.ui.platform.LocalDensity.current.fontScale
            val columnCount=if(maxWidth<340.dp && fontScale>1.2f)2 else if(maxWidth>=600.dp)(maxWidth.value/140).toInt() else 3
            key(tab) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(columnCount),
                state = activeGrid,
                modifier = Modifier.fillMaxSize().mintScrollEdges(activeGrid.canScrollBackward, activeGrid.canScrollForward),
                contentPadding = PaddingValues(10.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (tab == InventoryTab.ITEMS) {
                    items(state.items.filter { it.count > 0 }, key = { it.id }) { product ->
                        InventoryCard(product.name, if(product.equipped) "Đang dùng" else if(product.isCan) "Chạm để dùng" else "×${product.count}",
                            onClick = if(product.isCan && !product.equipped && !state.isLoading) ({ onEquip(product.id) }) else null) {
                            GardenArtwork(if(product.isCan) GardenArt.WATERING_CAN else GardenArt.FERTILIZER, it)
                        }
                    }
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Text("Chạm bình tưới để trang bị. Dùng phân bón tại màn hình cây; bình tưới dùng lâu dài.",
                            Modifier.padding(top = 8.dp), color = Muted, fontSize = 11.sp, lineHeight = 15.sp)
                    }
                } else {
                    items(ownedSeeds, key = { it.id }) { seed ->
                        InventoryCard(seed.name, "×${seed.count}", onClick = {
                            if (!state.isLoading) onSelectSeed(seed.id)
                        }) { PaintedGardenPlant(seed.id, it) }
                    }
                    if (ownedSeeds.isEmpty() && !state.isLoading) item(span = { GridItemSpan(maxLineSpan) }) {
                        Column(Modifier.fillMaxWidth().padding(vertical = 16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            GardenArtwork(GardenArt.SEED, Modifier.size(48.dp))
                            Text("Chưa có hạt trong túi", color = Deep, fontWeight = FontWeight.Bold)
                            Text("Ghé tiệm để chọn hạt đầu tiên.", color = Muted)
                        }
                    }
                }
                item(span = { GridItemSpan(maxLineSpan) }) {
                    GardenPaintedButton("Ghé tiệm", onOpenShop,
                        Modifier.fillMaxWidth().padding(top = 12.dp), primary = false)
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
    MintCatalogCard(name,quantity,onClick,artwork)
}

@Preview(widthDp = 360, heightDp = 640)
@Composable
fun InventoryScreenPreview() {
    MamXanhPreviewTheme {
        InventoryScreen(InventoryUiState(
            seeds = listOf(InventorySeed("cherry", "Đào hồng xuân", 3),
                InventorySeed("pine", "Thông xanh", 2), InventorySeed("succulent", "Sen đá ngọc", 1)),
            items = listOf(InventoryItem("can_basic","Bình cơ bản",1,true,true)), coins = 120),
            {}, {}, {}, targetPlotId = 5)
    }
}
