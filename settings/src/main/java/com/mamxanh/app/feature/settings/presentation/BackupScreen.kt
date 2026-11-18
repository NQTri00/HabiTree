package com.mamxanh.app.feature.settings.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.mamxanh.app.presentation.ui.design.*
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey

data class BackupDisplay(val sizeKiB: Int, val hasSafetyCopy: Boolean)

enum class BackupScreenAction { Export, Import, Recover, Clear, License, Diagnostics }

@Composable
fun BackupScreen(state: BackupDisplay, onAction: (BackupScreenAction) -> Unit) {
    BioCard(Modifier.fillMaxWidth()){
        Bt(AppText.text(TextKey.CopySaoLuuCuaBanEc4a6a),size=AppTypography.Size17,bold=true);Bt(AppText.text(TextKey.CopyLuuTrenThietBi0KbC7f8bb,state.sizeKiB),size=AppTypography.Size12,tint=bp.muted);Spacer(Modifier.height(AppDimensions.Dp18));Row(horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp12)){BioButton(AppText.text(TextKey.CopyXuatTepB5d886),{ onAction(BackupScreenAction.Export) },Modifier.weight(1f),"download");BioButton(AppText.text(TextKey.CopyKhoiPhucC652d9),{ onAction(BackupScreenAction.Import) },Modifier.weight(1f),"upload",true)}
        Spacer(Modifier.height(AppDimensions.Dp16));BioDetails(AppText.text(TextKey.CopyQuyenRiengTuCuaBanSaoF03d3e)){Bt(AppText.text(TextKey.CopyBanSaoCoTheChuaLichSuVaGhiChuRiengTuChiD59ce2),size=AppTypography.Size12,tint=bp.muted)}
    }
    Column(verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp6)){BioButton(AppText.text(TextKey.CopyBanAnToanGanNhat4ca154),{ onAction(BackupScreenAction.Recover) },Modifier.fillMaxWidth(),secondary=true,enabled=state.hasSafetyCopy);if(!state.hasSafetyCopy)Bt(AppText.text(TextKey.CopyChuaCoBanAnToanBanNayDuocTaoTruocKhiKhoi432d5b),size=AppTypography.Size12,tint=bp.muted)}
    BioButton(AppText.text(TextKey.CopyXoaDuLieu398085),{ onAction(BackupScreenAction.Clear) },Modifier.fillMaxWidth(),danger=true)
    BioDetails(AppText.text(TextKey.CopyUngDungRiengTu01362e)){Bt(AppText.text(TextKey.CopyKhongCanTaiKhoanDuLieuDuocLuuTrenThietBiE30fea),size=AppTypography.Size12,tint=bp.muted);BioTextAction(AppText.text(TextKey.CopyGiayPhepNguonMo2ecf92),{ onAction(BackupScreenAction.License) })}
    BioDetails(AppText.text(TextKey.CopyChanDoan906791)){Bt(AppText.text(TextKey.CopyXuatThongTinDungLuongVaSoLuongMucDeHoTroD6fe92),size=AppTypography.Size12,tint=bp.muted);BioButton(AppText.text(TextKey.CopyXuatChanDoan9d86ed),{ onAction(BackupScreenAction.Diagnostics) },Modifier.fillMaxWidth(),secondary=true)}
}

@Preview(showBackground = true, widthDp = 375)
@Composable
private fun BackupScreenPreview() {
    Column(verticalArrangement = Arrangement.spacedBy(AppDimensions.Dp18)) {
        BackupScreen(BackupDisplay(12, true), {})
    }
}
