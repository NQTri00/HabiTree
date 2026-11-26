package com.mamxanh.app.feature.tree.presentation.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.mamxanh.app.core.domain.RepositoryResult
import com.mamxanh.app.core.presentation.BaseViewModel
import com.mamxanh.app.feature.tree.domain.repository.TreeCareReceipt
import com.mamxanh.app.feature.tree.domain.repository.TreeDetailRepository
import com.mamxanh.app.feature.tree.domain.repository.TreeRepository
import com.mamxanh.app.feature.tree.domain.repository.WateringOutcome
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TreeViewModel(
    private val repository: TreeRepository,
    private val savedState: SavedStateHandle,
    private val details: TreeDetailRepository,
) : BaseViewModel<TreeState, TreeEvent, TreeAction>(
        initialState = TreeState(tab = savedState["treeTab"] ?: "care"),
    ) {
    val uiState get() = state
    val effects get() = events

    private var loadJob: Job? = null

    override fun handleAction(action: TreeAction) {
        val intent = action
        when (intent) {
            is TreeAction.Browse -> {
                val current = state.value
                val detail = current.detail?.takeIf { it.id == intent.currentId } ?: return
                val ids = detail.treeIds
                val index = ids.indexOf(intent.currentId)
                if (!current.isLoading && ids.size > 1 && index >= 0) {
                    val offset = if (intent.forward) 1 else ids.lastIndex
                    viewModelScope.launch { emitEvent(TreeEvent.BrowseTo(intent.currentId, ids[(index + offset) % ids.size])) }
                }
                return
            }
            is TreeAction.Load -> {
                load(intent.id)
                return
            }
            is TreeAction.SelectTab -> {
                savedState["treeTab"] = intent.value
                mutableState.value = state.value.copy(tab = intent.value)
                return
            }
            TreeAction.Back -> {
                if (mutableState.value.isPanelExpanded) {
                    mutableState.value = mutableState.value.copy(isPanelExpanded = false)
                } else {
                    viewModelScope.launch { emitEvent(TreeEvent.Back) }
                }
                return
            }
            is TreeAction.SetPanelExpanded -> {
                mutableState.value = mutableState.value.copy(isPanelExpanded = intent.expanded)
                return
            }
            TreeAction.DismissDisposition -> {
                mutableState.value = mutableState.value.copy(dispositionId = null, quote = null)
                return
            }
            TreeAction.OpenAccessoryShop -> viewModelScope.launch { emitEvent(TreeEvent.OpenAccessoryShop) }
            is TreeAction.OpenAccessory -> viewModelScope.launch { emitEvent(TreeEvent.OpenAccessory(intent.sku)) }
            TreeAction.OpenFertilizerShop -> {
                viewModelScope.launch { emitEvent(TreeEvent.OpenFertilizerShop) }
                return
            }
            else -> Unit
        }
        if (mutableState.value.isLoading)return
        val pending = mutableState.value
        mutableState.value = pending.copy(isLoading = true)
        viewModelScope.launch {
            try {
                if (intent is TreeAction.AskDisposition) {
                    val quote =
                        if (intent.sell) {
                            withContext(Dispatchers.IO) { repository.saleQuote(intent.id) }
                        } else {
                            success(TextKey.CopyDonCayKhongHoanXuHoacHatCdff29)
                        }
                    when (quote) {
                        is RepositoryResult.Success -> {
                            mutableState.value =
                                mutableState.value.copy(
                                    dispositionId = intent.id,
                                    selling = intent.sell,
                                    quote = quote.value,
                                    actionKey =
                                        java.util.UUID
                                            .randomUUID()
                                            .toString(),
                                )
                            if (intent.openDialog) emitEvent(TreeEvent.ConfirmDisposition(intent.id, intent.sell))
                        }
                        is RepositoryResult.Error -> emitEvent(TreeEvent.Feedback(quote.message))
                    }
                    return@launch
                }
                val result =
                    withContext(Dispatchers.IO) {
                        when (intent) {
                            is TreeAction.Water ->
                                when (repository.water(intent.id)) {
                                    WateringOutcome.Watered -> success(TextKey.CopyDaTuoiCayMienPhiC84ab7)
                                    WateringOutcome.AlreadyWatered -> failure(TextKey.CopyCayDaDuNuoc66849f)
                                    WateringOutcome.Dead -> failure(TextKey.CopyCayDaKhoHeo9c0717)
                                    WateringOutcome.ClockChanged,
                                    WateringOutcome.MissingTree,
                                    -> failure(TextKey.CopyChuaTheTuoiCayB8c05f)
                                }
                            is TreeAction.Fertilize -> repository.applyFertilizer(intent.id, intent.tier)
                            is TreeAction.Decorate -> {
                                repository.decorate(intent.id, intent.decoration)
                                success(TextKey.CopyDaCapNhatTrangTri472665)
                            }
                            is TreeAction.Unequip ->
                                when (val outcome = repository.unequipAccessory(intent.id)) {
                                    is RepositoryResult.Success -> RepositoryResult.Success(TreeCareReceipt(outcome.value))
                                    is RepositoryResult.Error -> outcome
                                }
                            is TreeAction.Dispose ->
                                repository.dispose(
                                    intent.id,
                                    intent.sell,
                                    pending.actionKey.takeIf {
                                        pending.dispositionId == intent.id && pending.selling == intent.sell && it.isNotBlank()
                                    } ?: java.util.UUID
                                        .randomUUID()
                                        .toString(),
                                )
                            TreeAction.ConfirmDisposition ->
                                pending.dispositionId?.let {
                                    repository.dispose(it, pending.selling, pending.actionKey)
                                } ?: failure(TextKey.CopyChonCayTruoc84691e)
                            else -> failure(TextKey.CopyKhongCoThaoTac432536)
                        }
                    }
                when (result) {
                    is RepositoryResult.Success -> {
                        if (intent == TreeAction.ConfirmDisposition) {
                            mutableState.value = mutableState.value.copy(dispositionId = null, quote = null, isPanelExpanded = false)
                            emitEvent(TreeEvent.Back)
                        }
                        if (intent is TreeAction.Dispose) {
                            mutableState.value = state.value.copy(dispositionId = null, quote = null, actionKey = "")
                            emitEvent(TreeEvent.Disposed(result.value.message))
                        } else {
                            emitEvent(TreeEvent.Feedback(result.value.message))
                        }
                    }
                    is RepositoryResult.Error -> emitEvent(TreeEvent.Feedback(result.message))
                }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                emitEvent(TreeEvent.Feedback(AppText.text(TextKey.CopyChuaTheCapNhatVuiLongThuLai42c8ac)))
            } finally {
                mutableState.value = mutableState.value.copy(isLoading = false)
            }
        }
    }

    private fun load(id: Long) {
        loadJob?.cancel()
        loadJob =
            viewModelScope.launch {
                try {
                    val detail = withContext(Dispatchers.IO) { details.load(id) }
                    mutableState.value = state.value.copy(detail = detail)
                } catch (cancelled: CancellationException) {
                    throw cancelled
                } catch (_: Exception) {
                    emitEvent(TreeEvent.Feedback(AppText.text(TextKey.CopyChuaTheCapNhatVuiLongThuLai42c8ac)))
                }
            }
    }

    private fun success(key: TextKey) = RepositoryResult.Success(TreeCareReceipt(AppText.text(key)))

    private fun failure(key: TextKey) = RepositoryResult.Error(AppText.text(key))
}
