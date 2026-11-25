package com.mamxanh.app.feature.tasks.presentation.ui.form

import android.os.Bundle
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.mamxanh.app.core.presentation.BaseViewModel
import com.mamxanh.app.feature.tasks.domain.model.Task
import com.mamxanh.app.feature.tasks.domain.repository.TaskRepository
import com.mamxanh.app.feature.tasks.domain.usecase.taskConfigFromDraft
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey
import com.mamxanh.shared.model.clockLabel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TaskFormViewModel(
    private val repository: TaskRepository,
    private val savedState: SavedStateHandle,
    private val argument: String,
) : BaseViewModel<TaskFormState, TaskFormEvent, TaskFormAction>(
        TaskFormState(
            draft =
                TaskFormField.entries
                    .mapNotNull { field ->
                        savedState.get<Bundle>("draft")?.getString(field.savedKey)?.let { field to it }
                    }.toMap(),
            error = savedState["task-error"] ?: "",
            errorAttempt = savedState["task-error-attempt"] ?: 0,
            busy = savedState.get<String>("task-saved-message") != null,
            savedMessage = savedState["task-saved-message"],
        ),
    ) {
    override fun handleAction(action: TaskFormAction) {
        val current = state.value
        if (current.busy) return
        when (action) {
            is TaskFormAction.Change ->
                update(
                    current.copy(
                        draft = current.draft + (action.field to action.value),
                        error = current.error.takeUnless { it == action.field.savedKey }.orEmpty(),
                    ),
                )
            TaskFormAction.Load -> load()
            TaskFormAction.Save -> current.initial?.let { save(current, it + current.draft) }
            TaskFormAction.Discard -> update(TaskFormState(initial = current.initial))
        }
    }

    fun acknowledgeSave(): String? {
        val current = state.value
        val message = current.savedMessage ?: return null
        update(TaskFormState(initial = current.initial))
        return message
    }

    private fun load() {
        if (argument.isBlank()) {
            mutableState.value = state.value.copy(initial = initialValues(null))
            return
        }
        viewModelScope.launch {
            try {
                val task =
                    withContext(Dispatchers.IO) {
                        repository.observeSnapshot().firstOrNull { it.id.toString() == argument }
                    }
                if (task == null) {
                    mutableState.value = state.value.copy(initial = null)
                    emitEvent(TaskFormEvent.Feedback(AppText.text(TextKey.CopyKhongTimThayViec07c69c)))
                } else {
                    mutableState.value = state.value.copy(initial = initialValues(task))
                }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                mutableState.value = state.value.copy(initial = null)
                emitEvent(TaskFormEvent.Feedback(AppText.text(TextKey.CopyChuaTheCapNhatVuiLongThuLai42c8ac)))
            }
        }
    }

    private fun initialValues(task: Task?): Map<TaskFormField, String> {
        val config = task?.config
        return mapOf(
            TaskFormField.Title to task?.title.orEmpty(),
            TaskFormField.Kind to (task?.kind ?: "DAILY"),
            TaskFormField.Category to (task?.category ?: AppText.canonical(TextKey.CopyHocTapA51516)),
            TaskFormField.Frequency to (task?.frequency ?: "EVERY_DAY"),
            TaskFormField.Verification to (config?.verification ?: "CLICK"),
            TaskFormField.Minutes to (config?.minutes ?: 5).toString(),
            TaskFormField.Due to task?.due.orEmpty(),
            TaskFormField.Days to config?.days.orEmpty(),
            TaskFormField.Description to config?.description.orEmpty(),
            TaskFormField.Start to config?.startMinute?.let(::clockLabel).orEmpty(),
            TaskFormField.End to config?.endMinute?.let(::clockLabel).orEmpty(),
            TaskFormField.Reminder to config?.reminderMinute?.let(::clockLabel).orEmpty(),
        )
    }

    private fun update(next: TaskFormState) {
        savedState["draft"] =
            Bundle().apply {
                next.draft.forEach { (field, value) -> putString(field.savedKey, value) }
            }
        savedState["task-error"] = next.error
        savedState["task-error-attempt"] = next.errorAttempt
        savedState["task-saved-message"] = next.savedMessage
        mutableState.value = next
    }

    private fun save(
        current: TaskFormState,
        values: Map<TaskFormField, String>,
    ) {
        val invalid = taskFormValidationError { values[it].orEmpty() }
        val validated = current.copy(error = invalid?.savedKey.orEmpty(), errorAttempt = current.errorAttempt + 1)
        update(validated)
        if (invalid != null) return
        val id = argument.toLongOrNull()
        if (argument.isNotEmpty() && id == null) return
        val config = taskConfigFromDraft(values.mapKeys { it.key.savedKey }, ::clock)
        mutableState.value = validated.copy(busy = true)
        viewModelScope.launch {
            var saved = false
            try {
                val result = withContext(Dispatchers.IO) { repository.save(id, config) }
                if (result.success) {
                    saved = true
                    update(TaskFormState(busy = true, savedMessage = result.message))
                } else {
                    emitEvent(TaskFormEvent.Feedback(result.message))
                }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                emitEvent(TaskFormEvent.Feedback(AppText.text(TextKey.CopyChuaTheCapNhatVuiLongThuLai42c8ac)))
            } finally {
                if (!saved) mutableState.value = state.value.copy(busy = false)
            }
        }
    }

    private fun clock(value: String): Int {
        if (value.isBlank()) return -1
        val match = clockPattern.matchEntire(value) ?: return -2
        val hour = match.groupValues[1].toInt()
        val minute = match.groupValues[2].toInt()
        return if (hour in 0..23 && minute in 0..59) hour * 60 + minute else -2
    }

    private companion object {
        val clockPattern = Regex("^(\\d{1,2}):(\\d{2})$")
    }
}
