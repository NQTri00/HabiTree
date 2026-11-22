package com.mamxanh.app.feature.shop.presentation.ui.shop

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.mamxanh.app.core.components.*
import com.mamxanh.app.core.components.components.*
import com.mamxanh.app.core.components.design.AppColors
import com.mamxanh.app.core.components.design.AppDimensions
import com.mamxanh.app.core.components.design.AppOpacity
import com.mamxanh.app.core.components.design.AppTypography
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey
import com.mamxanh.app.core.components.components.GardenRichText as Text

@Composable
fun PurchaseConfirmation(
    id: String,
    state: ShopState,
    onDismiss: () -> Unit,
    onEquip: () -> Unit = {},
    onBuy: (Int, String) -> Unit,
) {
    val seed = shopCatalog.firstOrNull { it.id == id }
    val product =
        com.mamxanh.shared.model.careProducts
            .firstOrNull { it.id == id }
    val permanent = product?.permanent == true
    val owned = permanent && (state.wallet.inventory[id] ?: 0) > 0
    val price = seed?.price ?: product?.price ?: 0
    var quantity by rememberSaveable(id) { mutableIntStateOf(1) }
    val key =
        rememberSaveable(id) {
            java.util.UUID
                .randomUUID()
                .toString()
        }
    val total = if (owned) 0 else price * quantity
    val remaining = state.wallet.coins - total
    val enabled = !state.isPurchasing && !state.isLoading
    MintDialog(
        if (permanent)AppText.text(TextKey.CopyDungCuChamSoc9f1ca9) else AppText.text(TextKey.CopyThemVaoTuiA0e7dc),
        onDismiss = { if (!state.isPurchasing)onDismiss() },
        confirmLabel =
            if (owned) {
                if (state.wallet.equippedCan ==
                    id
                ) {
                    AppText.text(TextKey.CopyDangDung275ca2)
                } else {
                    AppText.text(TextKey.CopyTrangBi9e8e12)
                }
            } else if (state.isPurchasing) {
                AppText.text(TextKey.CopyDangMuaC2f9ae)
            } else {
                AppText.text(TextKey.CopyMuaC86bcd)
            },
        onConfirm = { if (owned)onEquip() else onBuy(if (permanent)1 else quantity, key) },
        confirmEnabled = enabled && if (owned)state.wallet.equippedCan != id else remaining >= 0,
        dismissEnabled = !state.isPurchasing,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(AppDimensions.Dp6)) {
            if (seed != null) {
                PaintedGardenPlant(id, Modifier.size(AppDimensions.Dp36))
            } else {
                GardenArtwork(if (permanent)GardenArt.WATERING_CAN else GardenArt.FERTILIZER, Modifier.size(AppDimensions.Dp32))
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(AppDimensions.Dp5)) {
                Text(
                    seed?.name ?: product?.name.orEmpty(),
                    color = Deep,
                    fontSize = AppTypography.Sp13,
                    lineHeight = AppTypography.Sp17,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    AppText.text(TextKey.Copy0XuMonD8d2ec, price),
                    color = Green,
                    fontSize = AppTypography.Sp12,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    AppText.text(TextKey.CopyTrongTui079a061, state.wallet.inventory[id] ?: 0),
                    color = Muted,
                    fontSize = AppTypography.Sp11,
                )
            }
        }
        Text(
            seed?.description ?: product?.description.orEmpty(),
            color = Muted,
            fontSize = AppTypography.Sp12,
            lineHeight = AppTypography.Sp17,
        )
        if (seed != null) {
            val profile =
                com.mamxanh.shared.model
                    .lifecycleProfile(id)
            MintStatusPill(AppText.text(TextKey.Copy01DangC7b549, profile.tier, profile.stageCount))
        }
        if (!permanent)QuantitySelector(quantity, enabled, { quantity-- }, { quantity++ })
        MintMetricStrip(
            listOf(
                AppText.text(TextKey.CopyTong13fdba) to AppText.text(TextKey.Copy0Xu251e68, total),
                AppText.text(TextKey.CopyVi39a29f) to AppText.text(TextKey.Copy0Xu251e68, state.wallet.coins),
                (if (remaining >= 0)AppText.text(TextKey.CopyConLaiB3edb1) else AppText.text(TextKey.CopyCanThemE011ae)) to
                    AppText.text(TextKey.Copy0Xu251e68, kotlin.math.abs(remaining)),
            ),
        )
        if (remaining < 0) {
            val days = kotlin.math.ceil((-remaining).toDouble() / state.averageEarnings.coerceAtLeast(1)).toInt()
            Text(
                AppText.text(TextKey.CopyKhoang0NgayTichXuUocTinh1XuNgay8fd2f8, days, state.averageEarnings),
                color = Muted,
                fontSize = AppTypography.Sp11,
                lineHeight = AppTypography.Sp15,
            )
        }
    }
}

@Composable
private fun QuantitySelector(
    quantity: Int,
    enabled: Boolean,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .mintDepth(
                RoundedCornerShape(AppDimensions.Dp16),
                MintDepth.secondary,
                AppDimensions.Dp3,
            ).padding(AppDimensions.Dp2),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AppDimensions.Dp8),
    ) {
        QuantityButton(false, enabled && quantity > 1, onDecrease)
        Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                quantity.toString(),
                color = Deep,
                fontSize = AppTypography.Sp17,
                lineHeight = AppTypography.Sp20,
                fontWeight = FontWeight.Bold,
            )
            Text(AppText.text(TextKey.CopySoLuongBdb17c), color = Muted, fontSize = AppTypography.Sp11, lineHeight = AppTypography.Sp14)
        }
        QuantityButton(true, enabled && quantity < 99, onIncrease)
    }
}

@Composable
private fun QuantityButton(
    increase: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Box(
        Modifier
            .size(AppDimensions.Dp48)
            .alpha(if (enabled) AppOpacity.VALUE_1 else AppOpacity.VALUE_35)
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            Modifier
                .size(AppDimensions.Dp30)
                .mintDepth(
                    CircleShape,
                    if (increase) MintDepth.primary else MintDepth.card,
                    if (enabled)AppDimensions.Dp3 else AppDimensions.Dp0,
                ).clip(CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                if (increase)Icons.Rounded.Add else Icons.Rounded.Remove,
                if (increase)AppText.text(TextKey.CopyTangSoLuongF05853) else AppText.text(TextKey.CopyGiamSoLuong9abe78),
                tint = if (increase)AppColors.White else Green,
                modifier = Modifier.size(AppDimensions.Dp16),
            )
        }
    }
}

@Preview(name = "Seed purchase", widthDp = 375, heightDp = 812)
@Preview(name = "Seed purchase at large text", widthDp = 320, heightDp = 812, fontScale = 1.5f)
@Composable
private fun PurchaseConfirmationPreview() {
    MamXanhPreviewTheme {
        PurchaseConfirmation(
            id = shopCatalog.first().id,
            state = ShopState(isLoading = false),
            onDismiss = {},
            onBuy = { _, _ -> },
        )
    }
}
