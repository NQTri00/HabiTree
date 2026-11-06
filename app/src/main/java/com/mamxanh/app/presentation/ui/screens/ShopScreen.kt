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
fun ShopScreen(state:State,vm:GardenViewModel,modifier:Modifier=Modifier) {
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

