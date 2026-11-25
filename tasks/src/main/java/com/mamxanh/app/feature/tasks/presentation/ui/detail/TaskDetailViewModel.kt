package com.mamxanh.app.feature.tasks.presentation.ui.detail

import androidx.lifecycle.viewModelScope
import com.mamxanh.app.core.presentation.BaseViewModel
import com.mamxanh.app.feature.tasks.domain.model.Task
import com.mamxanh.app.feature.tasks.domain.repository.TaskOperationResult
import com.mamxanh.app.feature.tasks.domain.repository.TaskRepository
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey
import com.mamxanh.shared.model.clockLabel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TaskDetailViewModel(
    private val repository: TaskRepository,
    argument: String,
) : BaseViewModel<TaskDetailState, TaskDetailEvent, TaskDetailAction>(TaskDetailState()) {
    private val taskId = argument.toLongOrNull()

    init {
        viewModelScope.launch {
            repository.changes.collect {
                try {
                    val task = withContext(Dispatchers.IO) { repository.observeSnapshot().firstOrNull { it.id == taskId } }
                    mutableState.value = state.value.copy(task = task?.toDisplay(), loading = false)
                } catch (cancelled: CancellationException) {
                    throw cancelled
                } catch (_: Exception) {
                    mutableState.value = state.value.copy(loading = false)
                    emitEvent(TaskDetailEvent.Feedback(errorMessage()))
                }
            }
        }
    }

    override fun handleAction(action: TaskDetailAction) {
        if (action == TaskDetailAction.OpenList) {
            send(TaskDetailEvent.OpenList)
            return
        }
        val id = taskId ?: return
        val task = state.value.task ?: return
        when (action) {
            TaskDetailAction.OpenList -> Unit
            TaskDetailAction.Edit -> send(TaskDetailEvent.Edit)
            TaskDetailAction.Insights -> send(TaskDetailEvent.Insights)
            TaskDetailAction.RequestArchive -> send(TaskDetailEvent.ConfirmArchive)
            TaskDetailAction.Archive -> mutate(archive = true) { repository.lifecycle(id, "ARCHIVED") }
            is TaskDetailAction.ChangeLifecycle -> mutate { repository.lifecycle(id, action.value) }
            TaskDetailAction.Complete -> {
                if (task.timer) send(TaskDetailEvent.Focus) else mutate { repository.completeResult(id) }
            }
        }
    }

    private fun send(event: TaskDetailEvent) {
        viewModelScope.launch { emitEvent(event) }
    }

    private fun mutate(
        archive: Boolean = false,
        operation: () -> TaskOperationResult,
    ) {
        if (state.value.busy) return
        mutableState.value = state.value.copy(busy = true)
        viewModelScope.launch {
            try {
                val result = withContext(Dispatchers.IO) { operation() }
                emitEvent(TaskDetailEvent.Feedback(result.message, archive && result.success))
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                emitEvent(TaskDetailEvent.Feedback(errorMessage()))
            } finally {
                mutableState.value = state.value.copy(busy = false)
            }
        }
    }

    private fun errorMessage() = AppText.text(TextKey.CopyChuaTheCapNhatVuiLongThuLai42c8ac)

    private fun Task.toDisplay(): TaskDetailDisplay {
        val timer = config.verification == "TIMER"
        val reward =
            when {
                !timer -> 8
                config.minutes >= 45 -> 20
                config.minutes >= 20 -> 15
                else -> 10
            }
        return TaskDetailDisplay(
            category = category,
            title = title,
            kind = kind,
            frequency = frequency,
            days = config.days,
            description = config.description,
            start = clockLabel(config.startMinute),
            end = clockLabel(config.endMinute),
            due = due,
            lastDone = lastDone,
            lifecycle = lifecycle,
            minutes = config.minutes,
            streak = streak,
            reward = reward,
            done = doneToday || finished,
            timer = timer,
            history = history.map { TaskCompletionDisplay(it.date, it.granted, it.nominal) },
        )
    }
}
