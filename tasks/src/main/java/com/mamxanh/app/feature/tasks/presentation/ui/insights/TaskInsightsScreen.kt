package com.mamxanh.app.feature.tasks.presentation.ui.insights

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.mamxanh.app.core.components.design.*
import com.mamxanh.core.ui.R

@Composable
fun TaskInsightsScreen(
    task: TaskInsightsDisplay?,
    tasks: List<InsightTaskLink>,
    period: String,
    onPeriod: (String) -> Unit,
    onAddTask: () -> Unit,
    onTask: (String) -> Unit,
    onAdjust: () -> Unit,
    onSaveReminder: (String, String, Boolean) -> Unit,
    onSettings: () -> Unit,
    calendar: @Composable () -> Unit,
) {
    if (task ==
        null
    ) {
        Bt(
            stringResource(R.string.copy_nhip_thoi_quen_1b8324),
            size = AppTypography.SIZE_27,
            bold = true,
            spacing = AppTypography.TRACKING_MINUS_1,
        )
        if (tasks.isEmpty()) {
            BioEmpty(
                stringResource(R.string.copy_chua_co_nhip_thoi_quen_c06c83),
                stringResource(R.string.copy_them_viec_de_bat_dau_e6548a),
                "chart",
                stringResource(R.string.copy_them_viec_c371c2),
                onAddTask,
            )
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(AppDimensions.Dp12)) {
                tasks.forEach { BioLink(it.title, "chart", { onTask(it.id) }) }
            }
        }
        return
    }
    Bt(task.title, size = AppTypography.SIZE_27, bold = true, spacing = AppTypography.TRACKING_MINUS_1)
    BioTabs(
        listOf(
            "7" to stringResource(R.string.copy_7_ngay_fc11cc),
            "30" to stringResource(R.string.copy_30_ngay_b37f06),
        ),
        period,
        onPeriod,
    )
    BioCard(Modifier.fillMaxWidth()) { calendar() }
    BioMetrics(
        listOf(
            stringResource(R.string.copy_chuoi_hien_tai_460755) to task.streak.toString(),
            stringResource(R.string.copy_dai_nhat_67c21e) to task.longest.toString(),
            stringResource(R.string.copy_ty_le_30_ngay_84d03d) to if (task.rate != null)"${task.rate}%"else "—",
        ),
    )
    BioDetails(stringResource(R.string.copy_tap_trung_goi_y_2462a6)) {
        BioFacts(
            listOf(
                stringResource(R.string.copy_tap_trung_964002) to stringResource(R.string.copy_0_phut_2596d2, task.focusMinutes),
                stringResource(R.string.copy_gio_thuong_xong_450100) to task.commonHour,
            ),
        )
        ;if (task.suggestion.isNotBlank())BioButton(task.suggestion, onAdjust, secondary = true)
    }
    var minute by rememberSaveable(task.id) { mutableStateOf(task.minute) }
    var mode by rememberSaveable(task.id) { mutableStateOf(task.mode) }
    var follow by rememberSaveable(task.id) { mutableStateOf(task.followUp) }
    Bt(stringResource(R.string.copy_nhac_viec_9e9055), size = AppTypography.SIZE_17, bold = true)
    BioTimeField(stringResource(R.string.copy_gio_nhac_d9dac9), minute, {
        minute =
            it
    })
    BioSelect(
        stringResource(R.string.copy_khi_nao_b5af31),
        listOf(
            "OFF" to stringResource(R.string.copy_tat_328606),
            "AT" to stringResource(R.string.copy_theo_gio_4bd1ce),
            "BEFORE" to stringResource(R.string.copy_truoc_khung_gio_c97c51),
        ),
        mode,
        onSelect = {
            mode =
                it
        },
    )
    BioToggle(stringResource(R.string.copy_nhac_nhe_them_mot_lan_544e8e), follow) { follow = it }
    BioButton(
        stringResource(R.string.copy_luu_nhac_viec_de3aa9),
        { onSaveReminder(minute, mode, follow) },
        Modifier.fillMaxWidth(),
    )
    BioLink(stringResource(R.string.copy_quyen_nhac_toan_cuc_8225b7), "bell", onSettings)
}

@Preview(showBackground = true, widthDp = 375)
@Composable
private fun TaskInsightsScreenPreview() {
    Column(verticalArrangement = Arrangement.spacedBy(AppDimensions.Dp18)) {
        TaskInsightsScreen(null, emptyList(), "7", {}, {}, {}, {}, { _, _, _ -> }, {}, {})
    }
}
