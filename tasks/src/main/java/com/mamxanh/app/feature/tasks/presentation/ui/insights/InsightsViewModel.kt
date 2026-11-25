package com.mamxanh.app.feature.tasks.presentation.ui.insights

import android.os.Bundle
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.mamxanh.app.core.presentation.BaseViewModel
import com.mamxanh.app.feature.tasks.domain.repository.InsightsRepository
import com.mamxanh.app.feature.tasks.domain.repository.TaskReminderWriter
import com.mamxanh.app.feature.tasks.domain.repository.TaskRepository
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class InsightsViewModel(
    private val repository: TaskRepository,
    private val savedState: SavedStateHandle,
    private val reminderWriter: TaskReminderWriter,
    private val insights: InsightsRepository,
) : BaseViewModel<InsightsState, InsightsEvent, InsightsAction>(
        InsightsState(
            period = savedState["insightDays"] ?: "7",
            reminders =
                savedState
                    .get<Bundle>("reminderDrafts")
                    ?.let { root ->
                        root
                            .keySet()
                            .mapNotNull { id ->
                                root.getBundle(id)?.let { draft ->
                                    id to
                                        InsightsReminderDraft(
                                            draft.getString("minute").orEmpty(),
                                            draft.getString("mode", "OFF"),
                                            draft.getBoolean("followUp"),
                                        )
                                }
                            }.toMap()
                    }.orEmpty(),
        ),
    ) {
    private var loadJob: Job? = null

    override fun handleAction(action: InsightsAction) {
        when (action) {
            is InsightsAction.Load -> load(action.id)
            is InsightsAction.SetPeriod -> {
                if (action.value !in setOf("7", "30")) return
                savedState["insightDays"] = action.value
                mutableState.value = state.value.copy(period = action.value)
            }
            is InsightsAction.OpenTask -> send(InsightsEvent.OpenTask(action.id))
            is InsightsAction.RequestAdjustment -> send(InsightsEvent.ConfirmAdjustment(action.id))
            is InsightsAction.ApplyAdjustment -> adjust(action.id)
            is InsightsAction.EditReminder -> persistReminders(state.value.reminders + (action.id to action.draft))
            is InsightsAction.SaveReminder -> saveReminder(action.id, action.draft)
            InsightsAction.OpenSettings -> send(InsightsEvent.OpenSettings)
            InsightsAction.AddTask -> send(InsightsEvent.AddTask)
        }
    }

    private fun load(id: String) {
        loadJob?.cancel()
        loadJob =
            viewModelScope.launch {
                try {
                    val snapshot = withContext(Dispatchers.IO) { insights.load(id) }
                    mutableState.value = state.value.copy(snapshot = snapshot)
                } catch (cancelled: CancellationException) {
                    throw cancelled
                } catch (_: Exception) {
                    emitEvent(InsightsEvent.LoadFailed(id, AppText.text(TextKey.CopyChuaTheCapNhatVuiLongThuLai42c8ac)))
                }
            }
    }

    private fun persistReminders(drafts: Map<String, InsightsReminderDraft>) {
        savedState["reminderDrafts"] =
            Bundle().apply {
                drafts.forEach { (id, draft) ->
                    putBundle(
                        id,
                        Bundle().apply {
                            putString("minute", draft.minute)
                            putString("mode", draft.mode)
                            putBoolean("followUp", draft.followUp)
                        },
                    )
                }
            }
        mutableState.value = state.value.copy(reminders = drafts)
    }

    private fun saveReminder(
        id: String,
        draft: InsightsReminderDraft,
    ) {
        val taskId = id.toLongOrNull() ?: return
        if (state.value.busy) return
        mutableState.value = state.value.copy(busy = true)
        viewModelScope.launch {
            try {
                val result =
                    withContext(Dispatchers.IO) {
                        reminderWriter.save(taskId, draft.mode, parseClock(draft.minute), draft.followUp)
                    }
                if (result.success && state.value.reminders[id] == draft) persistReminders(state.value.reminders - id)
                emitEvent(InsightsEvent.ReminderResult(id, result.message))
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                emitEvent(InsightsEvent.ReminderResult(id, AppText.text(TextKey.CopyChuaTheCapNhatVuiLongThuLai42c8ac)))
            } finally {
                mutableState.value = state.value.copy(busy = false)
            }
        }
    }

    private fun parseClock(value: String): Int {
        if (value.isBlank()) return -1
        val match = Regex("^(\\d{1,2}):(\\d{2})$").matchEntire(value) ?: return -2
        val hour = match.groupValues[1].toInt()
        val minute = match.groupValues[2].toInt()
        return if (hour in 0..23 && minute in 0..59) hour * 60 + minute else -2
    }

    private fun send(event: InsightsEvent) {
        viewModelScope.launch { emitEvent(event) }
    }

    private fun adjust(id: String) {
        if (state.value.busy) return
        mutableState.value = state.value.copy(busy = true)
        viewModelScope.launch {
            try {
                val result =
                    withContext(Dispatchers.IO) {
                        repository.observeSnapshot().firstOrNull { it.id.toString() == id }?.let { task ->
                            repository.save(task.id, task.config.copy(frequency = "WEEKDAYS", days = ""))
                        }
                    }
                emitEvent(
                    InsightsEvent.AdjustmentResult(
                        id,
                        result?.success == true,
                        result?.message ?: AppText.text(TextKey.CopyKhongTimThayViec07c69c),
                    ),
                )
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                emitEvent(InsightsEvent.AdjustmentResult(id, false, AppText.text(TextKey.CopyChuaTheCapNhatVuiLongThuLai42c8ac)))
            } finally {
                mutableState.value = state.value.copy(busy = false)
            }
        }
    }
}
