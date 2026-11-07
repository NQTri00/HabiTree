package com.mamxanh.app.feature.shop.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mamxanh.app.feature.shop.domain.*
import com.mamxanh.app.feature.shop.domain.model.ShopWallet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ShopViewModel(
    private val repository: ShopRepository,
    private val onInventoryChanged: () -> Unit = {}
) : ViewModel() {
    private val _wallet = MutableStateFlow(ShopWallet(0, emptyMap()))
    val wallet = _wallet.asStateFlow()
    private val _message = MutableStateFlow("")
    val message = _message.asStateFlow()
    init { refresh() }
    fun refresh() = viewModelScope.launch { _wallet.value = withContext(Dispatchers.IO) { LoadShopWallet(repository)() } }
    fun buySeed(id: String) = viewModelScope.launch(Dispatchers.IO) {
        val bought = BuySeed(repository)(id)
        refresh()
        if (bought) onInventoryChanged()
        _message.value = if (bought) "Đã mua hạt giống." else "Bạn chưa đủ xu."
    }
    fun buyFertilizer() = viewModelScope.launch(Dispatchers.IO) {
        val bought = BuyFertilizer(repository)()
        refresh()
        if (bought) onInventoryChanged()
        _message.value = if (bought) "Đã mua phân bón." else "Bạn cần ít nhất 25 xu."
    }
}
