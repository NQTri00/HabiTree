package com.mamxanh.app.presentation.viewmodel


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


import com.mamxanh.app.data.local.GardenDatabase
import com.mamxanh.app.data.repository.OfflineGameRepository
import com.mamxanh.app.domain.usecase.GameUseCases
import com.mamxanh.app.domain.model.*
import com.mamxanh.app.domain.model.State
class GardenViewModel(context: Context) : ViewModel() {
    private val useCases: GameUseCases = GameUseCases(OfflineGameRepository(GardenDatabase(context)))
    private val _state=MutableStateFlow(State())
    val state=_state.asStateFlow()
    init { refresh() }
    fun refresh(message: String="") {
        viewModelScope.launch {
            val next=withContext(Dispatchers.IO) { State(useCases.loadTasks(),useCases.loadGarden(),useCases.loadStatistics(),false,message) }
            _state.value=next
        }
    }
    fun add(title:String,kind:String,category:String,reward:Int,frequency:String,due:String) = viewModelScope.launch(Dispatchers.IO) {
        if(title.isNotBlank()) useCases.addTask(title,kind,category,reward,frequency,due)
        refresh("Đã thêm nhiệm vụ")
    }
    fun delete(id:Long)=viewModelScope.launch(Dispatchers.IO) { useCases.deleteTask(id); refresh("Đã xóa nhiệm vụ") }
    fun complete(id:Long)=viewModelScope.launch(Dispatchers.IO) {
        val ok=useCases.completeTask(id); refresh(if(ok) "Bạn nhận xu và tất cả cây đã lớn thêm!" else "Nhiệm vụ này đã hoàn thành hôm nay")
    }
    fun bonus()=viewModelScope.launch(Dispatchers.IO) { refresh(if(useCases.claimDailyBonus()) "Tuyệt vời! Bạn nhận thêm 20 xu." else "Hoàn thành mọi nhiệm vụ hằng ngày để nhận thưởng.") }
    fun buy(id:String)=viewModelScope.launch(Dispatchers.IO) { refresh(if(useCases.buySeed(id)) "Đã mua hạt giống." else "Bạn chưa đủ xu.") }
    fun plant(id:String,name:String)=viewModelScope.launch(Dispatchers.IO) { refresh(if(useCases.plantSeed(id,name)) "Hạt giống đã được gieo." else "Bạn không còn hạt giống này.") }
    fun buyFertilizer()=viewModelScope.launch(Dispatchers.IO) { refresh(if(useCases.buyFertilizer()) "Đã mua phân bón." else "Bạn cần ít nhất 25 xu.") }
    fun fertilize(id:Long)=viewModelScope.launch(Dispatchers.IO) { refresh(if(useCases.fertilizeTree(id)) "Cây đã lớn thêm 25 điểm." else "Bạn chưa có phân bón.") }
    companion object {
        fun factory(context:Context)=object:ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T:ViewModel> create(modelClass:Class<T>):T {
                if(modelClass.isAssignableFrom(GardenViewModel::class.java)) return GardenViewModel(context) as T
                error("Unknown ViewModel")
            }
        }
    }
}

