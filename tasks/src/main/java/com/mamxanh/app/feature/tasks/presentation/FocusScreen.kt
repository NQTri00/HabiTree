package com.mamxanh.app.feature.tasks.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.mamxanh.app.presentation.ui.design.*
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey

data class FocusTaskChoice(val id: String, val title: String, val minutes: Int)

data class FocusDisplay(
    val title: String,
    val present: Boolean,
    val running: Boolean,
    val onBreak: Boolean,
    val remaining: Int,
    val date: String,
    val minutes: String,
    val task: String,
    val eligibleTasks: List<FocusTaskChoice>,
    val history: List<String>,
)

@Composable
fun ColumnScope.FocusScreen(state: FocusDisplay, onTask: (String) -> Unit, onMinutes: (String) -> Unit) {
    val present = state.present
    val running = state.running
    val minutes = state.minutes
    val task = state.task
    val landscape=LocalConfiguration.current.screenHeightDp<500
    if(present){if(!landscape)Bt(AppText.text(TextKey.CopyKhoangThoiGianCuaBanA0e014),size=AppTypography.Size10,mono=true,tint=bp.muted,spacing=AppTypography.Tracking1_8);Bt(state.title,Modifier.fillMaxWidth(),size=if(landscape)22 else 27,bold=true,align=TextAlign.Center)}else if(landscape)Bt(AppText.text(TextKey.CopyTapTrung964002),size=AppTypography.Size22,bold=true)else BioIntro(AppText.text(TextKey.CopyTapTrung964002),AppText.text(TextKey.CopyChiMotViecThoi83ccd7),AppText.text(TextKey.CopyDanhMotKhoangChoDieuQuanTrongDfd9b6),{Bi("sun")})
    val remaining=if(present)state.remaining else (minutes.toIntOrNull()?:0).coerceIn(0,240)*60
    val p=bp
    Box(Modifier.align(Alignment.CenterHorizontally).size(if(landscape)AppDimensions.Dp140 else AppDimensions.Dp200).drawBehind{drawCircle(p.soft,radius=size.minDimension/2-AppDimensions.Dp4.toPx(),style=androidx.compose.ui.graphics.drawscope.Stroke(AppDimensions.Dp8.toPx()))},contentAlignment=Alignment.Center){Column(horizontalAlignment=Alignment.CenterHorizontally){Bt("${remaining/60}:${(remaining%60).toString().padStart(2,'0')}",size=if(landscape)36 else 46,line=if(landscape)46f else 60f,mono=true,spacing=AppTypography.TrackingMinus2);if(!landscape)Bt(if(present)if(running)AppText.text(TextKey.CopyDangTapTrung1c969b)else if(state.onBreak)AppText.text(TextKey.CopyNghiMotChutF912b7)else AppText.text(TextKey.CopyDaTamDungA919ce)else AppText.text(TextKey.CopyBatDauKhiBanSanSang3d0a9a),size=AppTypography.Size12,tint=bp.muted)}}
    if(present){BioDetails(AppText.text(TextKey.CopyPhienDangLuuD6c8c9)){Bt(state.date+AppText.text(TextKey.CopyRoiTrangSeTamDungPhanDaLamLuonDuocGiuLaiC6be38),size=AppTypography.Size12,tint=bp.muted)}}
    else{
        BioSelect(AppText.text(TextKey.CopyDanhChoFf565f),listOf("0" to AppText.text(TextKey.CopyTuDoKhongThuongXu113e54))+state.eligibleTasks.map { it.id to AppText.text(TextKey.Copy01PhutCe85cf, it.title, it.minutes) },task){ onTask(it) }
        if(task=="0"){
            BioChips(listOf(15,25,45,60).map{it.toString() to AppText.text(TextKey.Copy0Phut2596d2,it)},minutes){ onMinutes(it) }
            BioField(AppText.text(TextKey.CopyPhutTapTrung5120Ca7d24),minutes,{ onMinutes(it) },number=true,error=if((minutes.toIntOrNull()?:0) !in 5..120)AppText.text(TextKey.CopyNhapSoPhutTu51202b514d)else "")
        }else BioCard(Modifier.fillMaxWidth(),bp.soft,padding=AppSpacing.Value12){Bt(AppText.text(TextKey.Copy0PhutTheoThoiGianCuaViecE07f34,minutes),size=AppTypography.Size13,semi=true);Bt(AppText.text(TextKey.CopySuaThoiGianTrongChiTietViec940772),size=AppTypography.Size12,tint=bp.muted)}
    }
    BioDetails(AppText.text(TextKey.CopyCacPhienDaLuu0HoanThanhBb1ef4,state.history.size)){if(state.history.size==0)Bt(AppText.text(TextKey.CopyChuaCoPhienHoanThanhFd9386),size=AppTypography.Size12,tint=bp.muted);state.history.forEach { Bt(it, size = AppTypography.Size13) }}
}

@Preview(showBackground = true, widthDp = 375)
@Composable
private fun FocusScreenPreview() {
    Column(verticalArrangement = Arrangement.spacedBy(AppDimensions.Dp18)) {
        FocusScreen(FocusDisplay("", false, false, false, 1500, "", "25", "0", emptyList(), emptyList()), {}, {})
    }
}
