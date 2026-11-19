package com.mamxanh.app.feature.shop.presentation

import androidx.lifecycle.viewModelScope
import com.mamxanh.app.core.domain.RepositoryResult
import com.mamxanh.app.core.presentation.BaseViewModel
import com.mamxanh.app.feature.shop.domain.ShopRepository
import com.mamxanh.app.feature.shop.domain.averageShopEarnings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ShopViewModel(private val repository: ShopRepository)  : BaseViewModel<ShopUiState, ShopEffect, ShopIntent>(
    initialState = ShopUiState(),
) {
    val uiState get() = state
    val effects get() = events
    init { dispatch(ShopIntent.Refresh) }

    override fun handleAction(action: ShopIntent) {
        val intent = action
        when(intent) {
            ShopIntent.OpenGarden -> viewModelScope.launch { emitEvent(ShopEffect.OpenGarden) }
            is ShopIntent.SetFilter -> mutableState.value = mutableState.value.copy(filter = intent.filter)
            ShopIntent.DismissOutcome -> mutableState.value = mutableState.value.copy(outcome = null)
            ShopIntent.Refresh -> viewModelScope.launch {
                mutableState.value = mutableState.value.copy(isLoading = true)
                val (wallet, earnings) = withContext(Dispatchers.IO) {
                    repository.loadWallet() to averageShopEarnings(repository.dailyEarnings())
                }
                mutableState.value = mutableState.value.copy(wallet = wallet, averageEarnings = earnings, isLoading = false)
            }
            is ShopIntent.Equip -> viewModelScope.launch {
                val result = withContext(Dispatchers.IO) { repository.equipCan(intent.id) }
                dispatch(ShopIntent.Refresh)
                emitEvent(ShopEffect.Feedback(result.message()))
                if (result is RepositoryResult.Success) emitEvent(ShopEffect.InventoryChanged)
            }
            is ShopIntent.Purchase -> purchase(intent.id,intent.quantity,intent.key)
            is ShopIntent.BuySeed -> purchase(intent.id)
            ShopIntent.BuyFertilizer -> purchase("fertilizer")
        }
    }

    private fun purchase(id: String,quantity: Int = 1,key: String = java.util.UUID.randomUUID().toString()) {
        if (mutableState.value.isPurchasing) return
        mutableState.value = mutableState.value.copy(isPurchasing = true)
        viewModelScope.launch {
            try {
                val (before, result, after) = withContext(Dispatchers.IO) {
                    val before = repository.loadWallet()
                    val result = repository.purchase(id, quantity, key)
                    Triple(before, result, repository.loadWallet())
                }
                val success = result is RepositoryResult.Success
                mutableState.value = mutableState.value.copy(wallet = after, isLoading = false,
                    outcome = PurchaseOutcome(success,
                        shopCatalog.firstOrNull { it.id == id }?.name ?: com.mamxanh.shared.model.careProducts.firstOrNull{it.id==id}?.name.orEmpty(),
                        before.coins, after.coins, before.inventory[id] ?: 0, after.inventory[id] ?: 0, result.message()))
                if (success) emitEvent(ShopEffect.InventoryChanged)
            } finally {
                mutableState.value = mutableState.value.copy(isPurchasing = false)
            }
        }
    }

    private fun RepositoryResult<String>.message(): String = when (this) {
        is RepositoryResult.Success -> value
        is RepositoryResult.Error -> message
    }
}
