package com.mamxanh.app.presentation.bio

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.*
import com.mamxanh.app.core.components.design.*
import com.mamxanh.app.core.components.design.AppDimensions
import com.mamxanh.app.feature.shop.presentation.ui.shop.PurchaseAvailability
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
            BioButton(stringResource(R.string.copy_mua_vao_tui_4cd4d5), {
                ui.send(
                    "buy",
                    mapOf(
                        "id" to id,
                        "quantity" to ui.shopSelection.quantity.toString(),
                    ),
                )
            }, Modifier.weight(1f), secondary = true, enabled = purchase.canSubmit)
            BioButton(
                if (ui.shopSelection.quantity >
                    1
                ) {
                    stringResource(R.string.copy_mua_gieo_1_690d4f)
                } else {
                    stringResource(R.string.copy_mua_gieo_754323)
                },
                {
                    ui.send(
                        "buy-plant",
                        mapOf(
                            "id" to id,
                            "quantity" to ui.shopSelection.quantity.toString(),
                            "plot" to ui.plantingSelection.plotKey,
                        ),
                    )
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
                enabled = !ui.busy,
            )
            val label =
                when (type) {
                    "purchase" ->
                        if (permanent) {
                            if (owned) {
                                if (world.state.s("equipped") ==
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
                    when (type) {
                        "purchase" -> purchase.canSubmit && !(owned && world.state.s("equipped") == id)
                        "unlock" ->
                            world.coins >=
                                400 + (world.state.i("unlocked", 5) - 5).coerceAtLeast(0) * 200
                        ;"ticket" -> world.coins >= 250
                        else -> true
                    }
            BioButton(label, {
                when (type) {
                    "purchase" ->
                        ui.send(
                            if (owned) {
                                "equip:$id"
                            } else if (permanent) {
                                "buy-equip"
                            } else {
                                "buy"
                            },
                            mapOf(
                                "id" to id,
                                "quantity" to ui.shopSelection.quantity.toString(),
                            ),
                        )
                    "plant", "fertilizer-help" -> ui.go("garden")
                    "discard" -> ui.discard()
                    "permission" -> if (world.native)ui.send("permission-allow")else ui.show("permission-choice")
                    "clear" -> ui.show("clear-final")
                    "clear-final" -> ui.send("clear-data")
                    "recover" -> ui.send("recover-data")
                    "confirm-import" -> ui.send("confirm-import")
                    else -> ui.send("confirm-$type", mapOf("id" to id))
                }
            }, Modifier.weight(1f), enabled = enabled, danger = type in listOf("remove", "sell", "clear-final"))
        }
    }
}
