package com.mamxanh.app.feature.tasks.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.mamxanh.app.presentation.ui.design.*
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey

data class TaskCompletionDisplay(val date: String, val granted: Int, val nominal: Int)

data class TaskDetailDisplay(
    val category: String,
    val title: String,
    val kind: String,
    val frequency: String,
    val days: String,
    val description: String,
    val start: String,
    val end: String,
    val due: String,
    val lastDone: String,
    val lifecycle: String,
    val minutes: Int,
    val streak: Int,
    val reward: Int,
    val done: Boolean,
    val timer: Boolean,
    val history: List<TaskCompletionDisplay>,
)

@Composable
fun TaskDetailScreen(
    t: TaskDetailDisplay?,
    onTasks: () -> Unit,
    onComplete: () -> Unit,
    onEdit: () -> Unit,
    onInsights: () -> Unit,
    onLifecycle: (String) -> Unit,
    onArchive: () -> Unit,
) {
    if(t==null){BioEmpty(AppText.text(TextKey.CopyViecKhongConODay7f288d),icon="tasks",action=AppText.text(TextKey.CopyVeDanhSach5c7bca),onClick=onTasks);return}
    Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.Top,horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp12)){Column(Modifier.weight(1f)){Bt(AppText.category(t.category),size=AppTypography.Size10,tint=bp.muted,mono=true,spacing=AppTypography.Tracking1_8);Spacer(Modifier.height(AppDimensions.Dp7));Bt(t.title,size=AppTypography.Size27,line=AppTypography.LineHeight35_1,bold=true,spacing=AppTypography.TrackingMinus1)};BioTag(AppText.text(TextKey.Copy0Xu7c2391,t.reward))}
    val lifecycle=t.lifecycle;val done=t.done;val timer=t.timer
    BioCard(Modifier.fillMaxWidth()){BioFacts(listOf(AppText.text(TextKey.CopyLoai3bfbf9) to when(t.kind){"HABIT"->AppText.text(TextKey.CopyThoiQuenE6e6e8);"TODO"->AppText.text(TextKey.CopyMotLanCa8e62);else->AppText.text(TextKey.CopyHangNgay545b54)},AppText.text(TextKey.CopyLichBe87b3) to when(t.frequency){"WEEKDAYS"->AppText.text(TextKey.CopyNgayThuongEf45e7);"WEEKENDS"->AppText.text(TextKey.CopyCuoiTuanA0fb27);"CUSTOM"->AppText.text(TextKey.CopyNgay96007e)+t.days;"ONCE"->AppText.text(TextKey.CopyMotLanCa8e62);else->AppText.text(TextKey.CopyMoiNgay8270cb)},AppText.text(TextKey.CopyXacNhan1503b0) to if(timer)AppText.text(TextKey.Copy0Phut2596d2,t.minutes)else AppText.text(TextKey.CopyChamDeHoanThanh856927),AppText.text(TextKey.CopyTrangThaiCb31de) to if(done)AppText.text(TextKey.CopyDaXong565e93)else if(lifecycle=="PAUSED")AppText.text(TextKey.TabPaused)else if(lifecycle=="ARCHIVED")AppText.text(TextKey.CopyLuuTru7e7c5f)else AppText.text(TextKey.CopyDangHoatDong767d67)));if(t.description.isNotBlank())Bt(t.description,size=AppTypography.Size12,tint=bp.muted)}
    BioDetails(AppText.text(TextKey.CopyLichLichSu7153b3)){BioFacts(listOf(AppText.text(TextKey.CopyKhungGio78354f) to if(t.start.isBlank())AppText.text(TextKey.CopyCaNgay891abb)else t.start+"–"+t.end,AppText.text(TextKey.CopyHanChot8a5e27) to t.due,AppText.text(TextKey.CopyChuoiNgay5ad2e6) to t.streak.toString(),AppText.text(TextKey.CopyLanCuoiAdde92) to t.lastDone.ifBlank{if(done)AppText.text(TextKey.CopyHomNayE6747a)else AppText.text(TextKey.CopyChuaGhiNhanF7654d)}));Bt(AppText.text(TextKey.Copy7NgayGanDay486803),size=AppTypography.Size12);if(t.history.isEmpty())Bt(AppText.text(TextKey.CopyChuaCoLanHoanThanh5b7c8b),size=AppTypography.Size12,tint=bp.muted)else BioFacts(t.history.map { it.date to AppText.text(TextKey.Copy01Xu8c9c4c, it.granted, it.nominal) })}
    BioButton(if(timer)AppText.text(TextKey.CopyTapTrung964002)else if(done)AppText.text(TextKey.CopyDaHoanThanh14eefb)else AppText.text(TextKey.CopyHoanThanh5eb36f),onComplete,Modifier.fillMaxWidth(),if(timer)"clock"else "check",enabled=!done&&lifecycle=="ACTIVE")
    BioButton(AppText.text(TextKey.CopySuaViec55ba70),onEdit,Modifier.fillMaxWidth(),"edit",true)
    BioLink(AppText.text(TextKey.CopyNhipThoiQuen1b8324),"chart",onInsights)
    Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween){BioTextAction(if(lifecycle=="ACTIVE")AppText.text(TextKey.CopyTamDung676358)else AppText.text(TextKey.CopyTiepTuc87a7ae),{ onLifecycle(if (lifecycle == "ACTIVE") "PAUSED" else "ACTIVE") });BioTextAction(AppText.text(TextKey.ActionArchive),onArchive)}
}

@Preview(showBackground = true, widthDp = 375)
@Composable
private fun TaskDetailScreenPreview() {
    TaskDetailScreen(null, {}, {}, {}, {}, {}, {})
}
