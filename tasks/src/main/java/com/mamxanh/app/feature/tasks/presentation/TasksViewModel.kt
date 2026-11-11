package com.mamxanh.app.feature.tasks.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mamxanh.app.feature.tasks.domain.*
import com.mamxanh.shared.model.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*

class TasksViewModel(private val repository: TaskRepository,private val savedState: SavedStateHandle): ViewModel() {
    private val _uiState=MutableStateFlow(TasksUiState(
        isAddingTask=savedState["isAddingTask"] ?: false,
        editingId=savedState["editingId"],
        draft=savedState.get<ArrayList<String>>("draft").orEmpty()))
    val uiState=_uiState.asStateFlow()
    private val effectsChannel=Channel<TasksEffect>(Channel.BUFFERED)
    val effects=effectsChannel.receiveAsFlow()
    private var foreground=false
    init {
        viewModelScope.launch { repository.changes.collect { refresh() } }
        viewModelScope.launch {
            // Persisted sessions recover paused, never accrue time while the process was absent.
            withContext(Dispatchers.IO) { repository.snapshot().sessions.filter { it.running }.forEach { repository.timer(it.taskId,"PAUSE") } }
            while(isActive) {
                delay(1000)
                if(foreground) {
                    withContext(Dispatchers.IO) { repository.snapshot().sessions.filter{it.running}.forEach{repository.timer(it.taskId,"TICK")} }
                    refresh()
                }
            }
        }
    }
    private suspend fun refresh() {
        val value=withContext(Dispatchers.IO) { Triple(repository.observeSnapshot(),repository.snapshot(),repository.isDailyBonusClaimed()) }
        val eligible=withContext(Dispatchers.IO) { repository.canClaimDailyBonus() }
        _uiState.value=_uiState.value.copy(tasks=value.first,phase=value.second,isDailyBonusClaimed=value.third,
            canClaimDailyBonus=eligible,isLoading=false)
    }
    private fun close() {
        savedState["isAddingTask"]=false; savedState["editingId"]=null; savedState["draft"]=arrayListOf<String>()
        _uiState.value=_uiState.value.copy(isAddingTask=false,editingId=null,draft=emptyList())
    }
    private fun mutate(showSuccess: Boolean = true,block: ()->ActionResult) {
        if(_uiState.value.busy)return
        _uiState.value=_uiState.value.copy(busy=true)
        viewModelScope.launch {
            try {
                val result=withContext(Dispatchers.IO){block()}
                refresh()
                if(showSuccess || !result.success)effectsChannel.send(TasksEffect.Feedback(result.message))
                if(result.success)effectsChannel.send(TasksEffect.DataChanged)
            } finally { _uiState.value=_uiState.value.copy(busy=false) }
        }
    }
    fun dispatch(intent: TasksIntent) {
        when(intent) {
            TasksIntent.Refresh -> viewModelScope.launch{refresh()}
            is TasksIntent.Foreground -> {
                foreground=intent.active
                if(!foreground)viewModelScope.launch { withContext(Dispatchers.IO) {
                    repository.snapshot().sessions.filter{it.running}.forEach{repository.timer(it.taskId,"PAUSE")}
                };refresh() }
            }
            is TasksIntent.UpdateDraft -> {
                savedState["draft"]=ArrayList(intent.values)
                _uiState.value=_uiState.value.copy(draft=intent.values)
            }
            TasksIntent.ShowAddDialog -> {
                savedState["isAddingTask"]=true
                _uiState.value=_uiState.value.copy(isAddingTask=true,editingId=null)
            }
            TasksIntent.DismissAddDialog -> close()
            is TasksIntent.Detail -> _uiState.value=_uiState.value.copy(detailId=intent.id)
            is TasksIntent.Edit -> {
                savedState["isAddingTask"]=true; savedState["editingId"]=intent.id
                _uiState.value=_uiState.value.copy(isAddingTask=true,editingId=intent.id,detailId=null,draft=emptyList())
            }
            is TasksIntent.Save -> {
                val id=_uiState.value.editingId
                if(intent.config.error()!=null)return
                mutate { repository.save(id,intent.config) };close()
            }
            is TasksIntent.Lifecycle -> mutate{repository.lifecycle(intent.id,intent.state)}
            is TasksIntent.Timer -> mutate(showSuccess=false){repository.timer(intent.id,intent.action)}
            is TasksIntent.Complete -> mutate{repository.completeResult(intent.id)}
            is TasksIntent.Delete -> mutate{repository.lifecycle(intent.id,"ARCHIVED")}
            is TasksIntent.SetFilter -> _uiState.value=_uiState.value.copy(filter=intent.filter)
            is TasksIntent.Add -> mutate{repository.save(null,TaskConfig(intent.title,intent.kind,intent.category,frequency=intent.frequency,due=intent.due))}
            TasksIntent.ClaimBonus -> mutate {
                val before=repository.snapshot().earnedToday
                val success=repository.claimDailyBonus()
                val after=repository.snapshot().earnedToday
                ActionResult(success,if(success)"Daily Clear +${after-before} xu · Giới hạn 50 xu/ngày" else "Hoàn thành kế hoạch bắt buộc")
            }
        }
    }
}
