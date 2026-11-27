package com.mamxanh.app.feature.tasks.presentation.ui.templates

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.*
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.tooling.preview.Preview
import com.mamxanh.app.core.components.design.*
import com.mamxanh.core.ui.R
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey

@Composable fun TemplateRow(
    t: TaskTemplateDisplay,
    selected: Boolean,
    onboarding: Boolean = false,
    onChange: () -> Unit,
) {
    BioCard(
        Modifier.fillMaxWidth().semantics {
            toggleableState = if (selected)ToggleableState.On else ToggleableState.Off
            role = Role.Checkbox
        },
        padding = if (onboarding)AppSpacing.VALUE_12 else AppSpacing.VALUE_14,
        radius = AppRadii.VALUE_18,
        tint = if (selected)bp.soft else bp.surface,
        onClick = onChange,
        borderColor = if (selected)bp.deep else bp.line,
    ) {
        Row(
            Modifier.fillMaxWidth().heightIn(min = if (onboarding)AppDimensions.Dp65 else AppDimensions.Dp60),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppDimensions.Dp10),
        ) {
            if (!onboarding) {
                Box(
                    Modifier.width(AppDimensions.Dp20).height(AppDimensions.Dp28).background(bp.soft, bioShape(AppRadii.VALUE_8)),
                    contentAlignment = Alignment.Center,
                ) {
                    Bi(if (t.timer)"clock"else "leaf", size = AppIcons.SIZE_20)
                }
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(AppDimensions.Dp4)) {
                Bt(t.title, size = AppTypography.SIZE_13, semi = true)
                val reward = stringResource(R.string.copy_0_xu_9c90c1, t.reward)
                val cadence =
                    if (t.weekends)stringResource(R.string.copy_cuoi_tuan_a0fb27)else stringResource(R.string.copy_moi_ngay_8270cb)
                Bt(
                    AppText.category(t.category) + " · " +
                        (
                            if (t.timer) {
                                stringResource(R.string.copy_bo_dem_0_phut_a99be5, t.minutes)
                            } else if (onboarding) {
                                AppText.text(TextKey.TasksOnboardingConfirmEffort, t.effort)
                            } else {
                                AppText.text(TextKey.CopyChamF4623f) +
                                    t.effort
                            }
                        ) + if (onboarding) reward else "",
                    size = if (onboarding) AppTypography.SIZE_11 else AppTypography.SIZE_12,
                    tint = bp.muted,
                )
                val cadenceDetails = if (t.timeWindow.isBlank()) cadence else "$cadence \u00b7 ${t.timeWindow}"
                Bt(
                    if (onboarding) cadenceDetails else cadence + reward,
                    size = if (onboarding) AppTypography.SIZE_11 else AppTypography.SIZE_12,
                    tint = bp.muted,
                )
            }
            Box(
                Modifier
                    .size(
                        if (onboarding) AppDimensions.Dp14 else AppDimensions.Dp20,
                    ).background(
                        if (selected)bp.deep else AppColors.Transparent,
                        RoundedCornerShape(AppDimensions.Dp3),
                    ).border(AppDimensions.Dp1, if (selected)bp.deep else bp.control, RoundedCornerShape(AppDimensions.Dp3)),
                contentAlignment = Alignment.Center,
            ) {
                if (selected)Bi("check", size = if (onboarding) AppIcons.SIZE_12 else AppIcons.SIZE_14, tint = AppColors.White)
            }
        }
    }
}

@Composable
fun TaskTemplatesScreen(
    templates: List<TaskTemplateDisplay>,
    chosen: Set<String>,
    onToggle: (String) -> Unit,
) {
    Column {
        Bt(
            stringResource(R.string.copy_chon_viec_vua_suc_895fff),
            size = AppTypography.SIZE_27,
            line = AppTypography.LINE_HEIGHT_35_1,
            bold = true,
            spacing = AppTypography.TRACKING_MINUS_1,
        )
        Bt(stringResource(R.string.copy_them_mot_lan_chinh_sua_bat_cu_luc_nao_55b241), size = AppTypography.SIZE_12, tint = bp.muted)
    }
    Column(verticalArrangement = Arrangement.spacedBy(AppDimensions.Dp12)) {
        templates.forEach { template ->
            TemplateRow(
                template,
                template.id in chosen,
            ) { onToggle(template.id) }
        }
    }
}

@Preview(showBackground = true, widthDp = 375)
@Composable
private fun TaskTemplatesScreenPreview() {
    Column(verticalArrangement = Arrangement.spacedBy(AppDimensions.Dp18)) {
        TaskTemplatesScreen(
            listOf(TaskTemplateDisplay("reading", "Read ten pages", "Study", 0, "Easy", false, false, 8)),
            setOf("reading"),
            {},
        )
    }
}
