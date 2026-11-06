package com.mamxanh.app.presentation.ui


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
import com.mamxanh.app.presentation.ui.screens.*
val Green=Color(0xFF356B45); val Deep=Color(0xFF203D2B); val Pale=Color(0xFFE6F1E5)
val Cream=Color(0xFFF7F6EE); val Gold=Color(0xFFFFE8A8); val Muted=Color(0xFF718075)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MamXanhApp(vm:GardenViewModel) {
    val state by vm.state.collectAsState()
    var page by remember { mutableStateOf(Page.TASKS) }
    var adding by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }
    LaunchedEffect(state.message) { if(state.message.isNotBlank()) message=state.message }
    MaterialTheme(colorScheme=lightColorScheme(primary=Green,secondary=Color(0xFF9A6A31),background=Cream,surface=Color.White)) {
        Scaffold(containerColor=Cream,topBar={
            TopAppBar(title={ Column {
                Text("Mầm Xanh",fontWeight=FontWeight.ExtraBold,color=Deep)
                Text("Nuôi dưỡng thói quen, lớn lên cùng cây",style=MaterialTheme.typography.labelSmall,color=Green)
            } },actions={
                Surface(color=Gold,shape=RoundedCornerShape(50),modifier=Modifier.padding(end=14.dp)) {
                    Text("🪙 "+state.stats.coins,modifier=Modifier.padding(horizontal=13.dp,vertical=8.dp),color=Deep,fontWeight=FontWeight.Bold)
                }
            })
        },floatingActionButton={
            if(page==Page.TASKS) FloatingActionButton(onClick={adding=true},containerColor=Green,contentColor=Color.White) {
                Icon(Icons.Rounded.Add,contentDescription="Thêm nhiệm vụ")
            }
        },bottomBar={
            NavigationBar(containerColor=Color.White) {
                Page.entries.forEach { item ->
                    val icon=when(item) {
                        Page.TASKS->Icons.Rounded.Check; Page.GARDEN->Icons.Rounded.Forest
                        Page.SHOP->Icons.Rounded.Store; Page.STATS->Icons.Rounded.Insights
                    }
                    NavigationBarItem(selected=page==item,onClick={page=item},icon={Icon(icon,contentDescription=item.label)},label={Text(item.label)})
                }
            }
        }) { padding ->
            when(page) {
                Page.TASKS->TasksScreen(state,vm,Modifier.padding(padding))
                Page.GARDEN->GardenScreen(state,vm,Modifier.padding(padding))
                Page.SHOP->ShopScreen(state,vm,Modifier.padding(padding))
                Page.STATS->StatsScreen(state,Modifier.padding(padding))
            }
        }
        if(adding) AddTaskDialog(onDismiss={adding=false},onSave={title,kind,category,reward,frequency,due ->
            vm.add(title,kind,category,reward,frequency,due); adding=false
        })
        if(message.isNotBlank()) AlertDialog(onDismissRequest={message=""},confirmButton={TextButton(onClick={message=""}){Text("Đóng")}},text={Text(message)})
    }
}

