package com.mamxanh.app.feature.tasks.presentation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.mamxanh.shared.model.Task
import com.mamxanh.app.presentation.ui.components.*
@Composable
fun TaskDetails(task: Task,state: TasksUiState,onIntent:(TasksIntent)->Unit) {
    Dialog(onDismissRequest={onIntent(TasksIntent.Detail(null))},properties=DialogProperties(usePlatformDefaultWidth=false)) {
        GardenBotanicalFrame(Modifier.fillMaxWidth(.94f).heightIn(max=650.dp)) {
            Column(Modifier.verticalScroll(rememberScrollState()),verticalArrangement=Arrangement.spacedBy(10.dp)) {
                Text(task.title,style=MaterialTheme.typography.titleLarge)
                Text(task.config.description)
                Text("${task.category} · ${when(task.kind){"HABIT"->"Thói quen";"DAILY"->"Hằng ngày";else->"Một lần"}}")
                Text(when(task.frequency){"WEEKDAYS"->"Thứ hai đến thứ sáu";"WEEKENDS"->"Cuối tuần";"CUSTOM"->"Ngày: ${task.config.days.map{if(it=='0')"CN" else "T${it.digitToInt()+1}"}.joinToString()}";"ONCE"->"Một lần · Hạn ${task.due.ifBlank{"Không có"}}";else->"Mỗi ngày"})
                Text(if(task.config.verification=="TIMER")"Timer · ${task.config.minutes} phút hoạt động" else "Click · Xác nhận một lần")
                Text(when{task.doneToday||task.finished->"Đã hoàn thành";task.lifecycle=="PAUSED"->"Đang tạm dừng";task.lifecycle=="ARCHIVED"->"Đã lưu trữ";else->"Đang hoạt động"})
                if(task.availability.isNotEmpty())Text(task.availability)
                Text("Chuỗi ${task.streak} · Lần cuối ${task.lastDone.ifBlank{"Chưa có"}}")
                Text("7 ngày gần đây")
                task.history.forEach{Text("${it.date}: ${it.granted}/${it.nominal} xu")}
                if(task.history.isEmpty())Text("Chưa có lần hoàn thành")
                state.phase.sessions.firstOrNull{it.taskId==task.id}?.let{session->
                    Text("Hoạt động ${session.activeSeconds/60}:${(session.activeSeconds%60).toString().padStart(2,'0')} / ${session.requiredSeconds/60} phút")
                    Text("Ra nền sẽ tạm dừng. Khởi động lại cần tiếp tục thủ công.")
                    GardenPaintedButton(if(session.running)"Tạm dừng" else "Tiếp tục",{
                        onIntent(TasksIntent.Timer(task.id,if(session.running)"PAUSE" else "RESUME"))
                    },enabled=!state.busy)
                    GardenPaintedButton("Hoàn thành", {onIntent(TasksIntent.Complete(task.id))},
                        enabled=!state.busy && session.activeSeconds>=session.requiredSeconds)
                } ?: if(task.config.verification=="TIMER" && !task.doneToday) {
                    GardenPaintedButton("Bắt đầu Timer",{onIntent(TasksIntent.Timer(task.id,"START"))},enabled=!state.busy && task.available)
                } else Unit
                GardenPaintedButton("Sửa",{onIntent(TasksIntent.Edit(task.id))},primary=false)
                GardenPaintedButton(if(task.lifecycle=="ACTIVE")"Tạm dừng việc" else "Tiếp tục việc",{
                    onIntent(TasksIntent.Lifecycle(task.id,if(task.lifecycle=="ACTIVE")"PAUSED" else "ACTIVE"))
                },primary=false)
                GardenPaintedButton("Lưu trữ",{onIntent(TasksIntent.Lifecycle(task.id,"ARCHIVED"));onIntent(TasksIntent.Detail(null))},primary=false)
                GardenPaintedButton("Đóng",{onIntent(TasksIntent.Detail(null))})
            }
        }
    }
}
