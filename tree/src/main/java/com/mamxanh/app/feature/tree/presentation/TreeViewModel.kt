package com.mamxanh.app.feature.tree.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mamxanh.app.data.local.model.TreeWateringResult
import com.mamxanh.app.feature.tree.domain.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TreeViewModel(private val repository: TreeRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(TreeUiState())
    val uiState = _uiState.asStateFlow()
    private val effectChannel = Channel<TreeEffect>(Channel.BUFFERED)
    val effects = effectChannel.receiveAsFlow()

    fun dispatch(intent: TreeIntent) {
        if (intent == TreeIntent.OpenWaterSupply || intent == TreeIntent.OpenFertilizerShop) {
            _uiState.value = _uiState.value.copy(isPanelExpanded = false)
            viewModelScope.launch {
                effectChannel.send(if (intent == TreeIntent.OpenWaterSupply) TreeEffect.OpenWaterSupply
                    else TreeEffect.OpenFertilizerShop)
            }
            return
        }
        if (intent is TreeIntent.SetPanelExpanded) {
            _uiState.value = _uiState.value.copy(isPanelExpanded = intent.expanded)
            return
        }
        if (intent == TreeIntent.Back) {
            if (_uiState.value.isPanelExpanded) {
                _uiState.value = _uiState.value.copy(isPanelExpanded = false)
            } else {
                viewModelScope.launch { effectChannel.send(TreeEffect.Back) }
            }
            return
        }
        if (_uiState.value.isLoading) return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                when (intent) {
                    TreeIntent.Back, TreeIntent.OpenWaterSupply, TreeIntent.OpenFertilizerShop,
                    is TreeIntent.SetPanelExpanded -> Unit // UI/navigation handled before mutations.
                    is TreeIntent.Water -> {
                        val result = withContext(Dispatchers.IO) { WaterTree(repository)(intent.id) }
                        if (result == TreeWateringResult.WATERED) effectChannel.send(TreeEffect.TreeChanged)
                        val message = when (result) {
                            TreeWateringResult.WATERED -> "Đã tưới cây: dùng 50 ml nước và cây lớn thêm 8 điểm."
                            TreeWateringResult.ALREADY_WATERED -> "Cây này đã được tưới hôm nay."
                            TreeWateringResult.NOT_ENOUGH_WATER -> "Bình tưới cần ít nhất 50 ml. Hãy bơm thêm nước ở Trang chủ."
                            TreeWateringResult.TREE_NOT_FOUND -> "Không tìm thấy cây này."
                        }
                        effectChannel.send(TreeEffect.Feedback(message))
                    }
                    is TreeIntent.Fertilize -> {
                        val changed = withContext(Dispatchers.IO) { FertilizeTree(repository)(intent.id) }
                        if (changed) effectChannel.send(TreeEffect.TreeChanged)
                        effectChannel.send(TreeEffect.Feedback(if (changed) "Cây đã lớn thêm 25 điểm." else "Bạn chưa có phân bón."))
                    }
                    is TreeIntent.Decorate -> {
                        withContext(Dispatchers.IO) { DecorateTree(repository)(intent.id, intent.decoration) }
                        effectChannel.send(TreeEffect.TreeChanged)
                        effectChannel.send(TreeEffect.Feedback("Đã cập nhật trang trí cho cây."))
                    }
                }
            } finally {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }
}
