package com.mamxanh.app.feature.tasks.presentation

import com.mamxanh.core.ui.R
import androidx.compose.ui.res.stringResource
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.mamxanh.app.presentation.ui.design.*

@Composable
fun ColumnScope.FocusScreen(state: FocusDisplay, onTask: (String) -> Unit, onMinutes: (String) -> Unit) {
    val present = state.present
    val running = state.running
    val minutes = state.minutes
    val task = state.task
    val configuration = LocalConfiguration.current
    val landscape = configuration.screenHeightDp < 500
    val ringSize = when {
        landscape -> AppDimensions.Dp140
        configuration.screenWidthDp < AppThemeEffects.CompactWidth -> AppDimensions.Dp200
        else -> AppDimensions.Dp240
    }
    if(present){if(!landscape)Bt(stringResource(R.string.copy_khoang_thoi_gian_cua_ban_a0e014),size=AppTypography.Size10,mono=true,tint=bp.muted,spacing=AppTypography.Tracking1_8);Bt(state.title,Modifier.fillMaxWidth(),size=if(landscape)22 else 27,bold=true,align=TextAlign.Center)}else if(landscape)Bt(stringResource(R.string.copy_tap_trung_964002),size=AppTypography.Size22,bold=true)else BioIntro(stringResource(R.string.copy_tap_trung_964002),stringResource(R.string.copy_chi_mot_viec_thoi_83ccd7),stringResource(R.string.copy_danh_mot_khoang_cho_dieu_quan_trong_dfd9b6),{Bi("sun")})
    val remaining=if(present)state.remaining else (minutes.toIntOrNull()?:0).coerceIn(0,240)*60
    val p=bp
    Box(Modifier.align(Alignment.CenterHorizontally).size(ringSize).drawBehind{drawCircle(p.soft,radius=size.minDimension/2-AppDimensions.Dp4.toPx(),style=androidx.compose.ui.graphics.drawscope.Stroke(AppDimensions.Dp8.toPx()))},contentAlignment=Alignment.Center){Column(horizontalAlignment=Alignment.CenterHorizontally){Bt("${remaining/60}:${(remaining%60).toString().padStart(2,'0')}",size=if(landscape)36 else 46,line=if(landscape)46f else 60f,mono=true,spacing=AppTypography.TrackingMinus2);if(!landscape)Bt(if(present)if(running)stringResource(R.string.copy_dang_tap_trung_1c969b)else if(state.onBreak)stringResource(R.string.copy_nghi_mot_chut_f912b7)else stringResource(R.string.copy_da_tam_dung_a919ce)else stringResource(R.string.copy_bat_dau_khi_ban_san_sang_3d0a9a),size=AppTypography.Size12,tint=bp.muted)}}
    if(present){BioDetails(stringResource(R.string.copy_phien_dang_luu_d6c8c9)){Bt(stringResource(R.string.copy_roi_trang_se_tam_dung_phan_da_lam_luon_duoc_giu_lai_c6be38, state.date),size=AppTypography.Size12,tint=bp.muted)}}
    else{
        if(task=="0"){
            BioChips(listOf(15,25,45,60).map{it.toString() to stringResource(R.string.copy_0_phut_2596d2, it)},minutes){ onMinutes(it) }
            BioField(stringResource(R.string.copy_phut_tap_trung_5_120_ca7d24),minutes,{ onMinutes(it) },number=true,error=if((minutes.toIntOrNull()?:0) !in 5..120)stringResource(R.string.copy_nhap_so_phut_tu_5_120_2b514d)else "")
        }
        BioSelect(stringResource(R.string.copy_danh_cho_ff565f),listOf("0" to stringResource(R.string.copy_tu_do_khong_thuong_xu_113e54))+state.eligibleTasks.map { it.id to stringResource(R.string.copy_0_1_phut_ce85cf, it.title, it.minutes) },task){ onTask(it) }
        if (task != "0") BioCard(Modifier.fillMaxWidth(),bp.soft,padding=AppSpacing.Value12){Bt(stringResource(R.string.copy_0_phut_theo_thoi_gian_cua_viec_e07f34, minutes),size=AppTypography.Size13,semi=true);Bt(stringResource(R.string.copy_sua_thoi_gian_trong_chi_tiet_viec_940772),size=AppTypography.Size12,tint=bp.muted)}
    }
    BioDetails(stringResource(R.string.copy_cac_phien_da_luu_0_hoan_thanh_bb1ef4, state.history.size)){if(state.history.size==0)Bt(stringResource(R.string.copy_chua_co_phien_hoan_thanh_fd9386),size=AppTypography.Size12,tint=bp.muted);state.history.forEach { Bt(it, size = AppTypography.Size13) }}
}

@Preview(showBackground = true, widthDp = 375)
@Composable
private fun FocusScreenPreview() {
    Column(verticalArrangement = Arrangement.spacedBy(AppDimensions.Dp18)) {
        FocusScreen(FocusDisplay("", false, false, false, 1500, "", "25", "0", emptyList(), emptyList()), {}, {})
    }
}
