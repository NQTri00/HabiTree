package com.mamxanh.app.feature.shop.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mamxanh.app.feature.shop.domain.*
import com.mamxanh.app.feature.shop.domain.model.shopCatalog
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
        when(intent) {
            ShopIntent.OpenGarden -> viewModelScope.launch { effectChannel.send(ShopEffect.OpenGarden) }
            is ShopIntent.SetFilter -> _uiState.value = _uiState.value.copy(filter = intent.filter)
            ShopIntent.DismissOutcome -> _uiState.value = _uiState.value.copy(outcome = null)
            ShopIntent.Refresh -> viewModelScope.launch {
                _uiState.value = _uiState.value.copy(isLoading = true)
                val wallet = withContext(Dispatchers.IO) { LoadShopWallet(repository)() }
                _uiState.value = _uiState.value.copy(wallet = wallet, averageEarnings = withContext(Dispatchers.IO){repository.averageEarnings()}, isLoading = false)
            }
            is ShopIntent.Purchase -> purchase(intent.id,intent.quantity,intent.key)
            is ShopIntent.BuySeed -> purchase(intent.id)
            ShopIntent.BuyFertilizer -> purchase("fertilizer")
        }
    }

    private fun purchase(id: String,quantity: Int = 1,key: String = java.util.UUID.randomUUID().toString()) {
        if (_uiState.value.isPurchasing) return
        _uiState.value = _uiState.value.copy(isPurchasing = true)
        viewModelScope.launch {
            try {
                val (before, success, after) = withContext(Dispatchers.IO) {
                    val before = LoadShopWallet(repository)()
                    val success = repository.purchase(id,quantity,key)
                    Triple(before, success, LoadShopWallet(repository)())
                }
                _uiState.value = _uiState.value.copy(wallet = after, isLoading = false,
                    outcome = PurchaseOutcome(success.success,
                        shopCatalog.firstOrNull { it.id == id }?.name ?: "Phân hữu cơ",
                        before.coins, after.coins, before.inventory[id] ?: 0, after.inventory[id] ?: 0,success.message))
                if(success.success) effectChannel.send(ShopEffect.InventoryChanged)
            } finally {
                _uiState.value = _uiState.value.copy(isPurchasing = false)
            }
        }
    }
}
