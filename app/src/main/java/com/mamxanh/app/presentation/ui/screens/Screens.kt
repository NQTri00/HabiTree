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
import com.mamxanh.app.presentation.viewmodel.GardenViewModel
import com.mamxanh.app.presentation.ui.*
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TasksScreen(state:State,vm:GardenViewModel,modifier:Modifier=Modifier) {
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

@Composable
private fun GardenScreen(state:State,vm:GardenViewModel,modifier:Modifier=Modifier) {
    var selected by remember{mutableStateOf<Species?>(null)}
    var treeName by remember{mutableStateOf("")}
    LazyColumn(modifier.fillMaxSize().padding(horizontal=16.dp),contentPadding=PaddingValues(top=14.dp,bottom=28.dp),verticalArrangement=Arrangement.spacedBy(12.dp)) {
        item {
            Card(colors=CardDefaults.cardColors(containerColor=Pale),shape=RoundedCornerShape(24.dp)) {
                Column(Modifier.fillMaxWidth().padding(20.dp),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(8.dp)) {
                    Text("🌳",style=MaterialTheme.typography.displayMedium)
                    Text("Khu vườn đang lớn lên",style=MaterialTheme.typography.titleLarge,color=Deep,fontWeight=FontWeight.Bold)
                    Text("Mỗi nhiệm vụ giúp tất cả cây trong vườn phát triển. Bạn không cần mở app mỗi ngày để giữ cây sống.",color=Deep)
                    Row(horizontalArrangement=Arrangement.spacedBy(22.dp)) {
                        Metric("Số cây",state.garden.trees.size.toString())
                        Metric("Hạt giống",state.garden.inventory.values.sum().toString())
                        Metric("Xu",state.stats.coins.toString())
                    }
                }
            }
        }
        item{Text("Cây đã trồng ("+state.garden.trees.size+")",style=MaterialTheme.typography.titleMedium,color=Deep,fontWeight=FontWeight.Bold)}
        if(state.garden.trees.isEmpty()) item{EmptyCard("Khu vườn còn trống","Mua hạt giống ở Cửa hàng rồi gieo cây đầu tiên tại đây.")}
        items(state.garden.trees,key={it.id}) { tree->
            val plant=catalog.firstOrNull{it.id==tree.speciesId}?:catalog.first()
            TreeCard(tree,plant,state.garden.inventory["fertilizer"]?:0,{vm.fertilize(tree.id)})
        }
        item{Text("Hạt giống trong kho",style=MaterialTheme.typography.titleMedium,color=Deep,fontWeight=FontWeight.Bold)}
        items(catalog.filter{(state.garden.inventory[it.id]?:0)>0},key={"seed-"+it.id}) { seed->
            Card(colors=CardDefaults.cardColors(containerColor=Color.White),shape=RoundedCornerShape(16.dp)) {
                Row(Modifier.fillMaxWidth().padding(13.dp),verticalAlignment=Alignment.CenterVertically) {
                    Text(seed.emoji,style=MaterialTheme.typography.headlineMedium)
                    Column(Modifier.weight(1f).padding(start=12.dp)) {
                        Text(seed.name,color=Deep,fontWeight=FontWeight.Bold)
                        Text("Còn "+(state.garden.inventory[seed.id]?:0)+" hạt · "+seed.description,color=Muted,style=MaterialTheme.typography.bodySmall)
                    }
                    Button(onClick={selected=seed}){Text("Gieo")}
                }
            }
        }
    }
    selected?.let{seed->
        AlertDialog(onDismissRequest={selected=null},title={Text("Gieo "+seed.name)},text={
            OutlinedTextField(treeName,{treeName=it},label={Text("Đặt tên cho cây (tùy chọn)")},singleLine=true)
        },confirmButton={Button(onClick={vm.plant(seed.id,treeName);treeName="";selected=null}){Text("Gieo cây")}},
            dismissButton={TextButton(onClick={selected=null}){Text("Để sau")}})
    }
}
@Composable
private fun Metric(label:String,value:String){Column(horizontalAlignment=Alignment.CenterHorizontally){Text(value,color=Green,style=MaterialTheme.typography.titleLarge,fontWeight=FontWeight.ExtraBold);Text(label,color=Muted,style=MaterialTheme.typography.labelSmall)}}

@Composable
private fun TreeCard(tree:Tree,plant:Species,fertilizerCount:Int,onFertilize:()->Unit) {
    val stage=when{tree.growth>=120->"Cây trưởng thành";tree.growth>=50->"Cây non";tree.growth>=10->"Cây con";else->"Mầm mới gieo"}
    val progress=when{tree.growth>=120->1f;tree.growth>=50->(tree.growth-50)/70f;tree.growth>=10->(tree.growth-10)/40f;else->tree.growth/10f}.coerceIn(0f,1f)
    val visual=when{tree.growth>=120->plant.emoji;tree.growth>=50->"🌳";tree.growth>=10->"🌿";else->"🌱"}
    Card(colors=CardDefaults.cardColors(containerColor=Color.White),shape=RoundedCornerShape(20.dp)) {
        Column(Modifier.fillMaxWidth().padding(15.dp),verticalArrangement=Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment=Alignment.CenterVertically) {
                Box(Modifier.size(66.dp).clip(RoundedCornerShape(17.dp)).background(Pale),contentAlignment=Alignment.Center){Text(visual,style=MaterialTheme.typography.displaySmall)}
                Column(Modifier.weight(1f).padding(start=12.dp)) {
                    Text(tree.name,color=Deep,style=MaterialTheme.typography.titleMedium,fontWeight=FontWeight.Bold)
                    Text(plant.name+" · "+stage,color=Green,style=MaterialTheme.typography.labelMedium)
                    Text(tree.growth.toString()+" / 120 điểm phát triển",color=Muted,style=MaterialTheme.typography.labelSmall)
                }
            }
            LinearProgressIndicator(progress={progress},modifier=Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),color=Green,trackColor=Pale)
            Row(verticalAlignment=Alignment.CenterVertically) {
                Text(if(tree.growth>=120)"Cây đã trưởng thành 🌼" else "Làm nhiệm vụ để cây lớn thêm",color=Muted,style=MaterialTheme.typography.bodySmall,modifier=Modifier.weight(1f))
                if(fertilizerCount>0) TextButton(onClick=onFertilize){Text("Bón phân ("+fertilizerCount+")")}
            }
        }
    }
}

@Composable
private fun ShopScreen(state:State,vm:GardenViewModel,modifier:Modifier=Modifier) {
    LazyColumn(modifier.fillMaxSize().padding(horizontal=16.dp),contentPadding=PaddingValues(top=14.dp,bottom=28.dp),verticalArrangement=Arrangement.spacedBy(12.dp)) {
        item {
            Card(colors=CardDefaults.cardColors(containerColor=Gold),shape=RoundedCornerShape(22.dp)) {
                Row(Modifier.fillMaxWidth().padding(18.dp),verticalAlignment=Alignment.CenterVertically) {
                    Text("🪙",style=MaterialTheme.typography.displaySmall)
                    Column(Modifier.padding(start=14.dp)) {
                        Text("Ví khu vườn",color=Deep,fontWeight=FontWeight.Bold)
                        Text(state.stats.coins.toString()+" xu",color=Deep,style=MaterialTheme.typography.headlineMedium,fontWeight=FontWeight.ExtraBold)
                    }
                }
            }
        }
        item{Text("Hạt giống",color=Deep,style=MaterialTheme.typography.titleLarge,fontWeight=FontWeight.Bold)}
        items(catalog,key={it.id}){seed->
            Card(colors=CardDefaults.cardColors(containerColor=Color.White),shape=RoundedCornerShape(18.dp)) {
                Row(Modifier.fillMaxWidth().padding(13.dp),verticalAlignment=Alignment.CenterVertically) {
                    Box(Modifier.size(52.dp).clip(RoundedCornerShape(14.dp)).background(Pale),contentAlignment=Alignment.Center){Text(seed.emoji,style=MaterialTheme.typography.headlineMedium)}
                    Column(Modifier.weight(1f).padding(start=11.dp)) {
                        Text(seed.name,color=Deep,fontWeight=FontWeight.Bold)
                        Text(seed.description,color=Muted,style=MaterialTheme.typography.bodySmall)
                        Text("Trong kho: "+(state.garden.inventory[seed.id]?:0),color=Green,style=MaterialTheme.typography.labelSmall)
                    }
                    Column(horizontalAlignment=Alignment.End) {
                        Text("🪙 "+seed.price,color=Deep,fontWeight=FontWeight.Bold)
                        Button(onClick={vm.buy(seed.id)},enabled=state.stats.coins>=seed.price){Text("Mua")}
                    }
                }
            }
        }
        item {
            Card(colors=CardDefaults.cardColors(containerColor=Color.White),shape=RoundedCornerShape(18.dp)) {
                Row(Modifier.fillMaxWidth().padding(14.dp),verticalAlignment=Alignment.CenterVertically) {
                    Text("🧪",style=MaterialTheme.typography.headlineMedium)
                    Column(Modifier.weight(1f).padding(start=12.dp)) {
                        Text("Phân bón hữu cơ",color=Deep,fontWeight=FontWeight.Bold)
                        Text("Một cây tăng ngay 25 điểm phát triển.",color=Muted,style=MaterialTheme.typography.bodySmall)
                        Text("Trong kho: "+(state.garden.inventory["fertilizer"]?:0),color=Green,style=MaterialTheme.typography.labelSmall)
                    }
                    Column(horizontalAlignment=Alignment.End) {
                        Text("🪙 25",color=Deep,fontWeight=FontWeight.Bold)
                        Button(onClick=vm::buyFertilizer,enabled=state.stats.coins>=25){Text("Mua")}
                    }
                }
            }
        }
        item{Text("Gieo hạt giống đã mua trong tab Khu vườn.",color=Muted,style=MaterialTheme.typography.bodySmall)}
    }
}

@Composable
private fun StatsScreen(state:State,modifier:Modifier=Modifier) {
    val s=state.stats
    LazyColumn(modifier.fillMaxSize().padding(16.dp),verticalArrangement=Arrangement.spacedBy(12.dp),contentPadding=PaddingValues(bottom=28.dp)) {
        item {
            Text("Dấu mốc của khu vườn",color=Deep,style=MaterialTheme.typography.headlineSmall,fontWeight=FontWeight.Bold)
            Text("Tiến bộ được ghi nhận ngay trên thiết bị này.",color=Muted)
        }
        item{Row(horizontalArrangement=Arrangement.spacedBy(10.dp)){StatCard("🌱","Tổng hoàn thành",s.total.toString(),Modifier.weight(1f));StatCard("🔥","Chuỗi ngày",s.streak.toString(),Modifier.weight(1f))}}
        item{Row(horizontalArrangement=Arrangement.spacedBy(10.dp)){StatCard("☀️","Hôm nay",s.today.toString(),Modifier.weight(1f));StatCard("📅","Tuần này",s.week.toString(),Modifier.weight(1f))}}
        item{Row(horizontalArrangement=Arrangement.spacedBy(10.dp)){StatCard("🌳","Cây đã trồng",s.treeCount.toString(),Modifier.weight(1f));StatCard("🪙","Xu hiện có",s.coins.toString(),Modifier.weight(1f))}}
        item {
            Card(colors=CardDefaults.cardColors(containerColor=Pale),shape=RoundedCornerShape(20.dp)) {
                Column(Modifier.fillMaxWidth().padding(18.dp),verticalArrangement=Arrangement.spacedBy(8.dp)) {
                    Text("Một khu vườn không lớn lên trong một ngày.",color=Deep,fontWeight=FontWeight.Bold,style=MaterialTheme.typography.titleMedium)
                    Text("Mỗi lần hoàn thành nhiệm vụ, bạn đang đầu tư vào khu vườn của mình. Hãy giữ nhịp độ vừa sức và quay lại vào ngày mai.",color=Deep)
                }
            }
        }
    }
}
@Composable
private fun StatCard(emoji:String,label:String,value:String,modifier:Modifier=Modifier) {
    Card(modifier,colors=CardDefaults.cardColors(containerColor=Color.White),shape=RoundedCornerShape(18.dp)) {
        Column(Modifier.fillMaxWidth().padding(15.dp),verticalArrangement=Arrangement.spacedBy(5.dp)) {
            Text(emoji,style=MaterialTheme.typography.titleLarge)
            Text(value,color=Deep,style=MaterialTheme.typography.headlineMedium,fontWeight=FontWeight.ExtraBold)
            Text(label,color=Muted,style=MaterialTheme.typography.labelSmall)
        }
    }
}
@Composable
private fun EmptyCard(title:String,description:String) {
    Card(colors=CardDefaults.cardColors(containerColor=Color.White),shape=RoundedCornerShape(18.dp)) {
        Column(Modifier.fillMaxWidth().padding(22.dp),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(8.dp)) {
            Text("🍃",style=MaterialTheme.typography.displaySmall)
            Text(title,color=Deep,fontWeight=FontWeight.Bold)
            Text(description,color=Muted,style=MaterialTheme.typography.bodySmall)
        }
    }
}
