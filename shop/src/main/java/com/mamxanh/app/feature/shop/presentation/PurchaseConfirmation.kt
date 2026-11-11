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

@Composable
fun PurchaseConfirmation(id: String, state: ShopUiState, onDismiss: () -> Unit, onEquip: () -> Unit = {}, onBuy: (Int, String) -> Unit) {
    val seed = shopCatalog.firstOrNull { it.id == id }
    val product = com.mamxanh.shared.model.careProducts.firstOrNull { it.id == id }
    val permanent = product?.permanent == true
    val owned = permanent && (state.wallet.inventory[id] ?: 0) > 0
    val price = seed?.price ?: product?.price ?: 0
    var quantity by rememberSaveable(id) { mutableIntStateOf(1) }
    val key = rememberSaveable(id) { java.util.UUID.randomUUID().toString() }
    val total = if(owned) 0 else price * quantity
    val remaining = state.wallet.coins - total
    val enabled = !state.isPurchasing && !state.isLoading
    MintDialog(if(permanent)"Dụng cụ chăm sóc" else "Thêm vào túi",
        onDismiss={if(!state.isPurchasing)onDismiss()},
        confirmLabel=if(owned)if(state.wallet.equippedCan==id)"Đang dùng" else "Trang bị" else if(state.isPurchasing)"Đang mua…" else "Mua",
        onConfirm={if(owned)onEquip() else onBuy(if(permanent)1 else quantity,key)},
        confirmEnabled=enabled && if(owned)state.wallet.equippedCan!=id else remaining>=0,
        dismissEnabled=!state.isPurchasing) {
        Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(6.dp)) {
            if(seed!=null)PaintedGardenPlant(id,Modifier.size(36.dp))
            else GardenArtwork(if(permanent)GardenArt.WATERING_CAN else GardenArt.FERTILIZER,Modifier.size(32.dp))
            Column(Modifier.weight(1f),verticalArrangement=Arrangement.spacedBy(5.dp)) {
                Text(seed?.name ?: product?.name.orEmpty(),color=Deep,fontSize=13.sp,lineHeight=17.sp,fontWeight=FontWeight.Bold)
                Text("$price xu / món",color=Green,fontSize=12.sp,fontWeight=FontWeight.SemiBold)
                Text("Trong túi: ${state.wallet.inventory[id] ?: 0}",color=Muted,fontSize=11.sp)
            }
        }
        Text(seed?.description ?: product?.description.orEmpty(),color=Muted,fontSize=12.sp,lineHeight=17.sp)
        if(seed!=null) {
            val profile=com.mamxanh.shared.model.lifecycleProfile(id)
            MintStatusPill("${profile.tier} · ${profile.stageCount} dáng")
        }
        if(!permanent)QuantitySelector(quantity,enabled,{quantity--},{quantity++})
        MintMetricStrip(listOf("Tổng" to "$total xu","Ví" to "${state.wallet.coins} xu",
            (if(remaining>=0)"Còn lại" else "Cần thêm") to "${kotlin.math.abs(remaining)} xu"))
        if(remaining<0) {
            val days=kotlin.math.ceil((-remaining).toDouble()/state.averageEarnings.coerceAtLeast(1)).toInt()
            Text("Khoảng $days ngày tích xu · Ước tính ${state.averageEarnings} xu/ngày",color=Muted,fontSize=11.sp,lineHeight=15.sp)
        }
    }
}

@Composable
private fun QuantitySelector(quantity: Int, enabled: Boolean, onDecrease: () -> Unit, onIncrease: () -> Unit) {
    Row(Modifier.fillMaxWidth().mintDepth(RoundedCornerShape(16.dp), MintDepth.secondary, 3.dp).padding(2.dp),
        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        QuantityButton(false, enabled && quantity > 1, onDecrease)
        Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(quantity.toString(), color = Deep, fontSize = 17.sp, lineHeight = 20.sp, fontWeight = FontWeight.Bold)
            Text("Số lượng", color = Muted, fontSize = 11.sp, lineHeight = 14.sp)
        }
        QuantityButton(true, enabled && quantity < 99, onIncrease)
    }
}

@Composable
private fun QuantityButton(increase: Boolean, enabled: Boolean, onClick: () -> Unit) {
    Box(Modifier.size(48.dp).alpha(if (enabled) 1f else .35f)
        .clickable(enabled = enabled, role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center) {
        Box(Modifier.size(30.dp)
            .mintDepth(CircleShape, if(increase) MintDepth.primary else MintDepth.card, if(enabled)3.dp else 0.dp)
            .clip(CircleShape),contentAlignment=Alignment.Center) {
            Icon(if(increase)Icons.Rounded.Add else Icons.Rounded.Remove,
                if(increase)"Tăng số lượng" else "Giảm số lượng",
                tint=if(increase)Color.White else Green,modifier=Modifier.size(16.dp))
        }
    }
}
