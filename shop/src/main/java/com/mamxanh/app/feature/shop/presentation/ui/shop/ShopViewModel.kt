package com.mamxanh.app.feature.shop.presentation.ui.shop

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.mamxanh.app.core.domain.RepositoryResult
import com.mamxanh.app.core.presentation.BaseViewModel
import com.mamxanh.app.feature.shop.domain.repository.ShopRepository
import com.mamxanh.app.feature.shop.domain.repository.averageShopEarnings
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class ShopViewModel(
    private val repository: ShopRepository,
    private val savedState: SavedStateHandle,
) : BaseViewModel<ShopState, ShopEvent, ShopAction>(
        initialState =
            ShopState(
                tab = savedState["shopTab"] ?: "seeds",
                purchaseId = savedState["purchaseId"] ?: "",
                quantity = (savedState.get<Int>("quantity") ?: 1).coerceIn(1, 99),
            ),
    ) {
    val uiState get() = state
    val effects get() = events
    private var refreshJob: Job? = null

    init {
        dispatch(ShopAction.Refresh)
    }

    override fun handleAction(action: ShopAction) {
        val intent = action
        when (intent) {
            is ShopAction.SelectProduct -> {
                if (state.value.isPurchasing) return
                viewModelScope.launch { emitEvent(ShopEvent.ShowProduct(intent.id, intent.accessoryTarget)) }
            }
            is ShopAction.OpenPurchase -> {
                if (state.value.purchaseId != intent.id) updatePurchase(intent.id, 1)
            }
            is ShopAction.SetQuantity -> {
                if (!state.value.isPurchasing) updatePurchase(state.value.purchaseId, intent.quantity)
            }
            is ShopAction.SelectTab -> {
                savedState["shopTab"] = intent.tab
                mutableState.value = state.value.copy(tab = intent.tab)
            }
            ShopAction.OpenGarden -> viewModelScope.launch { emitEvent(ShopEvent.OpenGarden) }
            is ShopAction.SetFilter -> mutableState.value = mutableState.value.copy(filter = intent.filter)
            ShopAction.DismissOutcome -> mutableState.value = mutableState.value.copy(outcome = null)
            ShopAction.Refresh -> refresh()
            is ShopAction.Equip -> purchase(intent.id, pending = ShopPendingAction.Equip)
            is ShopAction.Purchase -> purchase(intent.id, intent.quantity, intent.key)
            is ShopAction.BuyAndEquip -> purchase(intent.id, intent.quantity, pending = ShopPendingAction.BuyAndEquip)
            is ShopAction.BuyAndPlant ->
                purchase(
                    intent.id,
                    intent.quantity,
                    pending = ShopPendingAction.BuyAndPlant,
                    preferredPlot = intent.preferredPlot,
                )
            is ShopAction.BuySeed -> purchase(intent.id)
            ShopAction.BuyFertilizer -> purchase("fertilizer")
        }
    }

    private fun refresh() {
        if (state.value.isPurchasing) return
        refreshJob?.cancel()
        refreshJob =
            viewModelScope.launch {
                try {
                    val (wallet, earnings) =
                        withContext(Dispatchers.IO) {
                            repository.loadWallet() to averageShopEarnings(repository.dailyEarnings())
                        }
                    mutableState.value = state.value.copy(wallet = wallet, averageEarnings = earnings, isLoading = false)
                } catch (cancelled: CancellationException) {
                    throw cancelled
                } catch (_: Exception) {
                    mutableState.value = state.value.copy(isLoading = false)
                    emitEvent(ShopEvent.Feedback(AppText.text(TextKey.CopyChuaTheCapNhatVuiLongThuLai42c8ac)))
                }
            }
    }

    private fun updatePurchase(
        id: String,
        quantity: Int,
    ) {
        val boundedQuantity = quantity.coerceIn(1, 99)
        savedState["purchaseId"] = id
        savedState["quantity"] = boundedQuantity
        mutableState.value = state.value.copy(purchaseId = id, quantity = boundedQuantity)
    }

    private fun purchase(
        id: String,
        quantity: Int = 1,
        key: String = UUID.randomUUID().toString(),
        pending: ShopPendingAction = ShopPendingAction.Buy,
        preferredPlot: Int? = null,
    ) {
        if (mutableState.value.isPurchasing) return
        refreshJob?.cancel()
        mutableState.value = state.value.copy(isPurchasing = true, pendingAction = pending)
        viewModelScope.launch {
            var reloadWallet = false
            try {
                val (before, result, after) =
                    withContext(Dispatchers.IO) {
                        val before = repository.loadWallet()
                        val count = quantity.coerceIn(1, 99)
                        val result =
                            when (pending) {
                                ShopPendingAction.Equip -> finish(id, repository.equipCan(id))
                                ShopPendingAction.Buy -> finish(id, repository.purchase(id, count, key))
                                ShopPendingAction.BuyAndEquip -> finish(id, repository.purchaseAndEquip(id, count, key))
                                ShopPendingAction.BuyAndPlant ->
                                    when (val planted = repository.purchaseAndPlant(id, count, preferredPlot, key)) {
                                        is RepositoryResult.Success ->
                                            ShopEvent.PurchaseFinished(
                                                id = id,
                                                success = true,
                                                message = planted.value.message,
                                                openTree = planted.value.treeId != null,
                                                treeId = planted.value.treeId,
                                            )
                                        is RepositoryResult.Error -> ShopEvent.PurchaseFinished(id, false, planted.message)
                                    }
                            }
                        val after =
                            try {
                                repository.loadWallet()
                            } catch (cancelled: CancellationException) {
                                throw cancelled
                            } catch (_: Exception) {
                                null
                            }
                        Triple(before, result, after)
                    }
                reloadWallet = after == null
                val success = result.success
                mutableState.value =
                    mutableState.value.copy(
                        wallet = after ?: before,
                        isLoading = reloadWallet,
                        outcome =
                            after?.let { updated ->
                                PurchaseOutcome(
                                    success,
                                    shopCatalog.firstOrNull { it.id == id }?.name
                                        ?: com.mamxanh.shared.model.careProducts
                                            .firstOrNull { it.id == id }
                                            ?.name
                                            .orEmpty(),
                                    before.coins,
                                    updated.coins,
                                    before.inventory[id] ?: 0,
                                    updated.inventory[id] ?: 0,
                                    result.message,
                                )
                            },
                    )
                if (success) emitEvent(ShopEvent.InventoryChanged)
                emitEvent(result)
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                emitEvent(
                    ShopEvent.PurchaseFinished(
                        id,
                        false,
                        AppText.text(TextKey.CopyChuaTheCapNhatVuiLongThuLai42c8ac),
                    ),
                )
            } finally {
                mutableState.value = mutableState.value.copy(isPurchasing = false, pendingAction = null)
                if (reloadWallet) refresh()
            }
        }
    }

    private fun finish(
        id: String,
        result: RepositoryResult<String>,
    ) = ShopEvent.PurchaseFinished(id, result is RepositoryResult.Success, result.message())

    private fun RepositoryResult<String>.message(): String =
        when (this) {
            is RepositoryResult.Success -> value
            is RepositoryResult.Error -> message
        }
}
