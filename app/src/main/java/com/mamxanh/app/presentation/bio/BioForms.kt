package com.mamxanh.app.presentation.bio

import com.mamxanh.app.presentation.ui.design.*

import com.mamxanh.app.presentation.ui.design.AppColors
import com.mamxanh.app.presentation.ui.design.AppDimensions
import com.mamxanh.app.presentation.ui.design.AppIcons
import com.mamxanh.app.presentation.ui.design.AppOpacity
import com.mamxanh.app.presentation.ui.design.AppRadii
import com.mamxanh.app.presentation.ui.design.AppSpacing
import com.mamxanh.app.presentation.ui.design.AppTypography

import com.mamxanh.shared.localization.TextKey

import com.mamxanh.shared.localization.AppText

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.selection.toggleable
import androidx.compose.ui.semantics.*
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import com.mamxanh.app.presentation.ui.GardenFontFamily
import org.json.JSONObject

@Composable internal fun BioField(label:String,value:String,onChange:(String)->Unit,modifier:Modifier=Modifier,multiline:Boolean=false,placeholder:String="",icon:String="",number:Boolean=false,centered:Boolean=false,error:String=""){
    Column(modifier.fillMaxWidth(),verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp7)){
        if(label.isNotBlank())Bt(label,size=AppTypography.Size12)
        val textColor=bp.ink;val muted=bp.muted
        BasicTextField(value,onChange,Modifier.fillMaxWidth().heightIn(min=if(multiline)AppDimensions.Dp90 else AppDimensions.Dp48).background(bp.surface,RoundedCornerShape(AppDimensions.Dp12)).border(AppDimensions.Dp1,if(error.isBlank())bp.control else AppColors.Danger,RoundedCornerShape(AppDimensions.Dp12)).semantics{if(label.isNotBlank())contentDescription=label;if(error.isNotBlank())error(error)}.padding(AppDimensions.Dp12),singleLine=!multiline,textStyle=TextStyle(textAlign=if(centered)TextAlign.Center else TextAlign.Start,color=textColor,fontSize=AppTypography.Sp14,lineHeight=AppTypography.Sp21,fontFamily=GardenFontFamily,platformStyle=androidx.compose.ui.text.PlatformTextStyle(includeFontPadding=false)),keyboardOptions=KeyboardOptions(keyboardType=if(number)KeyboardType.Number else KeyboardType.Text),cursorBrush=SolidColor(bp.deep),decorationBox={inner->Row(verticalAlignment=if(multiline)Alignment.Top else Alignment.CenterVertically){Box(Modifier.weight(1f)){if(value.isBlank()&&placeholder.isNotBlank())Bt(placeholder,size=AppTypography.Size14,tint=muted);inner()};if(icon.isNotBlank())Bi(icon,size=AppIcons.Size15,tint=textColor)}})
        BioError(error)
    }
}
@Composable internal fun BioSelect(label:String,options:List<Pair<String,String>>,value:String,modifier:Modifier=Modifier,onSelect:(String)->Unit){
    var open by remember{mutableStateOf(false)}
    Column(modifier.fillMaxWidth(),verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp7)){
        if(label.isNotBlank())Bt(label,size=AppTypography.Size12)
        Box{
            Row(Modifier.fillMaxWidth().heightIn(min=AppDimensions.Dp48).background(bp.surface,RoundedCornerShape(AppDimensions.Dp12)).border(AppDimensions.Dp1,bp.control,RoundedCornerShape(AppDimensions.Dp12)).semantics{if(label.isNotBlank())contentDescription=label;stateDescription=if(open)AppText.text(TextKey.CopyDaMo617b34)else AppText.text(TextKey.CopyDaDong6b9194)}.clickable(role=Role.DropdownList){open=true}.padding(AppDimensions.Dp12),verticalAlignment=Alignment.CenterVertically){Bt(options.firstOrNull{it.first==value}?.second?:options.firstOrNull()?.second.orEmpty(),Modifier.weight(1f),size=AppTypography.Size14);Bi("next",Modifier.rotate(90f),tint=bp.ink,size=AppIcons.Size15)}
            DropdownMenu(open,{open=false},containerColor=bp.surface){options.forEach{(id,title)->DropdownMenuItem(text={Bt(title)},onClick={onSelect(id);open=false})}}
        }
    }
}
@Composable internal fun BioToggle(label:String,value:Boolean,onChange:(Boolean)->Unit){
    Row(Modifier.fillMaxWidth().heightIn(min=AppDimensions.Dp55).padding(end=AppDimensions.Dp3).toggleable(value=value,role=Role.Switch,onValueChange=onChange),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp12)){
        Bt(label,Modifier.weight(1f),size=AppTypography.Size13)
        Box(Modifier.size(AppDimensions.Dp42,AppDimensions.Dp25).background(if(value)bp.deep else AppColors.SwatchFFCFD6C8,RoundedCornerShape(AppDimensions.Dp20)).padding(AppDimensions.Dp3)){Box(Modifier.align(if(value)Alignment.CenterEnd else Alignment.CenterStart).size(AppDimensions.Dp19).background(AppColors.White,RoundedCornerShape(50)))}
    }
}
@Composable internal fun BioError(text:String){if(text.isNotBlank())Bt(text,Modifier.semantics{liveRegion=LiveRegionMode.Polite},size=AppTypography.Size12,tint=AppColors.Danger)}

@Composable internal fun ColumnScope.BioTaskDetail(world:BioWorld,ui:BioController){
    val t=world.task(ui.param)
    if(t==null){BioEmpty(AppText.text(TextKey.CopyViecKhongConODay7f288d),icon="tasks",action=AppText.text(TextKey.CopyVeDanhSach5c7bca),onClick={ui.go("tasks")});return}
    Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.Top,horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp12)){Column(Modifier.weight(1f)){Bt(AppText.category(t.s("category")),size=AppTypography.Size10,tint=bp.muted,mono=true,spacing=AppTypography.Tracking1_8);Spacer(Modifier.height(AppDimensions.Dp7));Bt(t.s("title"),size=AppTypography.Size27,line=AppTypography.LineHeight35_1,bold=true,spacing=AppTypography.TrackingMinus1)};BioTag(AppText.text(TextKey.Copy0Xu7c2391,taskReward(t)))}
    val lifecycle=t.s("lifecycle","ACTIVE");val done=t.b("done")||t.b("finished");val timer=t.s("verification")=="TIMER"
    BioCard(Modifier.fillMaxWidth()){BioFacts(listOf(AppText.text(TextKey.CopyLoai3bfbf9) to when(t.s("kind")){"HABIT"->AppText.text(TextKey.CopyThoiQuenE6e6e8);"TODO"->AppText.text(TextKey.CopyMotLanCa8e62);else->AppText.text(TextKey.CopyHangNgay545b54)},AppText.text(TextKey.CopyLichBe87b3) to when(t.s("frequency")){"WEEKDAYS"->AppText.text(TextKey.CopyNgayThuongEf45e7);"WEEKENDS"->AppText.text(TextKey.CopyCuoiTuanA0fb27);"CUSTOM"->AppText.text(TextKey.CopyNgay96007e)+t.s("days");"ONCE"->AppText.text(TextKey.CopyMotLanCa8e62);else->AppText.text(TextKey.CopyMoiNgay8270cb)},AppText.text(TextKey.CopyXacNhan1503b0) to if(timer)AppText.text(TextKey.Copy0Phut2596d2,t.i("minutes"))else AppText.text(TextKey.CopyChamDeHoanThanh856927),AppText.text(TextKey.CopyTrangThaiCb31de) to if(done)AppText.text(TextKey.CopyDaXong565e93)else if(lifecycle=="PAUSED")AppText.text(TextKey.TabPaused)else if(lifecycle=="ARCHIVED")AppText.text(TextKey.CopyLuuTru7e7c5f)else AppText.text(TextKey.CopyDangHoatDong767d67)));if(t.s("description").isNotBlank())Bt(t.s("description"),size=AppTypography.Size12,tint=bp.muted)}
    BioDetails(AppText.text(TextKey.CopyLichLichSu7153b3)){BioFacts(listOf(AppText.text(TextKey.CopyKhungGio78354f) to if(t.s("start").isBlank())AppText.text(TextKey.CopyCaNgay891abb)else t.s("start")+"–"+t.s("end"),AppText.text(TextKey.CopyHanChot8a5e27) to t.s("due",AppText.text(TextKey.CopyKhongCoAf48b6)),AppText.text(TextKey.CopyChuoiNgay5ad2e6) to t.i("streak").toString(),AppText.text(TextKey.CopyLanCuoiAdde92) to t.s("lastDone").ifBlank{if(done)AppText.text(TextKey.CopyHomNayE6747a)else AppText.text(TextKey.CopyChuaGhiNhanF7654d)}));Bt(AppText.text(TextKey.Copy7NgayGanDay486803),size=AppTypography.Size12);if(t.a("history").length()==0)Bt(AppText.text(TextKey.CopyChuaCoLanHoanThanh5b7c8b),size=AppTypography.Size12,tint=bp.muted)else BioFacts(t.a("history").objects().map{it.s("date") to AppText.text(TextKey.Copy01Xu8c9c4c,it.i("granted"),it.i("nominal"))})}
    BioButton(if(timer)AppText.text(TextKey.CopyTapTrung964002)else if(done)AppText.text(TextKey.CopyDaHoanThanh14eefb)else AppText.text(TextKey.CopyHoanThanh5eb36f),{if(timer)ui.go("focus",t.s("id"))else ui.send("complete:"+t.s("id"))},Modifier.fillMaxWidth(),if(timer)"clock"else "check",enabled=!done&&lifecycle=="ACTIVE")
    BioButton(AppText.text(TextKey.CopySuaViec55ba70),{ui.go("task-new",t.s("id"))},Modifier.fillMaxWidth(),"edit",true)
    BioLink(AppText.text(TextKey.CopyNhipThoiQuen1b8324),"chart",{ui.go("insights",t.s("id"))})
    Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween){BioTextAction(if(lifecycle=="ACTIVE")AppText.text(TextKey.CopyTamDung676358)else AppText.text(TextKey.CopyTiepTuc87a7ae),{ui.send("lifecycle:"+t.s("id")+":"+if(lifecycle=="ACTIVE")"PAUSED"else "ACTIVE")});BioTextAction(AppText.text(TextKey.ActionArchive),{ui.show("archive",t.s("id"))})}
}

@Composable internal fun ColumnScope.BioTaskForm(world:BioWorld,ui:BioController){
    val initial=taskFormInitial(world,ui)
    fun value(key:String,default:String="")=ui.fields[key]?:initial.s(key,default)
    fun change(key:String,v:String){ui.fields[key]=v;ui.dirty=true;if(ui.fields["task-error"]==key)ui.fields["task-error"]=""}
    val error=ui.fields["task-error"].orEmpty()
    val titleLocation=remember{BringIntoViewRequester()};val minutesLocation=remember{BringIntoViewRequester()};val daysLocation=remember{BringIntoViewRequester()}
    LaunchedEffect(error,ui.fields["task-error-attempt"]){when(error){"title"->titleLocation.bringIntoView();"minutes"->minutesLocation.bringIntoView();"days"->daysLocation.bringIntoView()}}
    Column(Modifier.fillMaxWidth().padding(top=AppDimensions.Dp16),verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp16)){
    BioField(AppText.text(TextKey.CopyTenViec672c8c),value("title"),{change("title",it.take(100))},Modifier.bringIntoViewRequester(titleLocation),placeholder=AppText.text(TextKey.CopyViDuDoc10TrangSach3a6cd3),error=if(error=="title")AppText.text(TextKey.CopyNhapTenViec1100KyTu712679)else "")
    BioSelect(AppText.text(TextKey.CopyLoai3bfbf9),listOf("HABIT" to AppText.text(TextKey.CopyThoiQuenE6e6e8),"DAILY" to AppText.text(TextKey.CopyHangNgay545b54),"TODO" to AppText.text(TextKey.CopyMotLanCa8e62)),value("kind")){change("kind",it)}
    BioSelect(AppText.text(TextKey.CopyNhomCd7a71),listOf(AppText.canonical(TextKey.CopyHocTapA51516),AppText.canonical(TextKey.CopySucKhoe071df7),AppText.canonical(TextKey.CopyNepSong8d1a98),AppText.canonical(TextKey.CopyViecNha126ef3),AppText.canonical(TextKey.CopyCaNhan983dcc)).map{it to AppText.category(it)},value("category")){change("category",it)}
    if(value("kind")=="TODO")BioDateField(AppText.text(TextKey.CopyHanChotTuyChon485109),value("due"),{change("due",it)})else{
        BioSelect(AppText.text(TextKey.CopyLichBe87b3),listOf("EVERY_DAY" to AppText.text(TextKey.CopyMoiNgay8270cb),"WEEKDAYS" to AppText.text(TextKey.CopyNgayThuongEf45e7),"WEEKENDS" to AppText.text(TextKey.CopyCuoiTuanA0fb27),"CUSTOM" to AppText.text(TextKey.CopyTuChon905069)),value("frequency")){change("frequency",it)}
        if(value("frequency")=="CUSTOM"){Column(Modifier.bringIntoViewRequester(daysLocation)){Bt(AppText.text(TextKey.ChooseWeekdays),size=AppTypography.Size12,tint=bp.muted);BioError(if(error=="days")AppText.text(TextKey.CopyChonItNhatMotNgayBa3aff)else "")};BioGrid(7,4){i,m->val selected=value("days").contains(i.toString());Row(m.heightIn(min=AppDimensions.Dp48).border(AppDimensions.Dp1,bp.line,bioShape(AppRadii.Value13)).background(if(selected)bp.accent else AppColors.White.copy(alpha=AppOpacity.Value_55),bioShape(AppRadii.Value13)).clickable{change("days",if(selected)value("days").replace(i.toString(),"")else value("days")+i)},verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.Center){Bt(if(selected)"☑"else "□",size=AppTypography.Size14);Spacer(Modifier.width(AppDimensions.Dp5));Bt(bioWeekday(i+1),size=AppTypography.Size11)}}}
    }
    BioSelect(AppText.text(TextKey.CopyHoanThanhBang2aaf0c),listOf("CLICK" to AppText.text(TextKey.CopyMotLanCham556209),"TIMER" to AppText.text(TextKey.CopyBoDemThoiGian80faf4)),value("verification")){change("verification",it)}
    if(value("verification")=="TIMER")BioField(AppText.text(TextKey.CopyThoiGian5240Phut47de8e),value("minutes","5"),{change("minutes",it)},Modifier.bringIntoViewRequester(minutesLocation),number=true,error=if(error=="minutes")AppText.text(TextKey.CopyNhapSoNguyenTu5240Phut87a223)else "")
    BioDetails(AppText.text(TextKey.CopyThemChiTietAe1911)){
        BioField(AppText.text(TextKey.CopyMoTa9eca25),value("description"),{change("description",it.take(500))},multiline=true)
        Row(horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp12)){BioTimeField(AppText.text(TextKey.CopyTuGio69edeb),value("start"),{change("start",it)},Modifier.weight(1f));BioTimeField(AppText.text(TextKey.CopyDenGioC031d6),value("end"),{change("end",it)},Modifier.weight(1f))}
        BioTimeField(AppText.text(TextKey.CopyNhacLuc1007f3),value("reminder"),{change("reminder",it)})
        Bt(AppText.text(TextKey.CopyLichMoiApDungTuNgayMaiHomNayLaTuyChonKhu15d639),size=AppTypography.Size12,tint=bp.muted)
    }
    val minutes=value("minutes","5").toIntOrNull()?:5
    BioCard(Modifier.fillMaxWidth(),bp.soft){Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween){Bt(AppText.text(TextKey.CopyThuongMoiLan6c1a24),size=AppTypography.Size12);Bt(AppText.text(TextKey.Copy0Xu251e68,if(value("verification")=="TIMER")if(minutes>=45)20 else if(minutes>=20)15 else 10 else 8),semi=true)};Bt(AppText.text(TextKey.CopyToiDa50XuNgay4c710c),size=AppTypography.Size12,tint=bp.muted)}
    }
}

@Composable internal fun ColumnScope.BioFocus(world:BioWorld,ui:BioController){
    val focus=world.state.o("focus");val running=focus.s("state")=="RUNNING";val present=focus.s("id").isNotBlank();val minutes=focusMinutes(world,ui);val task=focusTask(ui)
    val landscape=LocalConfiguration.current.screenHeightDp<500
    if(present){if(!landscape)Bt(AppText.text(TextKey.CopyKhoangThoiGianCuaBanA0e014),size=AppTypography.Size10,mono=true,tint=bp.muted,spacing=AppTypography.Tracking1_8);Bt(focus.s("title",AppText.text(TextKey.CopyMotKhoangRiengMinh9ead0d)),Modifier.fillMaxWidth(),size=if(landscape)22 else 27,bold=true,align=TextAlign.Center)}else if(landscape)Bt(AppText.text(TextKey.CopyTapTrung964002),size=AppTypography.Size22,bold=true)else BioIntro(AppText.text(TextKey.CopyTapTrung964002),AppText.text(TextKey.CopyChiMotViecThoi83ccd7),AppText.text(TextKey.CopyDanhMotKhoangChoDieuQuanTrongDfd9b6),{Bi("sun")})
    val remaining=if(present)focus.i("remaining",1500)else (minutes.toIntOrNull()?:0).coerceIn(0,240)*60
    val p=bp
    Box(Modifier.align(Alignment.CenterHorizontally).size(if(landscape)AppDimensions.Dp140 else AppDimensions.Dp200).drawBehind{drawCircle(p.soft,radius=size.minDimension/2-AppDimensions.Dp4.toPx(),style=androidx.compose.ui.graphics.drawscope.Stroke(AppDimensions.Dp8.toPx()))},contentAlignment=Alignment.Center){Column(horizontalAlignment=Alignment.CenterHorizontally){Bt("${remaining/60}:${(remaining%60).toString().padStart(2,'0')}",size=if(landscape)36 else 46,line=if(landscape)46f else 60f,mono=true,spacing=AppTypography.TrackingMinus2);if(!landscape)Bt(if(present)if(running)AppText.text(TextKey.CopyDangTapTrung1c969b)else if(focus.b("break"))AppText.text(TextKey.CopyNghiMotChutF912b7)else AppText.text(TextKey.CopyDaTamDungA919ce)else AppText.text(TextKey.CopyBatDauKhiBanSanSang3d0a9a),size=AppTypography.Size12,tint=bp.muted)}}
    if(present){BioDetails(AppText.text(TextKey.CopyPhienDangLuuD6c8c9)){Bt(bioDisplayDate(world.data.s("date"))+AppText.text(TextKey.CopyRoiTrangSeTamDungPhanDaLamLuonDuocGiuLaiC6be38),size=AppTypography.Size12,tint=bp.muted)}}
    else{
        BioSelect(AppText.text(TextKey.CopyDanhChoFf565f),listOf("0" to AppText.text(TextKey.CopyTuDoKhongThuongXu113e54))+world.tasks.filter{it.s("verification")=="TIMER"&&!it.b("done")&&it.b("available",true)&&it.s("lifecycle","ACTIVE")=="ACTIVE"}.map{it.s("id") to AppText.text(TextKey.Copy01PhutCe85cf,it.s("title"),it.i("minutes"))},task){ui.fields["focus:task"]=it}
        if(task=="0"){
            BioChips(listOf(15,25,45,60).map{it.toString() to AppText.text(TextKey.Copy0Phut2596d2,it)},minutes){ui.fields["focus:minutes"]=it}
            BioField(AppText.text(TextKey.CopyPhutTapTrung5120Ca7d24),minutes,{ui.fields["focus:minutes"]=it},number=true,error=if((minutes.toIntOrNull()?:0) !in 5..120)AppText.text(TextKey.CopyNhapSoPhutTu51202b514d)else "")
        }else BioCard(Modifier.fillMaxWidth(),bp.soft,padding=AppSpacing.Value12){Bt(AppText.text(TextKey.Copy0PhutTheoThoiGianCuaViecE07f34,minutes),size=AppTypography.Size13,semi=true);Bt(AppText.text(TextKey.CopySuaThoiGianTrongChiTietViec940772),size=AppTypography.Size12,tint=bp.muted)}
    }
    BioDetails(AppText.text(TextKey.CopyCacPhienDaLuu0HoanThanhBb1ef4,world.state.a("focusHistory").length())){if(world.state.a("focusHistory").length()==0)Bt(AppText.text(TextKey.CopyChuaCoPhienHoanThanhFd9386),size=AppTypography.Size12,tint=bp.muted);world.state.a("focusHistory").objects().forEach{Bt(it.s("title"),size=AppTypography.Size13)}}
}

@Composable internal fun BioTemplateRow(t:JSONObject,selected:Boolean,onboarding:Boolean=false,onChange:()->Unit){
    BioCard(Modifier.fillMaxWidth().semantics{toggleableState=if(selected)ToggleableState.On else ToggleableState.Off;role=Role.Checkbox},padding=if(onboarding)12 else 14,radius=AppRadii.Value18,tint=if(selected)bp.soft else bp.surface,onClick=onChange,borderColor=if(selected)bp.deep else bp.line){Row(Modifier.fillMaxWidth().heightIn(min=if(onboarding)AppDimensions.Dp65 else AppDimensions.Dp60),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp10)){
        if(!onboarding)Box(Modifier.width(AppDimensions.Dp20).height(AppDimensions.Dp28).background(bp.soft,bioShape(AppRadii.Value8)),contentAlignment=Alignment.Center){Bi(if(t.s("verification")=="TIMER")"clock"else "leaf",size=AppIcons.Size20)}
        Column(Modifier.weight(1f),verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp4)){
            Bt(t.s("title"),size=AppTypography.Size13,semi=true)
            Bt(AppText.category(t.s("category"))+" · "+(if(t.s("verification")=="TIMER")AppText.text(TextKey.CopyBoDem0PhutA99be5,t.i("minutes"))else AppText.text(TextKey.CopyChamF4623f)+t.s("effort")),size=AppTypography.Size12,tint=bp.muted)
            Bt((if(t.s("frequency")=="WEEKENDS")AppText.text(TextKey.CopyCuoiTuanA0fb27)else AppText.text(TextKey.CopyMoiNgay8270cb))+AppText.text(TextKey.Copy0Xu9c90c1,taskReward(t)),size=AppTypography.Size12,tint=bp.muted)
        }
        Box(Modifier.size(AppDimensions.Dp20).background(if(selected)bp.deep else AppColors.Transparent,RoundedCornerShape(AppDimensions.Dp3)).border(AppDimensions.Dp1,if(selected)bp.deep else bp.control,RoundedCornerShape(AppDimensions.Dp3)),contentAlignment=Alignment.Center){if(selected)Bi("check",size=AppIcons.Size14,tint=AppColors.White)}
    }}
}
@Composable internal fun ColumnScope.BioTemplates(world:BioWorld,ui:BioController){
    val chosen=selectedBioTemplates(ui);Column{Bt(AppText.text(TextKey.CopyChonViecVuaSuc895fff),size=AppTypography.Size27,line=AppTypography.LineHeight35_1,bold=true,spacing=AppTypography.TrackingMinus1);Bt(AppText.text(TextKey.CopyThemMotLanChinhSuaBatCuLucNao55b241),size=AppTypography.Size12,tint=bp.muted)}
    Column(verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp12)){world.catalog.a("templates").objects().forEach{t->BioTemplateRow(t,chosen.contains(t.s("id"))){ui.fields["template:"+t.s("id")]=(!chosen.contains(t.s("id"))).toString()}}}
}

@Composable internal fun ColumnScope.BioSettings(world:BioWorld,ui:BioController){
    val languageTitle=androidx.compose.ui.res.stringResource(com.mamxanh.core.ui.R.string.app_language_title)
    val languageDescription=androidx.compose.ui.res.stringResource(com.mamxanh.core.ui.R.string.app_language_description)
    val vietnamese=androidx.compose.ui.res.stringResource(com.mamxanh.core.ui.R.string.app_language_vi)
    val english=androidx.compose.ui.res.stringResource(com.mamxanh.core.ui.R.string.app_language_en)
    val settings=world.settings

    fun toggle(key:String,default:Boolean=false)=(ui.fields["settings:$key"]?:settings.b(key,default).toString()).toBoolean()
    var start by rememberSaveable{mutableStateOf(settings.s("quietStart","22:00"))};var end by rememberSaveable{mutableStateOf(settings.s("quietEnd","07:00"))}
    Bt(AppText.text(TextKey.CopyTheoNhipCuaBan8e2e15),size=AppTypography.Size27,bold=true,spacing=AppTypography.TrackingMinus1);Bt(AppText.text(TextKey.CopyNhacViecTuyChonCoTheTreDoTietKiemPinKhongCf13dd),size=AppTypography.Size12,tint=bp.muted)
    BioCard(Modifier.fillMaxWidth()){
        BioSelect(languageTitle,listOf("vi" to vietnamese,"en" to english),AppText.language.tag){ui.send("language",mapOf("tag" to it))}
        Spacer(Modifier.height(AppDimensions.Dp8));Bt(languageDescription,size=AppTypography.Size12,tint=bp.muted)
    }
    BioCard(Modifier.fillMaxWidth()){
        listOf("paused" to AppText.text(TextKey.CopyTamDungMoiNhacViec0fae21),"treeReminders" to AppText.text(TextKey.CopyNhacKhiCayCanTuoi246e3f),"breakPrompts" to AppText.text(TextKey.CopyGoiYNghiSau25Phut6e7319),"reducedMotion" to AppText.text(TextKey.CopyGiamChuyenDongFf3295)).forEachIndexed{i,(key,label)->if(i>0)HorizontalDivider(color=bp.line);BioToggle(label,toggle(key)){ui.fields["settings:$key"]=it.toString()}}
    }
    BioDetails(AppText.text(TextKey.CopyGioYenLang112f45)){Row(horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp12)){BioTimeField(AppText.text(TextKey.CopyBatDauB3cacc),start,{start=it},Modifier.weight(1f));BioTimeField(AppText.text(TextKey.CopyKetThuc351849),end,{end=it},Modifier.weight(1f))}}
    BioButton(AppText.text(TextKey.CopyLuuTuyChon3847ca),{ui.send("settings",listOf("paused","treeReminders","breakPrompts","reducedMotion").associateWith{toggle(it).toString()}+mapOf("quietStart" to start,"quietEnd" to end))},Modifier.fillMaxWidth())
    BioButton(AppText.text(TextKey.CopyQuyenThongBao58e97d),{ui.show("permission")},Modifier.fillMaxWidth(),"bell",true)
    BioLink(AppText.text(TextKey.CopyNhacRiengTungViecDf60c6),"clock",{ui.go("insights")});BioLink(AppText.text(TextKey.CopyDuLieuUngDung9843b7),"download",{ui.go("data")});BioLink(AppText.text(TextKey.CopyXemLaiBatDauB87622),"leaf",{ui.go("onboard")})
}

@Composable internal fun ColumnScope.BioData(world:BioWorld,ui:BioController){
    BioCard(Modifier.fillMaxWidth()){
        Bt(AppText.text(TextKey.CopySaoLuuCuaBanEc4a6a),size=AppTypography.Size17,bold=true);Bt(AppText.text(TextKey.CopyLuuTrenThietBi0KbC7f8bb,world.state.i("bytes",3072)/1024),size=AppTypography.Size12,tint=bp.muted);Spacer(Modifier.height(AppDimensions.Dp18));Row(horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp12)){BioButton(AppText.text(TextKey.CopyXuatTepB5d886),{ui.send("export")},Modifier.weight(1f),"download");BioButton(AppText.text(TextKey.CopyKhoiPhucC652d9),{ui.send("import")},Modifier.weight(1f),"upload",true)}
        Spacer(Modifier.height(AppDimensions.Dp16));BioDetails(AppText.text(TextKey.CopyQuyenRiengTuCuaBanSaoF03d3e)){Bt(AppText.text(TextKey.CopyBanSaoCoTheChuaLichSuVaGhiChuRiengTuChiD59ce2),size=AppTypography.Size12,tint=bp.muted)}
    }
    Column(verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp6)){BioButton(AppText.text(TextKey.CopyBanAnToanGanNhat4ca154),{ui.show("recover")},Modifier.fillMaxWidth(),secondary=true,enabled=world.state.b("safety"));if(!world.state.b("safety"))Bt(AppText.text(TextKey.CopyChuaCoBanAnToanBanNayDuocTaoTruocKhiKhoi432d5b),size=AppTypography.Size12,tint=bp.muted)}
    BioButton(AppText.text(TextKey.CopyXoaDuLieu398085),{ui.show("clear")},Modifier.fillMaxWidth(),danger=true)
    BioDetails(AppText.text(TextKey.CopyUngDungRiengTu01362e)){Bt(AppText.text(TextKey.CopyKhongCanTaiKhoanDuLieuDuocLuuTrenThietBiE30fea),size=AppTypography.Size12,tint=bp.muted);BioTextAction(AppText.text(TextKey.CopyGiayPhepNguonMo2ecf92),{ui.show("license")})}
    BioDetails(AppText.text(TextKey.CopyChanDoan906791)){Bt(AppText.text(TextKey.CopyXuatThongTinDungLuongVaSoLuongMucDeHoTroD6fe92),size=AppTypography.Size12,tint=bp.muted);BioButton(AppText.text(TextKey.CopyXuatChanDoan9d86ed),{ui.send("diagnostics")},Modifier.fillMaxWidth(),secondary=true)}
}
