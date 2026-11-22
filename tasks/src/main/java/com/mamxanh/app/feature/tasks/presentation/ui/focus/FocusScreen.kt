package com.mamxanh.app.feature.tasks.presentation.ui.focus

import com.mamxanh.core.ui.R
import androidx.compose.ui.res.stringResource
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.mamxanh.app.core.components.design.*

private object FocusLayout {
    val CompactTimerDiameter = AppDimensions.Dp140
    val NarrowTimerDiameter = AppDimensions.Dp200
    val TimerDiameter = AppDimensions.Dp240
    val SideBySideMinimumWidth = AppDimensions.Dp600
    val TimerToSetupGap = AppDimensions.Dp20
    val SetupFieldGap = AppDimensions.Dp10
    val PortraitSetupGap = AppDimensions.Dp15
    val PresetToDurationGap = AppDimensions.Dp12
    val RingStroke = AppDimensions.Dp8
    const val TIMER_WEIGHT = 1f
    const val SETUP_WEIGHT = 2f
    const val PRESETS_WEIGHT = 1.6f
    const val CUSTOM_DURATION_WEIGHT = 1f
}

@Composable
fun ColumnScope.FocusScreen(
    state: FocusDisplay,
    onTask: (String) -> Unit,
    onMinutes: (String) -> Unit,
    actions: (@Composable () -> Unit)? = null,
) {
    val present = state.present
    val minutes = state.minutes
    val configuration = LocalConfiguration.current
    val landscape = configuration.screenHeightDp < AppThemeEffects.CompactHeight
    val portraitSetup = !present && !landscape
    val ringSize = when {
        landscape -> FocusLayout.CompactTimerDiameter
        configuration.screenWidthDp < AppThemeEffects.CompactWidth -> FocusLayout.NarrowTimerDiameter
        else -> FocusLayout.TimerDiameter
    }
    if (present) {
        if (!landscape) {
            Bt(
                stringResource(R.string.copy_khoang_thoi_gian_cua_ban_a0e014),
                size = AppTypography.Size10,
                mono = true,
                tint = bp.muted,
                spacing = AppTypography.Tracking1_8,
            )
        }
        Bt(
            state.title,
            Modifier.fillMaxWidth(),
            size = if (landscape) AppTypography.Size22 else AppTypography.Size27,
            bold = true,
            align = TextAlign.Center,
        )
    } else if (landscape) {
        Bt(stringResource(R.string.copy_tap_trung_964002), size = AppTypography.Size22, bold = true)
    } else {
        BioIntro(
            stringResource(R.string.copy_tap_trung_964002),
            stringResource(R.string.copy_chi_mot_viec_thoi_83ccd7),
            stringResource(R.string.copy_danh_mot_khoang_cho_dieu_quan_trong_dfd9b6),
            { Bi("sun") },
        )
    }
    val remaining = if (present) state.remaining else (minutes.toIntOrNull() ?: 0).coerceIn(0, 240) * 60
    if (!present && landscape && configuration.screenWidthDp >= FocusLayout.SideBySideMinimumWidth.value) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(FocusLayout.TimerToSetupGap),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(Modifier.weight(FocusLayout.TIMER_WEIGHT), contentAlignment = Alignment.Center) {
                FocusTimer(state, remaining, ringSize, landscape)
            }
            Column(
                Modifier.weight(FocusLayout.SETUP_WEIGHT),
                verticalArrangement = Arrangement.spacedBy(FocusLayout.SetupFieldGap),
            ) {
                FocusSetupFields(state, onTask, onMinutes, wide = true)
            }
        }
    } else {
        FocusTimer(state, remaining, ringSize, landscape, Modifier.align(Alignment.CenterHorizontally))
        if (portraitSetup) {
            Column(
                Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(FocusLayout.PortraitSetupGap),
            ) {
                FocusSetupFields(state, onTask, onMinutes)
                actions?.invoke()
            }
        } else if (!present) {
            FocusSetupFields(state, onTask, onMinutes)
        }
    }
    if (!portraitSetup) actions?.invoke()
    if (present) {
        BioDetails(stringResource(R.string.copy_phien_dang_luu_d6c8c9)) {
            Bt(
                stringResource(R.string.copy_roi_trang_se_tam_dung_phan_da_lam_luon_duoc_giu_lai_c6be38, state.date),
                size = AppTypography.Size12,
                tint = bp.muted,
            )
        }
    }
    BioDetails(stringResource(R.string.copy_cac_phien_da_luu_0_hoan_thanh_bb1ef4, state.history.size)) {
        if (state.history.isEmpty()) {
            Bt(stringResource(R.string.copy_chua_co_phien_hoan_thanh_fd9386), size = AppTypography.Size12, tint = bp.muted)
        }
        state.history.forEach { Bt(it, size = AppTypography.Size13) }
    }
}


@Composable
private fun FocusTimer(
    state: FocusDisplay,
    remaining: Int,
    diameter: Dp,
    landscape: Boolean,
    modifier: Modifier = Modifier,
) {
    val ringTint = bp.soft
    Box(
        modifier.size(diameter).drawBehind {
            drawCircle(
                ringTint,
                radius = size.minDimension / 2 - FocusLayout.RingStroke.toPx() / 2,
                style = Stroke(FocusLayout.RingStroke.toPx()),
            )
        },
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Bt(
                "${remaining / 60}:${(remaining % 60).toString().padStart(2, '0')}",
                size = if (landscape) AppTypography.FocusTimerCompactSize else AppTypography.FocusTimerSize,
                line = if (landscape) AppTypography.FocusTimerCompactLineHeight else AppTypography.FocusTimerLineHeight,
                mono = true,
                spacing = AppTypography.TrackingMinus2,
            )
            if (!landscape) {
                val status = when {
                    !state.present -> R.string.copy_bat_dau_khi_ban_san_sang_3d0a9a
                    state.running -> R.string.copy_dang_tap_trung_1c969b
                    state.onBreak -> R.string.copy_nghi_mot_chut_f912b7
                    else -> R.string.copy_da_tam_dung_a919ce
                }
                Bt(stringResource(status), size = AppTypography.Size12, tint = bp.muted)
            }
        }
    }
}

@Composable
private fun FocusSetupFields(
    state: FocusDisplay,
    onTask: (String) -> Unit,
    onMinutes: (String) -> Unit,
    wide: Boolean = false,
) {
    val task = state.task
    val minutes = state.minutes
    if (task == "0") {
        val presets = listOf(15, 25, 45, 60).map {
            it.toString() to stringResource(R.string.copy_0_phut_2596d2, it)
        }
        if (wide) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(FocusLayout.PresetToDurationGap),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(Modifier.weight(FocusLayout.PRESETS_WEIGHT)) { BioChips(presets, minutes, onMinutes) }
                FocusDurationField(minutes, onMinutes, Modifier.weight(FocusLayout.CUSTOM_DURATION_WEIGHT))
            }
        } else {
            BioChips(presets, minutes, onMinutes)
            FocusDurationField(minutes, onMinutes)
        }
    }
    val choices = listOf("0" to stringResource(R.string.copy_tu_do_khong_thuong_xu_113e54)) +
        state.eligibleTasks.map { it.id to stringResource(R.string.copy_0_1_phut_ce85cf, it.title, it.minutes) }
    BioSelect(stringResource(R.string.copy_danh_cho_ff565f), choices, task, onSelect = onTask)
    if (task != "0") {
        BioCard(Modifier.fillMaxWidth(), bp.soft, padding = AppSpacing.Value12) {
            Bt(
                stringResource(R.string.copy_0_phut_theo_thoi_gian_cua_viec_e07f34, minutes),
                size = AppTypography.Size13,
                semi = true,
            )
            Bt(
                stringResource(R.string.copy_sua_thoi_gian_trong_chi_tiet_viec_940772),
                size = AppTypography.Size12,
                tint = bp.muted,
            )
        }
    }
}

@Composable
private fun FocusDurationField(minutes: String, onMinutes: (String) -> Unit, modifier: Modifier = Modifier) {
    val invalid = (minutes.toIntOrNull() ?: 0) !in 5..120
    BioField(
        stringResource(R.string.copy_phut_tap_trung_5_120_ca7d24),
        minutes,
        onMinutes,
        modifier,
        number = true,
        error = if (invalid) stringResource(R.string.copy_nhap_so_phut_tu_5_120_2b514d) else "",
    )
}

@Preview(name = "Focus setup", showBackground = true, widthDp = 375)
@Preview(name = "Focus setup in landscape", showBackground = true, widthDp = 844, heightDp = 390)
@Composable
private fun FocusScreenPreview() {
    Column(verticalArrangement = Arrangement.spacedBy(AppDimensions.Dp18)) {
        FocusScreen(FocusDisplay("", false, false, false, 1500, "", "25", "0", emptyList(), emptyList()), {}, {})
    }
}

@Preview(name = "Invalid duration", showBackground = true, widthDp = 375)
@Composable
private fun FocusDurationFieldPreview() {
    FocusDurationField(minutes = "121", onMinutes = {})
}
