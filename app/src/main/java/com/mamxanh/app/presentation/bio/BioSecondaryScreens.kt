package com.mamxanh.app.presentation.bio

import com.mamxanh.app.presentation.ui.design.AppColors
import com.mamxanh.app.presentation.ui.design.AppDimensions
import com.mamxanh.app.presentation.ui.design.AppIcons
import com.mamxanh.app.presentation.ui.design.AppRadii
import com.mamxanh.app.presentation.ui.design.AppSpacing
import com.mamxanh.app.presentation.ui.design.AppTypography

import com.mamxanh.shared.localization.TextKey

import com.mamxanh.shared.localization.AppText

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import org.json.JSONObject
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.*

internal fun bioDates(date:String,count:Int):List<String>{val format=SimpleDateFormat("yyyy-MM-dd",Locale.US);val c=Calendar.getInstance();try{c.time=format.parse(date)!!}catch(_:Exception){};c.add(Calendar.DATE,1-count);return (0 until count).map{format.format(c.time).also{c.add(Calendar.DATE,1)}}}
@Composable internal fun BioCalendar(world:BioWorld,count:Int=7,recordOverride:JSONArray?=null){
    val explicit=recordOverride?:world.data.optJSONArray("days")
    val dates=bioDates(world.data.s("date"),count)
    val values=dates.mapIndexed{i,date->val explicitRecord=explicit?.objects()?.firstOrNull{it.s("date")==date};val state=explicitRecord?.s("state")?:if(explicit!=null||world.native)"NONE"else if(i%7==3)"REST"else if(i%9==5)"PROTECTED"else if(i%3!=1)"SUCCESS"else "MISSED";date to state}
    Column(verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp8)){
        Bt(bioDisplayDate(dates.first())+" – "+bioDisplayDate(dates.last()),size=AppTypography.Size12,tint=bp.muted)
        Row(horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp6)){dates.take(7).forEach{date->val calendar=Calendar.getInstance().apply{time=SimpleDateFormat("yyyy-MM-dd",Locale.US).parse(date)!!};val day=calendar.get(Calendar.DAY_OF_WEEK);Bt(bioWeekday(day),Modifier.weight(1f),size=AppTypography.Size11,tint=bp.muted,align=TextAlign.Center)}}
        values.chunked(7).forEach{row->Row(horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp6)){repeat(7){i->if(i<row.size){val(date,state)=row[i];val fill=when(state){"SUCCESS"->bp.deep;"REST"->AppColors.SwatchFFE7E1F5;"PROTECTED"->AppColors.RewardSurface;else->AppColors.SwatchFFF0F2EB};Column(Modifier.weight(1f).heightIn(min=AppDimensions.Dp44).background(fill,RoundedCornerShape(AppDimensions.Dp10)).padding(vertical=AppDimensions.Dp4),horizontalAlignment=Alignment.CenterHorizontally){Bt(date.takeLast(2),size=AppTypography.Size12,tint=if(state=="SUCCESS")AppColors.White else bp.ink,line=AppTypography.LineHeight16);Bt(when(state){"SUCCESS"->"✓";"REST"->AppText.text(TextKey.CalendarRestMarker);"PROTECTED"->"◇";"MISSED"->"×";else->"—"},size=AppTypography.Size10,tint=if(state=="SUCCESS")AppColors.White else bp.muted,line=AppTypography.LineHeight14)}}else Spacer(Modifier.weight(1f))}}}
        Bt(AppText.text(TextKey.CopyXongNNghiBaoVeC04e4f),size=AppTypography.Size11,tint=bp.muted)
        Bt(AppText.text(TextKey.CopyBoLoChuaGhiNhan70b04e),size=AppTypography.Size11,tint=bp.muted)
    }
}
@Composable internal fun ColumnScope.BioReview(world:BioWorld,ui:BioController){
    val review=world.data.o("review")
    if(world.native&&review.length()==0){BioEmpty(AppText.text(TextKey.CopyChuaCoTongKet002b13),AppText.text(TextKey.CopySoLieuSeXuatHienKhiCoHoatDong43a1f5),"calendar",AppText.text(TextKey.CopyXemViec463e95),{ui.go("tasks")});return}
    var note by rememberSaveable{mutableStateOf(review.s("reflection",world.state.s("reflection")))}
    Bt(AppText.text(TextKey.CopyTuanTuA3719d)+bioDisplayDate(review.s("start","2026-11-16")),size=AppTypography.Size11,mono=true,tint=bp.muted,spacing=AppTypography.Tracking1_2);Bt(AppText.text(TextKey.CopyTuanCuaBanC82d43),size=AppTypography.Size27,bold=true,spacing=AppTypography.TrackingMinus1)
    BioMetrics(listOf(AppText.text(TextKey.CopyHoanThanh5eb36f) to "${review.i("completed")}/${review.i("scheduled")}",AppText.text(TextKey.CopyTyLeE86c12) to if(review.i("scheduled")>0)"${review.i("completed")*100/review.i("scheduled")}%"else "—",AppText.text(TextKey.CopyTapTrung964002) to "${review.i("focusMinutes")}′"))
    BioCard(Modifier.fillMaxWidth()){Bt(AppText.text(TextKey.Copy7NgayGanNhat442aa3),size=AppTypography.Size13,semi=true);Spacer(Modifier.height(AppDimensions.Dp8));BioBars(world);Spacer(Modifier.height(AppDimensions.Dp14));Bt(if(review.has("previousRate"))AppText.text(TextKey.CopyTuanTruoc0SoLieuDenHomNay87b0c8,review.i("previousRate"))else AppText.text(TextKey.CopyChuaDuDuLieuSoSanh24d4ce),size=AppTypography.Size12,tint=bp.muted)}
    BioDetails(AppText.text(TextKey.CopyNhungDieuDaLam9f52b2)){BioFacts(listOf(AppText.text(TextKey.CopyXuNhanChi548b91) to "${review.i("earned")} / ${review.i("spent")}",AppText.text(TextKey.CopyChamCayFff511) to AppText.text(TextKey.Copy0Ngay45fa48,review.i("careDays")),AppText.text(TextKey.CopyMocTruongThanhB6cf90) to review.i("milestones").toString(),AppText.text(TextKey.CopyDeuDanNhat2e2e31) to AppText.category(review.s("consistent",AppText.text(TextKey.CopyChuaDuDuLieu414142))),AppText.text(TextKey.CopyCoTheDieuChinhC73bca) to AppText.category(review.s("improve",AppText.text(TextKey.CopyChuaDuDuLieu414142)))))}
    BioField(AppText.text(TextKey.CopyMotDieuMuonNho455aad),note,{note=it.take(2000)},multiline=true,placeholder=AppText.text(TextKey.CopyTuanNayMinhE6de5c))
    BioButton(AppText.text(TextKey.CopyLuuSuyNgam18cdd3),{ui.send("reflection",mapOf("note" to note,"week" to review.s("start")))},Modifier.fillMaxWidth())
    BioLink(AppText.text(TextKey.CopyLichNghiBaoVe8eda57),"moon",{ui.go("rest")},AppText.text(TextKey.Copy0VeBaoVe8bf912,world.state.i("tickets")))
}
@Composable internal fun ColumnScope.BioRest(world:BioWorld,ui:BioController){
    var restDate by rememberSaveable{mutableStateOf(world.data.s("tomorrow","2026-11-18"))};var protectDate by rememberSaveable{mutableStateOf("")}
    Bt(AppText.text(TextKey.CopyNghiCungLaMotNhipE4f3b6),size=AppTypography.Size27,bold=true,spacing=AppTypography.TrackingMinus1);BioCard(Modifier.fillMaxWidth()){BioCalendar(world,30)}
    BioDateField(AppText.text(TextKey.CopyNgayNghiSapToiA8efda),restDate,{restDate=it},futureOnly=true);Bt(AppText.text(TextKey.CopyToiDa2NgayThangDatTruocNgayNghiKhongThuong8811c2),size=AppTypography.Size12,tint=bp.muted)
    BioButton(AppText.text(TextKey.CopyLenLichNghiD71876),{ui.send("rest",mapOf("date" to restDate))},Modifier.fillMaxWidth())
    val rest=world.state.a("rest").strings();if(rest.isNotEmpty())BioCard(Modifier.fillMaxWidth()){BioFacts(rest.map{AppText.text(TextKey.CopyNghiF0aa0a) to it})}
    BioSection(AppText.text(TextKey.CopyVeBaoVe5a852e));Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.SpaceBetween){BioTag(AppText.text(TextKey.Copy0Ve604049,world.state.i("tickets")),"award");BioButton(AppText.text(TextKey.CopyMua250XuAa5fe3),{ui.show("ticket")},secondary=true,enabled=world.coins>=250)}
    BioDateField(AppText.text(TextKey.CopyNgayDaBoLo9f55ee),protectDate,{protectDate=it});BioButton(AppText.text(TextKey.CopyDungVe767d96),{ui.send("protect",mapOf("date" to protectDate))},Modifier.fillMaxWidth(),enabled=world.state.i("tickets")>0&&protectDate.isNotBlank())
}
@Composable internal fun ColumnScope.BioInsights(world:BioWorld,ui:BioController){
    val task=world.task(ui.param)
    if(task==null){Bt(AppText.text(TextKey.CopyNhipThoiQuen1b8324),size=AppTypography.Size27,bold=true,spacing=AppTypography.TrackingMinus1);if(world.tasks.isEmpty())BioEmpty(AppText.text(TextKey.CopyChuaCoNhipThoiQuenC06c83),AppText.text(TextKey.CopyThemViecDeBatDauE6548a),"chart",AppText.text(TextKey.CopyThemViecC371c2),{ui.go("task-new")})else Column(verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp12)){world.tasks.forEach{BioLink(it.s("title"),"chart",{ui.go("insights",it.s("id"))})}};return}
    val supplied=world.data.a("insights").objects().firstOrNull{it.s("taskId")==task.s("id")};val insight=supplied?:json("longest" to 7,"rate" to 72,"focusMinutes" to 125,"commonHour" to "19:00","suggestion" to AppText.text(TextKey.CopyDieuChinhSangNgayThuong939cdd))
    Bt(task.s("title"),size=AppTypography.Size27,bold=true,spacing=AppTypography.TrackingMinus1);val period=ui.tabs["insightDays"]?:"7";BioTabs(listOf("7" to AppText.text(TextKey.Copy7NgayFc11cc),"30" to AppText.text(TextKey.Copy30NgayB37f06)),period){ui.tabs["insightDays"]=it}
    BioCard(Modifier.fillMaxWidth()){BioCalendar(world,period.toInt(),insight.optJSONArray("days"))}
    BioMetrics(listOf(AppText.text(TextKey.CopyChuoiHienTai460755) to task.i("streak").toString(),AppText.text(TextKey.CopyDaiNhat67c21e) to insight.i("longest").toString(),AppText.text(TextKey.CopyTyLe30Ngay84d03d) to if(insight.has("rate"))"${insight.i("rate")}%"else "—"))
    BioDetails(AppText.text(TextKey.CopyTapTrungGoiY2462a6)){BioFacts(listOf(AppText.text(TextKey.CopyTapTrung964002) to AppText.text(TextKey.Copy0Phut2596d2,insight.i("focusMinutes")),AppText.text(TextKey.CopyGioThuongXong450100) to insight.s("commonHour",AppText.text(TextKey.CopyChuaDuDuLieu414142))));if(insight.s("suggestion").isNotBlank())BioButton(insight.s("suggestion"),{ui.show("adjust",task.s("id"))},secondary=true)}
    val reminder=world.state.o("reminders").o(task.s("id"));var minute by rememberSaveable(task.s("id")){mutableStateOf(reminder.s("minute","09:00"))};var mode by rememberSaveable(task.s("id")){mutableStateOf(reminder.s("mode","OFF"))};var follow by rememberSaveable(task.s("id")){mutableStateOf(reminder.b("followUp"))}
    Bt(AppText.text(TextKey.CopyNhacViec9e9055),size=AppTypography.Size17,bold=true);BioTimeField(AppText.text(TextKey.CopyGioNhacD9dac9),minute,{minute=it});BioSelect(AppText.text(TextKey.CopyKhiNaoB5af31),listOf("OFF" to AppText.text(TextKey.CopyTat328606),"AT" to AppText.text(TextKey.CopyTheoGio4bd1ce),"BEFORE" to AppText.text(TextKey.CopyTruocKhungGioC97c51)),mode,onSelect={mode=it});BioToggle(AppText.text(TextKey.CopyNhacNheThemMotLan544e8e),follow){follow=it};BioButton(AppText.text(TextKey.CopyLuuNhacViecDe3aa9),{ui.send("reminder",mapOf("id" to task.s("id"),"minute" to minute,"mode" to mode,"followUp" to follow.toString()))},Modifier.fillMaxWidth());BioLink(AppText.text(TextKey.CopyQuyenNhacToanCuc8225b7),"bell",{ui.go("settings")})
}
@Composable internal fun ColumnScope.BioAchievements(world:BioWorld,ui:BioController){
    Bt(AppText.text(TextKey.CopyTungDauMocNhoA0b175),size=AppTypography.Size27,bold=true,spacing=AppTypography.TrackingMinus1);Bt(AppText.text(TextKey.CopyGhiNhanHanhTrinhKhongThuongXu061899),size=AppTypography.Size12,tint=bp.muted)
    val defaults=listOf(Triple(AppText.text(TextKey.CopyBuocDauTien5dbfda),AppText.text(TextKey.CopyHoanThanhMotViecHopLe912065),1 to 1),Triple(AppText.text(TextKey.CopyBaNgayTotLanhD6a627),AppText.text(TextKey.Copy3NgayCoLichHoanThanh0527b6),3 to 3),Triple(AppText.text(TextKey.CopyMotTuanDeuDanD7d5ad),AppText.text(TextKey.Copy7NgayCoLichHoanThanh86a291),5 to 7),Triple(AppText.text(TextKey.CopyKhoiDauTapTrungC3b5b9),AppText.text(TextKey.CopyHoanThanhMotPhienItNhat25Phut6bcfa2),0 to 1),Triple(AppText.text(TextKey.CopyNhipHocTapF10229),AppText.text(TextKey.Copy10LanHoanThanhViecHocDcd041),6 to 10),Triple(AppText.text(TextKey.CopyBanTayXanh9bd1dc),AppText.text(TextKey.CopyTuoiCayTrong10NgayKhacNhau786a07),3 to 10),Triple(AppText.text(TextKey.CopyBongHoaDauTien4d67d1),AppText.text(TextKey.CopyMotLoaiHoaDatGiaiDoanCuoi468527),0 to 1),Triple(AppText.text(TextKey.CopyCayTruongThanhDauTienF9a6e3),AppText.text(TextKey.CopyMotLoaiKhongHoaDatGiaiDoanCuoi120850),1 to 1),Triple(AppText.text(TextKey.CopyGocVuonDaDang8a3813),AppText.text(TextKey.CopyDangSoHuu3LoaiKhacNhauA27bf0),3 to 3),Triple(AppText.text(TextKey.CopyThangDeuDanD8b460),AppText.text(TextKey.Copy20NgayCoLichHoanThanh4b7346),5 to 20))
    val supplied=world.data.a("achievements").objects();val entries=if(world.native)supplied else defaults.map{(t,c,v)->json("title" to t,"criterion" to c,"progress" to v.first,"target" to v.second)}
    Column(verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp12)){entries.forEach{item->val n=item.i("progress");val max=item.i("target",1);val unlocked=item.l("unlockedAt")>0||n>=max
        BioCard(Modifier.fillMaxWidth(),padding=AppSpacing.Value18){Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp12)){Box(Modifier.size(AppDimensions.Dp48).background(if(unlocked)AppColors.RewardSurface else AppColors.SwatchFFF0F0F0,RoundedCornerShape(50)),contentAlignment=Alignment.Center){Bi(if(unlocked)"award"else "lock",size=AppIcons.Size22,tint=if(unlocked)AppColors.SwatchFF947A30 else bp.muted)};Column(Modifier.weight(1f)){Bt(item.s("title"),size=AppTypography.Size14,semi=true);Bt(item.s("criterion"),size=AppTypography.Size10,tint=bp.muted);Spacer(Modifier.height(AppDimensions.Dp6));Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp10)){BioProgress(n.toFloat()/max,Modifier.weight(1f));Bt("${minOf(n,max)}/$max"+if(unlocked)AppText.text(TextKey.CopyDaGhiNhanEbd27e)else "",size=AppTypography.Size11,tint=bp.muted)}}}}
    }}
}
@Composable internal fun ColumnScope.BioTimeline(world:BioWorld,ui:BioController){
    Bt(AppText.text(TextKey.CopyDieuMinhMuonNho665bc8),size=AppTypography.Size27,bold=true,spacing=AppTypography.TrackingMinus1)
    val memories=world.state.a("memories").objects()
    val timelineLine=bp.line;val timelineDot=bp.deep
    if(memories.isEmpty())BioEmpty(AppText.text(TextKey.CopyChuaCoKyNiemDa130b),AppText.text(TextKey.CopyNhungViecBanLamSeXuatHienODay735541),"book")else Column(Modifier.fillMaxWidth().padding(start=AppDimensions.Dp8).drawBehind{drawLine(timelineLine,Offset.Zero,Offset(0f,size.height),AppDimensions.Dp1.toPx())}.padding(start=AppDimensions.Dp20),verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp28)){
        memories.forEach{m->var note by rememberSaveable(m.s("id")){mutableStateOf(world.state.o("notes").s(m.s("id"),m.s("note")))}
            Column(Modifier.drawBehind{drawCircle(timelineDot,AppDimensions.Dp4_5.toPx(),Offset(AppDimensions.DpMinus20.toPx(),AppDimensions.Dp8.toPx()))},verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp0)){Bt(m.s("date"),size=AppTypography.Size10,tint=bp.muted);Spacer(Modifier.height(AppDimensions.Dp4));Bt(m.s("title"),size=AppTypography.Size12,semi=true);if(m.l("entity")>0)BioTextAction(AppText.text(TextKey.CopyXemLienQuan2167e0),{if(m.s("type").startsWith("TREE"))ui.go("tree",m.s("entity"))else ui.go("insights",m.s("entity"))},"next");BioField("",note,{note=it.take(1000)},multiline=true,placeholder=AppText.text(TextKey.CopyMotDieuMuonNho200cc4));Spacer(Modifier.height(AppDimensions.Dp8));BioTextAction(AppText.text(TextKey.CopyLuuGhiChuE3d8bd),{ui.send("memory-note",mapOf("id" to m.s("id"),"note" to note))},modifier=Modifier.fillMaxWidth())}
        }
    }
}
@Composable internal fun ColumnScope.BioEncyclopedia(world:BioWorld,ui:BioController){
    Bt(AppText.text(TextKey.CopyMoiLoaiMotCauChuyen3baf0d),size=AppTypography.Size27,bold=true,spacing=AppTypography.TrackingMinus1)
    val n=world.records.count{it.b("discovered")};BioCard(Modifier.fillMaxWidth(),bp.soft,padding=AppSpacing.Value18){Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween){Bt(AppText.text(TextKey.CopyBoSuuTam539bd6),semi=true);Bt("$n/${world.species.size} · ${n*100/world.species.size}%",semi=true)};BioProgress(n.toFloat()/world.species.size);Bt(AppText.text(TextKey.CopyGiuLichSuKhiDonHoacBanCay42a565),size=AppTypography.Size12,tint=bp.muted)}
    BioGrid(world.species.size,2){i,m->BioProduct(world,ui,world.species[i],m,encyclopedia=true)}
    BioSection(AppText.text(TextKey.CopyDauMocSuuTam1b494b))
    Column(verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp12)){
        world.data.a("goals").objects().filter{it.s("id") !in listOf("expansion","section")}.forEach{g->
            BioCard(Modifier.fillMaxWidth()){
                Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically){
                    Bt(g.s("title"),Modifier.weight(1f),size=AppTypography.Size14,bold=true)
                    Bt("${g.i("n")}/${g.i("target",1)}",size=AppTypography.Size12)
                    if(g.i("n")>=g.i("target",1)){Spacer(Modifier.width(AppDimensions.Dp18));Bi("check")}
                }
                Bt(when(g.s("reward")){"reward_sign"->AppText.text(TextKey.CopyBienNhaVuonD2e9b7);"reward_petals"->AppText.text(TextKey.CopyCanhHoa137700);else->AppText.text(TextKey.CopyHuyHieuBoSuuTam521407)},size=AppTypography.Size12,tint=bp.muted)
                Spacer(Modifier.height(AppDimensions.Dp8));BioProgress(g.i("n").toFloat()/g.i("target",1))
            }
        }
    }
}
@Composable internal fun ColumnScope.BioSpecies(world:BioWorld,ui:BioController){
    val s=world.species(ui.param);val id=s.s("id");val record=world.record(id);val known=record.b("discovered");val highest=record.i("highest");val trees=world.trees.filter{it.s("species")==id}
    BioCard(Modifier.fillMaxWidth(),bp.soft,padding=AppSpacing.Value18,radius=AppRadii.Value26){Column(Modifier.fillMaxWidth(),horizontalAlignment=Alignment.CenterHorizontally){BioTag(bioTier(s.s("tier"))+" · "+record.s("status",AppText.text(TextKey.CopyChuaKhamPhaC0b0cb)));if(known){Spacer(Modifier.height(AppDimensions.Dp2));BioPlant(id,Modifier.fillMaxWidth().height(AppDimensions.Dp220))}else Box(Modifier.fillMaxWidth().height(AppDimensions.Dp140),contentAlignment=Alignment.Center){Bi("leaf",size=AppIcons.Size36)};Bt(s.s("name"),size=AppTypography.Size27,bold=true,spacing=AppTypography.TrackingMinus1,align=TextAlign.Center);Bt(s.s("description"),size=AppTypography.Size12,tint=bp.muted,align=TextAlign.Center)}}
    BioSection(AppText.text(TextKey.CopyCacGiaiDoan01Bee217,highest,s.i("stages")))
    BioGrid(s.i("stages"),if(LocalDensity.current.fontScale>1.2f)2 else 3){i,m->val reached=i<highest;Column(m.background(if(reached)bp.soft else bp.surface,RoundedCornerShape(AppDimensions.Dp12)).padding(AppDimensions.Dp10),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp5)){Bt("${i+1}",size=AppTypography.Size22,mono=true,tint=if(reached)bp.deep else bp.muted);Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp4)){Bi(if(reached)"check"else "lock",size=AppIcons.Size12,tint=bp.muted);Bt(if(reached)AppText.text(TextKey.CopyDaDat1b9e88)else AppText.text(TextKey.CopyChuaDatF4aa8f),size=AppTypography.Size11,tint=bp.muted)}}}
    BioCard(Modifier.fillMaxWidth()){BioFacts(listOf(AppText.text(TextKey.CopyDaTrong42fa2b) to record.i("planted").toString(),AppText.text(TextKey.CopyTrongTui905deb) to world.count(id).toString(),AppText.text(TextKey.CopyDangSongE3573d) to trees.count{it.s("state")!="dead"}.toString(),AppText.text(TextKey.CopyTrongDauTien2e1aed) to bioDisplayDate(record.s("firstPlanted",AppText.text(TextKey.CopyChuaGhiNhanF7654d))),AppText.text(TextKey.CopyTruongThanhDauDd677a) to bioDisplayDate(record.s("firstMatured",AppText.text(TextKey.CopyChuaGhiNhanF7654d))),AppText.text(TextKey.CopyCayCaoNhatDangSong7d778f) to if(trees.isEmpty())AppText.text(TextKey.CopyChuaCoBc2b97)else "${trees.maxOf{it.i("stage")}}/${s.i("stages")}"))}
    BioDetails(AppText.text(TextKey.CopyChamSocLoaiF520bc)){Bt(AppText.text(TextKey.CopyBinhTuoi8121824GioBonPhanKhiCayDangLonVCf24d9),size=AppTypography.Size12,tint=bp.muted)}
    trees.forEach{BioLink(it.s("name"),"garden",{ui.go("tree",it.s("id"))})}
    BioButton(AppText.text(TextKey.CopyTimHatA56320),{ui.tabs["shop"]="seeds";ui.go("shop")},Modifier.fillMaxWidth(),"shop")
}

