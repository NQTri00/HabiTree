package com.mamxanh.app.feature.tasks.presentation.ui.list
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.mamxanh.app.core.presentation.BaseViewModel
import com.mamxanh.app.feature.tasks.domain.model.TaskConfig
import com.mamxanh.app.feature.tasks.domain.repository.TaskOperationResult
import com.mamxanh.app.feature.tasks.domain.repository.TaskRepository
import com.mamxanh.app.feature.tasks.presentation.ui.form.message
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey
import com.mamxanh.shared.model.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class TasksViewModel(
    private val repository: TaskRepository,
    private val savedState: SavedStateHandle,
) : BaseViewModel<TasksState, TasksEvent, TasksAction>(
        initialState =
            TasksState(
                isAddingTask = savedState["isAddingTask"] ?: false,
                editingId = savedState["editingId"],
                draft = savedState.get<ArrayList<String>>("draft").orEmpty(),
            ),
    ) {
    val uiState get() = state
    val effects get() = events
    private var foreground = false

    init {
        viewModelScope.launch { repository.changes.collect { refresh() } }
        viewModelScope.launch {
            // Persisted sessions recover paused, never accrue time while the process was absent.
            withContext(
                Dispatchers.IO,
            ) {
                repository
                    .snapshot()
                    .sessions
                    .filter { it.running }
                    .forEach { repository.timer(it.taskId, "PAUSE") }
            }
            while (isActive) {
                delay(1000)
                if (foreground &&
                    mutableState.value.phase.sessions
                        .any { it.running }
                ) {
                    val completed =
                        withContext(Dispatchers.IO) {
                            repository.snapshot().sessions.filter { it.running }.mapNotNull { session ->
                                repository.timer(session.taskId, "TICK")
                                val updated = repository.snapshot().sessions.firstOrNull { it.id == session.id }
                                if (updated?.running == true && updated.activeSeconds >= updated.requiredSeconds) {
                                    repository.completeResult(session.taskId).takeIf { it.success }
                                } else {
                                    null
                                }
                            }
                        }
                    completed.forEach {
                        emitEvent(TasksEvent.Feedback(it.message))
                        emitEvent(TasksEvent.DataChanged)
                    }
                    refresh()
                }
            }
        }
    }

    private suspend fun refresh() {
        val value =
            withContext(Dispatchers.IO) { Triple(repository.observeSnapshot(), repository.snapshot(), repository.isDailyBonusClaimed()) }
        val eligible = withContext(Dispatchers.IO) { repository.canClaimDailyBonus() }
        mutableState.value =
            mutableState.value.copy(
                tasks = value.first,
                phase = value.second,
                isDailyBonusClaimed = value.third,
                canClaimDailyBonus = eligible,
                isLoading = false,
            )
    }

    private fun close() {
        savedState["isAddingTask"] = false
        savedState["editingId"] = null
        savedState["draft"] = arrayListOf<String>()
        mutableState.value = mutableState.value.copy(isAddingTask = false, editingId = null, draft = emptyList())
    }

    private fun mutate(
        showSuccess: Boolean = true,
        block: () -> TaskOperationResult,
    ) {
        if (mutableState.value.busy)return
        mutableState.value = mutableState.value.copy(busy = true)
        viewModelScope.launch {
            try {
                val result = withContext(Dispatchers.IO) { block() }
                refresh()
                if (showSuccess || !result.success)emitEvent(TasksEvent.Feedback(result.message))
                if (result.success)emitEvent(TasksEvent.DataChanged)
            } finally {
                mutableState.value = mutableState.value.copy(busy = false)
            }
        }
    }

    override fun handleAction(action: TasksAction) {
        val intent = action
        when (intent) {
            TasksAction.Refresh -> viewModelScope.launch { refresh() }
            is TasksAction.Foreground -> {
                foreground = intent.active
                if (!foreground) {
                    viewModelScope.launch {
                        withContext(Dispatchers.IO) {
                            repository
                                .snapshot()
                                .sessions
                                .filter { it.running }
                                .forEach { repository.timer(it.taskId, "PAUSE") }
                        }
                        ;refresh()
                    }
                }
            }
            is TasksAction.UpdateDraft -> {
                savedState["draft"] = ArrayList(intent.values)
                mutableState.value = mutableState.value.copy(draft = intent.values)
            }
            TasksAction.ShowAddDialog -> {
                savedState["isAddingTask"] = true
                mutableState.value = mutableState.value.copy(isAddingTask = true, editingId = null)
            }
            TasksAction.DismissAddDialog -> close()
            is TasksAction.Detail -> mutableState.value = mutableState.value.copy(detailId = intent.id)
            is TasksAction.Edit -> {
                savedState["isAddingTask"] = true
                savedState["editingId"] = intent.id
                mutableState.value =
                    mutableState.value.copy(
                        isAddingTask = true,
                        editingId = intent.id,
                        detailId = null,
                        draft = emptyList(),
                    )
            }
            is TasksAction.Save -> {
                val id = mutableState.value.editingId
                if (intent.config.validationError() != null)return
                mutate { repository.save(id, intent.config) }
                close()
            }
            is TasksAction.Lifecycle -> mutate { repository.lifecycle(intent.id, intent.state) }
            is TasksAction.Timer -> mutate(showSuccess = false) { repository.timer(intent.id, intent.action) }
            is TasksAction.Complete -> mutate { repository.completeResult(intent.id) }
            is TasksAction.Delete -> mutate { repository.lifecycle(intent.id, "ARCHIVED") }
            is TasksAction.SetFilter -> mutableState.value = mutableState.value.copy(filter = intent.filter)
            is TasksAction.Add ->
                mutate {
                    repository.save(
                        null,
                        TaskConfig(intent.title, intent.kind, intent.category, frequency = intent.frequency, due = intent.due),
                    )
                }
            TasksAction.ClaimBonus ->
                mutate {
                    val before = repository.snapshot().earnedToday
                    val success = repository.claimDailyBonus()
                    val after = repository.snapshot().earnedToday
                    TaskOperationResult(
                        success,
                        if (success) {
                            AppText.text(
                                TextKey.CopyDailyClear0XuGioiHan1XuNgay6fe04e,
                                after - before,
                                Balance.DAILY_CAP,
                            )
                        } else {
                            AppText.text(TextKey.CopyHoanThanhKeHoachBatBuoc5342d8)
                        },
                    )
                }
        }
    }
}
