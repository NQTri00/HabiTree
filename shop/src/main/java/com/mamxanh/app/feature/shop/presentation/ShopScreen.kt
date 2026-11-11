package com.mamxanh.app.feature.shop.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.mamxanh.app.feature.shop.domain.model.*
import com.mamxanh.app.presentation.ui.*
import com.mamxanh.app.presentation.ui.components.*

@Composable
fun ShopScreen(state: ShopUiState, onIntent: (ShopIntent) -> Unit, modifier: Modifier = Modifier) {
    var selectedId by rememberSaveable { mutableStateOf<String?>(null) }
    val featured = listOf("succulent", "pine", "cherry", "mandarin", "cactus", "lavender")
    val products = shopCatalog.sortedBy { featured.indexOf(it.id).let { n -> if(n < 0) 100 + shopCatalog.indexOf(it) else n } }
    val seedGrid = rememberLazyGridState()
    val itemGrid = rememberLazyGridState()
    val activeGrid = if (state.filter == ShopFilter.ITEMS) itemGrid else seedGrid
    MintBackdrop(modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize()) {
            MintHeader("Tiệm hạt", state.wallet.coins)
            MintCatalogTabs(if (state.filter == ShopFilter.ITEMS) 0 else 1,
                { onIntent(ShopIntent.SetFilter(if (it == 0) ShopFilter.ITEMS else ShopFilter.SEEDS)) },
                Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
            if (state.isLoading) LinearProgressIndicator(Modifier.fillMaxWidth())
            LazyVerticalGrid(GridCells.Fixed(3), Modifier.weight(1f)
                .mintScrollEdges(activeGrid.canScrollBackward, activeGrid.canScrollForward),
                state = activeGrid,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(16.dp)) {
                if (state.filter == ShopFilter.ITEMS) {
                    item(key = "fertilizer") {
                        GardenBotanicalFrame(Modifier.fillMaxWidth().clickable(
                            enabled = !state.isPurchasing && !state.isLoading,
                            role = androidx.compose.ui.semantics.Role.Button,
                            onClickLabel = "Chọn phân hữu cơ") { selectedId = "fertilizer" },
                            contentPadding = PaddingValues(8.dp), cornerSize = 20.dp) {
                            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                GardenArtwork(GardenArt.FERTILIZER, Modifier.fillMaxWidth().height(64.dp))
                                Text("Phân hữu cơ", color = Deep, fontSize = 12.sp, lineHeight = 15.sp,
                                    minLines = 2, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                                GardenRichText("🪙 ${com.mamxanh.shared.model.Balance.FERTILIZER} xu", color = Deep, fontSize = 12.sp, lineHeight = 15.sp)
                                Text("Có ${state.wallet.inventory["fertilizer"] ?: 0}", color = Green,
                                    fontSize = 11.sp, lineHeight = 14.sp)
                            }
                        }
                    }
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Text("Bình tưới và bơm nước luôn có sẵn trong Túi đồ.",
                            Modifier.padding(top = 8.dp), color = Muted, fontSize = 13.sp, lineHeight = 18.sp)
                    }
                } else {
                    items(products, key = { it.id }) { seed ->
                        GardenBotanicalFrame(Modifier.fillMaxWidth().clickable { selectedId = seed.id },
                            contentPadding = PaddingValues(8.dp), cornerSize = 20.dp) {
                            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                PaintedGardenPlant(seed.id, Modifier.fillMaxWidth().height(64.dp))
                                Text(productName(seed), color = Deep, fontSize = 12.sp, lineHeight = 15.sp,
                                    minLines = 2, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                                GardenRichText("🪙 ${seed.price} xu", color = Deep, fontSize = 12.sp, lineHeight = 15.sp,
                                    fontWeight = FontWeight.Bold)
                                Text("Có ${state.wallet.inventory[seed.id] ?: 0}", color = Green,
                                    fontSize = 11.sp, lineHeight = 14.sp)
                            }
                        }
                    }
                }
                item(span = { GridItemSpan(maxLineSpan) }) {
                    GardenPaintedButton("Ghé Vườn ươm", { onIntent(ShopIntent.OpenGarden) },
                        Modifier.fillMaxWidth().padding(top = 12.dp), primary = false)
                }
            }
        }
    }
    selectedId?.let { id ->
        if(state.outcome==null) PurchaseConfirmation(id,state,{selectedId=null}) { quantity,key ->
            onIntent(ShopIntent.Purchase(id,quantity,key))
        }
    }
    state.outcome?.let { outcome ->
        Dialog(onDismissRequest = { selectedId = null; onIntent(ShopIntent.DismissOutcome) }) {
            GardenBotanicalFrame(skin = GardenPanelSkin.DIALOG) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(if(outcome.success) "✓" else "!", fontSize = 48.sp, color = if(outcome.success) Green else Color(0xFFCB3434))
                    Text(if(outcome.success) "Đã thêm vào túi" else "Chưa thể mua", fontWeight = FontWeight.Bold, fontSize = 22.sp, textAlign = TextAlign.Center)
                    Text(outcome.name, textAlign = TextAlign.Center)
                    Text(outcome.message, textAlign = TextAlign.Center)
                    if(outcome.success) {
                        GardenRichText("🪙 Số xu: ${outcome.beforeCoins} → ${outcome.afterCoins}")
                        GardenRichText("🌱 Đang có: ${outcome.beforeOwned} → ${outcome.afterOwned}")
                    } else Text("Số xu và số lượng được giữ nguyên.", textAlign = TextAlign.Center)
                    GardenPaintedButton("OK", { selectedId = null; onIntent(ShopIntent.DismissOutcome) }, Modifier.fillMaxWidth())
                }
            }
        }
    }
}

private fun productName(seed: ShopSeed) = when(seed.id) {
    "pine" -> "Thông nhỏ"; "cherry" -> "Đào hồng"; "cactus" -> "Xương rồng"; "lavender" -> "Oải hương"; else -> seed.name
}

@Composable
private fun ShopSeedDetailCard(plant: ShopSeed, ownedCount: Int, canAfford: Boolean,
    onDismiss: () -> Unit, onBuy: () -> Unit, missingCoins: Int = 0) {
    BoxWithConstraints {
    val compact = maxHeight < 360.dp * androidx.compose.ui.platform.LocalDensity.current.fontScale
    val fixedCompactClose = compact && maxHeight >= 240.dp
    GardenBotanicalFrame(Modifier.fillMaxWidth(), contentPadding = PaddingValues(if(compact) 4.dp else 18.dp), skin = GardenPanelSkin.DIALOG) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(if(compact) 4.dp else 12.dp)) {
            if(!compact) SeedDetailHeading(onDismiss)
            if(fixedCompactClose) Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                IconButton(onClick = onDismiss) { Text("×", fontSize = 28.sp) }
            }
            Column(Modifier.weight(1f, fill = false).verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                if(compact) SeedDetailHeading(onDismiss, showClose = !fixedCompactClose)
                PaintedGardenPlant(plant.id, Modifier.fillMaxWidth().height(if(compact) 100.dp else 170.dp))
                Text(plant.name, fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Deep, textAlign = TextAlign.Center)
                Text(plant.description, color = Deep, textAlign = TextAlign.Center)
                FlowRow(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                    verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    GardenBotanicalFrame(skin = GardenPanelSkin.PRICE, contentPadding = PaddingValues(10.dp)) {
                        GardenRichText("🪙 ${plant.price} xu", fontWeight = FontWeight.Bold)
                    }
                    GardenBotanicalFrame(skin = GardenPanelSkin.PRICE, contentPadding = PaddingValues(10.dp)) {
                        GardenRichText("🌱 Đang có: $ownedCount", fontWeight = FontWeight.Bold)
                    }
                }
                GardenBotanicalFrame(skin = GardenPanelSkin.HINT, contentPadding = PaddingValues(12.dp)) {
                    Text("Mua hạt để cất vào túi.\nGieo cây tại Vườn ươm.", textAlign = TextAlign.Center, color = Deep)
                }
                if(compact && missingCoins > 0) Text("Không đủ xu. Cần thêm $missingCoins xu để mua.", color = Color(0xFFB52A2A), textAlign = TextAlign.Center)
            }
            if(!compact && missingCoins > 0) Text("Không đủ xu. Cần thêm $missingCoins xu để mua.", color = Color(0xFFB52A2A), textAlign = TextAlign.Center)
            Row(Modifier.height(IntrinsicSize.Min), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                GardenPaintedButton("Để sau", onDismiss, Modifier.weight(1f).fillMaxHeight(), primary = false)
                GardenPaintedButton("Mua hạt · ${plant.price} xu", onBuy, Modifier.weight(1.6f).fillMaxHeight(), enabled = canAfford)
            }
        }
    }
}
}

@Composable
private fun SeedDetailHeading(onDismiss: () -> Unit, showClose: Boolean = true) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text("Chi tiết hạt giống", Modifier.weight(1f), color = Deep, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        if(showClose) IconButton(onClick = onDismiss) { Text("×", fontSize = 28.sp) }
    }
}

@Preview(widthDp = 360, heightDp = 640, device = "spec:width=360dp,height=640dp,dpi=160")
@Composable
fun ShopScreenPreview() { MamXanhPreviewTheme { ShopScreen(ShopUiState(ShopWallet(120, mapOf("succulent" to 2)), false), {}) } }
@Preview(widthDp = 360, heightDp = 1400, device = "spec:width=360dp,height=1400dp,dpi=160")
@Composable
fun ShopExtendedDocumentPreview() { ShopScreenPreview() }
@Preview(widthDp = 360, heightDp = 800, device = "spec:width=360dp,height=800dp,dpi=160")
@Composable
fun ShopSeedDetailDialogPreview(background: (@Composable () -> Unit)? = null) {
    MamXanhPreviewTheme {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            background?.invoke()
            Box(Modifier.matchParentSize().background(Color.Black.copy(alpha = .32f)))
            Box(Modifier.fillMaxWidth(.92f).heightIn(max = 680.dp)) { ShopSeedDetailCard(shopCatalog.first(), 2, true, {}, {}) }
        }
    }
}
@Preview(widthDp = 320, heightDp = 640, fontScale = 1.3f, device = "spec:width=320dp,height=640dp,dpi=160")
@Composable
fun ShopSeedDetailLargeTextPreview() { MamXanhPreviewTheme { Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { ShopSeedDetailCard(shopCatalog.first(), 0, false, {}, {}, 25) } } }
