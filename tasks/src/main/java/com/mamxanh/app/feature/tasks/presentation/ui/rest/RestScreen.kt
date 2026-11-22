package com.mamxanh.app.feature.tasks.presentation.ui.rest

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.mamxanh.app.core.components.design.*
import com.mamxanh.core.ui.R

@Composable
fun RestScreen(
    state: RestScreenState,
    onSchedule: (String) -> Unit,
    onBuy: () -> Unit,
    onProtect: (String) -> Unit,
    calendar: @Composable () -> Unit,
) {
    var restDate by rememberSaveable { mutableStateOf(state.tomorrow) }
    var protectDate by rememberSaveable { mutableStateOf("") }
    Bt(
        stringResource(R.string.copy_nghi_cung_la_mot_nhip_e4f3b6),
        size = AppTypography.SIZE_27,
        bold = true,
        spacing = AppTypography.TRACKING_MINUS_1,
    )
    BioCard(Modifier.fillMaxWidth()) { calendar() }
    BioDateField(stringResource(R.string.copy_ngay_nghi_sap_toi_a8efda), restDate, { restDate = it }, futureOnly = true)
    Bt(
        stringResource(R.string.copy_toi_da_2_ngay_thang_dat_truoc_ngay_nghi_khong_thuong_8811c2),
        size = AppTypography.SIZE_12,
        tint = bp.muted,
    )
    BioButton(stringResource(R.string.copy_len_lich_nghi_d71876), { onSchedule(restDate) }, Modifier.fillMaxWidth())
    val rest = state.scheduledDays
    if (rest.isNotEmpty()) {
        BioCard(Modifier.fillMaxWidth()) {
            BioFacts(
                rest.map {
                    stringResource(R.string.copy_nghi_f0aa0a) to
                        it
                },
            )
        }
    }
    BioSection(stringResource(R.string.copy_ve_bao_ve_5a852e))
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
        BioTag(stringResource(R.string.copy_0_ve_604049, state.tickets), "award")
        BioButton(stringResource(R.string.copy_mua_250_xu_aa5fe3), onBuy, secondary = true, enabled = state.canBuy)
    }
    BioDateField(stringResource(R.string.copy_ngay_da_bo_lo_9f55ee), protectDate, { protectDate = it })
    BioButton(
        stringResource(R.string.copy_dung_ve_767d96),
        { onProtect(protectDate) },
        Modifier.fillMaxWidth(),
        enabled =
            state.tickets > 0 && protectDate.isNotBlank(),
    )
}

@Preview(showBackground = true, widthDp = 375)
@Composable
private fun RestScreenPreview() {
    Column(verticalArrangement = Arrangement.spacedBy(AppDimensions.Dp18)) {
        RestScreen(RestScreenState("2026-11-18", emptyList(), 0, false), {}, {}, {}, {})
    }
}
