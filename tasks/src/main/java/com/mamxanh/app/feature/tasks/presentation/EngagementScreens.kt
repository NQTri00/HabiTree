package com.mamxanh.app.feature.tasks.presentation

import com.mamxanh.app.presentation.ui.design.AppColors
import com.mamxanh.app.presentation.ui.design.AppDimensions
import com.mamxanh.app.presentation.ui.design.AppTypography

import com.mamxanh.shared.localization.TextKey

import com.mamxanh.shared.localization.AppText

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.mamxanh.app.presentation.ui.*
import com.mamxanh.app.presentation.ui.components.*
import com.mamxanh.shared.model.*

@Composable
fun EngagementScreen(state:EngagementUiState,onOpen:(String,Long)->Unit,onAct:(EngagementAction)->Unit,
    onAdjust:(Task)->Unit,onPermission:()->Unit,onEntity:(MemoryEvent)->Unit,onDismissMessage:()->Unit,
    modifier:Modifier=Modifier) {
    MintBackdrop(modifier.fillMaxSize()) {
        ProvideTextStyle(MaterialTheme.typography.bodySmall.copy(fontSize=AppTypography.Sp15,lineHeight=AppTypography.Sp21)) {
        if(state.route=="ONBOARD")OnboardingScreen(state,onAct)
        else Column(Modifier.fillMaxSize()) {
            Row(Modifier.fillMaxWidth().padding(horizontal=AppDimensions.Dp8),verticalAlignment=Alignment.CenterVertically) {
                IconButton(onClick={onOpen("",0)}){Text("‹",fontSize=AppTypography.Sp24,color=Deep)}
                Text(when(state.route){"FOCUS"->AppText.text(TextKey.CopyTapTrung964002);"TEMPLATES"->AppText.text(TextKey.CopyViecMau535dc1);"REVIEW"->AppText.text(TextKey.CopyNhinLaiTuanFf3f0c);
                    "REST"->AppText.text(TextKey.CopyLichNghi588c3f);"ACHIEVEMENTS"->AppText.text(TextKey.CopyDauMocDffd66);"TIMELINE"->AppText.text(TextKey.CopyKyNiemE3e113);"SETTINGS"->AppText.text(TextKey.CopyTuyChon598c1b);"INSIGHTS"->AppText.text(TextKey.CopyNhipThoiQuen1b8324);else->AppText.text(TextKey.CopyHanhTrinh566b59)},
                    color=Deep,fontSize=AppTypography.Sp15,lineHeight=AppTypography.Sp20,fontWeight=FontWeight.Bold)
            }
            val scroll=key(state.route,state.selectedTask){rememberScrollState()}
            Column(Modifier.weight(1f).fillMaxWidth().mintScrollEdges(scroll.canScrollBackward,scroll.canScrollForward)
                .verticalScroll(scroll).padding(horizontal=AppDimensions.Dp24,vertical=AppDimensions.Dp12),
                verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp16)) {
                when(state.route) {
                    "FOCUS"->FocusContent(state,onAct,onOpen)
                    "TEMPLATES"->TemplateContent(state,onAct)
                    "REVIEW"->{ReviewContent(state,onAct);GardenNavigationCard(AppText.text(TextKey.CopyLichNghiBaoVe8eda57),{onOpen("REST",0)},AppText.text(TextKey.Copy0VeBaoVe8bf912,state.snapshot.protectionTickets))}
                    "REST"->RestContent(state,onAct)
                    "ACHIEVEMENTS"->{
                        GardenScreenIntro(AppText.text(TextKey.CopyTungDauMocNhoA0b175),subtitle=AppText.text(TextKey.CopyGhiNhanHanhTrinhKhongThuongXu061899))
                        state.snapshot.achievements.forEach{badge->
                            GardenBotanicalFrame(Modifier.fillMaxWidth(),contentPadding=PaddingValues(AppDimensions.Dp16),cornerSize=AppDimensions.Dp20){
                                Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp14)) {
                                    Box(Modifier.size(AppDimensions.Dp44).background(if(badge.unlockedAt>0)Gold else AppColors.SwatchFFEEEEEE,androidx.compose.foundation.shape.CircleShape),contentAlignment=Alignment.Center){GardenArtwork(GardenArt.RIBBON,Modifier.size(AppDimensions.Dp24))}
                                    Column(Modifier.weight(1f),verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp6)){
                                        Text(badge.title,color=Deep,fontSize=AppTypography.Sp14,lineHeight=AppTypography.Sp20,fontWeight=FontWeight.SemiBold)
                                        Text(badge.criterion,color=Muted,fontSize=AppTypography.Sp11,lineHeight=AppTypography.Sp16)
                                        Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp8)){
                                            MintProgress(badge.progress.toFloat()/badge.target,Modifier.weight(1f))
                                            Text("${badge.progress.coerceAtMost(badge.target)}/${badge.target}"+if(badge.unlockedAt>0)AppText.text(TextKey.CopyDaGhiNhanEbd27e) else "",color=Green,fontSize=AppTypography.Sp10)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    "TIMELINE"->{
                        GardenScreenIntro(AppText.text(TextKey.CopyDieuMinhMuonNho665bc8))
                        if(state.snapshot.memories.isEmpty())Text(AppText.text(TextKey.CopyKyNiemSeXuatHienTuNhungViecBanThucSuDaLaDf826a),color=Muted,fontSize=AppTypography.Sp12)
                        state.snapshot.memories.forEach{event->
                            var note by rememberSaveable(event.id,event.note){mutableStateOf(event.note)}
                            Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min),horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp14)){
                                Column(Modifier.width(AppDimensions.Dp10).fillMaxHeight().padding(top=AppDimensions.Dp4),horizontalAlignment=Alignment.CenterHorizontally){
                                    Box(Modifier.size(AppDimensions.Dp8).background(Green,androidx.compose.foundation.shape.CircleShape))
                                    Spacer(Modifier.height(AppDimensions.Dp5))
                                    Box(Modifier.width(AppDimensions.Dp1).weight(1f).background(AppColors.SwatchFFD8DFD0))
                                }
                                Column(Modifier.weight(1f),verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp10)){
                                    if(event.at>0)Text(java.text.SimpleDateFormat("dd/MM · HH:mm",java.util.Locale.getDefault()).format(java.util.Date(event.at)),color=Muted,fontSize=AppTypography.Sp11)
                                    Text(event.title,color=Deep,fontSize=AppTypography.Sp14,lineHeight=AppTypography.Sp20,fontWeight=FontWeight.SemiBold)
                                    if(event.entity>0)TextButton(onClick={onEntity(event)}){Text(AppText.text(TextKey.CopyXemLienQuan127914),fontSize=AppTypography.Sp13)}
                                    OutlinedTextField(note,{note=it.take(1000)},Modifier.fillMaxWidth().semantics{contentDescription=AppText.text(TextKey.CopyGhiChu0E5b4bc,event.title)},
                                        placeholder={Text(AppText.text(TextKey.CopyMotDieuMuonNho200cc4),fontSize=AppTypography.Sp12)},minLines=3,maxLines=6,
                                        shape=androidx.compose.foundation.shape.RoundedCornerShape(AppDimensions.Dp14),textStyle=MaterialTheme.typography.bodySmall,
                                        colors=OutlinedTextFieldDefaults.colors(unfocusedContainerColor=androidx.compose.ui.graphics.Color.White,focusedContainerColor=androidx.compose.ui.graphics.Color.White))
                                    TextButton(onClick={onAct(EngagementAction.MemoryNote(event.id,note))},enabled=!state.busy){Text(AppText.text(TextKey.CopyLuuGhiChuE3d8bd),fontSize=AppTypography.Sp13)}
                                }
                            }
                        }
                    }
                    "SETTINGS"->SettingsContent(state,onAct,onPermission,onData={onOpen("DATA",0)},onTaskReminders={onOpen("INSIGHTS",0)})
                    "INSIGHTS"->InsightsContent(state,onAct,onAdjust,onOpen)
                    else-> {
                        GardenScreenIntro(AppText.text(TextKey.CopyChamMotChutMoiNgayE8732c),subtitle=AppText.text(TextKey.CopyMotNhipSongVuaSucTheoCachCuaBan0f4521))
                        listOf("FOCUS" to AppText.text(TextKey.CopyTapTrung964002),"TEMPLATES" to AppText.text(TextKey.CopyThemTuViecMau4e3260),"REVIEW" to AppText.text(TextKey.CopyNhinLaiTuanFf3f0c),
                            "ACHIEVEMENTS" to AppText.text(TextKey.CopyDauMocDffd66),"TIMELINE" to AppText.text(TextKey.CopyKyNiemE3e113),"INSIGHTS" to AppText.text(TextKey.CopyNhipTungThoiQuen8481ee),"SETTINGS" to AppText.text(TextKey.CopyTuyChonNhacViecA44d85)).forEach{(route,title)->
                            GardenNavigationCard(title,{onOpen(route,0)},art=when(route){"FOCUS"->GardenArt.LANTERN;"REVIEW"->GardenArt.CALENDAR;"TIMELINE"->GardenArt.JOURNAL;"TEMPLATES"->GardenArt.CHECKLIST;"ACHIEVEMENTS"->GardenArt.RIBBON;else->GardenArt.CATEGORY})
                        }
                    }
                }
            }
        }
        if(state.message.isNotBlank())MintFeedback(state.message,onDismissMessage)
        }
    }
}

@Composable
internal fun TemplateContent(state:EngagementUiState,onAct:(EngagementAction)->Unit) {
    var selected by rememberSaveable{mutableStateOf(listOf<String>())}
    val key=rememberSaveable{java.util.UUID.randomUUID().toString()}
    GardenScreenIntro(AppText.text(TextKey.CopyChonViecVuaSuc895fff))
    Text(AppText.text(TextKey.CopyThemMotLanChinhSuaBatCuLucNao55b241),
        color=Muted,fontSize=AppTypography.Sp11,lineHeight=AppTypography.Sp16)
    habitTemplates.forEach{template->
        val added=template.id in state.snapshot.templates
        TemplateCard(template,template.id in selected,added){
            selected=if(template.id in selected)selected-template.id else selected+template.id
        }
    }
    GardenPaintedButton(AppText.text(TextKey.CopyThem0ViecD0390c,selected.size),{
        onAct(EngagementAction.Templates(selected,key))
    },Modifier.fillMaxWidth(),enabled=selected.isNotEmpty() && !state.busy)
}

@Composable
private fun TemplateCard(t:HabitTemplate,selected:Boolean,added:Boolean=false,onClick:()->Unit) {
    GardenBotanicalFrame(Modifier.fillMaxWidth().heightIn(min=AppDimensions.Dp48).clickable(enabled=!added,onClick=onClick),
        skin=if(selected)GardenPanelSkin.HINT else GardenPanelSkin.BADGE) {
        Row(horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp16),verticalAlignment=Alignment.CenterVertically) {
            GardenArtwork(if(t.config.verification=="TIMER")GardenArt.LANTERN else GardenArt.SPROUT,Modifier.size(AppDimensions.Dp24))
            Column(Modifier.weight(1f)) {
                Text(t.config.title,color=Deep,fontSize=AppTypography.Sp15,lineHeight=AppTypography.Sp21,fontWeight=FontWeight.Bold)
                Text("${AppText.category(t.config.category)} · ${if(t.config.verification=="TIMER")AppText.text(TextKey.CopyBoDemC8def0) else AppText.text(TextKey.CopyCham36a8bd)} · ${t.effort}",
                    color=Muted,fontSize=AppTypography.Sp12,lineHeight=AppTypography.Sp17)
                Text(AppText.text(TextKey.Copy01Xu8a2ae4,if(t.config.frequency=="WEEKENDS")AppText.text(TextKey.CopyCuoiTuanA0fb27) else AppText.text(TextKey.CopyMoiNgay8270cb),t.config.reward),color=Green,fontSize=AppTypography.Sp12,lineHeight=AppTypography.Sp17)
                if(t.config.startMinute>=0)Text(AppText.text(TextKey.CopyKhungGio01C4abe6,clockLabel(t.config.startMinute),clockLabel(t.config.endMinute)),color=Muted,fontSize=AppTypography.Sp12,lineHeight=AppTypography.Sp17)
            }
            Checkbox(selected||added,onCheckedChange=null,enabled=!added)
        }
    }
}

@Composable
internal fun FocusContent(state:EngagementUiState,onAct:(EngagementAction)->Unit,onOpen:(String,Long)->Unit) {
    var minutes by rememberSaveable{mutableStateOf("25")}
    var attached by rememberSaveable(state.selectedTask){mutableLongStateOf(state.selectedTask)}
    val current=state.snapshot.focus.firstOrNull{it.state in setOf("RUNNING","PAUSED","BREAK")}
    GardenScreenIntro(AppText.text(TextKey.CopyTapTrung964002),AppText.text(TextKey.CopyChiMotViecThoi83ccd7), AppText.text(TextKey.CopyDanhMotKhoangChoDieuQuanTrongDfd9b6))
    if(current!=null) {
        Column(Modifier.fillMaxWidth()) {
            Column(Modifier.fillMaxWidth(),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp16)) {
                Text(current.title,color=Deep,fontSize=AppTypography.Sp14,lineHeight=AppTypography.Sp18,fontWeight=FontWeight.Bold,textAlign=TextAlign.Center)
                Box(Modifier.size(AppDimensions.Dp240),contentAlignment=Alignment.Center) {
                    CircularProgressIndicator(progress={current.activeSeconds.toFloat()/current.targetSeconds.coerceAtLeast(1)},
                        modifier=Modifier.fillMaxSize(),color=Green,strokeWidth=AppDimensions.Dp9,trackColor=AppColors.SwatchFFDDEBE1)
                    Text("${current.remaining/60}:${(current.remaining%60).toString().padStart(2,'0')}",color=Deep,fontSize=AppTypography.Sp46,lineHeight=AppTypography.Sp56,fontWeight=FontWeight.Bold)
                }
                Text(if(current.state=="BREAK")AppText.text(TextKey.CopyNghiMotChutKhongTinhThoiGianNghiA616c7) else if(current.state=="RUNNING")AppText.text(TextKey.CopyDangTapTrung1c969b) else AppText.text(TextKey.CopyDaTamDungA919ce),
                    color=Muted,fontSize=AppTypography.Sp11)
                Row(horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp16)) {
                    MintDialogAction(if(current.state=="RUNNING")AppText.text(TextKey.CopyTamDung676358) else AppText.text(TextKey.CopyTiepTuc87a7ae),
                        {onAct(EngagementAction.Focus(current.id,if(current.state=="RUNNING")"PAUSE" else "RESUME"))},
                        Modifier.weight(1f),enabled=!state.busy)
                    MintDialogAction(AppText.text(TextKey.CopyKetThuc351849),{onAct(EngagementAction.Focus(current.id,"END"))},Modifier.weight(1f),primary=false,enabled=!state.busy)
                }
                GardenExpandableSection(AppText.text(TextKey.CopyPhienDangLuuD6c8c9)){Text(AppText.text(TextKey.CopyRoiUngDungSeTamDungPhanDaLamLuonDuocLuu381f0a),color=Muted,fontSize=AppTypography.Sp12,lineHeight=AppTypography.Sp17)}
            }
        }
    } else {
        Box(Modifier.fillMaxWidth().padding(vertical=AppDimensions.Dp4),contentAlignment=Alignment.Center) {
            Box(Modifier.size(AppDimensions.Dp240),contentAlignment=Alignment.Center) {
                CircularProgressIndicator(progress={0f},modifier=Modifier.fillMaxSize(),color=Green,strokeWidth=AppDimensions.Dp9,trackColor=AppColors.SwatchFFDFE7D1)
                Column(horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp8)) {
                    Text("${minutes.toIntOrNull() ?: 0}:00",color=Deep,fontSize=AppTypography.Sp46,lineHeight=AppTypography.Sp56,fontWeight=FontWeight.SemiBold)
                    Text(AppText.text(TextKey.CopyBatDauKhiBanSanSang3d0a9a),color=Muted,fontSize=AppTypography.Sp11)
                }
            }
        }
        FlowRow(horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp4)) {
            listOf(15,25,45,60).forEach{n->FilterChip(minutes==n.toString(),{minutes=n.toString()},label={Text(AppText.text(TextKey.Copy0Phut2596d2,n),fontSize=AppTypography.Sp11)})}
        }
        GardenTextField(minutes,{minutes=it.filter(Char::isDigit).take(3)},AppText.text(TextKey.CopyPhutTapTrung459fbd),placeholder=AppText.text(TextKey.Copy5120Phut0c4b29))
        Text(AppText.text(TextKey.CopyDanhChoFf565f),color=Muted,fontSize=AppTypography.Sp12)
        FilterChip(attached==0L,{attached=0},label={Text(AppText.text(TextKey.CopyTuDoKhongThuongXu113e54),fontSize=AppTypography.Sp12)})
        state.tasks.filter{it.config.verification=="TIMER" && it.lifecycle=="ACTIVE" && !it.doneToday && !it.finished}.forEach{task->
            FilterChip(attached==task.id,{attached=task.id},enabled=task.available,label={Text(task.title,fontSize=AppTypography.Sp11)})
        }
        GardenPaintedButton(AppText.text(TextKey.CopyBatDauB3cacc),{
            onAct(EngagementAction.StartFocus(attached,minutes.toIntOrNull() ?: 0,java.util.UUID.randomUUID().toString()))
        },Modifier.fillMaxWidth(),enabled=!state.busy && (minutes.toIntOrNull() ?: 0) in 5..120)
    }
    GardenExpandableSection(AppText.text(TextKey.CopyCacPhienDaLuu0HoanThanhBb1ef4,state.snapshot.focusCompleted)) {
    Text(AppText.text(TextKey.CopyDaHoanThanh0Phien3aedba,state.snapshot.focusCompleted),color=Deep,fontSize=AppTypography.Sp12,fontWeight=FontWeight.Bold)
    state.snapshot.focus.take(10).forEach{s->
        Text(AppText.text(TextKey.Copy012Phut31cab4a,s.date,s.activeSeconds/60,s.targetSeconds/60,when(s.state){"COMPLETE"->AppText.text(TextKey.CopyTronVen2a4501);"ENDED"->AppText.text(TextKey.CopyDaLuuPhanDaLam0f9d18);"RUNNING"->AppText.text(TextKey.CopyDangChayCd07e6);else->AppText.text(TextKey.TabPaused)}),
            color=Muted,fontSize=AppTypography.Sp13,lineHeight=AppTypography.Sp19)
    }
    }
}

@Composable
internal fun ReviewContent(state:EngagementUiState,onAct:(EngagementAction)->Unit) {
    val r=state.snapshot.review ?: return
    var note by rememberSaveable(r.start,r.reflection){mutableStateOf(r.reflection)}
    GardenScreenIntro(AppText.text(TextKey.CopyTuanCuaBanC82d43),AppText.text(TextKey.CopyTuanTu055af46,r.start))
    MintMetricStrip(listOf(AppText.text(TextKey.CopyHoanThanh5eb36f) to "${r.completed}/${r.scheduled}",AppText.text(TextKey.CopyTyLeE86c12) to (r.rate?.let{"$it%"} ?: "—"),
        AppText.text(TextKey.CopyTapTrung964002) to AppText.text(TextKey.Copy0Phut2596d2,r.focusMinutes)))
    Text(if(r.previousRate==null)AppText.text(TextKey.CopyChuaDuDuLieuDeSoSanhTuanTruoc63f9c2)
        else AppText.text(TextKey.CopyTuanTruoc0SoLieuDenHomNay1201e0,r.previousRate),color=Muted,fontSize=AppTypography.Sp11,lineHeight=AppTypography.Sp16)
    GardenExpandableSection(AppText.text(TextKey.CopyNhungDieuDaLam9f52b2)) {
        Column(verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp12)) {
            Text(AppText.text(TextKey.CopyThu0XuChi1XuD34d57,r.earned,r.spent),color=Deep,fontSize=AppTypography.Sp12)
            Text(AppText.text(TextKey.Copy0NgayChamCay1MocTruongThanhEb3af0,r.careDays,r.milestones),color=Deep,fontSize=AppTypography.Sp11)
            Text(if(r.consistent.isBlank())AppText.text(TextKey.CopyChuaDuDuLieuVeNhomThoiQuen2068bb) else AppText.text(TextKey.CopyDeuDanNhat0B18f80,r.consistent),color=Muted,fontSize=AppTypography.Sp11)
            if(r.improve.isNotBlank())Text(AppText.text(TextKey.CopyCoTheDieuChinhNhe0C674c2,r.improve),color=Muted,fontSize=AppTypography.Sp11)
        }
    }
    Text(AppText.text(TextKey.CopyDieuGiKhienTuanNayDeDangHon72fdd9),color=Deep,fontSize=AppTypography.Sp12,fontWeight=FontWeight.Bold)
    GardenTextField(note,{note=it.take(2000)},AppText.text(TextKey.CopySuyNgamTuan75229e),placeholder=AppText.text(TextKey.CopyGhiLaiChoRiengBanDa0345))
    GardenPaintedButton(AppText.text(TextKey.CopyLuuSuyNgam18cdd3),{onAct(EngagementAction.Reflection(r.start,note))},Modifier.fillMaxWidth(),enabled=!state.busy)
}

@Composable
private fun RestContent(state:EngagementUiState,onAct:(EngagementAction)->Unit) {
    var rest by rememberSaveable(state.snapshot.tomorrow){mutableStateOf(state.snapshot.tomorrow)}
    var protect by rememberSaveable{mutableStateOf("")}
    GardenScreenIntro(AppText.text(TextKey.CopyNghiCungLaMotNhipE4f3b6))
    DayCalendar(state.snapshot.days)
    Text(AppText.text(TextKey.CopyNgayNghiSapToiA8efda),color=Deep,fontSize=AppTypography.Sp12)
    GardenTextField(rest,{rest=it},AppText.text(TextKey.CopyNgayNghiYyyyMmDd2d6975))
    Text(AppText.text(TextKey.CopyToiDa2NgayThangDatTruocNgayNghiKhongThuong8e36f0),color=Muted,fontSize=AppTypography.Sp12,lineHeight=AppTypography.Sp18)
    GardenPaintedButton(AppText.text(TextKey.CopyLenLichNghiD71876),{onAct(EngagementAction.Rest(rest))},Modifier.fillMaxWidth(),enabled=!state.busy)
    Text(AppText.text(TextKey.CopyVeBaoVe5a852e),color=Deep,fontSize=AppTypography.Sp18,fontWeight=FontWeight.SemiBold)
    Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.SpaceBetween){
        MintStatusPill(AppText.text(TextKey.Copy0Ve604049,state.snapshot.protectionTickets))
        GardenPaintedButton(AppText.text(TextKey.CopyMua250XuAa5fe3),{onAct(EngagementAction.BuyProtection(java.util.UUID.randomUUID().toString()))},primary=false,enabled=!state.busy)
    }
    GardenTextField(protect,{protect=it},AppText.text(TextKey.CopyNgayDaBoLoYyyyMmDdD52a2a),placeholder=AppText.text(TextKey.DateIsoPlaceholder))
    GardenPaintedButton(AppText.text(TextKey.CopyDungVe767d96),{onAct(EngagementAction.Protect(protect,java.util.UUID.randomUUID().toString()))},Modifier.fillMaxWidth(),primary=false,enabled=!state.busy&&state.snapshot.protectionTickets>0)
}

@Composable
internal fun InsightsContent(state:EngagementUiState,onAct:(EngagementAction)->Unit,onAdjust:(Task)->Unit,onOpen:(String,Long)->Unit) {
    val insight=state.snapshot.insights.firstOrNull{it.taskId==state.selectedTask}
    if(insight==null) {
        GardenScreenIntro(AppText.text(TextKey.CopyMoiNgayMotChutF24459),AppText.text(TextKey.CopyNhipThoiQuen9c03a7))
        state.snapshot.insights.forEach{i->GardenNavigationCard(i.title,{onOpen("INSIGHTS",i.taskId)},art=GardenArt.CALENDAR)}
        if(state.snapshot.insights.isEmpty())Text(AppText.text(TextKey.CopyThemMotViecDeBatDauGhiLaiNhipThoiQuen5bc1b9),color=Muted,fontSize=AppTypography.Sp12)
        return
    }
    val task=state.tasks.firstOrNull{it.id==insight.taskId} ?: return
    var days by rememberSaveable{mutableIntStateOf(7)}
    var confirm by remember{mutableStateOf(false)}
    GardenScreenIntro(insight.title)
    Row(horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp12)) {
        listOf(7,30).forEach{n->FilterChip(days==n,{days=n},label={Text(AppText.text(TextKey.Copy0Ngay45fa48,n),fontSize=AppTypography.Sp11)})}
    }
    DayCalendar(insight.days.takeLast(days))
    MintMetricStrip(listOf(AppText.text(TextKey.CopyHienTai027390) to "${insight.streak}",AppText.text(TextKey.CopyDaiNhat67c21e) to "${insight.longest}",
        AppText.text(TextKey.CopyTyLe30Ngay84d03d) to (insight.rate?.let{"$it%"} ?: "—")))
    GardenExpandableSection(AppText.text(TextKey.CopyTapTrungGoiY2462a6)) {
    Text(AppText.text(TextKey.CopyTapTrung0PhutGioThuongHoanThanh1E3e85d,insight.focusMinutes,insight.commonHour.ifBlank{AppText.text(TextKey.CopyChuaDuDuLieu348596)}),
        color=Muted,fontSize=AppTypography.Sp11,lineHeight=AppTypography.Sp16)
    if(insight.suggestion.isNotBlank()) {
        Text(insight.suggestion,color=Muted,fontSize=AppTypography.Sp11)
        GardenPaintedButton(AppText.text(TextKey.CopyXemDieuChinhA56d7a),{confirm=true},Modifier.fillMaxWidth(),primary=false)
    }
    }
    val pref=state.snapshot.reminders.firstOrNull{it.taskId==task.id} ?: ReminderPreference(task.id)
    var minute by rememberSaveable(task.id,pref.minute){mutableStateOf(clockLabel(pref.minute))}
    Text(AppText.text(TextKey.CopyNhacViec9e9055),color=Deep,fontSize=AppTypography.Sp12,fontWeight=FontWeight.Bold)
    Text(AppText.text(TextKey.CopyMacDinhTatBatQuyenVaNhacToanCucTrongTuyCh703a98),color=Muted,fontSize=AppTypography.Sp11,lineHeight=AppTypography.Sp16)
    GardenTextField(minute,{minute=it},AppText.text(TextKey.CopyGioNhacHhMmAb3321))
    TextButton(onClick={parseMinute(minute)?.let{onAct(EngagementAction.Reminder(pref.copy(minute=it)))}}){Text(AppText.text(TextKey.CopyLuuGioNhacBc709c),fontSize=AppTypography.Sp11)}
    FlowRow(horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp4)) {
        listOf("OFF" to AppText.text(TextKey.CopyTat328606),"AT" to AppText.text(TextKey.CopyTheoGio4bd1ce),"BEFORE" to AppText.text(TextKey.CopyTruocKhungGioC97c51)).forEach{(mode,label)->
            FilterChip(pref.mode==mode,{onAct(EngagementAction.Reminder(pref.copy(mode=mode,minute=parseMinute(minute) ?: pref.minute)))},
                label={Text(label,fontSize=AppTypography.Sp11)})
        }
    }
    TextButton(onClick={onAct(EngagementAction.Reminder(pref.copy(followUp=!pref.followUp)))}) {
        Text(if(pref.followUp)AppText.text(TextKey.CopyMotLanNhacNheNeuChuaXong1e2491) else AppText.text(TextKey.CopyThemMotLanNhacNhe2b429d),fontSize=AppTypography.Sp11)
    }
    if(confirm)MintDialog(AppText.text(TextKey.CopyDieuChinhLich513357),{confirm=false},AppText.text(TextKey.CopyXacNhan1503b0),{onAdjust(task);confirm=false}) {
        Text(AppText.text(TextKey.CopyChuyenSangNgayThuongTuNgayMaiLichSuVaKeHoEbd07c),color=Muted,fontSize=AppTypography.Sp12,lineHeight=AppTypography.Sp17)
    }
}

internal fun parseMinute(s:String):Int? {
    val p=s.split(':');if(p.size!=2)return null
    val h=p[0].toIntOrNull() ?: return null;val m=p[1].toIntOrNull() ?: return null
    return if(h in 0..23 && m in 0..59)h*60+m else null
}
@Composable
internal fun DayCalendar(days:List<ReviewDay>) {
    GardenBotanicalFrame(Modifier.fillMaxWidth(),contentPadding=PaddingValues(AppDimensions.Dp14),cornerSize=AppDimensions.Dp20){
        Column(verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp12)){
    Column(verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp6)) {
        days.chunked(7).forEach{week->Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp6)) {
            week.forEach{d->
                Surface(Modifier.weight(1f).heightIn(min=AppDimensions.Dp36).semantics(mergeDescendants=true){contentDescription="${d.date}: ${when(d.state){"SUCCESS"->AppText.text(TextKey.CopyHoanThanh5eb36f);"REST"->AppText.text(TextKey.CopyNghiF0aa0a);"PROTECTED"->AppText.text(TextKey.CopyDaBaoVeF1dfce);"MISSED"->AppText.text(TextKey.CopyBoLoF8cf8f);else->AppText.text(TextKey.CopyChuaGhiNhanF7654d)}}"},shape=androidx.compose.foundation.shape.RoundedCornerShape(AppDimensions.Dp10),
                    color=when(d.state){"SUCCESS"->Green;"REST"->AppColors.SwatchFFE5DFF6;"PROTECTED"->Gold;else->AppColors.SwatchFFF0F4F1}) {
                    Text(d.date.takeLast(2)+when(d.state){"SUCCESS"->"✓";"REST"->" N";"PROTECTED"->" ◇";"MISSED"->" ·";else->""},Modifier.padding(vertical=AppDimensions.Dp10),textAlign=TextAlign.Center,color=if(d.state=="SUCCESS")androidx.compose.ui.graphics.Color.White else Deep,fontSize=AppTypography.Sp10)
                }
            }
            repeat(7-week.size){Spacer(Modifier.weight(1f))}
        }}
    }
    Text(AppText.text(TextKey.CopyHoanThanhNNghiBaoVeChamDaBoLoEc2735),color=Muted,fontSize=AppTypography.Sp12,lineHeight=AppTypography.Sp17)
        }
    }
}

@Preview(name="Engagement components · full review",widthDp=360,heightDp=1000,
    device="spec:width=360dp,height=1000dp,dpi=160")
@Composable
fun EngagementComponentsPreview() {
    MamXanhPreviewTheme {
        ProvideTextStyle(MaterialTheme.typography.bodySmall.copy(fontSize=AppTypography.Sp15,lineHeight=AppTypography.Sp21)) {
            Column(Modifier.fillMaxWidth().padding(AppDimensions.Dp12),verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp16)) {
                ReviewContent(EngagementUiState(snapshot=EngagementSnapshot(
                    review=WeeklyReview("2026-11-09",5,3,null,25,24,0,2,0,AppText.canonical(TextKey.CopyHocTapA51516),"",""),
                    days=listOf(ReviewDay("2026-11-10","REST")),tomorrow="2026-11-13")), {})
            }
        }
    }
}

@Preview(widthDp=414,heightDp=1000,device="spec:width=414dp,height=1000dp,dpi=160")
@Composable
fun FocusWebDesignPreview(){com.mamxanh.app.presentation.ui.MamXanhPreviewTheme{MintBackdrop{Column(Modifier.padding(AppDimensions.Dp24),verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp18)){FocusContent(EngagementUiState(loading=false,snapshot=EngagementSnapshot(focus=listOf(FocusSession("preview",0,AppText.text(TextKey.CopyMotKhoangChoRiengMinhCcf66e),"2026-11-16",1500,750,"PAUSED")))),{},{_,_->})}}}}


@Preview(widthDp=414,heightDp=1100,device="spec:width=414dp,height=1100dp,dpi=160")
@Composable fun FocusIdleWebDesignPreview(){com.mamxanh.app.presentation.ui.MamXanhPreviewTheme{MintBackdrop{Column(Modifier.padding(AppDimensions.Dp24),verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp18)){FocusContent(EngagementUiState(loading=false),{},{_,_->})}}}}

private fun webEngagementState(route:String)=EngagementUiState(loading=false,route=route,tasks=listOf(Task(1,AppText.text(TextKey.CopyDoc10TrangSachA3c873),"HABIT",AppText.canonical(TextKey.CopyHocTapA51516),8,"EVERY_DAY","","",4,"")),snapshot=EngagementSnapshot(
 insights=listOf(TaskInsight(1,AppText.text(TextKey.CopyDoc10TrangSachA3c873),(1..7).map{ReviewDay("2026-11-${it.toString().padStart(2,'0')}",if(it==3) "MISSED" else "SUCCESS")},86,4,6,"20:30",45,AppText.text(TextKey.CopyChonMotKhungGioVuaSucD8a536))),
 review=WeeklyReview("2026-11-09",15,10,null,45,40,0,3,0,AppText.canonical(TextKey.CopyHocTapA51516),AppText.canonical(TextKey.CopyNepSong8d1a98),""),
 achievements=listOf(Achievement("first",AppText.text(TextKey.CopyViecDauTien28bcbf),AppText.text(TextKey.CopyHoanThanhMotViec1c20b2),1,1,1),Achievement("three",AppText.text(TextKey.CopyBaNgayXanh0c655e),AppText.text(TextKey.CopyGiuNhipBaNgayB90ddb),2,3)),
 memories=listOf(MemoryEvent("first","TASK",1,AppText.text(TextKey.CopyViecDauTienDaXongB1f060),1794819600000)),
 days=(1..12).map{ReviewDay("2026-11-${it.toString().padStart(2,'0')}",if(it%3==0) "REST" else "SUCCESS")},tomorrow="2026-11-17"))

@Preview(widthDp=414,heightDp=1400,device="spec:width=414dp,height=1400dp,dpi=160")
@Composable fun TemplatesWebDesignPreview(){MamXanhPreviewTheme{EngagementScreen(webEngagementState("TEMPLATES"),{_,_->},{},{},{},{},{})}}

@Preview(widthDp=414,heightDp=1400,device="spec:width=414dp,height=1400dp,dpi=160")
@Composable fun ReviewWebDesignPreview(){MamXanhPreviewTheme{EngagementScreen(webEngagementState("REVIEW"),{_,_->},{},{},{},{},{})}}

@Preview(widthDp=414,heightDp=1400,device="spec:width=414dp,height=1400dp,dpi=160")
@Composable fun RestWebDesignPreview(){MamXanhPreviewTheme{EngagementScreen(webEngagementState("REST"),{_,_->},{},{},{},{},{})}}

@Preview(widthDp=414,heightDp=1400,device="spec:width=414dp,height=1400dp,dpi=160")
@Composable fun AchievementsWebDesignPreview(){MamXanhPreviewTheme{EngagementScreen(webEngagementState("ACHIEVEMENTS"),{_,_->},{},{},{},{},{})}}

@Preview(widthDp=414,heightDp=1400,device="spec:width=414dp,height=1400dp,dpi=160")
@Composable fun TimelineWebDesignPreview(){MamXanhPreviewTheme{EngagementScreen(webEngagementState("TIMELINE"),{_,_->},{},{},{},{},{})}}

@Preview(widthDp=414,heightDp=1400,device="spec:width=414dp,height=1400dp,dpi=160")
@Composable fun InsightsWebDesignPreview(){MamXanhPreviewTheme{EngagementScreen(webEngagementState("INSIGHTS"),{_,_->},{},{},{},{},{})}}

@Preview(widthDp=414,heightDp=1400,device="spec:width=414dp,height=1400dp,dpi=160")
@Composable fun InsightDetailWebDesignPreview(){MamXanhPreviewTheme{EngagementScreen(webEngagementState("INSIGHTS").copy(selectedTask=1),{_,_->},{},{},{},{},{})}}

@Preview(widthDp=414,heightDp=1000,device="spec:width=414dp,height=1000dp,dpi=160")
@Composable fun MoreWebDesignPreview(){MamXanhPreviewTheme{EngagementScreen(webEngagementState("MORE"),{_,_->},{},{},{},{},{})}}
