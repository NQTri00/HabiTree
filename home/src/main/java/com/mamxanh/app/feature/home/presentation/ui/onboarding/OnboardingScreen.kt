package com.mamxanh.app.feature.home.presentation.ui.onboarding

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import com.mamxanh.app.core.components.design.*
import com.mamxanh.app.core.components.design.AppColors
import com.mamxanh.app.core.components.design.AppDimensions
import com.mamxanh.app.core.components.design.AppRadii
import com.mamxanh.app.core.components.design.AppSpacing
import com.mamxanh.app.core.components.design.AppTypography
import com.mamxanh.core.ui.R
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey

@Composable private fun BioRadio(selected: Boolean) {
    Box(
        Modifier.size(AppDimensions.Dp14).border(AppDimensions.Dp1, if (selected)bp.deep else bp.control, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        if (selected)Box(Modifier.size(AppDimensions.Dp7).background(bp.deep, CircleShape))
    }
}

@Composable fun ColumnScope.OnboardingScreen(
    state: OnboardingDisplay,
    gutter: Int,
    onAction: (OnboardingAction) -> Unit,
    templateRow: @Composable (Int) -> Unit,
) {
    val step = state.step
    val footer: @Composable () -> Unit = {
        if (step ==
            3
        ) {
            Bt(
                stringResource(R.string.copy_da_chon_0_5_chon_it_nhat_2_viec_e813b8, state.selectedCount),
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
                    step != 3 || state.selectedCount in 2..5,
            )
        }
    }
    val scroll = rememberScrollState()
    Box(Modifier.weight(1f).fillMaxWidth()) {
        Column(
            Modifier.fillMaxSize().verticalScroll(scroll).padding(
                start = gutter.dp,
                end = gutter.dp,
                top = AppDimensions.Dp20,
                bottom =
                    if (step ==
                        3
                    ) {
                        AppDimensions.Dp0
                    } else {
                        AppDimensions.Dp24
                    },
            ),
            verticalArrangement = Arrangement.spacedBy(AppDimensions.Dp18),
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(AppDimensions.Dp5)) {
                repeat(5) {
                    Box(
                        Modifier.weight(1f).height(AppDimensions.Dp4).background(
                            if (it <=
                                step
                            ) {
                                bp.deep
                            } else {
                                AppColors.SwatchFFD8DFCE
                            },
                        ),
                    )
                }
            }
            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                when (step) {
                    0 -> {
                        BioPlant("succulent", Modifier.fillMaxWidth().height(AppDimensions.Dp220))
                        Bt(
                            stringResource(R.string.copy_mot_viec_nho_mot_mam_xanh_225e73),
                            Modifier.fillMaxWidth(),
                            size = AppTypography.SIZE_29,
                            line = AppTypography.LINE_HEIGHT_37_7,
                            bold = true,
                            align = TextAlign.Center,
                            spacing = AppTypography.TRACKING_MINUS_8,
                        )
                        Spacer(Modifier.height(AppDimensions.Dp12))
                        Bt(
                            stringResource(R.string.copy_chon_cay_chon_viec_lon_len_moi_ngay_b1308a),
                            size = AppTypography.SIZE_12,
                            tint = bp.muted,
                            align = TextAlign.Center,
                        )
                    }
                    1, 2 -> {
                        Bt(
                            if (step ==
                                1
                            ) {
                                stringResource(R.string.copy_hat_dau_tien_8b8f9a)
                            } else {
                                stringResource(R.string.copy_dieu_ban_muon_cham_4bb700)
                            },
                            Modifier.fillMaxWidth(),
                            size = AppTypography.SIZE_29,
                            line = AppTypography.LINE_HEIGHT_37_7,
                            bold = true,
                            align = TextAlign.Center,
                            spacing = AppTypography.TRACKING_MINUS_8,
                        )
                        Spacer(Modifier.height(AppDimensions.Dp12))
                        Bt(
                            if (step ==
                                1
                            ) {
                                stringResource(R.string.copy_mot_hat_mien_phi_giu_trong_tui_f10b37)
                            } else {
                                stringResource(R.string.copy_bat_dau_tu_dieu_quan_trong_b1a63b)
                            },
                            size = AppTypography.SIZE_12,
                            tint = bp.muted,
                            align = TextAlign.Center,
                        )
                        Spacer(Modifier.height(AppDimensions.Dp24))
                        Column(verticalArrangement = Arrangement.spacedBy(AppDimensions.Dp12)) {
                            val choices =
                                if (step ==
                                    1
                                ) {
                                    listOf("succulent", "monstera", "snake")
                                } else {
                                    listOf(
                                        AppText.canonical(TextKey.CopyHocTapA51516),
                                        AppText.canonical(TextKey.CopySucKhoe071df7),
                                        AppText.canonical(TextKey.CopyNepSong8d1a98),
                                        AppText.canonical(TextKey.CopyViecNha126ef3),
                                    )
                                }
                            choices.forEach { id ->
                                val selected = if (step == 1)state.seed == id else state.category == id
                                BioCard(
                                    Modifier.fillMaxWidth().semantics {
                                        this.selected = selected
                                        role = Role.RadioButton
                                    },
                                    if (selected)bp.soft else bp.surface,
                                    padding = AppSpacing.VALUE_12,
                                    radius = AppRadii.VALUE_16,
                                    onClick = {
                                        onAction(OnboardingAction.Choose(step, id))
                                    },
                                    borderColor = if (selected)bp.deep else bp.line,
                                ) {
                                    Row(
                                        Modifier.fillMaxWidth().heightIn(min = AppDimensions.Dp38),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(AppDimensions.Dp12),
                                    ) {
                                        if (step == 1)BioPlant(id, Modifier.size(AppDimensions.Dp54))
                                        Bt(
                                            if (step ==
                                                1
                                            ) {
                                                state.seedNames[id].orEmpty()
                                            } else {
                                                AppText.category(id)
                                            },
                                            Modifier.weight(1f),
                                            size = AppTypography.SIZE_12,
                                            semi = true,
                                        )
                                        BioRadio(selected)
                                    }
                                }
                            }
                        }
                    }
                    3 -> {
                        Bt(
                            stringResource(R.string.copy_chon_2_5_viec_e8b193),
                            Modifier.fillMaxWidth(),
                            size = AppTypography.SIZE_29,
                            line = AppTypography.LINE_HEIGHT_37_7,
                            bold = true,
                            align = TextAlign.Center,
                        )
                        Spacer(Modifier.height(AppDimensions.Dp12))
                        Bt(
                            stringResource(R.string.copy_vua_suc_thoi_co_the_sua_sau_c7359e),
                            size = AppTypography.SIZE_12,
                            tint = bp.muted,
                            align = TextAlign.Center,
                        )
                        Spacer(Modifier.height(AppDimensions.Dp24))
                        Column(verticalArrangement = Arrangement.spacedBy(AppDimensions.Dp12)) {
                            repeat(state.templateCount) { templateRow(it) }
                        }
                    }
                    else -> {
                        BioPlant(state.seed, Modifier.fillMaxWidth().height(AppDimensions.Dp150))
                        Bt(
                            stringResource(R.string.copy_san_sang_nay_mam_8ad6e8),
                            Modifier.fillMaxWidth(),
                            size = AppTypography.SIZE_29,
                            line = AppTypography.LINE_HEIGHT_37_7,
                            bold = true,
                            align = TextAlign.Center,
                        )
                        Spacer(Modifier.height(AppDimensions.Dp12))
                        Bt(
                            stringResource(
                                R.string.copy_0_1_viec_2_189a78,
                                state.seedNames[state.seed].orEmpty(),
                                state.selectedCount,
                                AppText.category(state.category),
                            ),
                            Modifier.fillMaxWidth(),
                            size = AppTypography.SIZE_12,
                            tint = bp.muted,
                            align = TextAlign.Center,
                        )
                        Spacer(Modifier.height(AppDimensions.Dp18))
                        state.selectedTitles.forEach {
                            Row(Modifier.fillMaxWidth().padding(vertical = AppDimensions.Dp11)) {
                                Bt(
                                    it + " · " + stringResource(R.string.copy_moi_ngay_8270cb),
                                    size = AppTypography.SIZE_12,
                                )
                            }
                            ;HorizontalDivider(color = bp.line)
                        }
                        BioToggle(
                            stringResource(R.string.copy_nhac_nhe_luc_09_00_cda6e7),
                            state.remind,
                        ) { onAction(OnboardingAction.Remind(it)) }
                        Bt(
                            stringResource(R.string.copy_khong_can_tai_khoan_cap_quyen_trong_tuy_chon_de_nhan_cc9847),
                            Modifier.fillMaxWidth(),
                            size = AppTypography.SIZE_12,
                            tint = bp.muted,
                            align = TextAlign.Center,
                        )
                    }
                }
            }
            if (step ==
                3
            ) {
                Column {
                    HorizontalDivider(color = bp.line)
                    Spacer(Modifier.height(AppDimensions.Dp13))
                    footer()
                    Spacer(Modifier.height(AppDimensions.Dp12))
                }
            } else {
                footer()
            }
            BioTextAction(
                stringResource(R.string.copy_bo_qua_c67ba2),
                { onAction(OnboardingAction.Skip) },
                modifier = Modifier.fillMaxWidth(),
            )
        }
        if (step == 3 &&
            scroll.canScrollForward
        ) {
            Column(
                Modifier
                    .align(
                        Alignment.BottomCenter,
                    ).fillMaxWidth()
                    .background(bp.bg)
                    .padding(horizontal = gutter.dp, vertical = AppDimensions.Dp12),
            ) {
                HorizontalDivider(color = bp.line)
                Spacer(Modifier.height(AppDimensions.Dp12))
                footer()
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 375, heightDp = 812)
@Composable
private fun OnboardingScreenPreview() {
    Column(Modifier.fillMaxSize()) {
        OnboardingScreen(OnboardingDisplay(0, "succulent", "", 0, emptyList(), emptyMap(), 0, false), 20, {}, {})
    }
}
