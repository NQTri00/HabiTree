package com.mamxanh.app.feature.settings.presentation.ui.backup

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.tooling.preview.Preview
import com.mamxanh.app.core.components.design.*
import com.mamxanh.core.ui.R

@Composable
fun BackupScreen(
    state: BackupDisplay,
    onAction: (BackupScreenAction) -> Unit,
) {
    BioCard(Modifier.fillMaxWidth()) {
        Bt(stringResource(R.string.copy_sao_luu_cua_ban_ec4a6a), size = AppTypography.SIZE_17, bold = true)
        Bt(stringResource(R.string.copy_luu_tren_thiet_bi_0_kb_c7f8bb, state.sizeKiB), size = AppTypography.SIZE_12, tint = bp.muted)
        Spacer(Modifier.height(AppDimensions.Dp18))
        Row(horizontalArrangement = Arrangement.spacedBy(AppDimensions.Dp12)) {
            BioButton(
                stringResource(R.string.copy_xuat_tep_b5d886),
                { onAction(BackupScreenAction.Export) },
                Modifier.weight(1f),
                "download",
            )
            BioButton(
                stringResource(R.string.copy_khoi_phuc_c652d9),
                { onAction(BackupScreenAction.Import) },
                Modifier.weight(1f),
                "upload",
                true,
            )
        }
        BioDetails(stringResource(R.string.copy_quyen_rieng_tu_cua_ban_sao_f03d3e)) {
            Bt(
                stringResource(R.string.copy_ban_sao_co_the_chua_lich_su_va_ghi_chu_rieng_tu_chi_d59ce2),
                size = AppTypography.SIZE_12,
                tint = bp.muted,
            )
        }
    }
    val safetyStatus = stringResource(R.string.copy_chua_co_ban_an_toan_ban_nay_duoc_tao_truoc_khi_khoi_432d5b)
    BioButton(
        stringResource(R.string.copy_ban_an_toan_gan_nhat_4ca154),
        { onAction(BackupScreenAction.Recover) },
        Modifier.fillMaxWidth().semantics {
            if (!state.hasSafetyCopy) stateDescription = safetyStatus
        },
        secondary = true,
        enabled = state.hasSafetyCopy,
    )
    BioButton(
        stringResource(R.string.copy_xoa_du_lieu_398085),
        { onAction(BackupScreenAction.Clear) },
        Modifier.fillMaxWidth(),
        danger = true,
    )
    BioDetails(stringResource(R.string.copy_ung_dung_rieng_tu_01362e)) {
        Bt(
            stringResource(R.string.copy_khong_can_tai_khoan_du_lieu_duoc_luu_tren_thiet_bi_e30fea),
            size = AppTypography.SIZE_12,
            tint = bp.muted,
        )
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            BioTextAction(stringResource(R.string.copy_giay_phep_nguon_mo_2ecf92), { onAction(BackupScreenAction.License) })
        }
    }
    BioDetails(stringResource(R.string.copy_chan_doan_906791)) {
        Bt(
            stringResource(R.string.copy_xuat_thong_tin_dung_luong_va_so_luong_muc_de_ho_tro_d6fe92),
            size = AppTypography.SIZE_12,
            tint = bp.muted,
        )
        BioButton(stringResource(R.string.copy_xuat_chan_doan_9d86ed), {
            onAction(BackupScreenAction.Diagnostics)
        }, Modifier.fillMaxWidth(), secondary = true)
    }
}

@Preview(showBackground = true, widthDp = 375)
@Composable
private fun BackupScreenPreview() {
    Column(verticalArrangement = Arrangement.spacedBy(AppDimensions.Dp18)) {
        BackupScreen(BackupDisplay(12, true), {})
    }
}
