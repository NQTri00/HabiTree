package com.mamxanh.app.feature.tasks.presentation.ui.engagement

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.mamxanh.app.core.components.*
import com.mamxanh.app.core.components.components.*
import com.mamxanh.app.core.components.design.AppDimensions
import com.mamxanh.app.core.components.design.AppTypography
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey
import com.mamxanh.shared.model.*

@Composable
internal fun SettingsContent(
    state: EngagementUiState,
    onAct: (EngagementAction) -> Unit,
    onPermission: () -> Unit,
    onData: () -> Unit = {},
    onTaskReminders: () -> Unit = {},
) {
    val settings = state.snapshot.settings
    var start by rememberSaveable(settings.quietStart) { mutableStateOf(clockLabel(settings.quietStart)) }
    var end by rememberSaveable(settings.quietEnd) { mutableStateOf(clockLabel(settings.quietEnd)) }
    GardenScreenIntro(AppText.text(TextKey.CopyTheoNhipCuaBan8e2e15))
    Text(
        AppText.text(TextKey.CopyNhacViecTuyChonCoTheTreDoTietKiemPinKhongCf13dd),
        color = Muted,
        fontSize = AppTypography.Sp12,
        lineHeight = AppTypography.Sp18,
    )
    GardenBotanicalFrame(Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(AppDimensions.Dp16)) {
            SettingToggle(AppText.text(TextKey.CopyTamDungMoiNhacViec0fae21), settings.paused, !state.busy) {
                onAct(EngagementAction.Settings(settings.copy(paused = it)))
            }
            SettingToggle(AppText.text(TextKey.CopyNhacKhiCayCanTuoi246e3f), settings.treeReminders, !state.busy) {
                onAct(EngagementAction.Settings(settings.copy(treeReminders = it)))
            }
            SettingToggle(AppText.text(TextKey.CopyGoiYNghiSau25Phut6e7319), settings.breakPrompts, !state.busy) {
                onAct(EngagementAction.Settings(settings.copy(breakPrompts = it)))
            }
            SettingToggle(AppText.text(TextKey.CopyGiamChuyenDongFf3295), settings.reducedMotion, !state.busy) {
                onAct(EngagementAction.Settings(settings.copy(reducedMotion = it)))
            }
        }
    }
    GardenExpandableSection(AppText.text(TextKey.CopyGioYenLang112f45)) {
        Row(horizontalArrangement = Arrangement.spacedBy(AppDimensions.Dp12)) {
            Column(Modifier.weight(1f)) {
                Text(AppText.text(TextKey.CopyBatDauB3cacc), color = Muted, fontSize = AppTypography.Sp12)
                GardenTextField(start, {
                    start =
                        it
                }, AppText.text(TextKey.CopyBatDauHhMmD6f81d))
            }
            Column(Modifier.weight(1f)) {
                Text(AppText.text(TextKey.CopyKetThuc351849), color = Muted, fontSize = AppTypography.Sp12)
                GardenTextField(end, {
                    end =
                        it
                }, AppText.text(TextKey.CopyKetThucHhMm80ed38))
            }
        }
        GardenPaintedButton(
            AppText.text(TextKey.CopyLuuGioYenLang9ba68f),
            {
                onAct(EngagementAction.Settings(settings.copy(quietStart = parseMinute(start)!!, quietEnd = parseMinute(end)!!)))
            },
            Modifier.fillMaxWidth(),
            enabled =
                !state.busy && parseMinute(start) != null && parseMinute(end) != null,
        )
    }
    GardenPaintedButton(AppText.text(TextKey.CopyQuyenThongBao58e97d), onPermission, Modifier.fillMaxWidth(), primary = false)
    GardenNavigationCard(AppText.text(TextKey.CopyNhacRiengTungViecDf60c6), onTaskReminders, art = GardenArt.CALENDAR)
    GardenNavigationCard(AppText.text(TextKey.CopyDuLieuUngDung9843b7), onData, art = GardenArt.CATEGORY)
}

@Composable
private fun SettingToggle(
    label: String,
    value: Boolean,
    enabled: Boolean,
    onChange: (Boolean) -> Unit,
) {
    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AppDimensions.Dp16),
    ) {
        Text(label, Modifier.weight(1f), color = Deep, fontSize = AppTypography.Sp15, lineHeight = AppTypography.Sp21)
        Switch(value, onChange, enabled = enabled)
    }
}

@Preview(
    name = "Settings component · complete",
    widthDp = 360,
    heightDp = 720,
    device = "spec:width=360dp,height=720dp,dpi=160",
)
@Composable
fun EngagementSettingsComponentPreview() {
    MamXanhPreviewTheme {
        ProvideTextStyle(MaterialTheme.typography.bodySmall.copy(fontSize = AppTypography.Sp15, lineHeight = AppTypography.Sp21)) {
            Column(Modifier.fillMaxWidth().padding(AppDimensions.Dp12), verticalArrangement = Arrangement.spacedBy(AppDimensions.Dp16)) {
                SettingsContent(EngagementUiState(), {}, {})
            }
        }
    }
}

@Preview(widthDp = 414, heightDp = 1000, device = "spec:width=414dp,height=1000dp,dpi=160")
@Composable
fun SettingsWebDesignPreview() {
    com.mamxanh.app.core.components.MamXanhPreviewTheme {
        MintBackdrop {
            Column(Modifier.padding(AppDimensions.Dp24), verticalArrangement = Arrangement.spacedBy(AppDimensions.Dp18)) {
                SettingsContent(EngagementUiState(loading = false), {}, {})
            }
        }
    }
}
