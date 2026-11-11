package com.mamxanh.app.feature.shop.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.mamxanh.app.feature.shop.domain.model.shopCatalog
import com.mamxanh.app.presentation.ui.*
import com.mamxanh.app.presentation.ui.components.*
import com.mamxanh.app.presentation.ui.components.GardenRichText as Text
import com.mamxanh.shared.model.Balance

@Composable
fun PurchaseConfirmation(id: String, state: ShopUiState, onDismiss: () -> Unit, onBuy: (Int, String) -> Unit) {
    val seed = shopCatalog.firstOrNull { it.id == id }
    val price = seed?.price ?: Balance.FERTILIZER
    var quantity by rememberSaveable(id) { mutableIntStateOf(1) }
    val key = rememberSaveable(id) { java.util.UUID.randomUUID().toString() }
    val total = price * quantity
    val remaining = state.wallet.coins - total
    val enabled = !state.isPurchasing && !state.isLoading
    Dialog(onDismissRequest = { if (!state.isPurchasing) onDismiss() },
        properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false)) {
        Box(Modifier.fillMaxSize().safeDrawingPadding().padding(16.dp), contentAlignment = Alignment.Center) {
            GardenBotanicalFrame(Modifier.widthIn(max = 440.dp).fillMaxWidth().heightIn(max = 680.dp),
                contentPadding = PaddingValues(16.dp), cornerSize = 28.dp, skin = GardenPanelSkin.DIALOG) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Text("Chọn số lượng", Modifier.weight(1f), color = Deep, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        IconButton(onClick = onDismiss, enabled = !state.isPurchasing, modifier = Modifier.size(48.dp)) {
                            Icon(Icons.Rounded.Close, "Đóng", tint = Deep, modifier = Modifier.size(22.dp))
                        }
                    }
                    Column(Modifier.weight(1f, fill = false).verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        if (seed != null) PaintedGardenPlant(id, Modifier.size(104.dp))
                        else GardenArtwork(GardenArt.FERTILIZER, Modifier.size(88.dp))
                        Text(seed?.name ?: "Phân hữu cơ", color = Deep, fontSize = 22.sp,
                            fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                        Text(seed?.description ?: "Giúp cây lớn thêm khi còn có thể tăng trưởng.",
                            color = Muted, fontSize = 13.sp, lineHeight = 18.sp, textAlign = TextAlign.Center)
                        FlowRow(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                            verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            GardenBotanicalFrame(contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                cornerSize = 18.dp, skin = GardenPanelSkin.PRICE) {
                                Text("🪙 $price xu / món", color = Deep, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                            GardenBotanicalFrame(contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp), cornerSize = 18.dp) {
                                Text("Trong túi: ${state.wallet.inventory[id] ?: 0}", color = Green, fontSize = 13.sp)
                            }
                        }
                        QuantitySelector(quantity, enabled, { quantity-- }, { quantity++ })
                        Column(Modifier.fillMaxWidth().mintDepth(RoundedCornerShape(18.dp), MintDepth.secondary, 2.dp).padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Tổng cộng", color = Deep, fontSize = 14.sp)
                                Text("🪙 $total xu", color = Green, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }
                            Text("Ví: ${state.wallet.coins} xu · " +
                                if (remaining >= 0) "Còn $remaining xu" else "Thiếu ${-remaining} xu",
                                color = if (remaining >= 0) Muted else Color(0xFFAC3F37), fontSize = 12.sp, lineHeight = 17.sp)
                            if (remaining < 0) {
                                val days = kotlin.math.ceil((-remaining).toDouble() / state.averageEarnings.coerceAtLeast(1)).toInt()
                                Text("Khoảng $days ngày tích xu · Ước tính ${state.averageEarnings} xu/ngày",
                                    color = Muted, fontSize = 11.sp, lineHeight = 16.sp)
                            }
                        }
                    }
                    Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        GardenPaintedButton("Để sau", onDismiss, Modifier.weight(1f).fillMaxHeight(),
                            primary = false, enabled = !state.isPurchasing)
                        GardenPaintedButton(if (state.isPurchasing) "Đang mua…" else "Mua · $total xu",
                            { onBuy(quantity, key) }, Modifier.weight(1.5f).fillMaxHeight(),
                            enabled = remaining >= 0 && enabled)
                    }
                }
            }
        }
    }
}

@Composable
private fun QuantitySelector(quantity: Int, enabled: Boolean, onDecrease: () -> Unit, onIncrease: () -> Unit) {
    Row(Modifier.fillMaxWidth().mintDepth(RoundedCornerShape(28.dp), MintDepth.secondary, 3.dp).padding(6.dp),
        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        QuantityButton(false, enabled && quantity > 1, onDecrease)
        Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(quantity.toString(), color = Deep, fontSize = 26.sp, lineHeight = 30.sp, fontWeight = FontWeight.Bold)
            Text("Số lượng", color = Muted, fontSize = 11.sp, lineHeight = 14.sp)
        }
        QuantityButton(true, enabled && quantity < 99, onIncrease)
    }
}

@Composable
private fun QuantityButton(increase: Boolean, enabled: Boolean, onClick: () -> Unit) {
    Box(Modifier.size(48.dp).alpha(if (enabled) 1f else .35f)
        .mintDepth(CircleShape, if (increase) MintDepth.primary else MintDepth.card, if (enabled) 3.dp else 0.dp)
        .clip(CircleShape).clickable(enabled = enabled, role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center) {
        Icon(if (increase) Icons.Rounded.Add else Icons.Rounded.Remove,
            if (increase) "Tăng số lượng" else "Giảm số lượng",
            tint = if (increase) Color.White else Green, modifier = Modifier.size(24.dp))
    }
}
