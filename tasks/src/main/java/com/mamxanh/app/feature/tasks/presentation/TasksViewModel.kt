package com.mamxanh.app.feature.tasks.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mamxanh.app.feature.tasks.domain.*
import com.mamxanh.app.feature.tasks.domain.model.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TasksViewModel(private val repository: TaskRepository) : ViewModel() {
    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks = _tasks.asStateFlow()
    private val _message = MutableStateFlow("")
    val message = _message.asStateFlow()
    init { refresh() }
    fun refresh() = viewModelScope.launch { _tasks.value = withContext(Dispatchers.IO) { LoadTasks(repository)() } }
    fun add(title: String, kind: String, category: String, reward: Int, frequency: String, due: String) = viewModelScope.launch(Dispatchers.IO) {
        if (title.isNotBlank()) AddTask(repository)(title, kind, category, reward, frequency, due)
        refresh(); _message.value = "Đã thêm nhiệm vụ"
    }
    fun delete(id: Long) = viewModelScope.launch(Dispatchers.IO) { DeleteTask(repository)(id); refresh(); _message.value = "Đã xóa nhiệm vụ" }
    fun complete(id: Long) = viewModelScope.launch(Dispatchers.IO) {
        val done = CompleteTask(repository)(id); refresh()
        _message.value = if (done) "Bạn nhận xu và tất cả cây đã lớn thêm!" else "Nhiệm vụ này đã hoàn thành hôm nay"
    }
    fun bonus() = viewModelScope.launch(Dispatchers.IO) {
        val claimed = ClaimDailyBonus(repository)(); refresh()
        _message.value = if (claimed) "Tuyệt vời! Bạn nhận thêm 20 xu." else "Hoàn thành mọi nhiệm vụ hằng ngày để nhận thưởng."
    }
}
