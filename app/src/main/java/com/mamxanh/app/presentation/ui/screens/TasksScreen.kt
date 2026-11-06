package com.mamxanh.app.presentation.ui.screens


import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Forest
import androidx.compose.material.icons.rounded.Insights
import androidx.compose.material.icons.rounded.Store
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


import com.mamxanh.app.domain.model.*
import com.mamxanh.app.domain.model.State
import com.mamxanh.app.presentation.viewmodel.GardenViewModel
import com.mamxanh.app.presentation.ui.*
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(state:State,vm:GardenViewModel,modifier:Modifier=Modifier) {
    var filter by remember { mutableStateOf("Tất cả") }
    val filters=listOf("Tất cả","Thói quen","Hằng ngày","Một lần")
    val active=state.tasks.filter { !it.finished && when(filter) {
        "Thói quen"->it.kind=="HABIT"; "Hằng ngày"->it.kind=="DAILY"; "Một lần"->it.kind=="TODO"; else->true
    } }
    val done=state.tasks.count { it.doneToday }
    val daily=state.tasks.filter { it.kind=="DAILY" }
    LazyColumn(modifier.fillMaxSize().padding(horizontal=16.dp),contentPadding=PaddingValues(top=12.dp,bottom=28.dp),verticalArrangement=Arrangement.spacedBy(12.dp)) {
        item {
            Card(colors=CardDefaults.cardColors(containerColor=Pale),shape=RoundedCornerShape(24.dp)) {
                Column(Modifier.fillMaxWidth().padding(18.dp),verticalArrangement=Arrangement.spacedBy(9.dp)) {
                    Text("Chào ngày mới! 🌿",color=Deep,style=MaterialTheme.typography.titleLarge,fontWeight=FontWeight.Bold)
                    Text("Mỗi việc nhỏ hôm nay là một vòng cây lớn lên.",color=Deep)
                    Row(verticalAlignment=Alignment.CenterVertically) {
                        Column { Text("$done / "+state.tasks.size,color=Green,style=MaterialTheme.typography.headlineSmall,fontWeight=FontWeight.ExtraBold); Text("việc đã hoàn thành",color=Muted,style=MaterialTheme.typography.labelSmall) }
                        Spacer(Modifier.weight(1f))
                        Text((done.toFloat()/state.tasks.size.coerceAtLeast(1)*100).toInt().toString()+"%",color=Green,fontWeight=FontWeight.Bold)
                    }
                    LinearProgressIndicator(progress={done.toFloat()/state.tasks.size.coerceAtLeast(1)},modifier=Modifier.fillMaxWidth().height(9.dp).clip(CircleShape),color=Green,trackColor=Color.White)
                    Text("Mỗi nhiệm vụ hoàn thành giúp cây tăng điểm phát triển.",color=Muted,style=MaterialTheme.typography.labelSmall)
                }
            }
        }
        item {
            ScrollableTabRow(selectedTabIndex=filters.indexOf(filter),edgePadding=0.dp,containerColor=Cream,divider={}) {
                filters.forEach { label-> Tab(selected=filter==label,onClick={filter=label},text={Text(label)}) }
            }
        }
        if(active.isEmpty()) item { EmptyCard("Không có nhiệm vụ ở mục này","Nhấn nút + để tạo thói quen hoặc nhiệm vụ mới.") }
        items(active,key={it.id}) { task->TaskCard(task,{vm.complete(task.id)},{vm.delete(task.id)}) }
        if(daily.isNotEmpty()) item {
            Card(colors=CardDefaults.cardColors(containerColor=Color(0xFFFFF2D6)),shape=RoundedCornerShape(20.dp)) {
                Row(Modifier.fillMaxWidth().padding(15.dp),verticalAlignment=Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("🌟 Thưởng hoàn thành ngày",fontWeight=FontWeight.Bold,color=Deep)
                        Text("Hoàn thành mọi nhiệm vụ hằng ngày để nhận thêm 20 xu.",style=MaterialTheme.typography.bodySmall,color=Muted)
                    }
                    Button(onClick=vm::bonus,enabled=daily.all{it.doneToday}){Text("Nhận")}
                }
            }
        }
        val finished=state.tasks.filter{it.finished}
        if(finished.isNotEmpty()) {
            item { Text("Đã hoàn tất",color=Muted,fontWeight=FontWeight.Bold) }
            items(finished,key={"finished-"+it.id}) { TaskCard(it,{}, {vm.delete(it.id)},true) }
        }
    }
}

@Composable
private fun TaskCard(task:Task,onComplete:()->Unit,onDelete:()->Unit,readOnly:Boolean=false) {
    var askDelete by remember{mutableStateOf(false)}
    val done=task.doneToday
    Card(colors=CardDefaults.cardColors(containerColor=Color.White),shape=RoundedCornerShape(18.dp)) {
        Row(Modifier.fillMaxWidth().padding(12.dp),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(10.dp)) {
            Box(Modifier.size(42.dp).clip(CircleShape).background(if(done||readOnly)Pale else Cream),contentAlignment=Alignment.Center) {
                Text(when(task.kind){"HABIT"->"🌱";"DAILY"->"☀️";else->"📌"})
            }
            Column(Modifier.weight(1f),verticalArrangement=Arrangement.spacedBy(3.dp)) {
                Text(task.title,color=if(done||readOnly)Muted else Deep,fontWeight=FontWeight.SemiBold,textDecoration=if(done||readOnly)TextDecoration.LineThrough else TextDecoration.None)
                Text(task.category+" · +"+task.reward+" xu",style=MaterialTheme.typography.labelSmall,color=Green)
                val repeat=when(task.frequency){"WEEKDAYS"->"Thứ 2 – Thứ 6";"WEEKENDS"->"Cuối tuần";"ONCE"->if(task.due.isBlank())"Không lặp lại" else "Hạn: "+task.due;else->"Mỗi ngày"}
                Text(repeat+(if(task.streak>1)" · 🔥 "+task.streak+" ngày" else ""),style=MaterialTheme.typography.labelSmall,color=Muted)
            }
            if(readOnly) Text("Hoàn tất",color=Green,style=MaterialTheme.typography.labelSmall)
            else {
                if(done) Icon(Icons.Rounded.Check,contentDescription="Đã xong",tint=Green)
                else FilledTonalButton(onClick=onComplete,contentPadding=PaddingValues(horizontal=12.dp,vertical=8.dp)){Text("Xong")}
                IconButton(onClick={askDelete=true}){Icon(Icons.Rounded.Delete,contentDescription="Xóa",tint=Muted)}
            }
        }
    }
    if(askDelete) AlertDialog(onDismissRequest={askDelete=false},title={Text("Xóa nhiệm vụ?")},text={Text("“"+task.title+"” sẽ bị xóa khỏi danh sách.")},
        confirmButton={TextButton(onClick={onDelete();askDelete=false}){Text("Xóa")}},dismissButton={TextButton(onClick={askDelete=false}){Text("Giữ lại")}})
}

@Composable
private fun AddTaskDialog(onDismiss:()->Unit,onSave:(String,String,String,Int,String,String)->Unit) {
    var title by remember{mutableStateOf("")}
    var kind by remember{mutableStateOf("DAILY")}
    var category by remember{mutableStateOf("Học tập")}
    var reward by remember{mutableStateOf("10")}
    var frequency by remember{mutableStateOf("EVERY_DAY")}
    var due by remember{mutableStateOf("")}
    var categoryMenu by remember{mutableStateOf(false)}
    AlertDialog(onDismissRequest=onDismiss,title={Text("Tạo nhiệm vụ mới",fontWeight=FontWeight.Bold)},text={
        Column(verticalArrangement=Arrangement.spacedBy(9.dp)) {
            OutlinedTextField(title,{title=it},label={Text("Tên nhiệm vụ")},singleLine=true)
            Text("Loại nhiệm vụ",fontWeight=FontWeight.SemiBold)
            Row(horizontalArrangement=Arrangement.spacedBy(4.dp)) {
                listOf("HABIT" to "Thói quen","DAILY" to "Hằng ngày","TODO" to "Một lần").forEach{(v,l)->
                    FilterChip(selected=kind==v,onClick={kind=v;frequency=if(v=="TODO")"ONCE" else "EVERY_DAY"},label={Text(l)})
                }
            }
            Box {
                OutlinedButton(onClick={categoryMenu=true}){Text(category)}
                DropdownMenu(expanded=categoryMenu,onDismissRequest={categoryMenu=false}) {
                    listOf("Học tập","Sức khỏe","Nếp sống","Công việc","Phát triển bản thân","Khác").forEach{
                        DropdownMenuItem(text={Text(it)},onClick={category=it;categoryMenu=false})
                    }
                }
            }
            if(kind!="TODO") Row(horizontalArrangement=Arrangement.spacedBy(3.dp)) {
                listOf("EVERY_DAY" to "Mỗi ngày","WEEKDAYS" to "Ngày thường","WEEKENDS" to "Cuối tuần").forEach{(v,l)->
                    FilterChip(selected=frequency==v,onClick={frequency=v},label={Text(l)})
                }
            } else OutlinedTextField(due,{due=it},label={Text("Hạn chót YYYY-MM-DD (tùy chọn)")},singleLine=true)
            OutlinedTextField(reward,{reward=it.filter(Char::isDigit).take(3)},label={Text("Xu thưởng (1–100)")},singleLine=true)
        }
    },confirmButton={Button(onClick={onSave(title,kind,category,reward.toIntOrNull()?:10,frequency,due)},enabled=title.isNotBlank()){Text("Tạo")}},
        dismissButton={TextButton(onClick=onDismiss){Text("Hủy")}})
}

