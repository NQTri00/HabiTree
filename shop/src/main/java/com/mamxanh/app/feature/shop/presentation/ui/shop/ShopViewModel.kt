package com.mamxanh.app.feature.shop.presentation.ui.shop

import androidx.lifecycle.viewModelScope
import com.mamxanh.app.core.domain.RepositoryResult
import com.mamxanh.app.core.presentation.BaseViewModel
import com.mamxanh.app.feature.shop.domain.repository.ShopRepository
import com.mamxanh.app.feature.shop.domain.repository.averageShopEarnings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ShopViewModel(
    private val repository: ShopRepository,
) : BaseViewModel<ShopState, ShopEvent, ShopAction>(
        initialState = ShopState(),
    ) {
    val uiState get() = state
    val effects get() = events

    init {
        dispatch(ShopAction.Refresh)
    }

    override fun handleAction(action: ShopAction) {
        val intent = action
        when (intent) {
            ShopAction.OpenGarden -> viewModelScope.launch { emitEvent(ShopEvent.OpenGarden) }
            is ShopAction.SetFilter -> mutableState.value = mutableState.value.copy(filter = intent.filter)
            ShopAction.DismissOutcome -> mutableState.value = mutableState.value.copy(outcome = null)
            ShopAction.Refresh ->
                viewModelScope.launch {
                    mutableState.value = mutableState.value.copy(isLoading = true)
                    val (wallet, earnings) =
                        withContext(Dispatchers.IO) {
                            repository.loadWallet() to averageShopEarnings(repository.dailyEarnings())
                        }
                    mutableState.value = mutableState.value.copy(wallet = wallet, averageEarnings = earnings, isLoading = false)
                }
            is ShopAction.Equip ->
                viewModelScope.launch {
                    val result = withContext(Dispatchers.IO) { repository.equipCan(intent.id) }
                    dispatch(ShopAction.Refresh)
                    emitEvent(ShopEvent.Feedback(result.message()))
                    if (result is RepositoryResult.Success) emitEvent(ShopEvent.InventoryChanged)
                }
            is ShopAction.Purchase -> purchase(intent.id, intent.quantity, intent.key)
            is ShopAction.BuySeed -> purchase(intent.id)
            ShopAction.BuyFertilizer -> purchase("fertilizer")
        }
    }

    private fun purchase(
        id: String,
        quantity: Int = 1,
        key: String =
            java.util.UUID
                .randomUUID()
                .toString(),
    ) {
        if (mutableState.value.isPurchasing) return
        mutableState.value = mutableState.value.copy(isPurchasing = true)
        viewModelScope.launch {
            try {
                val (before, result, after) =
                    withContext(Dispatchers.IO) {
                        val before = repository.loadWallet()
                        val result = repository.purchase(id, quantity, key)
                        Triple(before, result, repository.loadWallet())
                    }
                val success = result is RepositoryResult.Success
                mutableState.value =
                    mutableState.value.copy(
                        wallet = after,
                        isLoading = false,
                        outcome =
                            PurchaseOutcome(
                                success,
                                shopCatalog.firstOrNull { it.id == id }?.name
                                    ?: com.mamxanh.shared.model.careProducts
                                        .firstOrNull { it.id == id }
                                        ?.name
                                        .orEmpty(),
                                before.coins,
                                after.coins,
                                before.inventory[id] ?: 0,
                                after.inventory[id] ?: 0,
                                result.message(),
                            ),
                    )
                if (success) emitEvent(ShopEvent.InventoryChanged)
            } finally {
                mutableState.value = mutableState.value.copy(isPurchasing = false)
            }
        }
    }

    private fun RepositoryResult<String>.message(): String =
        when (this) {
            is RepositoryResult.Success -> value
            is RepositoryResult.Error -> message
        }
}
