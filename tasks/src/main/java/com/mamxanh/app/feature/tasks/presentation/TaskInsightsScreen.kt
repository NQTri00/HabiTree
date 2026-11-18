package com.mamxanh.app.feature.tasks.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.mamxanh.app.presentation.ui.design.*
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey

data class InsightTaskLink(val id: String, val title: String)

data class TaskInsightsDisplay(
    val id: String,
    val title: String,
    val streak: Int,
    val longest: Int,
    val rate: Int?,
    val focusMinutes: Int,
    val commonHour: String,
    val suggestion: String,
    val minute: String,
    val mode: String,
    val followUp: Boolean,
)

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
    if(task==null){Bt(AppText.text(TextKey.CopyNhipThoiQuen1b8324),size=AppTypography.Size27,bold=true,spacing=AppTypography.TrackingMinus1);if(tasks.isEmpty())BioEmpty(AppText.text(TextKey.CopyChuaCoNhipThoiQuenC06c83),AppText.text(TextKey.CopyThemViecDeBatDauE6548a),"chart",AppText.text(TextKey.CopyThemViecC371c2),onAddTask)else Column(verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp12)){tasks.forEach{BioLink(it.title,"chart",{ onTask(it.id) })}};return}
    Bt(task.title,size=AppTypography.Size27,bold=true,spacing=AppTypography.TrackingMinus1);BioTabs(listOf("7" to AppText.text(TextKey.Copy7NgayFc11cc),"30" to AppText.text(TextKey.Copy30NgayB37f06)), period, onPeriod)
    BioCard(Modifier.fillMaxWidth()){calendar()}
    BioMetrics(listOf(AppText.text(TextKey.CopyChuoiHienTai460755) to task.streak.toString(),AppText.text(TextKey.CopyDaiNhat67c21e) to task.longest.toString(),AppText.text(TextKey.CopyTyLe30Ngay84d03d) to if(task.rate != null)"${task.rate}%"else "—"))
    BioDetails(AppText.text(TextKey.CopyTapTrungGoiY2462a6)){BioFacts(listOf(AppText.text(TextKey.CopyTapTrung964002) to AppText.text(TextKey.Copy0Phut2596d2,task.focusMinutes),AppText.text(TextKey.CopyGioThuongXong450100) to task.commonHour));if(task.suggestion.isNotBlank())BioButton(task.suggestion,onAdjust,secondary=true)}
    var minute by rememberSaveable(task.id){mutableStateOf(task.minute)};var mode by rememberSaveable(task.id){mutableStateOf(task.mode)};var follow by rememberSaveable(task.id){mutableStateOf(task.followUp)}
    Bt(AppText.text(TextKey.CopyNhacViec9e9055),size=AppTypography.Size17,bold=true);BioTimeField(AppText.text(TextKey.CopyGioNhacD9dac9),minute,{minute=it});BioSelect(AppText.text(TextKey.CopyKhiNaoB5af31),listOf("OFF" to AppText.text(TextKey.CopyTat328606),"AT" to AppText.text(TextKey.CopyTheoGio4bd1ce),"BEFORE" to AppText.text(TextKey.CopyTruocKhungGioC97c51)),mode,onSelect={mode=it});BioToggle(AppText.text(TextKey.CopyNhacNheThemMotLan544e8e),follow){follow=it};BioButton(AppText.text(TextKey.CopyLuuNhacViecDe3aa9),{ onSaveReminder(minute, mode, follow) },Modifier.fillMaxWidth());BioLink(AppText.text(TextKey.CopyQuyenNhacToanCuc8225b7),"bell",onSettings)
}

@Preview(showBackground = true, widthDp = 375)
@Composable
private fun TaskInsightsScreenPreview() {
    Column(verticalArrangement = Arrangement.spacedBy(AppDimensions.Dp18)) {
        TaskInsightsScreen(null, emptyList(), "7", {}, {}, {}, {}, { _, _, _ -> }, {}, {})
    }
}
