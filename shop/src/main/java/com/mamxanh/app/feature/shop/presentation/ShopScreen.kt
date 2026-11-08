package com.mamxanh.app.feature.shop.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.mamxanh.app.feature.shop.domain.model.ShopSeed
import com.mamxanh.app.feature.shop.domain.model.ShopWallet
import com.mamxanh.app.feature.shop.domain.model.shopCatalog
import com.mamxanh.app.feature.shop.presentation.components.KawaiiPottedPlant
import com.mamxanh.app.feature.shop.presentation.components.PinkTileBackdrop
import com.mamxanh.app.feature.shop.presentation.components.TinyPlantCard
import com.mamxanh.app.presentation.ui.*

@Composable
fun ShopScreen(state: ShopUiState, onIntent: (ShopIntent) -> Unit, modifier: Modifier = Modifier) {
    val wallet = state.wallet
    var selectedItem by remember { mutableStateOf<ShopSeed?>(null) }
    var purchaseMessage by remember { mutableStateOf<String?>(null) }

    Box(modifier.fillMaxSize()) {
        PinkTileBackdrop(Modifier.fillMaxSize())
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 14.dp, end = 14.dp, top = 10.dp, bottom = 28.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) { ShopNurseryDisplay() }
            item(span = { GridItemSpan(maxLineSpan) }) {
                Row(
                    Modifier.fillMaxWidth().padding(top = 2.dp, bottom = 1.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f)) {
                        Text("Mầm xanh đang chờ", color = Deep, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
                        Text("Chạm xem, chọn hạt giống yêu thích.", color = Muted, style = MaterialTheme.typography.bodySmall)
                    }
                    Surface(color = Color.White.copy(alpha = .9f), shape = CircleShape, border = androidx.compose.foundation.BorderStroke(1.dp, ShopCardBorder)) {
                        Text("${shopCatalog.size} giống", color = Green, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp))
                    }
                }
            }
            itemsIndexed(shopCatalog) { index, plant ->
                TinyPlantCard(
                    plant = plant,
                    varietyIndex = index,
                    ownedCount = wallet.inventory[plant.id] ?: 0,
                    canAfford = wallet.coins >= plant.price,
                    onBuy = {
                        onIntent(ShopIntent.BuySeed(plant.id))
                        purchaseMessage = "Đã gieo ${plant.name} vào khu vườn! 🌱"
                    },
                    onInspect = { selectedItem = plant },
                    modifier = Modifier.heightIn(min = 174.dp)
                )
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                FertilizerShopCard(
                    ownedCount = wallet.inventory["fertilizer"] ?: 0,
                    canAfford = wallet.coins >= 25,
                    onBuy = {
                        onIntent(ShopIntent.BuyFertilizer)
                        purchaseMessage = "Đã thêm phân bón hữu cơ vào giỏ! 🌿"
                    }
                )
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                ShopGardenFooter(onClick = { onIntent(ShopIntent.OpenGarden) })
            }
        }

        AnimatedVisibility(
            visible = purchaseMessage != null,
            enter = fadeIn() + slideInVertically { it / 2 },
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 18.dp, start = 18.dp, end = 18.dp)
        ) {
            purchaseMessage?.let { message ->
                Surface(color = Deep, shape = RoundedCornerShape(22.dp), shadowElevation = 8.dp, border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = .7f))) {
                    Row(Modifier.padding(horizontal = 18.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                        Text("🌱", fontSize = 20.sp)
                        Text(message, color = Color.White, fontWeight = FontWeight.Bold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    }
                }
                LaunchedEffect(message) {
                    kotlinx.coroutines.delay(2200)
                    purchaseMessage = null
                }
            }
        }

        selectedItem?.let { plant ->
            ShopSeedDetailDialog(
                plant = plant,
                ownedCount = wallet.inventory[plant.id] ?: 0,
                canAfford = wallet.coins >= plant.price,
                onDismiss = { selectedItem = null },
                onBuy = {
                    onIntent(ShopIntent.BuySeed(plant.id))
                    purchaseMessage = "Đã gieo ${plant.name} vào khu vườn! 🌱"
                    selectedItem = null
                }
            )
        }
    }
}

@Composable
private fun ShopNurseryDisplay() {
    Surface(
        color = ShopCardBg,
        shape = RoundedCornerShape(24.dp),
        shadowElevation = 3.dp,
        border = androidx.compose.foundation.BorderStroke(1.5.dp, ShopCardBorder)
    ) {
        Box {
            Row(Modifier.fillMaxWidth().padding(start = 16.dp, end = 12.dp, top = 12.dp, bottom = 11.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text("Hạt giống khỏe, niềm vui lớn", color = Deep, fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.titleSmall)
                    Text("Mỗi chậu cây là một người bạn mới.", color = Muted, style = MaterialTheme.typography.bodySmall)
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                        Text("☀️", fontSize = 13.sp)
                        Text("Ươm mầm dưới nắng sớm", color = Green, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.labelSmall)
                    }
                }
                Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(0.dp)) {
                    KawaiiPottedPlant(0, Modifier.size(44.dp, 56.dp), potExpression = 0)
                    KawaiiPottedPlant(8, Modifier.size(48.dp, 64.dp), potExpression = 1)
                    KawaiiPottedPlant(4, Modifier.size(44.dp, 56.dp), potExpression = 2)
                }
            }
            Box(Modifier.align(Alignment.BottomCenter).fillMaxWidth().padding(horizontal = 18.dp).height(4.dp).background(ShelfWood, RoundedCornerShape(50)))
        }
    }
}

@Composable
private fun FertilizerShopCard(ownedCount: Int, canAfford: Boolean, onBuy: () -> Unit) {
    Surface(color = ShopCardBg, shape = RoundedCornerShape(22.dp), border = androidx.compose.foundation.BorderStroke(1.dp, ShopCardBorder), shadowElevation = 2.dp) {
        Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Surface(color = Pale, shape = RoundedCornerShape(17.dp), modifier = Modifier.size(58.dp)) {
                Box(contentAlignment = Alignment.Center) { Text("🧪", fontSize = 30.sp) }
            }
            Column(Modifier.weight(1f)) {
                Text("Phân bón hữu cơ", color = Deep, fontWeight = FontWeight.Bold)
                Text("Giúp cây lớn khỏe hơn", color = Muted, style = MaterialTheme.typography.bodySmall)
                if (ownedCount > 0) Text("Đang có: $ownedCount", color = Green, style = MaterialTheme.typography.labelSmall)
            }
            Button(
                onClick = onBuy,
                enabled = canAfford,
                shape = RoundedCornerShape(15.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ShopPriceGold)
            ) { Text("⭐ 25", color = Deep, fontWeight = FontWeight.ExtraBold) }
        }
    }
}

@Composable
private fun ShopGardenFooter(onClick: () -> Unit) {
    Surface(onClick = onClick, color = Color.White.copy(alpha = .9f), shape = RoundedCornerShape(18.dp), border = androidx.compose.foundation.BorderStroke(1.dp, Green.copy(alpha = .14f))) {
        Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("🧺", fontSize = 26.sp)
            Text("Đã chọn đủ hạt? Ghé khu vườn gieo ngay nhé.", color = Deep, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
            Text("→", color = Green, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun ShopSeedDetailDialog(plant: ShopSeed, ownedCount: Int, canAfford: Boolean, onDismiss: () -> Unit, onBuy: () -> Unit) {
    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        ShopSeedDetailCard(plant, ownedCount, canAfford, onDismiss, onBuy)
    }
}

@Composable
private fun ShopSeedDetailCard(plant: ShopSeed, ownedCount: Int, canAfford: Boolean, onDismiss: () -> Unit, onBuy: () -> Unit) {
    Surface(Modifier.fillMaxWidth(.9f).widthIn(max = 420.dp), color = Cream, shape = RoundedCornerShape(28.dp), shadowElevation = 16.dp) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(color = Green, shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp), modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(18.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    KawaiiPottedPlant(shopCatalog.indexOf(plant).coerceAtLeast(0), Modifier.size(92.dp, 104.dp), potExpression = 1)
                    Text(plant.name, color = Color.White, fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.titleLarge)
                    Text("Một người bạn mới cho khu vườn", color = Color.White.copy(alpha = .84f), style = MaterialTheme.typography.bodySmall)
                }
            }
            Text(plant.description, color = Deep, textAlign = androidx.compose.ui.text.style.TextAlign.Center, modifier = Modifier.padding(horizontal = 22.dp, vertical = 14.dp))
            Row(Modifier.fillMaxWidth().padding(horizontal = 18.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                DetailBadge("⭐ ${plant.price} xu", Modifier.weight(1f))
                DetailBadge("Đang có $ownedCount", Modifier.weight(1f))
            }
            Row(Modifier.fillMaxWidth().padding(18.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                TextButton(onClick = onDismiss, modifier = Modifier.weight(1f)) { Text("Để sau", color = Green) }
                Button(onClick = onBuy, enabled = canAfford, modifier = Modifier.weight(1.3f), shape = RoundedCornerShape(15.dp), colors = ButtonDefaults.buttonColors(containerColor = Green)) {
                    Text("Mang về 🌱", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun DetailBadge(text: String, modifier: Modifier = Modifier) {
    Surface(modifier, color = Pale, shape = RoundedCornerShape(14.dp)) {
        Text(text, color = Deep, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 10.dp, vertical = 9.dp), maxLines = 1)
    }
}

@Preview(name = "Nursery shelf", showBackground = true, widthDp = 360)
@Composable
fun ShopNurseryDisplayPreview() {
    MamXanhPreviewTheme { ShopNurseryDisplay() }
}

@Preview(name = "Seed shop · populated", showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun ShopScreenPreview() {
    MamXanhPreviewTheme {
        ShopScreen(
            ShopUiState(ShopWallet(75, mapOf("succulent" to 2, "fertilizer" to 1)), false),
            onIntent = {}
        )
    }
}

@Preview(name = "Seed detail popup", showBackground = true)
@Composable
fun ShopSeedDetailDialogPreview() {
    MamXanhPreviewTheme { ShopSeedDetailCard(shopCatalog.first(), 2, true, {}, {}) }
}

@Preview(name = "Fertilizer shop item", showBackground = true)
@Composable
fun FertilizerShopCardPreview() {
    MamXanhPreviewTheme { FertilizerShopCard(1, true, {}) }
}

@Preview(name = "Shop garden shortcut", showBackground = true)
@Composable
fun ShopGardenFooterPreview() {
    MamXanhPreviewTheme { ShopGardenFooter {} }
}

@Preview(name = "Shop detail price badge", showBackground = true)
@Composable
fun ShopDetailBadgePreview() {
    MamXanhPreviewTheme { DetailBadge("⭐ 25 xu", Modifier.width(150.dp)) }
}
