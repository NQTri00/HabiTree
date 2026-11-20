package com.mamxanh.app.feature.settings.presentation.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.mamxanh.app.core.components.design.*
import com.mamxanh.core.ui.R
import com.mamxanh.shared.localization.AppText

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
    Bt(stringResource(R.string.copy_theo_nhip_cua_ban_8e2e15),size=AppTypography.Size27,bold=true,spacing=AppTypography.TrackingMinus1);Bt(stringResource(R.string.copy_nhac_viec_tuy_chon_co_the_tre_do_tiet_kiem_pin_khong_cf13dd),size=AppTypography.Size12,tint=bp.muted)
    BioCard(Modifier.fillMaxWidth()){
        BioSelect(languageTitle,listOf("vi" to vietnamese,"en" to english), AppText.language.tag, onSelect = onLanguage)
        Spacer(Modifier.height(AppDimensions.Dp8));Bt(languageDescription,size=AppTypography.Size12,tint=bp.muted)
    }
    BioCard(Modifier.fillMaxWidth()){
        listOf("paused" to stringResource(R.string.copy_tam_dung_moi_nhac_viec_0fae21),"treeReminders" to stringResource(R.string.copy_nhac_khi_cay_can_tuoi_246e3f),"breakPrompts" to stringResource(R.string.copy_goi_y_nghi_sau_25_phut_6e7319),"reducedMotion" to stringResource(R.string.copy_giam_chuyen_dong_ff3295)).forEachIndexed{i,(key,label)->if(i>0)HorizontalDivider(color=bp.line);BioToggle(label,state.options.getValue(key)){onToggle(key, it)}}
    }
    BioDetails(stringResource(R.string.copy_gio_yen_lang_112f45)){Row(horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp12)){BioTimeField(stringResource(R.string.copy_bat_dau_b3cacc),start,{start=it},Modifier.weight(1f));BioTimeField(stringResource(R.string.copy_ket_thuc_351849),end,{end=it},Modifier.weight(1f))}}
    BioButton(stringResource(R.string.copy_luu_tuy_chon_3847ca),{ onSave(start, end) },Modifier.fillMaxWidth())
    BioButton(stringResource(R.string.copy_quyen_thong_bao_58e97d),{ onLink(SettingsLink.Permission) },Modifier.fillMaxWidth(),"bell",true)
    BioLink(stringResource(R.string.copy_nhac_rieng_tung_viec_df60c6),"clock",{ onLink(SettingsLink.Insights) });BioLink(stringResource(R.string.copy_du_lieu_ung_dung_9843b7),"download",{ onLink(SettingsLink.Data) });BioLink(stringResource(R.string.copy_xem_lai_bat_dau_b87622),"leaf",{ onLink(SettingsLink.Onboarding) })
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
