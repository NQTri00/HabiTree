package com.mamxanh.app.feature.tasks.presentation.ui.focus

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.mamxanh.app.core.domain.RepositoryResult
import com.mamxanh.app.core.presentation.BaseViewModel
import com.mamxanh.app.feature.tasks.domain.repository.FocusCommandResult
import com.mamxanh.app.feature.tasks.domain.repository.FocusSessionCommands
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.util.UUID

class FocusViewModel(
    private val commands: FocusSessionCommands,
    private val savedState: SavedStateHandle,
    argument: String,
) : BaseViewModel<FocusState, FocusEvent, FocusAction>(
        FocusState(
            taskId = savedState["focus:task"] ?: argument.ifBlank { "0" },
            minutes = savedState["focus:minutes"] ?: DEFAULT_FOCUS_MINUTES.toString(),
            lastValidMinutes =
                savedState["focus:last-valid-minutes"]
                    ?: savedState.get<String>("focus:minutes")?.toIntOrNull()?.takeIf { it in FreeFocusMinutes }
                    ?: DEFAULT_FOCUS_MINUTES,
        ),
    ) {
    private val sessionOperations = Mutex()

    override fun handleAction(action: FocusAction) {
        if (action == FocusAction.Leave) {
            viewModelScope.launch {
                try {
                    sessionOperations.withLock {
                        withContext(Dispatchers.IO) { commands.pauseIfRunning() }
                    }
                } catch (cancelled: CancellationException) {
                    throw cancelled
                } catch (_: Exception) {
                    emitEvent(FocusEvent.Feedback(AppText.text(TextKey.CopyChuaTheCapNhatVuiLongThuLai42c8ac), false))
                }
            }
            return
        }
        val current = state.value
        if (current.busy) return
        when (action) {
            is FocusAction.SelectTask -> {
                savedState["focus:task"] = action.id
                mutableState.value = current.copy(taskId = action.id)
            }
            is FocusAction.SetMinutes -> {
                savedState["focus:minutes"] = action.value
                val lastValidMinutes = action.value.toIntOrNull()?.takeIf { it in FreeFocusMinutes } ?: current.lastValidMinutes
                savedState["focus:last-valid-minutes"] = lastValidMinutes
                mutableState.value = current.copy(minutes = action.value, lastValidMinutes = lastValidMinutes)
            }
            is FocusAction.Start -> {
                if (action.taskId == 0L && action.minutes !in FreeFocusMinutes) return
                val key = UUID.randomUUID().toString()
                mutate { commands.start(action.taskId, action.minutes, key) }
            }
            FocusAction.Toggle -> mutate { commands.toggle().result() }
            FocusAction.End -> mutate { commands.end().result() }
            FocusAction.RequestEnd -> viewModelScope.launch { emitEvent(FocusEvent.ConfirmEnd) }
            FocusAction.Leave -> Unit
        }
    }

    private fun mutate(operation: () -> RepositoryResult<String>) {
        mutableState.value = state.value.copy(busy = true)
        viewModelScope.launch {
            try {
                val result = sessionOperations.withLock { withContext(Dispatchers.IO) { operation() } }
                emitEvent(
                    when (result) {
                        is RepositoryResult.Success -> FocusEvent.Feedback(result.value, true)
                        is RepositoryResult.Error -> FocusEvent.Feedback(result.message, false)
                    },
                )
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                emitEvent(FocusEvent.Feedback(AppText.text(TextKey.CopyChuaTheCapNhatVuiLongThuLai42c8ac), false))
            } finally {
                mutableState.value = state.value.copy(busy = false)
            }
        }
    }

    private fun FocusCommandResult.result(): RepositoryResult<String> =
        when (this) {
            FocusCommandResult.NoActiveSession -> RepositoryResult.Error(AppText.text(TextKey.CopyChuaCoPhienTapTrung80d4ae))
            is FocusCommandResult.Handled -> result
        }
}
