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
        ProvideTextStyle(MaterialTheme.typography.bodySmall.copy(fontSize=12.sp,lineHeight=16.sp)) {
        if(state.route=="ONBOARD")OnboardingScreen(state,onAct)
        else Column(Modifier.fillMaxSize()) {
            Row(Modifier.fillMaxWidth().padding(horizontal=8.dp),verticalAlignment=Alignment.CenterVertically) {
                IconButton(onClick={onOpen("",0)}){Text("‹",fontSize=24.sp,color=Deep)}
                Text(when(state.route){"FOCUS"->"Tập trung";"TEMPLATES"->"Việc mẫu";"REVIEW"->"Nhìn lại tuần";
                    "ACHIEVEMENTS"->"Dấu mốc";"TIMELINE"->"Kỷ niệm";"SETTINGS"->"Tùy chọn";"INSIGHTS"->"Nhịp thói quen";else->"Hành trình"},
                    color=Deep,fontSize=15.sp,lineHeight=20.sp,fontWeight=FontWeight.Bold)
            }
            val scroll=key(state.route,state.selectedTask){rememberScrollState()}
            Column(Modifier.weight(1f).fillMaxWidth().mintScrollEdges(scroll.canScrollBackward,scroll.canScrollForward)
                .verticalScroll(scroll).padding(horizontal=12.dp,vertical=6.dp),
                verticalArrangement=Arrangement.spacedBy(8.dp)) {
                when(state.route) {
                    "FOCUS"->FocusContent(state,onAct,onOpen)
                    "TEMPLATES"->TemplateContent(state,onAct)
                    "REVIEW"->ReviewContent(state,onAct)
                    "ACHIEVEMENTS"->state.snapshot.achievements.forEach{badge->
                        GardenBotanicalFrame(Modifier.fillMaxWidth()) {
                            Column(verticalArrangement=Arrangement.spacedBy(4.dp)) {
                                Text((if(badge.unlockedAt>0)"✓ " else "○ ")+badge.title,color=Deep,fontSize=12.sp,fontWeight=FontWeight.Bold)
                                Text(badge.criterion,color=Muted,fontSize=11.sp,lineHeight=15.sp)
                                MintProgress(badge.progress.toFloat()/badge.target)
                                Text(if(badge.unlockedAt>0)"Đã ghi nhận · Huy hiệu không thưởng xu" else "${badge.progress.coerceAtMost(badge.target)} / ${badge.target}",color=Green,fontSize=10.sp)
                            }
                        }
                    }
                    "TIMELINE"-> {
                        if(state.snapshot.memories.isEmpty())Text("Kỷ niệm sẽ xuất hiện từ những việc bạn thực sự đã làm.",color=Muted,fontSize=12.sp)
                        state.snapshot.memories.forEach{event->
                            var note by rememberSaveable(event.id,event.note){mutableStateOf(event.note)}
                            GardenBotanicalFrame(Modifier.fillMaxWidth()) {
                                Column(verticalArrangement=Arrangement.spacedBy(5.dp)) {
                                    Text(event.title,color=Deep,fontSize=12.sp,fontWeight=FontWeight.Bold)
                                    if(event.at>0)Text(java.text.SimpleDateFormat("dd/MM/yyyy HH:mm",java.util.Locale.getDefault()).format(java.util.Date(event.at)),color=Muted,fontSize=10.sp)
                                    if(event.entity>0)TextButton(onClick={onEntity(event)}){Text("Xem liên quan ›",fontSize=11.sp)}
                                    GardenTextField(note,{note=it.take(1000)},"Ghi chú kỷ niệm",placeholder="Một điều muốn nhớ")
                                    TextButton(onClick={onAct(EngagementAction.MemoryNote(event.id,note))},enabled=!state.busy){Text("Lưu ghi chú",fontSize=11.sp)}
                                }
                            }
                        }
                    }
                    "SETTINGS"->SettingsContent(state,onAct,onPermission,onData={onOpen("DATA",0)})
                    "INSIGHTS"->InsightsContent(state,onAct,onAdjust,onOpen)
                    else-> {
                        Text("Một nhịp sống vừa sức, theo cách của bạn.",color=Muted,fontSize=12.sp,lineHeight=17.sp)
                        listOf("FOCUS" to "Tập trung","TEMPLATES" to "Thêm từ việc mẫu","REVIEW" to "Nhìn lại tuần",
                            "ACHIEVEMENTS" to "Dấu mốc","TIMELINE" to "Kỷ niệm","INSIGHTS" to "Nhịp từng thói quen","SETTINGS" to "Tùy chọn & nhắc việc").forEach{(route,title)->
                            GardenBotanicalFrame(Modifier.fillMaxWidth().heightIn(min=48.dp).clickable{onOpen(route,0)}) {
                                Text("$title  ›",color=Deep,fontSize=12.sp,fontWeight=FontWeight.SemiBold)
                            }
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
    Text("Chọn việc vừa sức. Mỗi mẫu chỉ được thêm một lần; bạn có thể sửa bản sao sau đó.",
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
        Row(horizontalArrangement=Arrangement.spacedBy(8.dp),verticalAlignment=Alignment.CenterVertically) {
            Text(if(added)"✓" else if(selected)"●" else "○",color=Green)
            Column(Modifier.weight(1f)) {
                Text(t.config.title,color=Deep,fontSize=12.sp,lineHeight=16.sp,fontWeight=FontWeight.Bold)
                Text("${t.config.category} · ${if(t.config.frequency=="WEEKENDS")"Cuối tuần" else "Mỗi ngày"} · ${t.effort}",
                    color=Muted,fontSize=10.sp,lineHeight=14.sp)
                Text("${t.config.verification} · +${t.config.reward} xu",color=Green,fontSize=10.sp,lineHeight=14.sp)
                if(t.config.startMinute>=0)Text("Khung giờ ${clockLabel(t.config.startMinute)}–${clockLabel(t.config.endMinute)}",color=Muted,fontSize=10.sp,lineHeight=14.sp)
            }
        }
    }
}

@Composable
internal fun FocusContent(state:EngagementUiState,onAct:(EngagementAction)->Unit,onOpen:(String,Long)->Unit) {
    var minutes by rememberSaveable{mutableStateOf("25")}
    var attached by rememberSaveable(state.selectedTask){mutableLongStateOf(state.selectedTask)}
    val current=state.snapshot.focus.firstOrNull{it.state in setOf("RUNNING","PAUSED","BREAK")}
    if(current!=null) {
        GardenBotanicalFrame(Modifier.fillMaxWidth()) {
            Column(Modifier.fillMaxWidth(),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(8.dp)) {
                Text(current.title,color=Deep,fontSize=14.sp,lineHeight=18.sp,fontWeight=FontWeight.Bold,textAlign=TextAlign.Center)
                Box(Modifier.size(112.dp),contentAlignment=Alignment.Center) {
                    CircularProgressIndicator(progress={current.activeSeconds.toFloat()/current.targetSeconds.coerceAtLeast(1)},
                        modifier=Modifier.fillMaxSize(),color=Green,strokeWidth=6.dp,trackColor=androidx.compose.ui.graphics.Color(0xFFDDEBE1))
                    Text("${current.remaining/60}:${(current.remaining%60).toString().padStart(2,'0')}",color=Deep,fontSize=24.sp,lineHeight=30.sp,fontWeight=FontWeight.Bold)
                }
                Text(if(current.state=="BREAK")"Nghỉ một chút · Không tính thời gian nghỉ" else if(current.state=="RUNNING")"Đang tập trung" else "Đã tạm dừng",
                    color=Muted,fontSize=11.sp)
                Row(horizontalArrangement=Arrangement.spacedBy(8.dp)) {
                    MintDialogAction(if(current.state=="RUNNING")"Tạm dừng" else "Tiếp tục",
                        {onAct(EngagementAction.Focus(current.id,if(current.state=="RUNNING")"PAUSE" else "RESUME"))},
                        Modifier.weight(1f),enabled=!state.busy)
                    MintDialogAction("Kết thúc",{onAct(EngagementAction.Focus(current.id,"END"))},Modifier.weight(1f),primary=false,enabled=!state.busy)
                }
                Text("Rời ứng dụng sẽ tạm dừng. Phần đã làm luôn được lưu.",color=Muted,fontSize=10.sp,lineHeight=14.sp)
            }
        }
    } else {
        Text("Một khoảng thời gian dành cho điều quan trọng.",color=Muted,fontSize=12.sp,lineHeight=16.sp)
        FlowRow(horizontalArrangement=Arrangement.spacedBy(4.dp)) {
            listOf(15,25,45,60).forEach{n->FilterChip(minutes==n.toString(),{minutes=n.toString()},label={Text("$n phút",fontSize=11.sp)})}
        }
        GardenTextField(minutes,{minutes=it.filter(Char::isDigit).take(3)},"Phút tập trung",placeholder="5–120 phút")
        Text("Gắn vào việc Timer (thời lượng theo việc đã lưu), hoặc tập trung tự do không thưởng xu.",color=Muted,fontSize=11.sp,lineHeight=16.sp)
        FilterChip(attached==0L,{attached=0},label={Text("Tự do",fontSize=11.sp)})
        state.tasks.filter{it.config.verification=="TIMER" && it.lifecycle=="ACTIVE" && !it.doneToday && !it.finished}.forEach{task->
            FilterChip(attached==task.id,{attached=task.id},enabled=task.available,label={Text(task.title,fontSize=11.sp)})
        }
        GardenPaintedButton("Bắt đầu",{
            onAct(EngagementAction.StartFocus(attached,minutes.toIntOrNull() ?: 0,java.util.UUID.randomUUID().toString()))
        },Modifier.fillMaxWidth(),enabled=!state.busy && (minutes.toIntOrNull() ?: 0) in 5..120)
    }
    Text("Đã hoàn thành ${state.snapshot.focusCompleted} phiên",color=Deep,fontSize=12.sp,fontWeight=FontWeight.Bold)
    state.snapshot.focus.take(10).forEach{s->
        Text("${s.date} · ${s.activeSeconds/60} / ${s.targetSeconds/60} phút · ${when(s.state){"COMPLETE"->"Trọn vẹn";"ENDED"->"Đã lưu phần đã làm";"RUNNING"->"Đang chạy";else->"Tạm dừng"}}",
            color=Muted,fontSize=11.sp,lineHeight=15.sp)
    }
}

@Composable
internal fun ReviewContent(state:EngagementUiState,onAct:(EngagementAction)->Unit) {
    val r=state.snapshot.review ?: return
    var note by rememberSaveable(r.start,r.reflection){mutableStateOf(r.reflection)}
    Text("Tuần từ ${r.start}",color=Deep,fontSize=12.sp,fontWeight=FontWeight.Bold)
    MintMetricStrip(listOf("Hoàn thành" to "${r.completed}/${r.scheduled}","Tỷ lệ" to (r.rate?.let{"$it%"} ?: "—"),
        "Tập trung" to "${r.focusMinutes} phút"))
    Text(if(r.previousRate==null)"Chưa đủ dữ liệu để so sánh tuần trước."
        else "Tuần trước: ${r.previousRate}% · Số liệu đến hôm nay.",color=Muted,fontSize=11.sp,lineHeight=16.sp)
    GardenBotanicalFrame(Modifier.fillMaxWidth()) {
        Column(verticalArrangement=Arrangement.spacedBy(6.dp)) {
            Text("Thu ${r.earned} xu · Chi ${r.spent} xu",color=Deep,fontSize=12.sp)
            Text("${r.careDays} ngày chăm cây · ${r.milestones} mốc trưởng thành",color=Deep,fontSize=11.sp)
            Text(if(r.consistent.isBlank())"Chưa đủ dữ liệu về nhóm thói quen." else "Đều đặn nhất: ${r.consistent}",color=Muted,fontSize=11.sp)
            if(r.improve.isNotBlank())Text("Có thể điều chỉnh nhẹ: ${r.improve}",color=Muted,fontSize=11.sp)
        }
    }
    Text("Điều gì khiến tuần này dễ dàng hơn?",color=Deep,fontSize=12.sp,fontWeight=FontWeight.Bold)
    GardenTextField(note,{note=it.take(2000)},"Suy ngẫm tuần",placeholder="Ghi lại cho riêng bạn")
    GardenPaintedButton("Lưu suy ngẫm",{onAct(EngagementAction.Reflection(r.start,note))},Modifier.fillMaxWidth(),enabled=!state.busy)
    RestContent(state,onAct)
}

@Composable
private fun RestContent(state:EngagementUiState,onAct:(EngagementAction)->Unit) {
    var rest by rememberSaveable(state.snapshot.tomorrow){mutableStateOf(state.snapshot.tomorrow)}
    var protect by rememberSaveable{mutableStateOf("")}
    Text("Lịch nghỉ",color=Deep,fontSize=12.sp,fontWeight=FontWeight.Bold)
    Text("Tối đa 2 ngày mỗi tháng, lên lịch trước ngày nghỉ. Không thưởng xu hoặc Daily Clear.",color=Muted,fontSize=11.sp,lineHeight=16.sp)
    GardenTextField(rest,{rest=it},"Ngày nghỉ YYYY-MM-DD")
    GardenPaintedButton("Lên lịch ngày nghỉ",{onAct(EngagementAction.Rest(rest))},Modifier.fillMaxWidth(),enabled=!state.busy)
    DayCalendar(state.snapshot.days)
    Text("Vé bảo vệ: ${state.snapshot.protectionTickets} · Tùy chọn, không cần mua để tiếp tục.",color=Muted,fontSize=11.sp,lineHeight=16.sp)
    GardenPaintedButton("Mua 1 vé · 250 xu",{onAct(EngagementAction.BuyProtection(java.util.UUID.randomUUID().toString()))},
        Modifier.fillMaxWidth(),primary=false,enabled=!state.busy)
    GardenTextField(protect,{protect=it},"Ngày đã bỏ lỡ YYYY-MM-DD",placeholder="YYYY-MM-DD")
    GardenPaintedButton("Dùng vé cho ngày này",{onAct(EngagementAction.Protect(protect,java.util.UUID.randomUUID().toString()))},
        Modifier.fillMaxWidth(),primary=false,enabled=!state.busy && state.snapshot.protectionTickets>0)
}

@Composable
internal fun InsightsContent(state:EngagementUiState,onAct:(EngagementAction)->Unit,onAdjust:(Task)->Unit,onOpen:(String,Long)->Unit) {
    val insight=state.snapshot.insights.firstOrNull{it.taskId==state.selectedTask}
    if(insight==null) {
        state.snapshot.insights.forEach{i->GardenBotanicalFrame(Modifier.fillMaxWidth().heightIn(min=48.dp).clickable{onOpen("INSIGHTS",i.taskId)}) {
            Text(i.title+"  ›",color=Deep,fontSize=12.sp)
        }}
        if(state.snapshot.insights.isEmpty())Text("Thêm một việc để bắt đầu ghi lại nhịp thói quen.",color=Muted,fontSize=12.sp)
        return
    }
    val task=state.tasks.firstOrNull{it.id==insight.taskId} ?: return
    var days by rememberSaveable{mutableIntStateOf(7)}
    var confirm by remember{mutableStateOf(false)}
    Text(insight.title,color=Deep,fontSize=14.sp,lineHeight=18.sp,fontWeight=FontWeight.Bold)
    Row(horizontalArrangement=Arrangement.spacedBy(6.dp)) {
        listOf(7,30).forEach{n->FilterChip(days==n,{days=n},label={Text("$n ngày",fontSize=11.sp)})}
    }
    DayCalendar(insight.days.takeLast(days))
    MintMetricStrip(listOf("Hiện tại" to "${insight.streak}","Dài nhất" to "${insight.longest}",
        "Tỷ lệ 30 ngày" to (insight.rate?.let{"$it%"} ?: "—")))
    Text("Tập trung: ${insight.focusMinutes} phút · Giờ thường hoàn thành: ${insight.commonHour.ifBlank{"chưa đủ dữ liệu"}}",
        color=Muted,fontSize=11.sp,lineHeight=16.sp)
    if(insight.suggestion.isNotBlank()) {
        Text(insight.suggestion,color=Muted,fontSize=11.sp)
        GardenPaintedButton("Xem điều chỉnh",{confirm=true},Modifier.fillMaxWidth(),primary=false)
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
    FlowRow(horizontalArrangement=Arrangement.spacedBy(4.dp),verticalArrangement=Arrangement.spacedBy(4.dp)) {
        days.forEach{d->
            Surface(shape=androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
                color=when(d.state){"SUCCESS"->androidx.compose.ui.graphics.Color(0xFFCCE8D5);"REST","PROTECTED"->androidx.compose.ui.graphics.Color(0xFFE4EAF6);else->androidx.compose.ui.graphics.Color(0xFFF0F4F1)}) {
                Text(d.date.takeLast(2)+(when(d.state){"SUCCESS"->" ✓";"REST"->" N";"PROTECTED"->" ◇";"MISSED"->" ·";else->""}),
                    Modifier.padding(6.dp),color=Deep,fontSize=10.sp)
            }
        }
    }
    Text("✓ Hoàn thành · N Nghỉ · ◇ Bảo vệ · Chấm: đã bỏ lỡ",color=Muted,fontSize=10.sp,lineHeight=14.sp)
}

@Preview(name="Engagement components · full review",widthDp=360,heightDp=1000,
    device="spec:width=360dp,height=1000dp,dpi=160")
@Composable
fun EngagementComponentsPreview() {
    MamXanhPreviewTheme {
        ProvideTextStyle(MaterialTheme.typography.bodySmall.copy(fontSize=12.sp,lineHeight=16.sp)) {
            Column(Modifier.fillMaxWidth().padding(12.dp),verticalArrangement=Arrangement.spacedBy(8.dp)) {
                ReviewContent(EngagementUiState(snapshot=EngagementSnapshot(
                    review=WeeklyReview("2026-11-09",5,3,null,25,24,0,2,0,"Học tập","",""),
                    days=listOf(ReviewDay("2026-11-10","REST")),tomorrow="2026-11-13")), {})
            }
        }
    }
}
