package com.mamxanh.app.feature.tasks.presentation.ui.form

import com.mamxanh.core.ui.R
import androidx.compose.ui.res.stringResource
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.mamxanh.app.core.components.design.*
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey

@Composable
fun TaskFormScreen(
    state: TaskFormDisplay,
    actions: (@Composable () -> Unit)? = null,
    onFieldChanged: (TaskFormField, String) -> Unit,
) {
    fun value(field: TaskFormField) = state.values[field].orEmpty()
    fun change(field: TaskFormField, value: String) = onFieldChanged(field, value)
    val error = state.error
    val titleLocation=remember{BringIntoViewRequester()};val minutesLocation=remember{BringIntoViewRequester()};val daysLocation=remember{BringIntoViewRequester()}
    LaunchedEffect(error,state.errorAttempt){when(error){"title"->titleLocation.bringIntoView();"minutes"->minutesLocation.bringIntoView();"days"->daysLocation.bringIntoView()}}
    Column(Modifier.fillMaxWidth().padding(top=AppDimensions.Dp16),verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp16)){
    BioField(stringResource(R.string.copy_ten_viec_672c8c),value(TaskFormField.Title),{change(TaskFormField.Title,it.take(100))},Modifier.bringIntoViewRequester(titleLocation),placeholder=stringResource(R.string.copy_vi_du_doc_10_trang_sach_3a6cd3),error=if(error=="title")stringResource(R.string.copy_nhap_ten_viec_1_100_ky_tu_712679)else "")
    BioSelect(stringResource(R.string.copy_loai_3bfbf9),listOf("HABIT" to stringResource(R.string.copy_thoi_quen_e6e6e8),"DAILY" to stringResource(R.string.copy_hang_ngay_545b54),"TODO" to stringResource(R.string.copy_mot_lan_ca8e62)),value(TaskFormField.Kind)){change(TaskFormField.Kind,it)}
    BioSelect(stringResource(R.string.copy_nhom_cd7a71),listOf(AppText.canonical(TextKey.CopyHocTapA51516),AppText.canonical(TextKey.CopySucKhoe071df7),AppText.canonical(TextKey.CopyNepSong8d1a98),AppText.canonical(TextKey.CopyViecNha126ef3),AppText.canonical(TextKey.CopyCaNhan983dcc)).map{it to AppText.category(it)},value(TaskFormField.Category)){change(TaskFormField.Category,it)}
    if(value(TaskFormField.Kind)=="TODO")BioDateField(stringResource(R.string.copy_han_chot_tuy_chon_485109),value(TaskFormField.Due),{change(TaskFormField.Due,it)})else{
        BioSelect(stringResource(R.string.copy_lich_be87b3),listOf("EVERY_DAY" to stringResource(R.string.copy_moi_ngay_8270cb),"WEEKDAYS" to stringResource(R.string.copy_ngay_thuong_ef45e7),"WEEKENDS" to stringResource(R.string.copy_cuoi_tuan_a0fb27),"CUSTOM" to stringResource(R.string.copy_tu_chon_905069)),value(TaskFormField.Frequency)){change(TaskFormField.Frequency,it)}
        if(value(TaskFormField.Frequency)=="CUSTOM"){Column(Modifier.bringIntoViewRequester(daysLocation)){Bt(stringResource(R.string.choose_weekdays),size=AppTypography.Size12,tint=bp.muted);BioError(if(error=="days")stringResource(R.string.copy_chon_it_nhat_mot_ngay_ba3aff)else "")};BioGrid(7,4){i,m->val selected=value(TaskFormField.Days).contains(i.toString());Row(m.heightIn(min=AppDimensions.Dp48).border(AppDimensions.Dp1,bp.line,bioShape(AppRadii.Value13)).background(if(selected)bp.accent else AppColors.White.copy(alpha=AppOpacity.Value_55),bioShape(AppRadii.Value13)).clickable{change(TaskFormField.Days,if(selected)value(TaskFormField.Days).replace(i.toString(),"")else value(TaskFormField.Days)+i)},verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.Center){Bt(if(selected)"☑"else "□",size=AppTypography.Size14);Spacer(Modifier.width(AppDimensions.Dp5));Bt(bioWeekday(i+1),size=AppTypography.Size11)}}}
    }
    BioSelect(stringResource(R.string.copy_hoan_thanh_bang_2aaf0c),listOf("CLICK" to stringResource(R.string.copy_mot_lan_cham_556209),"TIMER" to stringResource(R.string.copy_bo_dem_thoi_gian_80faf4)),value(TaskFormField.Verification)){change(TaskFormField.Verification,it)}
    if(value(TaskFormField.Verification)=="TIMER")BioField(stringResource(R.string.copy_thoi_gian_5_240_phut_47de8e),value(TaskFormField.Minutes),{change(TaskFormField.Minutes,it)},Modifier.bringIntoViewRequester(minutesLocation),number=true,error=if(error=="minutes")stringResource(R.string.copy_nhap_so_nguyen_tu_5_240_phut_87a223)else "")
    BioDetails(stringResource(R.string.copy_them_chi_tiet_ae1911)){
        BioField(stringResource(R.string.copy_mo_ta_9eca25),value(TaskFormField.Description),{change(TaskFormField.Description,it.take(500))},multiline=true)
        Row(horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp12)){BioTimeField(stringResource(R.string.copy_tu_gio_69edeb),value(TaskFormField.Start),{change(TaskFormField.Start,it)},Modifier.weight(1f));BioTimeField(stringResource(R.string.copy_den_gio_c031d6),value(TaskFormField.End),{change(TaskFormField.End,it)},Modifier.weight(1f))}
        BioTimeField(stringResource(R.string.copy_nhac_luc_1007f3),value(TaskFormField.Reminder),{change(TaskFormField.Reminder,it)})
        Bt(stringResource(R.string.copy_lich_moi_ap_dung_tu_ngay_mai_hom_nay_la_tuy_chon_khu_15d639),size=AppTypography.Size12,tint=bp.muted)
    }
    BioCard(Modifier.fillMaxWidth(),bp.soft){Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween){Bt(stringResource(R.string.copy_thuong_moi_lan_6c1a24),size=AppTypography.Size12);Bt(stringResource(R.string.copy_0_xu_251e68, state.reward),semi=true)};Bt(stringResource(R.string.copy_toi_da_50_xu_ngay_4c710c),size=AppTypography.Size12,tint=bp.muted)}
    actions?.invoke()
    }
}

@Preview(showBackground = true, widthDp = 375)
@Composable
private fun TaskFormScreenPreview() {
    TaskFormScreen(TaskFormDisplay(emptyMap(), "title", null, 8)) { _, _ -> }
}
