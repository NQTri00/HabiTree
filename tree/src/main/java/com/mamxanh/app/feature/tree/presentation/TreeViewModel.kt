package com.mamxanh.app.feature.tree.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mamxanh.app.feature.tree.domain.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TreeViewModel(
    private val repository: TreeRepository,
    private val onTreeChanged: () -> Unit = {}
) : ViewModel() {
    private val _message = MutableStateFlow("")
    val message = _message.asStateFlow()

    fun water(id: Long) = viewModelScope.launch(Dispatchers.IO) {
        val changed = WaterTree(repository)(id)
        if (changed) onTreeChanged()
        _message.value = if (changed) "Bạn đã tưới cây. Cây nhận thêm 8 điểm phát triển." else "Cây này đã được tưới hôm nay."
    }
    fun fertilize(id: Long) = viewModelScope.launch(Dispatchers.IO) {
        val changed = FertilizeTree(repository)(id)
        if (changed) onTreeChanged()
        _message.value = if (changed) "Cây đã lớn thêm 25 điểm." else "Bạn chưa có phân bón."
    }
    fun decorate(id: Long, decoration: String) = viewModelScope.launch(Dispatchers.IO) {
        DecorateTree(repository)(id, decoration)
        onTreeChanged()
        _message.value = "Đã cập nhật trang trí cho cây."
    }
}
