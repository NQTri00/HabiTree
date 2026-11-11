package com.mamxanh.app.feature.tree.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mamxanh.app.feature.tree.domain.TreeRepository
import com.mamxanh.shared.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TreeViewModel(private val repository:TreeRepository):ViewModel() {
    private val _uiState=MutableStateFlow(TreeUiState())
    val uiState=_uiState.asStateFlow()
    private val effectChannel=Channel<TreeEffect>(Channel.BUFFERED)
    val effects=effectChannel.receiveAsFlow()
    fun dispatch(intent:TreeIntent) {
        when(intent) {
            TreeIntent.Back -> {
                if(_uiState.value.isPanelExpanded)_uiState.value=_uiState.value.copy(isPanelExpanded=false)
                else viewModelScope.launch{effectChannel.send(TreeEffect.Back)}
                return
            }
            is TreeIntent.SetPanelExpanded -> {_uiState.value=_uiState.value.copy(isPanelExpanded=intent.expanded);return}
            TreeIntent.DismissDisposition -> {_uiState.value=_uiState.value.copy(dispositionId=null,quote=null);return}
            TreeIntent.OpenFertilizerShop -> {viewModelScope.launch{effectChannel.send(TreeEffect.OpenFertilizerShop)};return}
            else -> Unit
        }
        if(_uiState.value.isLoading)return
        val pending=_uiState.value
        _uiState.value=pending.copy(isLoading=true)
        viewModelScope.launch {
            try {
                if(intent is TreeIntent.AskDisposition) {
                    val quote=withContext(Dispatchers.IO){if(intent.sell)repository.saleQuote(intent.id) else ActionResult(true,"Dọn cây không hoàn xu hoặc hạt")}
                    if(quote.success)_uiState.value=_uiState.value.copy(dispositionId=intent.id,selling=intent.sell,
                        quote=quote,actionKey=java.util.UUID.randomUUID().toString())
                    else effectChannel.send(TreeEffect.Feedback(quote.message))
                    return@launch
                }
                val result=withContext(Dispatchers.IO) {
                    when(intent) {
                        is TreeIntent.Water -> {
                            val result=repository.water(intent.id)
                            ActionResult(result==TreeWateringResult.WATERED,when(result) {
                                TreeWateringResult.WATERED -> "Đã tưới cây · Cửa sổ tăng trưởng đã bắt đầu"
                                TreeWateringResult.ALREADY_WATERED -> "Cây đang được tưới; thời gian không cộng dồn"
                                TreeWateringResult.DEAD -> "Cây đã khô héo vĩnh viễn; chỉ có thể dọn cây"
                                TreeWateringResult.CLOCK_CHANGED -> "Đồng hồ đã lùi; hãy chờ giờ thực bắt kịp"
                                TreeWateringResult.TREE_NOT_FOUND -> "Cây không còn trong vườn"
                            })
                        }
                        is TreeIntent.Fertilize -> repository.applyFertilizer(intent.id,intent.tier)
                        is TreeIntent.Decorate -> {repository.decorate(intent.id,intent.decoration);ActionResult(true,"Đã cập nhật trang trí")}
                        TreeIntent.ConfirmDisposition -> pending.dispositionId?.let{repository.dispose(it,pending.selling,pending.actionKey)}
                            ?: ActionResult(false,"Chọn cây trước")
                        else -> ActionResult(false,"Không có thao tác")
                    }
                }
                if(result.success)effectChannel.send(TreeEffect.TreeChanged)
                if(intent==TreeIntent.ConfirmDisposition && result.success) {
                    _uiState.value=_uiState.value.copy(dispositionId=null,quote=null,isPanelExpanded=false)
                    effectChannel.send(TreeEffect.Back)
                }
                effectChannel.send(TreeEffect.Feedback(result.message))
            } finally {_uiState.value=_uiState.value.copy(isLoading=false)}
        }
    }
}
