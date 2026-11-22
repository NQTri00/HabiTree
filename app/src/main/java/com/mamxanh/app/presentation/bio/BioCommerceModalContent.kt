package com.mamxanh.app.presentation.bio

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import com.mamxanh.app.core.components.design.*
import com.mamxanh.app.core.components.design.AppColors
import com.mamxanh.app.core.components.design.AppDimensions
import com.mamxanh.app.core.components.design.AppIcons
import com.mamxanh.app.core.components.design.AppOpacity
import com.mamxanh.app.core.components.design.AppRadii
import com.mamxanh.app.core.components.design.AppTypography
import com.mamxanh.app.feature.shop.presentation.ui.shop.PurchaseAvailability
import com.mamxanh.core.ui.R
import org.json.JSONObject

@Composable
internal fun BioPurchaseBody(
    id: String,
    world: BioWorld,
    ui: BioController,
    item: JSONObject,
    seed: Boolean,
    permanent: Boolean,
    total: Int,
    full: Boolean,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AppDimensions.Dp16),
    ) {
        if (seed) {
            BioPlant(
                id,
                Modifier.size(AppDimensions.Dp90),
            )
        } else {
            Box(Modifier.size(AppDimensions.Dp48), contentAlignment = Alignment.Center) {
                Bi(if (permanent)"water"else "fertilizer", size = AppIcons.SIZE_32)
            }
        }
        Column(Modifier.weight(1f)) {
            Bt(item.s("name"), size = AppTypography.SIZE_17, bold = true)
            val price = stringResource(R.string.copy_0_xu_mon_3dd1cb, item.i("price"))
            val inInventory = stringResource(R.string.copy_trong_tui_0_79a061, world.count(id))
            Bt("$price · $inInventory", size = AppTypography.SIZE_12, tint = bp.muted)
        }
    }
    Bt(
        item.s("description").ifBlank {
            if (permanent) {
                stringResource(
                    R.string.copy_tuoi_trong_0_gio_dung_lau_dai_7c36d5,
                    item.i("hours"),
                )
            } else {
                stringResource(
                    R.string.copy_0_trong_1_gio_dung_khi_cay_da_tuoi_mot_lan_moi_24_gi_acf259,
                    item.s("rate"),
                    item.i("hours"),
                )
            }
        },
        size = AppTypography.SIZE_12,
        tint = bp.muted,
    )
    if (seed) {
        BioTag(
            stringResource(R.string.copy_0_giai_doan_26f2b4, bioTier(item.s("tier")), item.i("stages")),
            modifier = Modifier.fillMaxWidth(),
        )
    }
    if (!permanent) {
        BioPurchaseQuantity(
            quantity = ui.shopSelection.quantity,
            busy = ui.busy,
            onQuantity = { ui.shopSelection.updateQuantity(it) },
        )
    }
    BioMetrics(
        listOf(
            stringResource(R.string.copy_tong_13fdba) to total.toString(),
            stringResource(R.string.copy_vi_39a29f) to world.coins.toString(),
            (
                if (world.coins <
                    total
                ) {
                    stringResource(R.string.copy_can_them_e011ae)
                } else {
                    stringResource(R.string.copy_con_lai_b3edb1)
                }
            ) to
                kotlin.math.abs(world.coins - total).toString(),
        ),
    )
    if (world.coins <
        total
    ) {
        Bt(
            stringResource(
                R.string.copy_khoang_0_ngay_uoc_tinh_40_xu_ngay_8be6c6,
                kotlin.math
                    .ceil(
                        (total - world.coins) / 40.0,
                    ).toInt(),
            ),
            size = AppTypography.SIZE_12,
            tint = bp.muted,
        )
    }
    if (seed &&
        full
    ) {
        Bt(
            stringResource(R.string.copy_vuon_da_day_ban_van_co_the_mua_hat_vao_tui_e14bdf),
            size = AppTypography.SIZE_12,
            tint = bp.muted,
        )
    }
}

@Composable
internal fun BioAccessoryBody(
    id: String,
    world: BioWorld,
    ui: BioController,
    item: JSONObject,
    owned: Boolean,
    purchase: PurchaseAvailability,
    living: List<JSONObject>,
    target: JSONObject?,
) {
    Box(
        Modifier
            .fillMaxWidth()
            .height(
                AppDimensions.Dp135,
            ).background(
                Brush.linearGradient(listOf(AppColors.White.copy(alpha = AppOpacity.VALUE_6), bp.soft)),
                bioShape(AppRadii.VALUE_20),
            ).border(AppDimensions.Dp1, AppColors.White.copy(alpha = AppOpacity.VALUE_6), bioShape(AppRadii.VALUE_20)),
        contentAlignment = Alignment.Center,
    ) {
        Bi(accessoryIcon(id), size = AppIcons.SIZE_56)
        BioTag(
            if (owned) {
                stringResource(
                    R.string.copy_da_so_huu_822625,
                )
            } else {
                stringResource(R.string.copy_0_xu_251e68, item.i("price"))
            },
            modifier = Modifier.align(Alignment.BottomEnd).padding(AppDimensions.Dp12),
        )
    }
    if (living.isNotEmpty()) {
        BioSelect(
            stringResource(R.string.copy_deo_cho_cay_afc821),
            living.map {
                it.s("id") to
                    it.s("name")
            },
            target?.s("id").orEmpty(),
            onSelect = { ui.shopSelection.targetTree = it },
        )
    } else {
        Bt(
            stringResource(R.string.copy_gieo_mot_cay_truoc_khi_deo_phu_kien_a3a9b9),
            size = AppTypography.SIZE_12,
            tint = bp.muted,
        )
    }
    val other = world.trees.firstOrNull { it.s("decor") == id && it.s("id") != target?.s("id") }
    if (other != null &&
        owned
    ) {
        Bt(
            stringResource(R.string.copy_chuyen_tu_0_khong_mat_them_xu_c8b60f, other.s("name")),
            size = AppTypography.SIZE_12,
            tint = bp.muted,
        )
    }
    Bt(
        stringResource(R.string.copy_chi_trang_tri_khong_thay_doi_toc_do_lon_c1bae1),
        size = AppTypography.SIZE_12,
        tint = bp.muted,
    )
    if (!owned &&
        world.coins < item.i("price")
    ) {
        BioError(
            stringResource(
                R.string.copy_can_them_0_xu_d7d41b,
                item.i("price") - world.coins,
            ),
        )
    }
    val using = target?.s("decor") == id
    Column(verticalArrangement = Arrangement.spacedBy(AppDimensions.Dp10)) {
        BioButton(
            if (using) {
                stringResource(R.string.copy_dang_dung_275ca2)
            } else if (owned) {
                stringResource(R.string.copy_deo_cho_cay_afc821)
            } else {
                stringResource(R.string.copy_mua_deo_d9c74d)
            },
            {
                ui.send(
                    "accessory-use",
                    mapOf(
                        "id" to id,
                        "tree" to target?.s("id").orEmpty(),
                    ),
                )
            },
            Modifier.fillMaxWidth(),
            enabled =
                purchase.canEquipAccessory(
                    target != null,
                    using,
                ),
        )
        if (!owned) {
            BioButton(stringResource(R.string.copy_chi_mua_vao_tui_cbc02d), {
                ui.send(
                    "accessory-buy",
                    mapOf(
                        "id" to id,
                    ),
                )
            }, Modifier.fillMaxWidth(), secondary = true, enabled = purchase.canBuyAccessory)
        }
    }
}

@Preview(name = "Seed purchase content", widthDp = 335, showBackground = true)
@Composable
private fun BioPurchaseBodyPreview() {
    val context = LocalContext.current
    val world = remember(context) { BioWorld.reference(context) }
    BioTheme(world, "1") {
        Column {
            val item = world.species.first()
            BioPurchaseBody(item.s("id"), world, remember { BioController("shop") }, item, true, false, item.i("price"), false)
        }
    }
}
