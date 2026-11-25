package com.mamxanh.app.feature.tasks.presentation.ui.rest

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.mamxanh.app.core.presentation.BaseViewModel
import com.mamxanh.app.feature.tasks.domain.repository.RestRepository
import com.mamxanh.app.feature.tasks.domain.repository.TaskOperationResult
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class RestViewModel(
    private val repository: RestRepository,
    private val savedState: SavedStateHandle,
) : BaseViewModel<RestState, RestEvent, RestAction>(
        RestState(restDate = savedState["restDate"], protectionDate = savedState["protectDate"] ?: ""),
    ) {
    private var refreshJob: Job? = null

    override fun handleAction(action: RestAction) {
        val current = state.value
        if (current.busy) return
        when (action) {
            RestAction.Refresh -> refresh()
            is RestAction.SelectRestDate -> {
                savedState["restDate"] = action.date
                mutableState.value = current.copy(restDate = action.date)
            }
            is RestAction.SelectProtectionDate -> {
                savedState["protectDate"] = action.date
                mutableState.value = current.copy(protectionDate = action.date)
            }
            is RestAction.Schedule -> if (action.date.isNotBlank()) mutate { repository.schedule(action.date) } else Unit
            is RestAction.Protect -> mutate { repository.protect(action.date, UUID.randomUUID().toString()) }
            RestAction.RequestTicket -> viewModelScope.launch { emitEvent(RestEvent.ConfirmTicket) }
            RestAction.BuyTicket -> mutate(ticket = true) { repository.buyTicket(UUID.randomUUID().toString()) }
        }
    }

    private fun refresh() {
        refreshJob?.cancel()
        refreshJob =
            viewModelScope.launch {
                try {
                    val schedule = withContext(Dispatchers.IO) { repository.load() }
                    mutableState.value = state.value.copy(schedule = schedule)
                } catch (cancelled: CancellationException) {
                    throw cancelled
                } catch (_: Exception) {
                    emitEvent(RestEvent.Feedback(AppText.text(TextKey.CopyChuaTheCapNhatVuiLongThuLai42c8ac), false, false))
                }
            }
    }

    private fun mutate(
        ticket: Boolean = false,
        operation: () -> TaskOperationResult,
    ) {
        mutableState.value = state.value.copy(busy = true)
        viewModelScope.launch {
            try {
                val result = withContext(Dispatchers.IO) { operation() }
                if (result.success) refresh()
                emitEvent(RestEvent.Feedback(result.message, result.success, ticket))
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                emitEvent(RestEvent.Feedback(AppText.text(TextKey.CopyChuaTheCapNhatVuiLongThuLai42c8ac), false, ticket))
            } finally {
                mutableState.value = state.value.copy(busy = false)
            }
        }
    }
}
