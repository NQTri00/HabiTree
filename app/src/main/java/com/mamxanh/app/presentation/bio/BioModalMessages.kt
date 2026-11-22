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
import com.mamxanh.app.core.components.design.AppTypography
import com.mamxanh.core.ui.R
import org.json.JSONObject

@Composable
internal fun BioModalMessages(
    type: String,
    world: BioWorld,
    ui: BioController,
    tree: JSONObject?,
    dismiss: () -> Unit,
) {
    when (type) {
        "water-all" -> {
            BioTag(stringResource(R.string.copy_mien_phi_dfc4f7), modifier = Modifier.fillMaxWidth())
            BioFacts(
                listOf(
                    stringResource(R.string.copy_can_tuoi_a0957f) to
                        world.trees.count { !it.b("watered") && it.s("state") != "dead" }.toString(),
                    stringResource(R.string.copy_da_tuoi_de2042) to world.trees.count { it.b("watered") }.toString(),
                    stringResource(R.string.copy_kho_heo_bf546a) to world.trees.count { it.s("state") == "dead" }.toString(),
                ),
            )
            Bt(
                stringResource(R.string.copy_chi_tuoi_cay_song_can_nuoc_thoi_gian_khong_cong_don_6737f7),
                size = AppTypography.SIZE_12,
                tint = bp.muted,
            )
        }
        "unlock" -> {
            val cost =
                400 + (world.state.i("unlocked", 5) - 5).coerceAtLeast(0) * 200
            BioFacts(
                listOf(
                    stringResource(R.string.copy_gia_mo_o_708ec3) to stringResource(R.string.copy_0_xu_251e68, cost),
                    stringResource(R.string.copy_vi_hien_tai_127da5) to stringResource(R.string.copy_0_xu_251e68, world.coins),
                ),
            )
            if (world.coins <
                cost
            ) {
                Bt(
                    stringResource(R.string.copy_can_them_0_xu_d7d41b, cost - world.coins),
                    size = AppTypography.SIZE_12,
                    tint = bp.muted,
                )
            }
        }
        "fertilizer-help" ->
            Bt(
                stringResource(R.string.copy_bon_phan_tai_cay_dang_lon_da_duoc_tuoi_ee5c0a),
                size = AppTypography.SIZE_12,
                tint = bp.muted,
            )
        "remove", "sell" -> {
            Bt(tree?.s("name").orEmpty(), size = AppTypography.SIZE_17, bold = true)
            Bt(
                stringResource(R.string.copy_cay_roi_o_vinh_vien_khong_hoan_lai_hat_hoac_tien_tri_af5127),
                size = AppTypography.SIZE_12,
                tint = bp.muted,
            )
            if (type ==
                "sell"
            ) {
                val sale = world.species(tree?.s("species") ?: "succulent").i("sale")
                BioFacts(
                    listOf(
                        stringResource(R.string.copy_gia_ban_358aea) to stringResource(R.string.copy_0_xu_251e68, sale),
                        stringResource(R.string.copy_nhan_hom_nay_78a91a) to
                            stringResource(
                                R.string.copy_0_xu_251e68,
                                minOf(sale, (50 - world.state.i("earned")).coerceAtLeast(0)),
                            ),
                    ),
                )
                Bt(
                    stringResource(R.string.copy_phan_vuot_50_xu_ngay_se_mat_khong_de_danh_f04e9d),
                    size = AppTypography.SIZE_12,
                    tint = AppColors.Danger,
                )
            } else {
                BioTag(
                    stringResource(R.string.copy_khong_nhan_xu_hoac_hat_a3884a),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
        "archive" ->
            Bt(
                stringResource(R.string.copy_luu_tru_viec_giu_nguyen_lich_su_630f63),
                size = AppTypography.SIZE_12,
                tint = bp.muted,
            )
        "discard" ->
            Bt(
                stringResource(R.string.copy_noi_dung_chua_luu_se_mat_20dc15),
                size = AppTypography.SIZE_12,
                tint = bp.muted,
            )
        "ticket" ->
            Bt(
                stringResource(R.string.copy_250_xu_bao_ve_mot_ngay_bo_lo_6a13cf),
                size = AppTypography.SIZE_12,
                tint = bp.muted,
            )
        "adjust" ->
            Bt(
                stringResource(R.string.copy_chuyen_sang_ngay_thuong_tu_ngay_mai_giu_lich_su_va_k_02be72),
                size = AppTypography.SIZE_12,
                tint = bp.muted,
            )
        "permission" ->
            Bt(
                stringResource(R.string.copy_chi_nhac_nhung_viec_da_chon_ton_trong_gio_yen_lang_t_ae6dcc),
                size = AppTypography.SIZE_12,
                tint = bp.muted,
            )
        "permission-choice", "permission-settings" -> {
            BioButton(stringResource(R.string.copy_cho_phep_f1a437), { ui.send("permission-allow") }, Modifier.fillMaxWidth())
            BioButton(
                if (type ==
                    "permission-choice"
                ) {
                    stringResource(R.string.copy_de_sau_9be0a7)
                } else {
                    stringResource(R.string.copy_tat_thong_bao_9ba509)
                },
                {
                    if (type ==
                        "permission-choice"
                    ) {
                        dismiss()
                    } else {
                        ui.send("permission-settings")
                    }
                },
                Modifier.fillMaxWidth(),
                secondary = true,
            )
        }
        "clear" ->
            Bt(
                stringResource(R.string.copy_ban_co_the_xuat_ban_sao_truoc_khi_tiep_tuc_60f8b1),
                size = AppTypography.SIZE_12,
                tint = bp.muted,
            )
        "clear-final" ->
            Bt(
                stringResource(R.string.copy_xoa_du_lieu_va_bat_dau_lai_60edd1),
                size = AppTypography.SIZE_12,
                tint = bp.muted,
            )
        "recover" ->
            Bt(
                stringResource(R.string.copy_thay_du_lieu_hien_tai_tren_thiet_bi_khong_gop_xuat_b_b779a4),
                size = AppTypography.SIZE_12,
                tint = bp.muted,
            )
        "confirm-import" -> {
            BioFacts(
                listOf(
                    stringResource(R.string.copy_viec_818768) to (ui.settingsRequests.backupImport?.tasks ?: "0"),
                    stringResource(R.string.copy_cay_c854d7) to (ui.settingsRequests.backupImport?.trees ?: "0"),
                    stringResource(
                        R.string.copy_luot_hoan_thanh_02de15,
                    ) to (ui.settingsRequests.backupImport?.occurrences ?: "0"),
                    stringResource(R.string.copy_tao_luc_b1415f) to
                        (
                            ui.settingsRequests.backupImport
                                ?.created
                                ?.takeIf { it.isNotBlank() } ?: stringResource(
                                R.string.copy_khong_ro_8af7da,
                            )
                        ),
                ),
            )
            Bt(
                stringResource(R.string.copy_khong_gop_mot_ban_an_toan_duoc_luu_truoc_khi_thay_ca_11d8fa),
                size = AppTypography.SIZE_12,
                tint = bp.muted,
            )
        }
        "license" -> {
            val context = LocalContext.current
            val license =
                remember {
                    context.resources
                        .openRawResource(com.mamxanh.app.R.raw.apache_license)
                        .bufferedReader()
                        .use {
                            it.readText()
                        }.replace(
                            "\r\n",
                            "\n",
                        ).replace(
                            Regex("(?m)^[ \\t]+"),
                            "",
                        ).replace(Regex("(?<!\\n)\\n(?!\\n)"), " ")
                        .replace(Regex("[ \\t]{2,}"), " ")
                        .trim()
                }
            Bt(license, size = AppTypography.SIZE_13, line = AppTypography.LINE_HEIGHT_21_45)
        }
        "focus-end" ->
            Bt(
                stringResource(R.string.copy_phan_da_lam_se_duoc_luu_1e29e9),
                size = AppTypography.SIZE_12,
                tint = bp.muted,
            )
    }
}

@Preview(name = "Focus warning content", widthDp = 335, showBackground = true)
@Composable
private fun BioModalMessagesPreview() {
    val context = LocalContext.current
    val world = remember(context) { BioWorld.reference(context) }
    BioTheme(world, "1") {
        Column {
            BioModalMessages("focus-end", world, remember { BioController("focus") }, null, {})
        }
    }
}
