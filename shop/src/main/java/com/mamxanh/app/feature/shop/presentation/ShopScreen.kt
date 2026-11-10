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
    MintBackdrop(modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize()) {
            MintHeader("Tiệm hạt", state.wallet.coins)
            MintSegments(listOf("Tất cả", "Cây", "Hạt giống"), state.filter.ordinal,
                { onIntent(ShopIntent.SetFilter(ShopFilter.entries[it])) }, Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
            LazyVerticalGrid(GridCells.Fixed(2), Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 16.dp)) {
                if(state.filter == ShopFilter.PLANTS) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Column(Modifier.fillMaxWidth().padding(vertical = 32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            GardenArtwork(GardenArt.SPROUT, Modifier.size(96.dp))
                            Text("Chưa có cây trưởng thành để bán", color = Deep, textAlign = TextAlign.Center)
                            Text("Mua hạt và chăm cây tại Vườn ươm.", color = Muted, textAlign = TextAlign.Center)
                        }
                    }
                } else {
                    items(products, key = { it.id }) { seed ->
                        GardenBotanicalFrame(Modifier.fillMaxWidth().clickable { selectedId = seed.id },
                            contentPadding = PaddingValues(10.dp), cornerSize = 20.dp) {
                            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                PaintedGardenPlant(seed.id, Modifier.fillMaxWidth().height(88.dp), potted = true)
                                Text(productName(seed), color = Deep, fontSize = 15.sp, lineHeight = 19.sp,
                                    fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                                GardenBotanicalFrame(contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    skin = GardenPanelSkin.PRICE, cornerSize = 50.dp) {
                                    GardenRichText("🪙 ${seed.price} xu", color = Deep, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                    if(state.filter == ShopFilter.ALL) item(span = { GridItemSpan(maxLineSpan) }) {
                        GardenBotanicalFrame(Modifier.fillMaxWidth(), contentPadding = PaddingValues(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                GardenArtwork(GardenArt.FERTILIZER, Modifier.size(48.dp))
                                Column(Modifier.weight(1f)) {
                                    Text("Phân hữu cơ", fontWeight = FontWeight.Bold, color = Deep)
                                    Text("25 xu · Đang có ${state.wallet.inventory["fertilizer"] ?: 0}", fontSize = 13.sp, color = Deep)
                                }
                                GardenPaintedButton("Mua", { onIntent(ShopIntent.BuyFertilizer) },
                                    enabled = state.wallet.coins >= 25 && !state.isPurchasing)
                            }
                        }
                    }
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        GardenPaintedButton("Ghé Vườn ươm", { onIntent(ShopIntent.OpenGarden) }, Modifier.fillMaxWidth(), primary = false)
                    }
                }
            }
        }
    }
    val seed = shopCatalog.firstOrNull { it.id == selectedId }
    if(seed != null && state.outcome == null) {
        Dialog(onDismissRequest = { selectedId = null }, properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false)) {
            Box(Modifier.fillMaxSize().safeDrawingPadding().padding(16.dp), contentAlignment = Alignment.Center) {
                Box(Modifier.widthIn(max = 440.dp).fillMaxWidth().heightIn(max = 680.dp)) {
                    ShopSeedDetailCard(seed, state.wallet.inventory[seed.id] ?: 0,
                        state.wallet.coins >= seed.price && !state.isPurchasing, { selectedId = null },
                        { onIntent(ShopIntent.BuySeed(seed.id)) }, (seed.price - state.wallet.coins).coerceAtLeast(0))
                }
            }
        }
    }
    state.outcome?.let { outcome ->
        Dialog(onDismissRequest = { selectedId = null; onIntent(ShopIntent.DismissOutcome) }) {
            GardenBotanicalFrame(skin = GardenPanelSkin.DIALOG) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(if(outcome.success) "✓" else "!", fontSize = 48.sp, color = if(outcome.success) Green else Color(0xFFCB3434))
                    Text(if(outcome.success) "Đã thêm 1 vào túi" else "Chưa đủ xu", fontWeight = FontWeight.Bold, fontSize = 22.sp, textAlign = TextAlign.Center)
                    Text(outcome.name, textAlign = TextAlign.Center)
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
    GardenBotanicalFrame(Modifier.fillMaxWidth(), contentPadding = PaddingValues(18.dp), skin = GardenPanelSkin.DIALOG) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text("Chi tiết hạt giống", Modifier.weight(1f), color = Deep, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                IconButton(onClick = onDismiss) { Text("×", fontSize = 28.sp) }
            }
            Column(Modifier.weight(1f, fill = false).verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                PaintedGardenPlant(plant.id, Modifier.fillMaxWidth().height(170.dp))
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
            }
            if(missingCoins > 0) Text("Không đủ xu. Cần thêm $missingCoins xu để mua.", color = Color(0xFFB52A2A), textAlign = TextAlign.Center)
            Row(Modifier.height(IntrinsicSize.Min), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                GardenPaintedButton("Để sau", onDismiss, Modifier.weight(1f).fillMaxHeight(), primary = false)
                GardenPaintedButton("Mua hạt · ${plant.price} xu", onBuy, Modifier.weight(1.6f).fillMaxHeight(), enabled = canAfford)
            }
        }
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
