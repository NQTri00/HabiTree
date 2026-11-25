package com.mamxanh.app.feature.tree.presentation.ui.accessory

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.mamxanh.app.core.domain.RepositoryResult
import com.mamxanh.app.core.presentation.BaseViewModel
import com.mamxanh.app.feature.tree.domain.repository.AccessoryRepository
import com.mamxanh.app.feature.tree.domain.repository.TreeRepository
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class AccessoryViewModel(
    private val repository: TreeRepository,
    private val savedState: SavedStateHandle,
    private val accessories: AccessoryRepository,
) : BaseViewModel<AccessoryState, AccessoryEvent, AccessoryAction>(
        AccessoryState(sku = savedState["accessorySku"] ?: "", selected = savedState["targetTree"] ?: ""),
    ) {
    private var loadJob: Job? = null

    override fun handleAction(action: AccessoryAction) {
        val current = state.value
        if (current.busy) return
        when (action) {
            AccessoryAction.RefreshCatalog -> load(null)
            is AccessoryAction.Open -> load(action)
            is AccessoryAction.Select -> {
                if (action.target in current.eligibleTargets) update(current.copy(selected = action.target))
            }
            AccessoryAction.Buy -> submit(current, equip = false)
            AccessoryAction.Equip -> submit(current, equip = true)
            AccessoryAction.Close -> {
                loadJob?.cancel()
                update(AccessoryState(snapshot = current.snapshot))
            }
        }
    }

    private fun load(action: AccessoryAction.Open?) {
        loadJob?.cancel()
        if (action != null && state.value.sku != action.sku) {
            update(AccessoryState(sku = action.sku, snapshot = state.value.snapshot))
        }
        loadJob =
            viewModelScope.launch {
                try {
                    val snapshot = withContext(Dispatchers.IO) { accessories.load() }
                    val eligible = snapshot.trees.filter { it.living }.map { it.id }
                    if (action == null) {
                        mutableState.value = state.value.copy(snapshot = snapshot)
                    } else {
                        val selected =
                            listOf(state.value.selected, action.preferredTarget, action.homeTarget)
                                .firstOrNull { it in eligible } ?: eligible.firstOrNull().orEmpty()
                        update(state.value.copy(snapshot = snapshot, selected = selected, loading = false))
                    }
                } catch (cancelled: CancellationException) {
                    throw cancelled
                } catch (_: Exception) {
                    if (action !=
                        null
                    ) {
                        emitEvent(AccessoryEvent.Completed(action.sku, false, AppText.text(TextKey.CopyChuaTheCapNhatVuiLongThuLai42c8ac)))
                    }
                }
            }
    }

    private fun update(next: AccessoryState) {
        savedState["accessorySku"] = next.sku
        savedState["targetTree"] = next.selected
        mutableState.value = next
    }

    private fun submit(
        current: AccessoryState,
        equip: Boolean,
    ) {
        val target = current.selected.toLongOrNull()
        if (current.loading || current.sku.isBlank() || equip && (target == null || current.selected !in current.eligibleTargets)) return
        mutableState.value = current.copy(busy = true)
        val key = UUID.randomUUID().toString()
        viewModelScope.launch {
            try {
                val result =
                    withContext(Dispatchers.IO) {
                        if (equip) repository.equipAccessory(current.sku, target, key) else repository.buyAccessory(current.sku, key)
                    }
                emitEvent(
                    when (result) {
                        is RepositoryResult.Success -> AccessoryEvent.Completed(current.sku, true, result.value)
                        is RepositoryResult.Error -> AccessoryEvent.Completed(current.sku, false, result.message)
                    },
                )
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                emitEvent(AccessoryEvent.Completed(current.sku, false, AppText.text(TextKey.CopyChuaTheCapNhatVuiLongThuLai42c8ac)))
            } finally {
                mutableState.value = state.value.copy(busy = false)
            }
        }
    }
}
