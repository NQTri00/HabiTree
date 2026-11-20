package com.mamxanh.app.feature.tasks.presentation

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
import com.mamxanh.app.presentation.ui.design.*
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey

@Composable
fun TaskFormScreen(state: TaskFormDisplay, onFieldChanged: (TaskFormField, String) -> Unit) {
    fun value(field: TaskFormField) = state.values[field].orEmpty()
    fun change(field: TaskFormField, value: String) = onFieldChanged(field, value)
    val error = state.error
    val titleLocation=remember{BringIntoViewRequester()};val minutesLocation=remember{BringIntoViewRequester()};val daysLocation=remember{BringIntoViewRequester()}
    LaunchedEffect(error,state.errorAttempt){when(error){"title"->titleLocation.bringIntoView();"minutes"->minutesLocation.bringIntoView();"days"->daysLocation.bringIntoView()}}
    Column(Modifier.fillMaxWidth().padding(top=AppDimensions.Dp16),verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp16)){
    BioField(AppText.text(TextKey.CopyTenViec672c8c),value(TaskFormField.Title),{change(TaskFormField.Title,it.take(100))},Modifier.bringIntoViewRequester(titleLocation),placeholder=AppText.text(TextKey.CopyViDuDoc10TrangSach3a6cd3),error=if(error=="title")AppText.text(TextKey.CopyNhapTenViec1100KyTu712679)else "")
    BioSelect(AppText.text(TextKey.CopyLoai3bfbf9),listOf("HABIT" to AppText.text(TextKey.CopyThoiQuenE6e6e8),"DAILY" to AppText.text(TextKey.CopyHangNgay545b54),"TODO" to AppText.text(TextKey.CopyMotLanCa8e62)),value(TaskFormField.Kind)){change(TaskFormField.Kind,it)}
    BioSelect(AppText.text(TextKey.CopyNhomCd7a71),listOf(AppText.canonical(TextKey.CopyHocTapA51516),AppText.canonical(TextKey.CopySucKhoe071df7),AppText.canonical(TextKey.CopyNepSong8d1a98),AppText.canonical(TextKey.CopyViecNha126ef3),AppText.canonical(TextKey.CopyCaNhan983dcc)).map{it to AppText.category(it)},value(TaskFormField.Category)){change(TaskFormField.Category,it)}
    if(value(TaskFormField.Kind)=="TODO")BioDateField(AppText.text(TextKey.CopyHanChotTuyChon485109),value(TaskFormField.Due),{change(TaskFormField.Due,it)})else{
        BioSelect(AppText.text(TextKey.CopyLichBe87b3),listOf("EVERY_DAY" to AppText.text(TextKey.CopyMoiNgay8270cb),"WEEKDAYS" to AppText.text(TextKey.CopyNgayThuongEf45e7),"WEEKENDS" to AppText.text(TextKey.CopyCuoiTuanA0fb27),"CUSTOM" to AppText.text(TextKey.CopyTuChon905069)),value(TaskFormField.Frequency)){change(TaskFormField.Frequency,it)}
        if(value(TaskFormField.Frequency)=="CUSTOM"){Column(Modifier.bringIntoViewRequester(daysLocation)){Bt(AppText.text(TextKey.ChooseWeekdays),size=AppTypography.Size12,tint=bp.muted);BioError(if(error=="days")AppText.text(TextKey.CopyChonItNhatMotNgayBa3aff)else "")};BioGrid(7,4){i,m->val selected=value(TaskFormField.Days).contains(i.toString());Row(m.heightIn(min=AppDimensions.Dp48).border(AppDimensions.Dp1,bp.line,bioShape(AppRadii.Value13)).background(if(selected)bp.accent else AppColors.White.copy(alpha=AppOpacity.Value_55),bioShape(AppRadii.Value13)).clickable{change(TaskFormField.Days,if(selected)value(TaskFormField.Days).replace(i.toString(),"")else value(TaskFormField.Days)+i)},verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.Center){Bt(if(selected)"☑"else "□",size=AppTypography.Size14);Spacer(Modifier.width(AppDimensions.Dp5));Bt(bioWeekday(i+1),size=AppTypography.Size11)}}}
    }
    BioSelect(AppText.text(TextKey.CopyHoanThanhBang2aaf0c),listOf("CLICK" to AppText.text(TextKey.CopyMotLanCham556209),"TIMER" to AppText.text(TextKey.CopyBoDemThoiGian80faf4)),value(TaskFormField.Verification)){change(TaskFormField.Verification,it)}
    if(value(TaskFormField.Verification)=="TIMER")BioField(AppText.text(TextKey.CopyThoiGian5240Phut47de8e),value(TaskFormField.Minutes),{change(TaskFormField.Minutes,it)},Modifier.bringIntoViewRequester(minutesLocation),number=true,error=if(error=="minutes")AppText.text(TextKey.CopyNhapSoNguyenTu5240Phut87a223)else "")
    BioDetails(AppText.text(TextKey.CopyThemChiTietAe1911)){
        BioField(AppText.text(TextKey.CopyMoTa9eca25),value(TaskFormField.Description),{change(TaskFormField.Description,it.take(500))},multiline=true)
        Row(horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp12)){BioTimeField(AppText.text(TextKey.CopyTuGio69edeb),value(TaskFormField.Start),{change(TaskFormField.Start,it)},Modifier.weight(1f));BioTimeField(AppText.text(TextKey.CopyDenGioC031d6),value(TaskFormField.End),{change(TaskFormField.End,it)},Modifier.weight(1f))}
        BioTimeField(AppText.text(TextKey.CopyNhacLuc1007f3),value(TaskFormField.Reminder),{change(TaskFormField.Reminder,it)})
        Bt(AppText.text(TextKey.CopyLichMoiApDungTuNgayMaiHomNayLaTuyChonKhu15d639),size=AppTypography.Size12,tint=bp.muted)
    }
    BioCard(Modifier.fillMaxWidth(),bp.soft){Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween){Bt(AppText.text(TextKey.CopyThuongMoiLan6c1a24),size=AppTypography.Size12);Bt(AppText.text(TextKey.Copy0Xu251e68,state.reward),semi=true)};Bt(AppText.text(TextKey.CopyToiDa50XuNgay4c710c),size=AppTypography.Size12,tint=bp.muted)}
    }
}

@Preview(showBackground = true, widthDp = 375)
@Composable
private fun TaskFormScreenPreview() {
    TaskFormScreen(TaskFormDisplay(emptyMap(), "title", null, 8)) { _, _ -> }
}
