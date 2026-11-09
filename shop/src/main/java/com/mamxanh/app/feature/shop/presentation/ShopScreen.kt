package com.mamxanh.app.feature.shop.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import com.mamxanh.app.presentation.ui.components.GardenRichText as Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.mamxanh.app.feature.shop.domain.model.*
import com.mamxanh.app.presentation.ui.*
import com.mamxanh.app.presentation.ui.components.*
import com.mamxanh.core.ui.R

@Composable
fun ShopScreen(state: ShopUiState, onIntent: (ShopIntent) -> Unit, modifier: Modifier = Modifier) {
    var selected by remember { mutableStateOf<ShopSeed?>(null) }
    val featuredIds = listOf("succulent", "pine", "cherry", "mandarin")
    val featured = featuredIds.mapNotNull { id -> shopCatalog.firstOrNull { it.id == id } }
    val other = shopCatalog.filterNot { it.id in featuredIds }
    Box(modifier.fillMaxSize()) {
        Image(painterResource(R.drawable.simple_shop_v2), null,
            Modifier.fillMaxSize(), contentScale = ContentScale.FillBounds)
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 18.dp, vertical = 36.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            GardenWoodSign("Tiệm hạt", Modifier.fillMaxWidth(.85f))
            GardenBotanicalFrame(contentPadding = PaddingValues(horizontal = 22.dp, vertical = 8.dp), cornerSize = 20.dp) {
                Text("🪙 ${state.wallet.coins} xu", color = Deep, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(48.dp))
            featured.chunked(2).forEach { row ->
                NurseryShelf {
                    row.forEach { seed -> ShelfSeed(seed, { selected = seed }, Modifier.weight(1f)) }
                }
            }
            NurseryShelf {
                FertilizerShopCard(state.wallet.inventory["fertilizer"] ?: 0, state.wallet.coins >= 25, { onIntent(ShopIntent.BuyFertilizer) })
            }
            ShopGardenFooter { onIntent(ShopIntent.OpenGarden) }
            if (other.isNotEmpty()) {
                GardenBotanicalFrame(Modifier.padding(top = 12.dp), contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp), cornerSize = 20.dp) {
                    Text("Những mầm xanh khác", color = Deep, fontWeight = FontWeight.Bold)
                }
                other.chunked(2).forEach { row ->
                    NurseryShelf {
                        row.forEach { seed -> ShelfSeed(seed, { selected = seed }, Modifier.weight(1f)) }
                    }
                }
            }
        }
    }
    selected?.let { seed ->
        Dialog(onDismissRequest = { selected = null }, properties = DialogProperties(usePlatformDefaultWidth = false)) {
            Box(Modifier.fillMaxWidth(.92f).widthIn(max = 440.dp)) {
                ShopSeedDetailCard(seed, state.wallet.inventory[seed.id] ?: 0, state.wallet.coins >= seed.price,
                    onDismiss = { selected = null },
                    onBuy = { onIntent(ShopIntent.BuySeed(seed.id)); selected = null })
            }
        }
    }
}

@Composable
private fun NurseryShelf(content: @Composable RowScope.() -> Unit) {
    Box(Modifier.fillMaxWidth().padding(top = 2.dp, bottom = 4.dp)) {
        Box(Modifier.align(Alignment.BottomCenter).fillMaxWidth().height(16.dp)
            .background(Color(0xFFC78D51), androidx.compose.foundation.shape.RoundedCornerShape(10.dp)))
        Row(Modifier.fillMaxWidth().padding(start = 7.dp, end = 7.dp, bottom = 16.dp), verticalAlignment = Alignment.Bottom, content = content)
    }
}

@Composable
private fun ShelfSeed(seed: ShopSeed, onInspect: () -> Unit, modifier: Modifier = Modifier) {
    Column(modifier.clickable(onClickLabel = "Xem hạt ${seed.name}", onClick = onInspect), horizontalAlignment = Alignment.CenterHorizontally) {
        PaintedGardenPlant(seed.id, Modifier.fillMaxWidth().height(94.dp), potted = true)
        GardenBotanicalFrame(Modifier.fillMaxWidth(.90f), contentPadding = PaddingValues(horizontal = 8.dp, vertical = 5.dp), cornerSize = 14.dp) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text(when(seed.id) { "pine" -> "Thông nhỏ"; "cherry" -> "Đào hồng"; else -> seed.name },
                    color = Deep, fontSize = 16.sp, lineHeight = 18.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                Text("${seed.price} xu", color = Deep, fontSize = 16.sp, lineHeight = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun FertilizerShopCard(ownedCount: Int, canAfford: Boolean, onBuy: () -> Unit) {
    Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp), verticalAlignment = Alignment.CenterVertically) {
        GardenArtwork(GardenArt.FERTILIZER, Modifier.size(88.dp, 94.dp))
        GardenBotanicalFrame(Modifier.weight(1f).clickable(enabled = canAfford,
            onClickLabel = "Mua phân hữu cơ, 25 xu", onClick = onBuy),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp), cornerSize = 18.dp) {
            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Phân hữu cơ", color = Deep, fontSize = 17.sp, lineHeight = 20.sp, fontWeight = FontWeight.Bold)
                Text("25 xu", color = Deep, fontSize = 16.sp, lineHeight = 18.sp, fontWeight = FontWeight.Bold)
                Text("Đang có $ownedCount", color = Deep, fontSize = 11.sp, lineHeight = 13.sp)
            }
        }
    }
}

@Composable
private fun ShopGardenFooter(onClick: () -> Unit) {
    GardenBotanicalFrame(Modifier.fillMaxWidth(), contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp), cornerSize = 26.dp) {
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("Hạt mua được sẽ nằm trong túi", color = Deep, fontSize = 13.sp, lineHeight = 16.sp, fontStyle = FontStyle.Italic)
            GardenPaintedButton("Ghé vườn ươm →", onClick, Modifier.fillMaxWidth(.85f), primary = false)
        }
    }
}

@Composable
private fun ShopSeedDetailCard(plant: ShopSeed, ownedCount: Int, canAfford: Boolean, onDismiss: () -> Unit, onBuy: () -> Unit) {
    GardenBotanicalFrame(Modifier.fillMaxWidth(), contentPadding = PaddingValues(horizontal = 26.dp, vertical = 32.dp), cornerSize = 42.dp, skin = GardenPanelSkin.DIALOG) {
        Column(Modifier.verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
            PaintedGardenPlant(plant.id, Modifier.fillMaxWidth().height(165.dp))
            Text(plant.name, color = Deep, fontSize = 28.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            Text(plant.description, color = Deep, fontStyle = FontStyle.Italic, textAlign = TextAlign.Center)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                DetailBadge("🪙 ${plant.price} xu", Modifier.weight(1f))
                DetailBadge("🌱 Đang có $ownedCount", Modifier.weight(1f))
            }
            Row(Modifier.fillMaxWidth(.65f), verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                HorizontalDivider(Modifier.weight(1f), color = Color(0xFFC5AD7B))
                GardenArtwork(GardenArt.SPROUT, Modifier.size(26.dp))
                HorizontalDivider(Modifier.weight(1f), color = Color(0xFFC5AD7B))
            }
            GardenBotanicalFrame(Modifier.fillMaxWidth(), contentPadding = PaddingValues(horizontal = 12.dp, vertical = 9.dp),
                cornerSize = 24.dp, skin = GardenPanelSkin.HINT) {
                Text("Mua hạt để cất vào túi.\nGieo cây tại Vườn ươm.", color = Deep, fontSize = 17.sp,
                    fontStyle = FontStyle.Italic, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            }
            if (!canAfford) Text("Bạn cần thêm xu để mang hạt về.", color = Color(0xFF88511B), textAlign = TextAlign.Center)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                GardenPaintedButton("Để sau", onDismiss, Modifier.weight(.8f), primary = false)
                GardenPaintedButton("Mang về · ${plant.price} xu", onBuy, Modifier.weight(1.3f), enabled = canAfford)
            }
        }
        IconButton(onClick = onDismiss, modifier = Modifier.align(Alignment.TopEnd).size(40.dp)
            .background(Color(0xFFFFE2AC), androidx.compose.foundation.shape.CircleShape)
            .border(1.5.dp, Color(0xFFCB9656), androidx.compose.foundation.shape.CircleShape)
            .semantics { contentDescription = "Đóng chi tiết hạt" }) {
            Text("×", color = Deep, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun DetailBadge(text: String, modifier: Modifier = Modifier) {
    GardenBotanicalFrame(modifier, contentPadding = PaddingValues(horizontal = 10.dp, vertical = 9.dp), cornerSize = 18.dp) {
        Text(text, color = Deep, fontSize = 14.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
    }
}

@Preview(name = "Seed shop · normal", widthDp = 360, heightDp = 800)
@Preview(name = "Seed shop · large text", widthDp = 360, heightDp = 800, fontScale = 1.3f)
@Composable
fun ShopScreenPreview() { MamXanhPreviewTheme { ShopScreen(ShopUiState(ShopWallet(120, mapOf("succulent" to 2, "fertilizer" to 1)), false), {}) } }

@Preview(name = "Seed detail popup", widthDp = 360, heightDp = 720)
@Composable
fun ShopSeedDetailDialogPreview() { MamXanhPreviewTheme { Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { GardenForestScene(Modifier.fillMaxSize(), false); ShopSeedDetailCard(shopCatalog.first(), 2, true, {}, {}) } } }

@Preview(name = "Seed detail · large text, insufficient funds", widthDp = 360, heightDp = 800, fontScale = 1.3f)
@Composable
fun ShopSeedDetailLargeTextPreview() { MamXanhPreviewTheme { Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { GardenForestScene(Modifier.fillMaxSize(), false); ShopSeedDetailCard(shopCatalog.first(), 0, false, {}, {}) } } }

@Preview(name = "Nursery shelf", widthDp = 340)
@Composable
fun ShopNurseryDisplayPreview() { MamXanhPreviewTheme { NurseryShelf { ShelfSeed(shopCatalog.first(), {}, Modifier.weight(1f)); ShelfSeed(shopCatalog.last(), {}, Modifier.weight(1f)) } } }

@Preview(name = "Fertilizer shelf", widthDp = 340)
@Composable
fun FertilizerShopCardPreview() { MamXanhPreviewTheme { FertilizerShopCard(1, true, {}) } }

@Preview(name = "Shop garden shortcut", widthDp = 340)
@Composable
fun ShopGardenFooterPreview() { MamXanhPreviewTheme { ShopGardenFooter {} } }

@Preview(name = "Shop detail price badge", widthDp = 150)
@Composable
fun ShopDetailBadgePreview() { MamXanhPreviewTheme { DetailBadge("🪙 25 xu") } }
