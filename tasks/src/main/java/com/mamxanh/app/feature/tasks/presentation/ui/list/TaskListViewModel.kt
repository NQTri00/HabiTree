package com.mamxanh.app.feature.tasks.presentation.ui.list

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.mamxanh.app.core.presentation.BaseViewModel
import com.mamxanh.app.feature.tasks.domain.model.Task
import com.mamxanh.app.feature.tasks.domain.repository.TaskRepository
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

class TaskListViewModel(
    private val repository: TaskRepository,
    private val savedState: SavedStateHandle,
) : BaseViewModel<TaskListState, TaskListEvent, TaskListAction>(
        TaskListState(savedState[FILTER_KEY] ?: "today", emptyList(), 0, 0, 0, false),
    ) {
    private val refreshMutex = Mutex()
    private var tasks = emptyList<Task>()

    init {
        viewModelScope.launch {
            repository.changes.collect {
                try {
                    refresh()
                } catch (cancelled: CancellationException) {
                    throw cancelled
                } catch (_: Exception) {
                    mutableState.value = state.value.copy(loading = false)
                    emitEvent(TaskListEvent.Feedback(AppText.text(TextKey.CopyChuaTheCapNhatVuiLongThuLai42c8ac)))
                }
            }
        }
    }

    override fun handleAction(action: TaskListAction) {
        when (action) {
            TaskListAction.Add -> send(TaskListEvent.Add)
            TaskListAction.Focus -> send(TaskListEvent.Focus())
            TaskListAction.Templates -> send(TaskListEvent.Templates)
            TaskListAction.Insights -> send(TaskListEvent.Insights)
            is TaskListAction.Open -> send(TaskListEvent.Detail(action.id))
            is TaskListAction.Filter -> {
                savedState[FILTER_KEY] = action.value
                mutableState.value = state.value.copy(filter = action.value, items = filtered(action.value))
            }
            is TaskListAction.Complete -> {
                if (action.timer) {
                    send(TaskListEvent.Focus(action.id))
                } else {
                    val id = action.id.toLongOrNull() ?: return
                    mutate { repository.completeResult(id).message }
                }
            }
            TaskListAction.ClaimReward ->
                mutate {
                    repository.claimDailyBonus()
                    AppText.text(TextKey.CopyDaNhanThuongHomNayC5cad9)
                }
        }
    }

    private fun send(event: TaskListEvent) {
        viewModelScope.launch { emitEvent(event) }
    }

    private fun mutate(block: () -> String) {
        if (state.value.busy) return
        mutableState.value = state.value.copy(busy = true)
        viewModelScope.launch {
            try {
                val message = withContext(Dispatchers.IO) { block() }
                refresh()
                emitEvent(TaskListEvent.Feedback(message))
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                emitEvent(TaskListEvent.Feedback(AppText.text(TextKey.CopyChuaTheCapNhatVuiLongThuLai42c8ac)))
            } finally {
                mutableState.value = state.value.copy(busy = false)
            }
        }
    }

    private suspend fun refresh() =
        refreshMutex.withLock {
            val snapshot =
                withContext(Dispatchers.IO) {
                    val progress = repository.snapshot()
                    val claimed = repository.isDailyBonusClaimed()
                    val eligible = repository.canClaimDailyBonus()
                    ListSnapshot(
                        repository.observeSnapshot(),
                        progress.completed,
                        progress.required,
                        progress.earnedToday,
                        claimed,
                        eligible,
                    )
                }
            tasks = snapshot.tasks
            mutableState.value =
                state.value.copy(
                    items = filtered(state.value.filter),
                    completed = snapshot.completed,
                    required = snapshot.required,
                    earned = snapshot.earned,
                    rewardClaimed = snapshot.claimed,
                    canClaimReward = snapshot.eligible,
                    loading = false,
                )
        }

    private fun filtered(filter: String): List<TaskListItem> {
        val lifecycle =
            when (filter) {
                "paused" -> "PAUSED"
                "archived" -> "ARCHIVED"
                else -> "ACTIVE"
            }
        return tasks.filter { it.lifecycle == lifecycle }.map { task ->
            val timer = task.config.verification == "TIMER"
            val minutes = task.config.minutes
            val reward =
                when {
                    !timer -> 8
                    minutes >= 45 -> 20
                    minutes >= 20 -> 15
                    else -> 10
                }
            TaskListItem(
                task.id.toString(),
                task.title,
                task.category,
                minutes,
                reward,
                task.doneToday || task.finished,
                timer,
                lifecycle == "ACTIVE",
            )
        }
    }

    private data class ListSnapshot(
        val tasks: List<Task>,
        val completed: Int,
        val required: Int,
        val earned: Int,
        val claimed: Boolean,
        val eligible: Boolean,
    )

    private companion object {
        const val FILTER_KEY = "taskFilter"
    }
}
