package com.mamxanh.app.feature.shop.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mamxanh.app.feature.shop.domain.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ShopViewModel(private val repository: ShopRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(ShopUiState())
    val uiState = _uiState.asStateFlow()
    private val effectChannel = Channel<ShopEffect>(Channel.BUFFERED)
    val effects = effectChannel.receiveAsFlow()

    init { dispatch(ShopIntent.Refresh) }

    fun dispatch(intent: ShopIntent) {
        when (intent) {
            ShopIntent.OpenGarden -> viewModelScope.launch { effectChannel.send(ShopEffect.OpenGarden) }
            ShopIntent.Refresh -> viewModelScope.launch {
                _uiState.value = _uiState.value.copy(isLoading = true)
                val wallet = withContext(Dispatchers.IO) { LoadShopWallet(repository)() }
                _uiState.value = ShopUiState(wallet, false)
            }
            is ShopIntent.BuySeed -> viewModelScope.launch {
                val result = withContext(Dispatchers.IO) { BuySeed(repository)(intent.id) }
                dispatch(ShopIntent.Refresh)
                if (result) effectChannel.send(ShopEffect.InventoryChanged)
                effectChannel.send(ShopEffect.Feedback(if (result) "Đã mua hạt giống." else "Bạn chưa đủ xu."))
            }
            ShopIntent.BuyFertilizer -> viewModelScope.launch {
                val result = withContext(Dispatchers.IO) { BuyFertilizer(repository)() }
                dispatch(ShopIntent.Refresh)
                if (result) effectChannel.send(ShopEffect.InventoryChanged)
                effectChannel.send(ShopEffect.Feedback(if (result) "Đã mua phân bón." else "Bạn cần ít nhất 25 xu."))
            }
        }
    }
}
