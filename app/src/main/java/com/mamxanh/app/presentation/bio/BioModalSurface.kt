package com.mamxanh.app.presentation.bio

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.*
import com.mamxanh.app.core.components.design.*
import com.mamxanh.app.core.components.design.AppColors
import com.mamxanh.app.core.components.design.AppDimensions
import com.mamxanh.app.core.components.design.AppIcons
import com.mamxanh.app.core.components.design.AppOpacity
import com.mamxanh.app.core.components.design.AppRadii
import com.mamxanh.app.core.components.design.AppSpacing
import com.mamxanh.app.core.components.design.AppTypography
import com.mamxanh.core.ui.R

@Composable
internal fun BioModalSurface(
    title: String,
    busy: Boolean,
    message: String,
    scrollToBottom: Boolean,
    dismiss: () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
    footer: @Composable ColumnScope.() -> Unit,
) {
    BoxWithConstraints(Modifier.fillMaxSize()) {
        Box(Modifier.fillMaxSize().background(AppColors.Swatch60252A2E).clickable(onClick = dismiss))
        val limit = maxHeight - AppDimensions.Dp32
        Column(
            Modifier
                .align(
                    Alignment.Center,
                ).widthIn(
                    max = AppDimensions.Dp432,
                ).fillMaxWidth()
                .padding(
                    horizontal = AppDimensions.Dp16,
                ).heightIn(
                    max = limit,
                ).shadow(
                    AppDimensions.Dp12,
                    bioShape(AppRadii.VALUE_28),
                ).background(
                    bp.bg,
                    bioShape(AppRadii.VALUE_28),
                ).border(
                    AppDimensions.Dp1,
                    AppColors.White.copy(alpha = AppOpacity.VALUE_6),
                    bioShape(AppRadii.VALUE_28),
                ).padding(AppDimensions.Dp1)
                .clip(bioShape(AppRadii.VALUE_28))
                .clickable(enabled = false) {
                },
        ) {
            Row(
                Modifier.fillMaxWidth().padding(
                    start = AppDimensions.Dp16,
                    end = AppDimensions.Dp8,
                    top = AppDimensions.Dp8,
                    bottom = AppDimensions.Dp4,
                ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AppDimensions.Dp8),
            ) {
                Bt(
                    title,
                    Modifier.weight(1f),
                    size = AppTypography.SIZE_22,
                    line = AppTypography.LINE_HEIGHT_30_8,
                    bold = true,
                    spacing = AppTypography.TRACKING_MINUS_7,
                )
                BioIconButton("close", dismiss, enabled = !busy, iconSize = AppIcons.SIZE_22, tint = bp.muted)
            }
            if (busy)LinearProgressIndicator(Modifier.fillMaxWidth(), color = bp.deep, trackColor = bp.soft)
            Column(
                Modifier
                    .weight(1f, fill = false)
                    .fillMaxWidth()
                    .verticalScroll(
                        rememberScrollState(
                            if (scrollToBottom) {
                                Int.MAX_VALUE
                            } else {
                                0
                            },
                        ),
                    ).padding(
                        start = AppDimensions.Dp16,
                        end = AppDimensions.Dp16,
                        top = AppDimensions.Dp12,
                        bottom = AppDimensions.Dp18,
                    ),
                verticalArrangement = Arrangement.spacedBy(AppDimensions.Dp18),
            ) {
                if (message.isNotBlank()) {
                    BioCard(
                        Modifier.fillMaxWidth(),
                        bp.soft,
                        padding = AppSpacing.VALUE_12,
                        radius = AppRadii.VALUE_14,
                    ) {
                        Bt(message, size = AppTypography.SIZE_12)
                    }
                }
                content()
            }
            footer()
        }
    }
}

@Composable
internal fun bioModalTitle(
    type: String,
    id: String,
    permanent: Boolean,
    full: Boolean,
    accessoryName: () -> String,
): String =
    when (type) {
        "tree-picker" -> stringResource(R.string.copy_cay_tren_trang_chu_76c8b5)
        "seed-picker" -> stringResource(R.string.copy_gieo_mot_mam_moi_2a80ed)
        "purchase" ->
            if (permanent) {
                stringResource(
                    R.string.copy_dung_cu_cham_soc_9f1ca9,
                )
            } else {
                stringResource(R.string.copy_them_vao_tui_a0e7dc)
            }
        "accessory" -> accessoryName()
        "plant" ->
            if (full) {
                stringResource(
                    R.string.copy_chua_co_o_trong_6abd67,
                )
            } else {
                stringResource(R.string.copy_gieo_mot_mam_xanh_776075)
            }
        "water-all" -> stringResource(R.string.copy_tuoi_ca_vuon_20a14a)
        "unlock" -> stringResource(R.string.copy_mo_o_0_01668e, id)
        "fertilizer-help" -> stringResource(R.string.copy_dung_phan_bon_79fd5f)
        "remove" -> stringResource(R.string.copy_don_cay_e3d410)
        "sell" -> stringResource(R.string.copy_ban_cay_5210c7)
        "archive" -> stringResource(R.string.copy_luu_tru_viec_50123f)
        "discard" -> stringResource(R.string.copy_bo_thay_doi_b07820)
        "ticket" -> stringResource(R.string.copy_mua_ve_bao_ve_acdfc5)
        "adjust" -> stringResource(R.string.copy_dieu_chinh_lich_bdb141)
        "permission" -> stringResource(R.string.copy_nhac_viec_tuy_chon_907252)
        "permission-choice" -> stringResource(R.string.copy_cho_phep_thong_bao_6c0308)
        "permission-settings" -> stringResource(R.string.copy_cai_dat_thong_bao_765600)
        "clear" -> stringResource(R.string.copy_xoa_du_lieu_8ce180)
        "clear-final" -> stringResource(R.string.copy_xac_nhan_lan_cuoi_8421a1)
        "recover" -> stringResource(R.string.copy_khoi_phuc_ban_an_toan_9361dd)
        "confirm-import" -> stringResource(R.string.copy_khoi_phuc_du_lieu_feaa4a)
        "license" -> stringResource(R.string.copy_giay_phep_nguon_mo_2ecf92)
        "focus-end" -> stringResource(R.string.copy_ket_thuc_phien_7ea116)
        else -> stringResource(R.string.copy_xac_nhan_1503b0)
    }
