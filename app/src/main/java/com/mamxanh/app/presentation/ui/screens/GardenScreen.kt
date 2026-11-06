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
@Composable
fun GardenScreen(state:State,vm:GardenViewModel,modifier:Modifier=Modifier) {
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

