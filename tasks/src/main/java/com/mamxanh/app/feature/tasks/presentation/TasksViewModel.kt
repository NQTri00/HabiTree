package com.mamxanh.app.feature.tasks.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mamxanh.app.feature.tasks.domain.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TasksViewModel(private val repository: TaskRepository, private val savedState: SavedStateHandle) : ViewModel() {
    private val _uiState = MutableStateFlow(TasksUiState(
        isAddingTask = savedState["isAddingTask"] ?: false,
        draft = savedState.get<ArrayList<String>>("draft").orEmpty(),
        filter = TaskFilter.entries.firstOrNull { it.name == savedState.get<String>("filter") } ?: TaskFilter.ALL
    ))
    val uiState = _uiState.asStateFlow()
    private val effectChannel = Channel<TasksEffect>(Channel.BUFFERED)
    val effects = effectChannel.receiveAsFlow()

    init { dispatch(TasksIntent.Refresh) }

    private fun setAddingTask(adding: Boolean) {
        savedState["isAddingTask"] = adding
        if (!adding) savedState["draft"] = arrayListOf<String>()
        _uiState.value = _uiState.value.copy(isAddingTask = adding,
            draft = if (adding) _uiState.value.draft else emptyList())
    }

    fun dispatch(intent: TasksIntent) {
        when (intent) {
            is TasksIntent.UpdateDraft -> if (_uiState.value.isAddingTask) {
                savedState["draft"] = ArrayList(intent.values)
                _uiState.value = _uiState.value.copy(draft = intent.values)
            }
            TasksIntent.ShowAddDialog -> setAddingTask(true)
            TasksIntent.DismissAddDialog -> setAddingTask(false)
            TasksIntent.Refresh -> viewModelScope.launch {
                _uiState.value = _uiState.value.copy(isLoading = true)
                val (tasks, claimed, eligible) = withContext(Dispatchers.IO) {
                    Triple(LoadTasks(repository)(), repository.isDailyBonusClaimed(), repository.canClaimDailyBonus())
                }
                _uiState.value = _uiState.value.copy(tasks = tasks, isDailyBonusClaimed = claimed,
                    canClaimDailyBonus = eligible, isLoading = false)
            }
            is TasksIntent.SetFilter -> {
                savedState["filter"] = intent.filter.name
                _uiState.value = _uiState.value.copy(filter = intent.filter)
            }
            is TasksIntent.Add -> viewModelScope.launch {
                if (!_uiState.value.isAddingTask || intent.title.isBlank() || intent.reward !in 1..100) return@launch
                // Consume this dialog submission before suspending so repeated taps cannot insert twice.
                setAddingTask(false)
                withContext(Dispatchers.IO) { AddTask(repository)(intent.title.trim(), intent.kind, intent.category, intent.reward, intent.frequency, intent.due) }
                dispatch(TasksIntent.Refresh)
                effectChannel.send(TasksEffect.DataChanged)
                effectChannel.send(TasksEffect.Feedback("Đã thêm nhiệm vụ"))
            }
            is TasksIntent.Delete -> viewModelScope.launch {
                withContext(Dispatchers.IO) { DeleteTask(repository)(intent.id) }
                dispatch(TasksIntent.Refresh)
                effectChannel.send(TasksEffect.DataChanged)
                effectChannel.send(TasksEffect.Feedback("Đã xóa nhiệm vụ"))
            }
            is TasksIntent.Complete -> viewModelScope.launch {
                val reward = _uiState.value.tasks.firstOrNull { it.id == intent.id }?.reward
                val done = withContext(Dispatchers.IO) { CompleteTask(repository)(intent.id) }
                dispatch(TasksIntent.Refresh)
                if (done) effectChannel.send(TasksEffect.DataChanged)
                effectChannel.send(TasksEffect.Feedback(if (done) {
                    if (reward != null) "+$reward xu · Tất cả cây đã lớn thêm!"
                    else "Bạn nhận xu và tất cả cây đã lớn thêm!"
                } else "Nhiệm vụ này đã hoàn thành hôm nay"))
            }
            TasksIntent.ClaimBonus -> viewModelScope.launch {
                val claimed = withContext(Dispatchers.IO) { ClaimDailyBonus(repository)() }
                dispatch(TasksIntent.Refresh)
                effectChannel.send(TasksEffect.DataChanged)
                effectChannel.send(TasksEffect.Feedback(if (claimed) "Tuyệt vời! Bạn nhận thêm 20 xu." else "Hoàn thành mọi nhiệm vụ hằng ngày để nhận thưởng."))
            }
        }
    }
}
