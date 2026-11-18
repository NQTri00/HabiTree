package com.mamxanh.app.feature.tasks.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.mamxanh.app.presentation.ui.design.*
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey

data class RestScreenState(
    val tomorrow: String,
    val scheduledDays: List<String>,
    val tickets: Int,
    val canBuy: Boolean,
)

@Composable
fun RestScreen(
    state: RestScreenState,
    onSchedule: (String) -> Unit,
    onBuy: () -> Unit,
    onProtect: (String) -> Unit,
    calendar: @Composable () -> Unit,
) {
    var restDate by rememberSaveable{mutableStateOf(state.tomorrow)};var protectDate by rememberSaveable{mutableStateOf("")}
    Bt(AppText.text(TextKey.CopyNghiCungLaMotNhipE4f3b6),size=AppTypography.Size27,bold=true,spacing=AppTypography.TrackingMinus1);BioCard(Modifier.fillMaxWidth()){calendar()}
    BioDateField(AppText.text(TextKey.CopyNgayNghiSapToiA8efda),restDate,{restDate=it},futureOnly=true);Bt(AppText.text(TextKey.CopyToiDa2NgayThangDatTruocNgayNghiKhongThuong8811c2),size=AppTypography.Size12,tint=bp.muted)
    BioButton(AppText.text(TextKey.CopyLenLichNghiD71876),{ onSchedule(restDate) },Modifier.fillMaxWidth())
    val rest=state.scheduledDays;if(rest.isNotEmpty())BioCard(Modifier.fillMaxWidth()){BioFacts(rest.map{AppText.text(TextKey.CopyNghiF0aa0a) to it})}
    BioSection(AppText.text(TextKey.CopyVeBaoVe5a852e));Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.SpaceBetween){BioTag(AppText.text(TextKey.Copy0Ve604049,state.tickets),"award");BioButton(AppText.text(TextKey.CopyMua250XuAa5fe3),onBuy,secondary=true,enabled=state.canBuy)}
    BioDateField(AppText.text(TextKey.CopyNgayDaBoLo9f55ee),protectDate,{protectDate=it});BioButton(AppText.text(TextKey.CopyDungVe767d96),{ onProtect(protectDate) },Modifier.fillMaxWidth(),enabled=state.tickets>0&&protectDate.isNotBlank())
}

@Preview(showBackground = true, widthDp = 375)
@Composable
private fun RestScreenPreview() {
    Column(verticalArrangement = Arrangement.spacedBy(AppDimensions.Dp18)) {
        RestScreen(RestScreenState("2026-11-18", emptyList(), 0, false), {}, {}, {}, {})
    }
}
