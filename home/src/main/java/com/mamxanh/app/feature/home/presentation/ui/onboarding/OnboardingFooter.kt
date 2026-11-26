package com.mamxanh.app.feature.home.presentation.ui.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.mamxanh.app.core.components.design.*
import com.mamxanh.core.ui.R

@Composable
internal fun OnboardingFooter(
    step: Int,
    selectedCount: Int,
    onAction: (OnboardingAction) -> Unit,
) {
    if (step ==
        3
    ) {
        Bt(
            stringResource(R.string.copy_da_chon_0_5_chon_it_nhat_2_viec_e813b8, selectedCount),
            Modifier.fillMaxWidth().padding(bottom = AppDimensions.Dp8),
            size = AppTypography.SIZE_12,
            tint = bp.muted,
        )
    }
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(AppDimensions.Dp10)) {
        BioButton(
            stringResource(R.string.copy_quay_lai_6d7935),
            { onAction(OnboardingAction.Back) },
            Modifier.weight(1f),
            secondary = true,
            enabled = step > 0,
        )
        BioButton(
            if (step ==
                4
            ) {
                stringResource(R.string.copy_vao_vuon_b6df0e)
            } else {
                stringResource(R.string.copy_tiep_tuc_87a7ae)
            },
            { onAction(OnboardingAction.Next) },
            Modifier.weight(1f),
            enabled =
                step != 3 || selectedCount in 2..5,
        )
    }
}

@Preview(name = "Select tasks", showBackground = true, widthDp = 375)
@Composable
private fun OnboardingFooterSelectionPreview() {
    Column { OnboardingFooter(step = 3, selectedCount = 1, onAction = {}) }
}

@Preview(name = "Enter garden", showBackground = true, widthDp = 375)
@Composable
private fun OnboardingFooterReadyPreview() {
    Column { OnboardingFooter(step = 4, selectedCount = 2, onAction = {}) }
}
