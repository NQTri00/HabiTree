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
fun StatsScreen(state:State,modifier:Modifier=Modifier) {
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
