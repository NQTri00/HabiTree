package com.mamxanh.app.feature.tasks.presentation.ui.engagement
import com.mamxanh.app.feature.tasks.presentation.ui.form.message

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.mamxanh.app.core.presentation.BaseViewModel
import com.mamxanh.app.core.presentation.UiEvent
import com.mamxanh.app.feature.tasks.domain.repository.FocusSessionCommands
import com.mamxanh.shared.data.GameStorage
import com.mamxanh.shared.model.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class EngagementViewModel(
    private val storage: GameStorage,
    private val saved: SavedStateHandle,
    private val focusCommands: FocusSessionCommands,
) : BaseViewModel<EngagementUiState, UiEvent, EngagementUiAction>(
    initialState = EngagementUiState(route=saved["engagement_route"] ?: "",selectedTask=saved["engagement_task"] ?: 0),
) {
    private var foreground=false
    private var gate=false
    init {
        viewModelScope.launch {
            withContext(Dispatchers.IO) { focusCommands.recover() }
            refresh()
            storage.changes.collect{refresh()}
        }
        viewModelScope.launch {
            while(isActive) {
                delay(1000)
                if(foreground) {
                    val running=mutableState.value.snapshot.focus.filter{it.state=="RUNNING"}
                    if(running.isNotEmpty() && !gate && !mutableState.value.busy) {
                        gate=true
                        try {
                            withContext(Dispatchers.IO) { running.forEach { focusCommands.tick(it.id) } }
                            refresh()
                        } finally{gate=false}
                    }
                }
            }
        }
    }
    private suspend fun refresh() {
        val (snapshot,tasks)=withContext(Dispatchers.IO){storage.engagementSnapshot() to storage.tasks()}
        val route=if(!snapshot.onboarded)"ONBOARD" else mutableState.value.route.takeUnless{it=="ONBOARD"}.orEmpty()
        mutableState.value=mutableState.value.copy(snapshot=snapshot,tasks=tasks,route=route,loading=false)
    }
    fun open(route:String,taskId:Long=0) {
        saved["engagement_route"]=route;saved["engagement_task"]=taskId
        mutableState.value=mutableState.value.copy(route=route,selectedTask=taskId)
    }
    fun dismissMessage(){mutableState.value=mutableState.value.copy(message="")}
    fun foreground(active:Boolean) {
        foreground=active
        if(!active)viewModelScope.launch {
            withContext(Dispatchers.IO) { focusCommands.recover() }
            refresh()
        } else viewModelScope.launch{refresh()}
    }
    fun act(action: EngagementAction) = dispatch(EngagementUiAction(action))

    override fun handleAction(action: EngagementUiAction) {
        perform(action.mutation)
    }

    private fun perform(action: EngagementAction) {
        if(mutableState.value.busy)return
        mutableState.value=mutableState.value.copy(busy=true)
        viewModelScope.launch {
            try {
                val result=withContext(Dispatchers.IO){storage.engage(action)}
                refresh()
                mutableState.value=mutableState.value.copy(message=result.message)
                if(result.success && action is EngagementAction.StartFocus)open("FOCUS",action.taskId)
                if(result.success && action is EngagementAction.Onboard)open("")
            } finally{mutableState.value=mutableState.value.copy(busy=false)}
        }
    }
    fun adjust(task:Task) {
        if(mutableState.value.busy)return
        mutableState.value=mutableState.value.copy(busy=true)
        viewModelScope.launch {
            try {
                val result=withContext(Dispatchers.IO){storage.saveTask(task.id,task.config.copy(frequency="WEEKDAYS",days=""))}
                refresh();mutableState.value=mutableState.value.copy(message=result.message)
            }finally{mutableState.value=mutableState.value.copy(busy=false)}
        }
    }
}
