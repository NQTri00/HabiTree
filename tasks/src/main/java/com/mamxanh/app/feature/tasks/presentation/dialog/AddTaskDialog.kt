package com.mamxanh.app.feature.tasks.presentation.dialog

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.mamxanh.app.presentation.ui.components.*
import com.mamxanh.shared.model.*

@Composable
fun AddTaskDialog(onDismiss: ()->Unit,onSave: (TaskConfig)->Unit,
    initial: TaskConfig = TaskConfig(),draft: List<String> = emptyList(),onDraftChange: (List<String>)->Unit = {}) {
    val defaults=listOf(initial.title,initial.kind,initial.category,initial.description,initial.frequency,initial.days,
        initial.due,initial.verification,initial.minutes.toString(),clockLabel(initial.startMinute),
        clockLabel(initial.endMinute),clockLabel(initial.reminderMinute))
    var values by rememberSaveable { mutableStateOf(if(draft.size==12)draft else defaults) }
    var error by rememberSaveable { mutableStateOf("") }
    var discard by rememberSaveable { mutableStateOf(false) }
    val latestDraft by rememberUpdatedState(onDraftChange)
    LaunchedEffect(values){latestDraft(values)}
    fun update(index: Int,value: String){ values=values.toMutableList().apply{this[index]=value} }
    fun dismiss(){if(values!=defaults)discard=true else onDismiss()}
    fun minute(value: String): Int {
        if(value.isBlank())return -1
        val p=value.split(':').map{it.toIntOrNull() ?: return -2}
        return if(p.size==2 && p[0] in 0..23 && p[1] in 0..59)p[0]*60+p[1] else -2
    }
    Dialog(onDismissRequest=::dismiss,properties=DialogProperties(usePlatformDefaultWidth=false,decorFitsSystemWindows=false)) {
        MintBackdrop(Modifier.fillMaxSize().safeDrawingPadding().imePadding()) {
            Column(Modifier.fillMaxSize().padding(16.dp),verticalArrangement=Arrangement.spacedBy(8.dp)) {
                MintHeader(if(initial.title.isBlank())"Thêm việc" else "Sửa việc",0)
                Column(Modifier.weight(1f).verticalScroll(rememberScrollState()),verticalArrangement=Arrangement.spacedBy(12.dp)) {
                    TaskInput(values[0],{update(0,it.take(100))},"Tên việc (1–100)",Modifier.fillMaxWidth())
                    TaskInput(values[3],{update(3,it.take(500))},"Mô tả (tối đa 500)",Modifier.fillMaxWidth())
                    TaskChoices("Loại",listOf("HABIT" to "Thói quen","DAILY" to "Hằng ngày","TODO" to "Một lần"),values[1]){update(1,it)}
                    TaskChoices("Nhóm",listOf("Học tập","Sức khỏe","Nếp sống","Việc nhà","Cá nhân").map{it to it},values[2]){update(2,it)}
                    if(values[1]!="TODO") {
                        TaskChoices("Lịch",listOf("EVERY_DAY" to "Mỗi ngày","WEEKDAYS" to "Ngày thường","WEEKENDS" to "Cuối tuần","CUSTOM" to "Tự chọn"),values[4]){update(4,it)}
                        if(values[4]=="CUSTOM") {
                            Text("Chọn ngày")
                            FlowRow(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(4.dp),verticalArrangement=Arrangement.spacedBy(4.dp)) {
                                listOf("CN","T2","T3","T4","T5","T6","T7").forEachIndexed { i,label ->
                                    GardenChoiceChip(label,i.toString() in values[5],onClick={
                                        update(5,if(i.toString() in values[5])values[5].replace(i.toString(),"") else values[5]+i)
                                    },modifier=Modifier.widthIn(min=48.dp))
                                }
                            }
                        }
                    } else TaskInput(values[6],{update(6,it)},"Hạn chót (YYYY-MM-DD, tùy chọn)",Modifier.fillMaxWidth())
                    TaskChoices("Xác nhận",listOf("CLICK" to "Click","TIMER" to "Timer"),values[7]){update(7,it)}
                    if(values[7]=="TIMER")TaskInput(values[8],{update(8,it.filter(Char::isDigit).take(3))},"Phút hoạt động (5–240)",Modifier.fillMaxWidth())
                    Text("Khung giờ tùy chọn · HH:mm. Kết thúc sớm hơn bắt đầu nghĩa là qua đêm.")
                    TaskInput(values[9],{update(9,it)},"Bắt đầu (HH:mm)",Modifier.fillMaxWidth())
                    TaskInput(values[10],{update(10,it)},"Kết thúc (HH:mm)",Modifier.fillMaxWidth())
                    TaskInput(values[11],{update(11,it)},"Nhắc việc (HH:mm, tùy chọn)",Modifier.fillMaxWidth())
                    Text("Thông báo cần quyền hệ thống; từ chối quyền vẫn lưu được việc.")
                    Text("Thưởng: ${Balance.reward(values[7],values[8].toIntOrNull() ?: 5)} xu · Tổng tối đa 50 xu/ngày")
                    if(error.isNotEmpty())Text(error,color=MaterialTheme.colorScheme.error)
                }
                Row(horizontalArrangement=Arrangement.spacedBy(8.dp)) {
                    GardenPaintedButton("Hủy",::dismiss,Modifier.weight(1f),primary=false)
                    GardenPaintedButton("Lưu",{
                        val c=TaskConfig(values[0],values[1],values[2],values[3],
                            if(values[1]=="TODO")"ONCE" else values[4],values[5],values[6],values[7],
                            values[8].toIntOrNull() ?: 0,minute(values[9]),minute(values[10]),minute(values[11]))
                        error=c.error().orEmpty()
                        if(error.isEmpty())onSave(c)
                    },Modifier.weight(1f))
                }
            }
        }
    }
    if(discard)AlertDialog(onDismissRequest={discard=false},title={Text("Bỏ thay đổi?")},
        confirmButton={TextButton(onClick=onDismiss){Text("Bỏ thay đổi")}},
        dismissButton={TextButton(onClick={discard=false}){Text("Tiếp tục sửa")}})
}
@Composable
private fun TaskInput(value:String,onValueChange:(String)->Unit,label:String,modifier:Modifier) {
    Column {
        Text(label,color=com.mamxanh.app.presentation.ui.Deep)
        GardenTextField(value,onValueChange,label,modifier)
    }
}
@Composable
private fun TaskChoices(title: String,options: List<Pair<String,String>>,value: String,onChange:(String)->Unit) {
    Column {
        Text(title)
        var expanded by remember { mutableStateOf(false) }
        Box {
            GardenFieldButton(options.firstOrNull{it.first==value}?.second ?: value,{expanded=true},Modifier.fillMaxWidth())
            DropdownMenu(expanded=expanded,onDismissRequest={expanded=false}) {
                options.forEach{(key,label)->
                    DropdownMenuItem(text={Text(label)},onClick={onChange(key);expanded=false})
                }
            }
        }
    }
}
