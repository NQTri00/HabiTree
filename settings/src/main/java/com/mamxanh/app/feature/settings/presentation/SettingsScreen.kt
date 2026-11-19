package com.mamxanh.app.feature.settings.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.mamxanh.app.presentation.ui.design.*
import com.mamxanh.core.ui.R
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey

data class SettingsDisplay(val options: Map<String, Boolean>, val quietStart: String, val quietEnd: String)

enum class SettingsLink { Permission, Insights, Data, Onboarding }

@Composable
fun SettingsScreen(
    state: SettingsDisplay,
    onLanguage: (String) -> Unit,
    onToggle: (String, Boolean) -> Unit,
    onSave: (String, String) -> Unit,
    onLink: (SettingsLink) -> Unit,
) {
    val languageTitle=stringResource(R.string.app_language_title)
    val languageDescription=stringResource(R.string.app_language_description)
    val vietnamese=stringResource(R.string.app_language_vi)
    val english=stringResource(R.string.app_language_en)
    var start by rememberSaveable { mutableStateOf(state.quietStart) }
    var end by rememberSaveable { mutableStateOf(state.quietEnd) }
    Bt(AppText.text(TextKey.CopyTheoNhipCuaBan8e2e15),size=AppTypography.Size27,bold=true,spacing=AppTypography.TrackingMinus1);Bt(AppText.text(TextKey.CopyNhacViecTuyChonCoTheTreDoTietKiemPinKhongCf13dd),size=AppTypography.Size12,tint=bp.muted)
    BioCard(Modifier.fillMaxWidth()){
        BioSelect(languageTitle,listOf("vi" to vietnamese,"en" to english), AppText.language.tag, onSelect = onLanguage)
        Spacer(Modifier.height(AppDimensions.Dp8));Bt(languageDescription,size=AppTypography.Size12,tint=bp.muted)
    }
    BioCard(Modifier.fillMaxWidth()){
        listOf("paused" to AppText.text(TextKey.CopyTamDungMoiNhacViec0fae21),"treeReminders" to AppText.text(TextKey.CopyNhacKhiCayCanTuoi246e3f),"breakPrompts" to AppText.text(TextKey.CopyGoiYNghiSau25Phut6e7319),"reducedMotion" to AppText.text(TextKey.CopyGiamChuyenDongFf3295)).forEachIndexed{i,(key,label)->if(i>0)HorizontalDivider(color=bp.line);BioToggle(label,state.options.getValue(key)){onToggle(key, it)}}
    }
    BioDetails(AppText.text(TextKey.CopyGioYenLang112f45)){Row(horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp12)){BioTimeField(AppText.text(TextKey.CopyBatDauB3cacc),start,{start=it},Modifier.weight(1f));BioTimeField(AppText.text(TextKey.CopyKetThuc351849),end,{end=it},Modifier.weight(1f))}}
    BioButton(stringResource(R.string.copy_luu_tuy_chon_3847ca),{ onSave(start, end) },Modifier.fillMaxWidth())
    BioButton(AppText.text(TextKey.CopyQuyenThongBao58e97d),{ onLink(SettingsLink.Permission) },Modifier.fillMaxWidth(),"bell",true)
    BioLink(AppText.text(TextKey.CopyNhacRiengTungViecDf60c6),"clock",{ onLink(SettingsLink.Insights) });BioLink(AppText.text(TextKey.CopyDuLieuUngDung9843b7),"download",{ onLink(SettingsLink.Data) });BioLink(stringResource(R.string.copy_xem_lai_bat_dau_b87622),"leaf",{ onLink(SettingsLink.Onboarding) })
}

@Preview(showBackground = true, widthDp = 375)
@Composable
private fun SettingsScreenPreview() {
    Column(verticalArrangement = Arrangement.spacedBy(AppDimensions.Dp18)) {
        SettingsScreen(
            SettingsDisplay(mapOf("paused" to false, "treeReminders" to true, "breakPrompts" to false, "reducedMotion" to false), "22:00", "07:00"),
            {}, { _, _ -> }, { _, _ -> }, {},
        )
    }
}
