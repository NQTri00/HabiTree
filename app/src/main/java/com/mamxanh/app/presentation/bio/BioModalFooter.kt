package com.mamxanh.app.presentation.bio

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import com.mamxanh.app.core.components.design.*
import com.mamxanh.app.core.components.design.AppDimensions
import com.mamxanh.app.feature.shop.presentation.ui.shop.PurchaseAvailability
import com.mamxanh.app.feature.shop.presentation.ui.shop.ShopPendingAction
import com.mamxanh.core.ui.R

@Composable
internal fun BioModalFooter(
    type: String,
    id: String,
    world: BioWorld,
    ui: BioController,
    purchase: PurchaseAvailability,
    seed: Boolean,
    permanent: Boolean,
    owned: Boolean,
    full: Boolean,
    dismiss: () -> Unit,
    confirmArchive: (() -> Unit)? = null,
    archiveBusy: Boolean = false,
    confirmFocusEnd: (() -> Unit)? = null,
    focusBusy: Boolean = false,
    confirmGardenAction: (() -> Unit)? = null,
    gardenBusy: Boolean = false,
    canUnlock: Boolean? = null,
    confirmDisposition: (() -> Unit)? = null,
    treeBusy: Boolean = false,
    confirmTicket: (() -> Unit)? = null,
    restBusy: Boolean = false,
    canBuyTicket: Boolean? = null,
    confirmAdjustment: (() -> Unit)? = null,
    insightsBusy: Boolean = false,
    buy: (() -> Unit)? = null,
    shopBuying: Boolean = false,
    pendingPurchase: ShopPendingAction? = null,
    buyAndEquip: (() -> Unit)? = null,
    equip: (() -> Unit)? = null,
    buyAndPlant: (() -> Unit)? = null,
    quantity: Int = ui.shopSelection.quantity,
    equippedCan: String = world.state.s("equipped"),
    requestPermission: (() -> Unit)? = null,
    backupAction: (() -> Unit)? = null,
) {
    HorizontalDivider(color = bp.line)
    Row(
        Modifier.fillMaxWidth().padding(
            start = AppDimensions.Dp16,
            end = AppDimensions.Dp16,
            top = AppDimensions.Dp12,
            bottom = AppDimensions.Dp16,
        ),
        horizontalArrangement = Arrangement.spacedBy(AppDimensions.Dp10),
    ) {
        if (type == "purchase" && seed) {
            val buyLabel =
                if (pendingPurchase == ShopPendingAction.Buy) {
                    R.string.copy_dang_mua_c2f9ae
                } else {
                    R.string.copy_mua_vao_tui_4cd4d5
                }
            BioButton(stringResource(buyLabel), {
                if (buy != null) buy() else Unit
            }, Modifier.weight(1f), secondary = true, enabled = purchase.canSubmit)
            BioButton(
                stringResource(
                    when {
                        pendingPurchase == ShopPendingAction.BuyAndPlant -> R.string.copy_dang_mua_c2f9ae
                        quantity > 1 -> R.string.copy_mua_gieo_1_690d4f
                        else -> R.string.copy_mua_gieo_754323
                    },
                ),
                {
                    if (buyAndPlant != null) buyAndPlant() else Unit
                },
                Modifier.weight(1f),
                enabled =
                    !full && purchase.canSubmit,
            )
        } else {
            BioButton(
                if (type == "purchase" &&
                    permanent &&
                    !owned
                ) {
                    stringResource(R.string.copy_de_sau_9be0a7)
                } else {
                    stringResource(R.string.copy_huy_74fcd3)
                },
                dismiss,
                Modifier.weight(1f),
                secondary = true,
                enabled = !ui.busy && !shopBuying,
            )
            val label =
                when (type) {
                    "purchase" ->
                        if (pendingPurchase == ShopPendingAction.Buy ||
                            pendingPurchase == ShopPendingAction.BuyAndEquip
                        ) {
                            stringResource(R.string.copy_dang_mua_c2f9ae)
                        } else if (permanent) {
                            if (owned) {
                                if (equippedCan ==
                                    id
                                ) {
                                    stringResource(R.string.copy_dang_dung_275ca2)
                                } else {
                                    stringResource(R.string.copy_trang_bi_9e8e12)
                                }
                            } else {
                                stringResource(R.string.copy_mua_trang_bi_33a520)
                            }
                        } else {
                            stringResource(R.string.copy_mua_c86bcd)
                        }
                    ;"plant" -> stringResource(R.string.copy_xem_o_khoa_2d0677)
                    "water-all" -> stringResource(R.string.copy_tuoi_cay_0990f6)
                    "unlock" -> stringResource(R.string.copy_mo_o_6d274c)
                    "fertilizer-help" -> stringResource(R.string.copy_chon_cay_61f0ec)
                    "remove" -> stringResource(R.string.copy_don_cay_e3d410)
                    "sell" -> stringResource(R.string.copy_ban_cay_5210c7)
                    "archive" -> stringResource(R.string.action_archive)
                    "discard" -> stringResource(R.string.copy_bo_thay_doi_e01800)
                    "ticket" -> stringResource(R.string.copy_mua_ve_e9dab8)
                    "adjust" -> stringResource(R.string.copy_ap_dung_ff6315)
                    "permission", "clear" -> stringResource(R.string.copy_tiep_tuc_87a7ae)
                    "clear-final" -> stringResource(R.string.copy_xoa_du_lieu_398085)
                    "recover" -> stringResource(R.string.copy_khoi_phuc_c652d9)
                    "confirm-import" -> stringResource(R.string.copy_thay_the_e4d19c)
                    "focus-end" -> stringResource(R.string.copy_ket_thuc_351849)
                    else -> stringResource(R.string.copy_xac_nhan_1503b0)
                }
            val enabled =
                !ui.busy &&
                    !shopBuying &&
                    !archiveBusy &&
                    !focusBusy &&
                    !gardenBusy &&
                    !treeBusy &&
                    !restBusy &&
                    !insightsBusy &&
                    when (type) {
                        "purchase" -> purchase.canSubmit && !(owned && equippedCan == id)
                        "unlock" ->
                            canUnlock ?: (world.coins >= 400 + (world.state.i("unlocked", 5) - 5).coerceAtLeast(0) * 200)
                        "ticket" -> canBuyTicket ?: (world.coins >= 250)
                        else -> true
                    }
            BioButton(label, {
                when (type) {
                    "purchase" ->
                        if (!permanent && buy != null) {
                            buy()
                        } else if (permanent && owned && equip != null) {
                            equip()
                        } else if (permanent && !owned && buyAndEquip != null) {
                            buyAndEquip()
                        } else {
                            Unit
                        }
                    "plant", "fertilizer-help" -> ui.go("garden")
                    "discard" -> ui.discard()
                    "focus-end" -> confirmFocusEnd?.invoke()
                    "ticket" -> confirmTicket?.invoke()
                    "adjust" -> confirmAdjustment?.invoke()
                    "unlock", "water-all" -> confirmGardenAction?.invoke()
                    "remove", "sell" -> confirmDisposition?.invoke()
                    "archive" -> if (confirmArchive != null) confirmArchive() else Unit
                    "permission" -> if (requestPermission != null) requestPermission() else ui.show("permission-choice")
                    "clear" -> if (backupAction != null) backupAction() else ui.show("clear-final")
                    "clear-final", "recover", "confirm-import" -> backupAction?.invoke()
                    else -> Unit
                }
            }, Modifier.weight(1f), enabled = enabled, danger = type in listOf("remove", "sell", "clear-final"))
        }
    }
}

@Preview(name = "Destructive confirmation actions", widthDp = 335, showBackground = true)
@Composable
private fun BioModalFooterPreview() {
    val context = LocalContext.current
    val world = remember(context) { BioWorld.reference(context) }
    val ui = remember { BioController("tree") }
    val purchase = ui.shopSelection.purchaseAvailability(world.coins, 0, false, false, false)
    BioTheme(world, "1") {
        Column { BioModalFooter("remove", "1", world, ui, purchase, false, false, false, false, {}) }
    }
}
