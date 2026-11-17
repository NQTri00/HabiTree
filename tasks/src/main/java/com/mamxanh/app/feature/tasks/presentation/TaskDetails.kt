package com.mamxanh.app.feature.tasks.presentation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.font.FontWeight
import com.mamxanh.app.presentation.ui.*
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.mamxanh.shared.model.Task
import com.mamxanh.app.presentation.ui.components.*
@Composable
fun TaskDetails(task: Task,state: TasksUiState,onIntent:(TasksIntent)->Unit) {
    val body: @Composable () -> Unit = {
        Surface(Modifier.widthIn(max=440.dp).fillMaxWidth(.94f).heightIn(max=650.dp),shape=RoundedCornerShape(28.dp),color=Cream) {
            Column(Modifier.verticalScroll(rememberScrollState()).padding(20.dp),verticalArrangement=Arrangement.spacedBy(16.dp)) {
                GardenBotanicalFrame(Modifier.fillMaxWidth(),skin=GardenPanelSkin.HINT,cornerSize=24.dp){
                    Column(verticalArrangement=Arrangement.spacedBy(10.dp)) {
                        MintStatusPill(task.category)
                        Text(task.title,color=Deep,fontSize=26.sp,lineHeight=34.sp,fontWeight=FontWeight.Bold)
                        if(task.config.description.isNotBlank())Text(task.config.description,color=Muted,fontSize=12.sp)
                    }
                }
                MintMetricStrip(listOf("Thưởng" to "+${task.reward} xu","Chuỗi" to "${task.streak} ngày"))
                GardenExpandableSection("Lịch & xác nhận") {
                Text("${task.category} · ${when(task.kind){"HABIT"->"Thói quen";"DAILY"->"Hằng ngày";else->"Một lần"}}")
                Text(when(task.frequency){"WEEKDAYS"->"Thứ hai đến thứ sáu";"WEEKENDS"->"Cuối tuần";"CUSTOM"->"Ngày: ${task.config.days.map{if(it=='0')"CN" else "T${it.digitToInt()+1}"}.joinToString()}";"ONCE"->"Một lần · Hạn ${task.due.ifBlank{"Không có"}}";else->"Mỗi ngày"})
                Text(if(task.config.verification=="TIMER")"Timer · ${task.config.minutes} phút hoạt động" else "Click · Xác nhận một lần")
                }
                Text(when{task.doneToday||task.finished->"Đã hoàn thành";task.lifecycle=="PAUSED"->"Đang tạm dừng";task.lifecycle=="ARCHIVED"->"Đã lưu trữ";else->"Đang hoạt động"})
                if(task.availability.isNotEmpty())Text(task.availability)
                GardenExpandableSection("7 ngày gần đây") {
                Text("Lần cuối ${task.lastDone.ifBlank{"Chưa có"}}",color=Muted,fontSize=12.sp)
                task.history.forEach{Text("${it.date}: ${it.granted}/${it.nominal} xu")}
                if(task.history.isEmpty())Text("Chưa có lần hoàn thành")
                }
                if(task.config.verification=="CLICK")GardenPaintedButton("Hoàn thành",{onIntent(TasksIntent.Complete(task.id))},Modifier.fillMaxWidth(),
                    enabled=!state.busy&&task.available&&!task.doneToday&&!task.finished&&task.lifecycle=="ACTIVE")
                state.phase.sessions.firstOrNull{it.taskId==task.id}?.let{session->
                    Text("Hoạt động ${session.activeSeconds/60}:${(session.activeSeconds%60).toString().padStart(2,'0')} / ${session.requiredSeconds/60} phút")
                    Text("Ra nền sẽ tạm dừng. Khởi động lại cần tiếp tục thủ công.")
                    GardenPaintedButton(if(session.running)"Tạm dừng" else "Tiếp tục",{
                        onIntent(TasksIntent.Timer(task.id,if(session.running)"PAUSE" else "RESUME"))
                    },Modifier.fillMaxWidth(),enabled=!state.busy)
                    GardenPaintedButton("Hoàn thành", {onIntent(TasksIntent.Complete(task.id))},
                        modifier=Modifier.fillMaxWidth(),enabled=!state.busy && session.activeSeconds>=session.requiredSeconds)
                } ?: if(task.config.verification=="TIMER" && !task.doneToday) {
                    GardenPaintedButton("Bắt đầu Timer",{onIntent(TasksIntent.Timer(task.id,"START"))},Modifier.fillMaxWidth(),enabled=!state.busy && task.available && task.lifecycle=="ACTIVE" && !task.finished)
                } else Unit
                Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(12.dp)) {
                GardenPaintedButton("Sửa",{onIntent(TasksIntent.Edit(task.id))},Modifier.weight(1f),primary=false)
                GardenPaintedButton(if(task.lifecycle=="ACTIVE")"Tạm dừng việc" else "Tiếp tục việc",{
                    onIntent(TasksIntent.Lifecycle(task.id,if(task.lifecycle=="ACTIVE")"PAUSED" else "ACTIVE"))
                },Modifier.weight(1f),primary=false)
                }
                GardenExpandableSection("Quản lý việc") {
                    GardenPaintedButton("Lưu trữ",{onIntent(TasksIntent.Lifecycle(task.id,"ARCHIVED"));onIntent(TasksIntent.Detail(null))},Modifier.fillMaxWidth(),primary=false)
                }
                GardenPaintedButton("Đóng",{onIntent(TasksIntent.Detail(null))},Modifier.fillMaxWidth(),primary=false)
            }
        }
    }
    if(androidx.compose.ui.platform.LocalInspectionMode.current) {
        MintBackdrop { Box(Modifier.fillMaxSize(),contentAlignment=androidx.compose.ui.Alignment.Center){body()} }
    } else Dialog(onDismissRequest={onIntent(TasksIntent.Detail(null))},properties=DialogProperties(usePlatformDefaultWidth=false)) { body() }
}
@androidx.compose.ui.tooling.preview.Preview(widthDp=414,heightDp=1100,device="spec:width=414dp,height=1100dp,dpi=160")
@Composable fun TaskDetailWebDesignPreview(){MamXanhPreviewTheme{TaskDetails(Task(1,"Đọc 10 trang sách","HABIT","Học tập",8,"EVERY_DAY","","",4,""),TasksUiState(isLoading=false),{})}}
