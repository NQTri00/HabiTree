package com.mamxanh.app.feature.tasks.presentation

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
        ProvideTextStyle(MaterialTheme.typography.bodySmall.copy(fontSize=15.sp,lineHeight=21.sp)) {
        if(state.route=="ONBOARD")OnboardingScreen(state,onAct)
        else Column(Modifier.fillMaxSize()) {
            Row(Modifier.fillMaxWidth().padding(horizontal=8.dp),verticalAlignment=Alignment.CenterVertically) {
                IconButton(onClick={onOpen("",0)}){Text("‹",fontSize=24.sp,color=Deep)}
                Text(when(state.route){"FOCUS"->"Tập trung";"TEMPLATES"->"Việc mẫu";"REVIEW"->"Nhìn lại tuần";
                    "REST"->"Lịch nghỉ";"ACHIEVEMENTS"->"Dấu mốc";"TIMELINE"->"Kỷ niệm";"SETTINGS"->"Tùy chọn";"INSIGHTS"->"Nhịp thói quen";else->"Hành trình"},
                    color=Deep,fontSize=15.sp,lineHeight=20.sp,fontWeight=FontWeight.Bold)
            }
            val scroll=key(state.route,state.selectedTask){rememberScrollState()}
            Column(Modifier.weight(1f).fillMaxWidth().mintScrollEdges(scroll.canScrollBackward,scroll.canScrollForward)
                .verticalScroll(scroll).padding(horizontal=24.dp,vertical=12.dp),
                verticalArrangement=Arrangement.spacedBy(16.dp)) {
                when(state.route) {
                    "FOCUS"->FocusContent(state,onAct,onOpen)
                    "TEMPLATES"->TemplateContent(state,onAct)
                    "REVIEW"->{ReviewContent(state,onAct);GardenNavigationCard("Lịch nghỉ & bảo vệ",{onOpen("REST",0)},"${state.snapshot.protectionTickets} vé bảo vệ")}
                    "REST"->RestContent(state,onAct)
                    "ACHIEVEMENTS"->{
                        GardenScreenIntro("Từng dấu mốc nhỏ",subtitle="Ghi nhận hành trình · Không thưởng xu")
                        state.snapshot.achievements.forEach{badge->
                            GardenBotanicalFrame(Modifier.fillMaxWidth(),contentPadding=PaddingValues(16.dp),cornerSize=20.dp){
                                Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(14.dp)) {
                                    Box(Modifier.size(44.dp).background(if(badge.unlockedAt>0)Gold else androidx.compose.ui.graphics.Color(0xFFEEEEEE),androidx.compose.foundation.shape.CircleShape),contentAlignment=Alignment.Center){GardenArtwork(GardenArt.RIBBON,Modifier.size(24.dp))}
                                    Column(Modifier.weight(1f),verticalArrangement=Arrangement.spacedBy(6.dp)){
                                        Text(badge.title,color=Deep,fontSize=14.sp,lineHeight=20.sp,fontWeight=FontWeight.SemiBold)
                                        Text(badge.criterion,color=Muted,fontSize=11.sp,lineHeight=16.sp)
                                        Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(8.dp)){
                                            MintProgress(badge.progress.toFloat()/badge.target,Modifier.weight(1f))
                                            Text("${badge.progress.coerceAtMost(badge.target)}/${badge.target}"+if(badge.unlockedAt>0)" · Đã ghi nhận" else "",color=Green,fontSize=10.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    "TIMELINE"->{
                        GardenScreenIntro("Điều mình muốn nhớ")
                        if(state.snapshot.memories.isEmpty())Text("Kỷ niệm sẽ xuất hiện từ những việc bạn thực sự đã làm.",color=Muted,fontSize=12.sp)
                        state.snapshot.memories.forEach{event->
                            var note by rememberSaveable(event.id,event.note){mutableStateOf(event.note)}
                            Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min),horizontalArrangement=Arrangement.spacedBy(14.dp)){
                                Column(Modifier.width(10.dp).fillMaxHeight().padding(top=4.dp),horizontalAlignment=Alignment.CenterHorizontally){
                                    Box(Modifier.size(8.dp).background(Green,androidx.compose.foundation.shape.CircleShape))
                                    Spacer(Modifier.height(5.dp))
                                    Box(Modifier.width(1.dp).weight(1f).background(androidx.compose.ui.graphics.Color(0xFFD8DFD0)))
                                }
                                Column(Modifier.weight(1f),verticalArrangement=Arrangement.spacedBy(10.dp)){
                                    if(event.at>0)Text(java.text.SimpleDateFormat("dd/MM · HH:mm",java.util.Locale.getDefault()).format(java.util.Date(event.at)),color=Muted,fontSize=11.sp)
                                    Text(event.title,color=Deep,fontSize=14.sp,lineHeight=20.sp,fontWeight=FontWeight.SemiBold)
                                    if(event.entity>0)TextButton(onClick={onEntity(event)}){Text("Xem liên quan ›",fontSize=13.sp)}
                                    OutlinedTextField(note,{note=it.take(1000)},Modifier.fillMaxWidth().semantics{contentDescription="Ghi chú: ${event.title}"},
                                        placeholder={Text("Một điều muốn nhớ…",fontSize=12.sp)},minLines=3,maxLines=6,
                                        shape=androidx.compose.foundation.shape.RoundedCornerShape(14.dp),textStyle=MaterialTheme.typography.bodySmall,
                                        colors=OutlinedTextFieldDefaults.colors(unfocusedContainerColor=androidx.compose.ui.graphics.Color.White,focusedContainerColor=androidx.compose.ui.graphics.Color.White))
                                    TextButton(onClick={onAct(EngagementAction.MemoryNote(event.id,note))},enabled=!state.busy){Text("Lưu ghi chú",fontSize=13.sp)}
                                }
                            }
                        }
                    }
                    "SETTINGS"->SettingsContent(state,onAct,onPermission,onData={onOpen("DATA",0)},onTaskReminders={onOpen("INSIGHTS",0)})
                    "INSIGHTS"->InsightsContent(state,onAct,onAdjust,onOpen)
                    else-> {
                        GardenScreenIntro("Chăm một chút, mỗi ngày.",subtitle="Một nhịp sống vừa sức, theo cách của bạn.")
                        listOf("FOCUS" to "Tập trung","TEMPLATES" to "Thêm từ việc mẫu","REVIEW" to "Nhìn lại tuần",
                            "ACHIEVEMENTS" to "Dấu mốc","TIMELINE" to "Kỷ niệm","INSIGHTS" to "Nhịp từng thói quen","SETTINGS" to "Tùy chọn & nhắc việc").forEach{(route,title)->
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
    GardenScreenIntro("Chọn việc vừa sức")
    Text("Thêm một lần, chỉnh sửa bất cứ lúc nào.",
        color=Muted,fontSize=11.sp,lineHeight=16.sp)
    habitTemplates.forEach{template->
        val added=template.id in state.snapshot.templates
        TemplateCard(template,template.id in selected,added){
            selected=if(template.id in selected)selected-template.id else selected+template.id
        }
    }
    GardenPaintedButton("Thêm ${selected.size} việc",{
        onAct(EngagementAction.Templates(selected,key))
    },Modifier.fillMaxWidth(),enabled=selected.isNotEmpty() && !state.busy)
}

@Composable
private fun TemplateCard(t:HabitTemplate,selected:Boolean,added:Boolean=false,onClick:()->Unit) {
    GardenBotanicalFrame(Modifier.fillMaxWidth().heightIn(min=48.dp).clickable(enabled=!added,onClick=onClick),
        skin=if(selected)GardenPanelSkin.HINT else GardenPanelSkin.BADGE) {
        Row(horizontalArrangement=Arrangement.spacedBy(16.dp),verticalAlignment=Alignment.CenterVertically) {
            GardenArtwork(if(t.config.verification=="TIMER")GardenArt.LANTERN else GardenArt.SPROUT,Modifier.size(24.dp))
            Column(Modifier.weight(1f)) {
                Text(t.config.title,color=Deep,fontSize=15.sp,lineHeight=21.sp,fontWeight=FontWeight.Bold)
                Text("${t.config.category} · ${if(t.config.verification=="TIMER")"Bộ đếm" else "Chạm"} · ${t.effort}",
                    color=Muted,fontSize=12.sp,lineHeight=17.sp)
                Text("${if(t.config.frequency=="WEEKENDS")"Cuối tuần" else "Mỗi ngày"} · +${t.config.reward} xu",color=Green,fontSize=12.sp,lineHeight=17.sp)
                if(t.config.startMinute>=0)Text("Khung giờ ${clockLabel(t.config.startMinute)}–${clockLabel(t.config.endMinute)}",color=Muted,fontSize=12.sp,lineHeight=17.sp)
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
    GardenScreenIntro("Tập trung","CHỈ MỘT VIỆC THÔI", "Dành một khoảng cho điều quan trọng.")
    if(current!=null) {
        Column(Modifier.fillMaxWidth()) {
            Column(Modifier.fillMaxWidth(),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(16.dp)) {
                Text(current.title,color=Deep,fontSize=14.sp,lineHeight=18.sp,fontWeight=FontWeight.Bold,textAlign=TextAlign.Center)
                Box(Modifier.size(240.dp),contentAlignment=Alignment.Center) {
                    CircularProgressIndicator(progress={current.activeSeconds.toFloat()/current.targetSeconds.coerceAtLeast(1)},
                        modifier=Modifier.fillMaxSize(),color=Green,strokeWidth=9.dp,trackColor=androidx.compose.ui.graphics.Color(0xFFDDEBE1))
                    Text("${current.remaining/60}:${(current.remaining%60).toString().padStart(2,'0')}",color=Deep,fontSize=46.sp,lineHeight=56.sp,fontWeight=FontWeight.Bold)
                }
                Text(if(current.state=="BREAK")"Nghỉ một chút · Không tính thời gian nghỉ" else if(current.state=="RUNNING")"Đang tập trung" else "Đã tạm dừng",
                    color=Muted,fontSize=11.sp)
                Row(horizontalArrangement=Arrangement.spacedBy(16.dp)) {
                    MintDialogAction(if(current.state=="RUNNING")"Tạm dừng" else "Tiếp tục",
                        {onAct(EngagementAction.Focus(current.id,if(current.state=="RUNNING")"PAUSE" else "RESUME"))},
                        Modifier.weight(1f),enabled=!state.busy)
                    MintDialogAction("Kết thúc",{onAct(EngagementAction.Focus(current.id,"END"))},Modifier.weight(1f),primary=false,enabled=!state.busy)
                }
                GardenExpandableSection("Phiên đang lưu"){Text("Rời ứng dụng sẽ tạm dừng. Phần đã làm luôn được lưu.",color=Muted,fontSize=12.sp,lineHeight=17.sp)}
            }
        }
    } else {
        Box(Modifier.fillMaxWidth().padding(vertical=4.dp),contentAlignment=Alignment.Center) {
            Box(Modifier.size(240.dp),contentAlignment=Alignment.Center) {
                CircularProgressIndicator(progress={0f},modifier=Modifier.fillMaxSize(),color=Green,strokeWidth=9.dp,trackColor=androidx.compose.ui.graphics.Color(0xFFDFE7D1))
                Column(horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(8.dp)) {
                    Text("${minutes.toIntOrNull() ?: 0}:00",color=Deep,fontSize=46.sp,lineHeight=56.sp,fontWeight=FontWeight.SemiBold)
                    Text("Bắt đầu khi bạn sẵn sàng",color=Muted,fontSize=11.sp)
                }
            }
        }
        FlowRow(horizontalArrangement=Arrangement.spacedBy(4.dp)) {
            listOf(15,25,45,60).forEach{n->FilterChip(minutes==n.toString(),{minutes=n.toString()},label={Text("$n phút",fontSize=11.sp)})}
        }
        GardenTextField(minutes,{minutes=it.filter(Char::isDigit).take(3)},"Phút tập trung",placeholder="5–120 phút")
        Text("Dành cho",color=Muted,fontSize=12.sp)
        FilterChip(attached==0L,{attached=0},label={Text("Tự do · không thưởng xu",fontSize=12.sp)})
        state.tasks.filter{it.config.verification=="TIMER" && it.lifecycle=="ACTIVE" && !it.doneToday && !it.finished}.forEach{task->
            FilterChip(attached==task.id,{attached=task.id},enabled=task.available,label={Text(task.title,fontSize=11.sp)})
        }
        GardenPaintedButton("Bắt đầu",{
            onAct(EngagementAction.StartFocus(attached,minutes.toIntOrNull() ?: 0,java.util.UUID.randomUUID().toString()))
        },Modifier.fillMaxWidth(),enabled=!state.busy && (minutes.toIntOrNull() ?: 0) in 5..120)
    }
    GardenExpandableSection("Các phiên đã lưu · ${state.snapshot.focusCompleted} hoàn thành") {
    Text("Đã hoàn thành ${state.snapshot.focusCompleted} phiên",color=Deep,fontSize=12.sp,fontWeight=FontWeight.Bold)
    state.snapshot.focus.take(10).forEach{s->
        Text("${s.date} · ${s.activeSeconds/60} / ${s.targetSeconds/60} phút · ${when(s.state){"COMPLETE"->"Trọn vẹn";"ENDED"->"Đã lưu phần đã làm";"RUNNING"->"Đang chạy";else->"Tạm dừng"}}",
            color=Muted,fontSize=13.sp,lineHeight=19.sp)
    }
    }
}

@Composable
internal fun ReviewContent(state:EngagementUiState,onAct:(EngagementAction)->Unit) {
    val r=state.snapshot.review ?: return
    var note by rememberSaveable(r.start,r.reflection){mutableStateOf(r.reflection)}
    GardenScreenIntro("Tuần của bạn","TUẦN TỪ ${r.start}")
    MintMetricStrip(listOf("Hoàn thành" to "${r.completed}/${r.scheduled}","Tỷ lệ" to (r.rate?.let{"$it%"} ?: "—"),
        "Tập trung" to "${r.focusMinutes} phút"))
    Text(if(r.previousRate==null)"Chưa đủ dữ liệu để so sánh tuần trước."
        else "Tuần trước: ${r.previousRate}% · Số liệu đến hôm nay.",color=Muted,fontSize=11.sp,lineHeight=16.sp)
    GardenExpandableSection("Những điều đã làm") {
        Column(verticalArrangement=Arrangement.spacedBy(12.dp)) {
            Text("Thu ${r.earned} xu · Chi ${r.spent} xu",color=Deep,fontSize=12.sp)
            Text("${r.careDays} ngày chăm cây · ${r.milestones} mốc trưởng thành",color=Deep,fontSize=11.sp)
            Text(if(r.consistent.isBlank())"Chưa đủ dữ liệu về nhóm thói quen." else "Đều đặn nhất: ${r.consistent}",color=Muted,fontSize=11.sp)
            if(r.improve.isNotBlank())Text("Có thể điều chỉnh nhẹ: ${r.improve}",color=Muted,fontSize=11.sp)
        }
    }
    Text("Điều gì khiến tuần này dễ dàng hơn?",color=Deep,fontSize=12.sp,fontWeight=FontWeight.Bold)
    GardenTextField(note,{note=it.take(2000)},"Suy ngẫm tuần",placeholder="Ghi lại cho riêng bạn")
    GardenPaintedButton("Lưu suy ngẫm",{onAct(EngagementAction.Reflection(r.start,note))},Modifier.fillMaxWidth(),enabled=!state.busy)
}

@Composable
private fun RestContent(state:EngagementUiState,onAct:(EngagementAction)->Unit) {
    var rest by rememberSaveable(state.snapshot.tomorrow){mutableStateOf(state.snapshot.tomorrow)}
    var protect by rememberSaveable{mutableStateOf("")}
    GardenScreenIntro("Nghỉ cũng là một nhịp.")
    DayCalendar(state.snapshot.days)
    Text("Ngày nghỉ sắp tới",color=Deep,fontSize=12.sp)
    GardenTextField(rest,{rest=it},"Ngày nghỉ YYYY-MM-DD")
    Text("Tối đa 2 ngày/tháng · Đặt trước ngày nghỉ · Không thưởng xu.",color=Muted,fontSize=12.sp,lineHeight=18.sp)
    GardenPaintedButton("Lên lịch nghỉ",{onAct(EngagementAction.Rest(rest))},Modifier.fillMaxWidth(),enabled=!state.busy)
    Text("Vé bảo vệ",color=Deep,fontSize=18.sp,fontWeight=FontWeight.SemiBold)
    Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.SpaceBetween){
        MintStatusPill("${state.snapshot.protectionTickets} vé")
        GardenPaintedButton("Mua · 250 xu",{onAct(EngagementAction.BuyProtection(java.util.UUID.randomUUID().toString()))},primary=false,enabled=!state.busy)
    }
    GardenTextField(protect,{protect=it},"Ngày đã bỏ lỡ YYYY-MM-DD",placeholder="YYYY-MM-DD")
    GardenPaintedButton("Dùng vé",{onAct(EngagementAction.Protect(protect,java.util.UUID.randomUUID().toString()))},Modifier.fillMaxWidth(),primary=false,enabled=!state.busy&&state.snapshot.protectionTickets>0)
}

@Composable
internal fun InsightsContent(state:EngagementUiState,onAct:(EngagementAction)->Unit,onAdjust:(Task)->Unit,onOpen:(String,Long)->Unit) {
    val insight=state.snapshot.insights.firstOrNull{it.taskId==state.selectedTask}
    if(insight==null) {
        GardenScreenIntro("Mỗi ngày một chút","NHỊP THÓI QUEN")
        state.snapshot.insights.forEach{i->GardenNavigationCard(i.title,{onOpen("INSIGHTS",i.taskId)},art=GardenArt.CALENDAR)}
        if(state.snapshot.insights.isEmpty())Text("Thêm một việc để bắt đầu ghi lại nhịp thói quen.",color=Muted,fontSize=12.sp)
        return
    }
    val task=state.tasks.firstOrNull{it.id==insight.taskId} ?: return
    var days by rememberSaveable{mutableIntStateOf(7)}
    var confirm by remember{mutableStateOf(false)}
    GardenScreenIntro(insight.title)
    Row(horizontalArrangement=Arrangement.spacedBy(12.dp)) {
        listOf(7,30).forEach{n->FilterChip(days==n,{days=n},label={Text("$n ngày",fontSize=11.sp)})}
    }
    DayCalendar(insight.days.takeLast(days))
    MintMetricStrip(listOf("Hiện tại" to "${insight.streak}","Dài nhất" to "${insight.longest}",
        "Tỷ lệ 30 ngày" to (insight.rate?.let{"$it%"} ?: "—")))
    GardenExpandableSection("Tập trung & gợi ý") {
    Text("Tập trung: ${insight.focusMinutes} phút · Giờ thường hoàn thành: ${insight.commonHour.ifBlank{"chưa đủ dữ liệu"}}",
        color=Muted,fontSize=11.sp,lineHeight=16.sp)
    if(insight.suggestion.isNotBlank()) {
        Text(insight.suggestion,color=Muted,fontSize=11.sp)
        GardenPaintedButton("Xem điều chỉnh",{confirm=true},Modifier.fillMaxWidth(),primary=false)
    }
    }
    val pref=state.snapshot.reminders.firstOrNull{it.taskId==task.id} ?: ReminderPreference(task.id)
    var minute by rememberSaveable(task.id,pref.minute){mutableStateOf(clockLabel(pref.minute))}
    Text("Nhắc việc",color=Deep,fontSize=12.sp,fontWeight=FontWeight.Bold)
    Text("Mặc định tắt. Bật quyền và nhắc toàn cục trong Tùy chọn nếu muốn nhận thông báo.",color=Muted,fontSize=11.sp,lineHeight=16.sp)
    GardenTextField(minute,{minute=it},"Giờ nhắc HH:mm")
    TextButton(onClick={parseMinute(minute)?.let{onAct(EngagementAction.Reminder(pref.copy(minute=it)))}}){Text("Lưu giờ nhắc",fontSize=11.sp)}
    FlowRow(horizontalArrangement=Arrangement.spacedBy(4.dp)) {
        listOf("OFF" to "Tắt","AT" to "Theo giờ","BEFORE" to "Trước khung giờ").forEach{(mode,label)->
            FilterChip(pref.mode==mode,{onAct(EngagementAction.Reminder(pref.copy(mode=mode,minute=parseMinute(minute) ?: pref.minute)))},
                label={Text(label,fontSize=11.sp)})
        }
    }
    TextButton(onClick={onAct(EngagementAction.Reminder(pref.copy(followUp=!pref.followUp)))}) {
        Text(if(pref.followUp)"✓ Một lần nhắc nhẹ nếu chưa xong" else "Thêm một lần nhắc nhẹ",fontSize=11.sp)
    }
    if(confirm)MintDialog("Điều chỉnh lịch",{confirm=false},"Xác nhận",{onAdjust(task);confirm=false}) {
        Text("Chuyển sang ngày thường từ ngày mai. Lịch sử và kế hoạch hôm nay được giữ nguyên.",color=Muted,fontSize=12.sp,lineHeight=17.sp)
    }
}

internal fun parseMinute(s:String):Int? {
    val p=s.split(':');if(p.size!=2)return null
    val h=p[0].toIntOrNull() ?: return null;val m=p[1].toIntOrNull() ?: return null
    return if(h in 0..23 && m in 0..59)h*60+m else null
}
@Composable
internal fun DayCalendar(days:List<ReviewDay>) {
    GardenBotanicalFrame(Modifier.fillMaxWidth(),contentPadding=PaddingValues(14.dp),cornerSize=20.dp){
        Column(verticalArrangement=Arrangement.spacedBy(12.dp)){
    Column(verticalArrangement=Arrangement.spacedBy(6.dp)) {
        days.chunked(7).forEach{week->Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(6.dp)) {
            week.forEach{d->
                Surface(Modifier.weight(1f).heightIn(min=36.dp).semantics(mergeDescendants=true){contentDescription="${d.date}: ${when(d.state){"SUCCESS"->"Hoàn thành";"REST"->"Nghỉ";"PROTECTED"->"Đã bảo vệ";"MISSED"->"Bỏ lỡ";else->"Chưa ghi nhận"}}"},shape=androidx.compose.foundation.shape.RoundedCornerShape(10.dp),
                    color=when(d.state){"SUCCESS"->Green;"REST"->androidx.compose.ui.graphics.Color(0xFFE5DFF6);"PROTECTED"->Gold;else->androidx.compose.ui.graphics.Color(0xFFF0F4F1)}) {
                    Text(d.date.takeLast(2)+when(d.state){"SUCCESS"->"✓";"REST"->" N";"PROTECTED"->" ◇";"MISSED"->" ·";else->""},Modifier.padding(vertical=10.dp),textAlign=TextAlign.Center,color=if(d.state=="SUCCESS")androidx.compose.ui.graphics.Color.White else Deep,fontSize=10.sp)
                }
            }
            repeat(7-week.size){Spacer(Modifier.weight(1f))}
        }}
    }
    Text("✓ Hoàn thành · N Nghỉ · ◇ Bảo vệ · Chấm: đã bỏ lỡ",color=Muted,fontSize=12.sp,lineHeight=17.sp)
        }
    }
}

@Preview(name="Engagement components · full review",widthDp=360,heightDp=1000,
    device="spec:width=360dp,height=1000dp,dpi=160")
@Composable
fun EngagementComponentsPreview() {
    MamXanhPreviewTheme {
        ProvideTextStyle(MaterialTheme.typography.bodySmall.copy(fontSize=15.sp,lineHeight=21.sp)) {
            Column(Modifier.fillMaxWidth().padding(12.dp),verticalArrangement=Arrangement.spacedBy(16.dp)) {
                ReviewContent(EngagementUiState(snapshot=EngagementSnapshot(
                    review=WeeklyReview("2026-11-09",5,3,null,25,24,0,2,0,"Học tập","",""),
                    days=listOf(ReviewDay("2026-11-10","REST")),tomorrow="2026-11-13")), {})
            }
        }
    }
}

@Preview(widthDp=414,heightDp=1000,device="spec:width=414dp,height=1000dp,dpi=160")
@Composable
fun FocusWebDesignPreview(){com.mamxanh.app.presentation.ui.MamXanhPreviewTheme{MintBackdrop{Column(Modifier.padding(24.dp),verticalArrangement=Arrangement.spacedBy(18.dp)){FocusContent(EngagementUiState(loading=false,snapshot=EngagementSnapshot(focus=listOf(FocusSession("preview",0,"Một khoảng cho riêng mình","2026-11-16",1500,750,"PAUSED")))),{},{_,_->})}}}}


@Preview(widthDp=414,heightDp=1100,device="spec:width=414dp,height=1100dp,dpi=160")
@Composable fun FocusIdleWebDesignPreview(){com.mamxanh.app.presentation.ui.MamXanhPreviewTheme{MintBackdrop{Column(Modifier.padding(24.dp),verticalArrangement=Arrangement.spacedBy(18.dp)){FocusContent(EngagementUiState(loading=false),{},{_,_->})}}}}

private fun webEngagementState(route:String)=EngagementUiState(loading=false,route=route,tasks=listOf(Task(1,"Đọc 10 trang sách","HABIT","Học tập",8,"EVERY_DAY","","",4,"")),snapshot=EngagementSnapshot(
 insights=listOf(TaskInsight(1,"Đọc 10 trang sách",(1..7).map{ReviewDay("2026-11-${it.toString().padStart(2,'0')}",if(it==3) "MISSED" else "SUCCESS")},86,4,6,"20:30",45,"Chọn một khung giờ vừa sức.")),
 review=WeeklyReview("2026-11-09",15,10,null,45,40,0,3,0,"Học tập","Nếp sống",""),
 achievements=listOf(Achievement("first","Việc đầu tiên","Hoàn thành một việc",1,1,1),Achievement("three","Ba ngày xanh","Giữ nhịp ba ngày",2,3)),
 memories=listOf(MemoryEvent("first","TASK",1,"Việc đầu tiên đã xong",1794819600000)),
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
