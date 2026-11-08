package com.mamxanh.app.feature.tasks.presentation

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

class TasksViewModel(private val repository: TaskRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(TasksUiState())
    val uiState = _uiState.asStateFlow()
    private val effectChannel = Channel<TasksEffect>(Channel.BUFFERED)
    val effects = effectChannel.receiveAsFlow()

    init { dispatch(TasksIntent.Refresh) }

    fun dispatch(intent: TasksIntent) {
        when (intent) {
            TasksIntent.ShowAddDialog -> _uiState.value = _uiState.value.copy(isAddingTask = true)
            TasksIntent.DismissAddDialog -> _uiState.value = _uiState.value.copy(isAddingTask = false)
            TasksIntent.Refresh -> viewModelScope.launch {
                _uiState.value = _uiState.value.copy(isLoading = true)
                val tasks = withContext(Dispatchers.IO) { LoadTasks(repository)() }
                _uiState.value = _uiState.value.copy(tasks = tasks, isLoading = false)
            }
            is TasksIntent.SetFilter -> _uiState.value = _uiState.value.copy(filter = intent.filter)
            is TasksIntent.Add -> viewModelScope.launch {
                if (intent.title.isBlank()) return@launch
                withContext(Dispatchers.IO) { AddTask(repository)(intent.title, intent.kind, intent.category, intent.reward, intent.frequency, intent.due) }
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
                val done = withContext(Dispatchers.IO) { CompleteTask(repository)(intent.id) }
                dispatch(TasksIntent.Refresh)
                effectChannel.send(TasksEffect.DataChanged)
                effectChannel.send(TasksEffect.Feedback(if (done) "Bạn nhận xu và tất cả cây đã lớn thêm!" else "Nhiệm vụ này đã hoàn thành hôm nay"))
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
