package com.mamxanh.app.feature.shop.presentation.ui.shop

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

@Stable
class ShopSelectionState {
    var shopTab by mutableStateOf("seeds")
    var inventoryTab by mutableStateOf("seeds")
    var targetTree by mutableStateOf("")
    var quantity by mutableIntStateOf(1)
        private set

    fun updateQuantity(value: Int) {
        quantity = value.coerceIn(1, 99)
    }

    fun purchaseAvailability(
        coins: Int,
        unitPrice: Int,
        owned: Boolean,
        permanent: Boolean,
        busy: Boolean,
    ): PurchaseAvailability = purchaseAvailability(coins, unitPrice, owned, permanent, busy, quantity)

    fun accessoryTarget(
        livingIds: List<String>,
        homeTreeId: String,
    ): String? =
        targetTree.takeIf(livingIds::contains)
            ?: homeTreeId.takeIf(livingIds::contains)
            ?: livingIds.firstOrNull()
}

fun purchaseAvailability(
    coins: Int,
    unitPrice: Int,
    owned: Boolean,
    permanent: Boolean,
    busy: Boolean,
    quantity: Int,
): PurchaseAvailability {
    val total = if (owned) 0 else unitPrice * if (permanent) 1 else quantity.coerceIn(1, 99)
    return PurchaseAvailability(
        total = total,
        canSubmit = !busy && coins >= total,
        canBuyAccessory = !busy && coins >= unitPrice,
        canEquipOrBuyAccessory = !busy && (owned || coins >= unitPrice),
    )
}

data class PurchaseAvailability(
    val total: Int,
    val canSubmit: Boolean,
    val canBuyAccessory: Boolean,
    val canEquipOrBuyAccessory: Boolean,
) {
    fun canEquipAccessory(
        hasTarget: Boolean,
        equipped: Boolean,
    ): Boolean = hasTarget && !equipped && canEquipOrBuyAccessory
}
