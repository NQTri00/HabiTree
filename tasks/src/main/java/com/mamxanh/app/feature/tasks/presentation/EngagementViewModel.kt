package com.mamxanh.app.feature.tasks.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mamxanh.shared.data.GameStorage
import com.mamxanh.shared.model.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

data class EngagementUiState(val snapshot:EngagementSnapshot=EngagementSnapshot(),val tasks:List<Task> = emptyList(),
    val route:String="",val selectedTask:Long=0,val busy:Boolean=false,val loading:Boolean=true,val message:String="")
class EngagementViewModel(private val storage:GameStorage,private val saved:SavedStateHandle):ViewModel() {
    private val _state=MutableStateFlow(EngagementUiState(route=saved["engagement_route"] ?: "",selectedTask=saved["engagement_task"] ?: 0))
    val state=_state.asStateFlow()
    private var foreground=false
    private var gate=false
    init {
        viewModelScope.launch {
            withContext(Dispatchers.IO){storage.engage(EngagementAction.Focus("","RECOVER"))}
            refresh()
            storage.changes.collect{refresh()}
        }
        viewModelScope.launch {
            while(isActive) {
                delay(1000)
                if(foreground) {
                    val running=_state.value.snapshot.focus.filter{it.state=="RUNNING"}
                    if(running.isNotEmpty() && !gate && !_state.value.busy) {
                        gate=true
                        try {
                            withContext(Dispatchers.IO){running.forEach{storage.engage(EngagementAction.Focus(it.id,"TICK"))}}
                            refresh()
                        } finally{gate=false}
                    }
                }
            }
        }
    }
    private suspend fun refresh() {
        val (snapshot,tasks)=withContext(Dispatchers.IO){storage.engagementSnapshot() to storage.tasks()}
        val route=if(!snapshot.onboarded)"ONBOARD" else _state.value.route.takeUnless{it=="ONBOARD"}.orEmpty()
        _state.value=_state.value.copy(snapshot=snapshot,tasks=tasks,route=route,loading=false)
    }
    fun open(route:String,taskId:Long=0) {
        saved["engagement_route"]=route;saved["engagement_task"]=taskId
        _state.value=_state.value.copy(route=route,selectedTask=taskId)
    }
    fun dismissMessage(){_state.value=_state.value.copy(message="")}
    fun foreground(active:Boolean) {
        foreground=active
        if(!active)viewModelScope.launch {
            withContext(Dispatchers.IO){storage.engage(EngagementAction.Focus("","RECOVER"))};refresh()
        } else viewModelScope.launch{refresh()}
    }
    fun act(action:EngagementAction) {
        if(_state.value.busy)return
        _state.value=_state.value.copy(busy=true)
        viewModelScope.launch {
            try {
                val result=withContext(Dispatchers.IO){storage.engage(action)}
                refresh()
                _state.value=_state.value.copy(message=result.message)
                if(result.success && action is EngagementAction.StartFocus)open("FOCUS",action.taskId)
                if(result.success && action is EngagementAction.Onboard)open("")
            } finally{_state.value=_state.value.copy(busy=false)}
        }
    }
    fun adjust(task:Task) {
        if(_state.value.busy)return
        _state.value=_state.value.copy(busy=true)
        viewModelScope.launch {
            try {
                val result=withContext(Dispatchers.IO){storage.saveTask(task.id,task.config.copy(frequency="WEEKDAYS",days=""))}
                refresh();_state.value=_state.value.copy(message=result.message)
            }finally{_state.value=_state.value.copy(busy=false)}
        }
    }
}
