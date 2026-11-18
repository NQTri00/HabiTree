package com.mamxanh.app.presentation.bio

import com.mamxanh.app.feature.stats.presentation.AchievementDisplay
import com.mamxanh.app.feature.stats.presentation.AchievementsScreen
import com.mamxanh.app.feature.stats.presentation.TimelineMemory
import com.mamxanh.app.feature.stats.presentation.TimelineScreen
import com.mamxanh.app.feature.stats.presentation.WeeklyReviewDisplay
import com.mamxanh.app.feature.stats.presentation.WeeklyReviewScreen
import com.mamxanh.app.feature.tasks.presentation.RestScreen
import com.mamxanh.app.feature.tasks.presentation.RestScreenState
import com.mamxanh.app.feature.tasks.presentation.InsightTaskLink
import com.mamxanh.app.feature.tasks.presentation.TaskInsightsDisplay
import com.mamxanh.app.feature.tasks.presentation.TaskInsightsScreen

import com.mamxanh.app.feature.stats.presentation.StatisticsBars
import com.mamxanh.app.presentation.ui.design.*

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
@Composable
internal fun BioReview(world: BioWorld, ui: BioController) {
    val review = world.data.o("review")
    val display = if (world.native && review.length() == 0) null else WeeklyReviewDisplay(
        startDisplay = review.s("start", "2026-11-16"),
        reflection = review.s("reflection", world.state.s("reflection")),
        completed = review.i("completed"),
        scheduled = review.i("scheduled"),
        focusMinutes = review.i("focusMinutes"),
        previousRate = if (review.has("previousRate")) review.i("previousRate") else null,
        earned = review.i("earned"),
        spent = review.i("spent"),
        careDays = review.i("careDays"),
        milestones = review.i("milestones"),
        consistent = review.s("consistent", AppText.text(TextKey.CopyChuaDuDuLieu414142)),
        improve = review.s("improve", AppText.text(TextKey.CopyChuaDuDuLieu414142)),
        tickets = world.state.i("tickets"),
        dailyCompletions = world.statisticsDailyCompletions(),
        dayLabels = world.statisticsDayLabels(),
    )
    WeeklyReviewScreen(
        review = display,
        onTasks = { ui.go("tasks") },
        onSaveNote = { note -> ui.send("reflection", mapOf("note" to note, "week" to review.s("start"))) },
        onRest = { ui.go("rest") },
    )
}

@Composable
internal fun BioRest(world: BioWorld, ui: BioController) {
    RestScreen(
        state = RestScreenState(
            tomorrow = world.data.s("tomorrow", "2026-11-18"),
            scheduledDays = world.state.a("rest").strings(),
            tickets = world.state.i("tickets"),
            canBuy = world.coins >= 250,
        ),
        onSchedule = { ui.send("rest", mapOf("date" to it)) },
        onBuy = { ui.show("ticket") },
        onProtect = { ui.send("protect", mapOf("date" to it)) },
        calendar = { BioCalendar(world, 30) },
    )
}

@Composable
internal fun BioInsights(world: BioWorld, ui: BioController) {
    val task = world.task(ui.param)
    val id = task?.s("id").orEmpty()
    val insight = if (task == null) null else world.data.a("insights").objects().firstOrNull {
        it.s("taskId") == id
    } ?: json("longest" to 7, "rate" to 72, "focusMinutes" to 125, "commonHour" to "19:00",
        "suggestion" to AppText.text(TextKey.CopyDieuChinhSangNgayThuong939cdd))
    val display = if (task == null || insight == null) null else {
        val reminder = world.state.o("reminders").o(id)
        TaskInsightsDisplay(
            id = id,
            title = task.s("title"),
            streak = task.i("streak"),
            longest = insight.i("longest"),
            rate = if (insight.has("rate")) insight.i("rate") else null,
            focusMinutes = insight.i("focusMinutes"),
            commonHour = insight.s("commonHour", AppText.text(TextKey.CopyChuaDuDuLieu414142)),
            suggestion = insight.s("suggestion"),
            minute = reminder.s("minute", "09:00"),
            mode = reminder.s("mode", "OFF"),
            followUp = reminder.b("followUp"),
        )
    }
    val period = ui.tabs["insightDays"] ?: "7"
    TaskInsightsScreen(
        task = display,
        tasks = if (display == null) world.tasks.map { InsightTaskLink(it.s("id"), it.s("title")) } else emptyList(),
        period = period,
        onPeriod = { ui.tabs["insightDays"] = it },
        onAddTask = { ui.go("task-new") },
        onTask = { ui.go("insights", it) },
        onAdjust = { ui.show("adjust", id) },
        onSaveReminder = { minute, mode, follow ->
            ui.send("reminder", mapOf("id" to id, "minute" to minute, "mode" to mode, "followUp" to follow.toString()))
        },
        onSettings = { ui.go("settings") },
        calendar = { BioCalendar(world, period.toInt(), insight?.optJSONArray("days")) },
    )
}

@Composable
internal fun BioAchievements(world: BioWorld) {
    val entries = if (world.native) {
        world.data.a("achievements").objects().map {
            AchievementDisplay(it.s("title"), it.s("criterion"), it.i("progress"), it.i("target", 1), it.l("unlockedAt") > 0)
        }
    } else {
        val defaults=listOf(Triple(AppText.text(TextKey.CopyBuocDauTien5dbfda),AppText.text(TextKey.CopyHoanThanhMotViecHopLe912065),1 to 1),Triple(AppText.text(TextKey.CopyBaNgayTotLanhD6a627),AppText.text(TextKey.Copy3NgayCoLichHoanThanh0527b6),3 to 3),Triple(AppText.text(TextKey.CopyMotTuanDeuDanD7d5ad),AppText.text(TextKey.Copy7NgayCoLichHoanThanh86a291),5 to 7),Triple(AppText.text(TextKey.CopyKhoiDauTapTrungC3b5b9),AppText.text(TextKey.CopyHoanThanhMotPhienItNhat25Phut6bcfa2),0 to 1),Triple(AppText.text(TextKey.CopyNhipHocTapF10229),AppText.text(TextKey.Copy10LanHoanThanhViecHocDcd041),6 to 10),Triple(AppText.text(TextKey.CopyBanTayXanh9bd1dc),AppText.text(TextKey.CopyTuoiCayTrong10NgayKhacNhau786a07),3 to 10),Triple(AppText.text(TextKey.CopyBongHoaDauTien4d67d1),AppText.text(TextKey.CopyMotLoaiHoaDatGiaiDoanCuoi468527),0 to 1),Triple(AppText.text(TextKey.CopyCayTruongThanhDauTienF9a6e3),AppText.text(TextKey.CopyMotLoaiKhongHoaDatGiaiDoanCuoi120850),1 to 1),Triple(AppText.text(TextKey.CopyGocVuonDaDang8a3813),AppText.text(TextKey.CopyDangSoHuu3LoaiKhacNhauA27bf0),3 to 3),Triple(AppText.text(TextKey.CopyThangDeuDanD8b460),AppText.text(TextKey.Copy20NgayCoLichHoanThanh4b7346),5 to 20))
        defaults.map { (title, criterion, progress) ->
            AchievementDisplay(title, criterion, progress.first, progress.second)
        }
    }
    AchievementsScreen(entries)
}

@Composable
internal fun BioTimeline(world: BioWorld, ui: BioController) {
    val notes = world.state.o("notes")
    val memories = world.state.a("memories").objects().map { memory ->
        val id = memory.s("id")
        TimelineMemory(
            id = id,
            date = memory.s("date"),
            title = memory.s("title"),
            entityId = memory.s("entity"),
            isTree = memory.s("type").startsWith("TREE"),
            note = notes.s(id, memory.s("note")),
        )
    }
    TimelineScreen(
        memories = memories,
        onRelated = { ui.go(if (it.isTree) "tree" else "insights", it.entityId) },
        onSaveNote = { id, note -> ui.send("memory-note", mapOf("id" to id, "note" to note)) },
    )
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

